"""
End-to-end acceptance tests for ``cdb-mqtt-message-broker``.

Spawns the real ``cdb-mqtt start`` CLI process configured with ``--handlers-dir`` and
``--config``, uses the ComponentDB REST API (and Selenium for UI-only machine-design
trash/restore and template instantiation) to trigger all 18 ``CdbEventType`` events on
a live portal instance, and verifies that ``cdb-mqtt`` receives, parses, and dispatches
every event to its typed handler method.

Excluded from default unit test runs via ``-m "not acceptance"`` in ``pytest.ini``.
Run with ``make test-acceptance`` or ``sbin/cdb_mqtt_test.sh``.
"""

from __future__ import annotations

import json
import os
import shutil
import subprocess
import sys
import tempfile
import textwrap
import threading
import time
import unittest
import uuid
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Callable, Dict, List, Optional, Set

import pytest
from pydantic import BaseModel

from cdb_mqtt import (
    CDB_EVENT_MODELS,
    CdbEventType,
    CdbMachineAddEvent,
    CdbMachineTrashEvent,
    CdbOperation,
)
from tests.conftest import EXAMPLE_HANDLERS_DIR, PACKAGE_DIR, REPO_ROOT

PYTHON_CLIENT_DIR = REPO_ROOT / "tools" / "developer_tools" / "python-client"
PYTHON_CLIENT_API_DIR = PYTHON_CLIENT_DIR / "packages" / "api"
PYTHON_CLIENT_CLI_DIR = PYTHON_CLIENT_DIR / "packages" / "cli"
PYTHON_CLIENT_TEST_DIR = PYTHON_CLIENT_DIR / "test"
SELENIUM_DIR = REPO_ROOT / "tools" / "developer_tools" / "portal_testing" / "PythonSeleniumTest"
BASE_BROKER_SRC = REPO_ROOT / "tools" / "developer_tools" / "cdb-bely-message-broker-base" / "src"

for extra_path in (
    PYTHON_CLIENT_API_DIR,
    PYTHON_CLIENT_CLI_DIR,
    PYTHON_CLIENT_TEST_DIR,
    PYTHON_CLIENT_DIR,
    SELENIUM_DIR,
):
    path_str = str(extra_path)
    if extra_path.is_dir() and path_str not in sys.path:
        sys.path.append(path_str)

# Allow importing python-client/test/* under ``test`` (even if stdlib ``test`` is loaded)
# and PythonSeleniumTest/tests/* under ``tests``.
import test as _test_pkg  # noqa: E402
import tests as _tests_pkg  # noqa: E402

_api_test_dir = str(PYTHON_CLIENT_TEST_DIR)
if PYTHON_CLIENT_TEST_DIR.is_dir() and _api_test_dir not in _test_pkg.__path__:
    _test_pkg.__path__.insert(0, _api_test_dir)

_selenium_tests_dir = str(SELENIUM_DIR / "tests")
if (SELENIUM_DIR / "tests").is_dir() and _selenium_tests_dir not in _tests_pkg.__path__:
    _tests_pkg.__path__.append(_selenium_tests_dir)

try:
    import mqtt_test_support
    from mqtt_test_support import (
        PUBLISH_FAILURE_HINT,
        MqttTestSettings,
        MqttUnavailable,
        _is_failure,
    )
except ImportError:
    mqtt_test_support = None  # type: ignore[assignment]
    PUBLISH_FAILURE_HINT = ""
    MqttTestSettings = None  # type: ignore[assignment,misc]
    MqttUnavailable = Exception  # type: ignore[assignment,misc]

    def _is_failure(code: Any, subscribe: bool = False) -> bool:
        return bool(code)


try:
    from test.mqtt_test import MqttIntegrationTest as _BaseApiMqttTest

    _BaseApiMqttTest.__test__ = False  # type: ignore[attr-defined]
except ImportError as _api_import_err:
    _API_IMPORT_ERROR: Optional[Exception] = _api_import_err
    _BaseApiMqttTest = unittest.TestCase  # type: ignore[assignment,misc]
else:
    _API_IMPORT_ERROR = None

try:
    from tests.test_mqtt_machine_design import (
        MachineDesignMqttTest as _BaseSeleniumMachineDesignTest,
    )

    _BaseSeleniumMachineDesignTest.__test__ = False  # type: ignore[attr-defined]
except ImportError as _selenium_import_err:
    _SELENIUM_IMPORT_ERROR: Optional[Exception] = _selenium_import_err
    _BaseSeleniumMachineDesignTest = unittest.TestCase  # type: ignore[assignment,misc]
else:
    _SELENIUM_IMPORT_ERROR = None

API_EVENT_TYPES: Set[CdbEventType] = set(CdbEventType) - {
    CdbEventType.MACHINE_DESIGN_TRASH,
    CdbEventType.MACHINE_DESIGN_RESTORE,
}

RECORDER_HANDLER_SOURCE = textwrap.dedent("""\
    import json
    from pathlib import Path
    from typing import Any, Optional
    from pydantic import BaseModel
    from cdb_mqtt import CDB_EVENT_MODELS, CdbEventType, CdbMQTTHandler, MQTTMessage


    def _make_event_method(event_type: CdbEventType):
        method_name = event_type.handler_method_name

        async def _handle(self, event: BaseModel) -> None:
            self._write_typed_event(event_type, method_name, event)

        _handle.__name__ = method_name
        return _handle


    class AcceptanceRecordingHandler(CdbMQTTHandler):
        \"\"\"Records every CDB MQTT event dispatched by ``cdb-mqtt start`` to a JSONL file.\"\"\"

        def __init__(
            self,
            events_file: Optional[str] = None,
            api_factory: Any = None,
            global_config: Optional[Any] = None,
        ):
            super().__init__(api_factory=api_factory, global_config=global_config)
            self.events_file = Path(events_file) if events_file else None
            if self.events_file is not None:
                self.events_file.parent.mkdir(parents=True, exist_ok=True)
            self._current_message: Optional[MQTTMessage] = None

        async def dispatch(self, message: MQTTMessage) -> None:
            self._current_message = message
            try:
                await super().dispatch(message)
            except Exception as ex:
                self._append_record(
                    {
                        "kind": "error",
                        "topic": message.topic,
                        "error": f"{type(ex).__name__}: {ex}",
                    }
                )
                raise
            finally:
                self._current_message = None

        def _write_typed_event(
            self, event_type: CdbEventType, method_name: str, event: BaseModel
        ) -> None:
            expected_cls = CDB_EVENT_MODELS[event_type]
            if not isinstance(event, expected_cls):
                self._append_record(
                    {
                        "kind": "error",
                        "topic": event_type.value,
                        "error": (
                            f"Expected {expected_cls.__name__}, got {type(event).__name__}"
                        ),
                    }
                )
                return
            msg = self._current_message
            assert msg is not None
            self._append_record(
                {
                    "kind": "typed",
                    "topic": msg.topic,
                    "event_type": event_type.value,
                    "handler_method": method_name,
                    "model_name": type(event).__name__,
                    "payload": msg.payload,
                    "has_global_config": self.global_config is not None,
                }
            )

        async def handle_generic(self, message: MQTTMessage) -> None:
            self._append_record(
                {
                    "kind": "generic",
                    "topic": message.topic,
                    "payload": message.payload,
                }
            )

        def _append_record(self, data: dict) -> None:
            if self.events_file is None:
                return
            with self.events_file.open("a", encoding="utf-8") as f:
                f.write(json.dumps(data) + "\\n")
                f.flush()


    for _event_type in CdbEventType:
        setattr(
            AcceptanceRecordingHandler,
            _event_type.handler_method_name,
            _make_event_method(_event_type),
        )
    """)


@dataclass
class DispatchedRecord:
    """A single MQTT message processed by the ``cdb-mqtt start`` CLI process."""

    topic: str
    payload: Dict[str, Any]
    received_at: float
    event_type: Optional[CdbEventType] = None
    handler_method: Optional[str] = None
    typed_event: Optional[BaseModel] = None

    def summary(self) -> str:
        model_name = type(self.typed_event).__name__ if self.typed_event else "generic"
        text = json.dumps(self.payload, sort_keys=True)
        if len(text) > 300:
            text = text[:300] + "..."
        return f"{self.topic} [{model_name}] {text}"


class CdbBrokerCollector:
    """
    Live subscriber that spawns the real ``cdb-mqtt start`` CLI process and tails the
    events dispatched through ``CdbMQTTClient``, ``CdbPluginManager``, and ``CdbMQTTHandler``.
    """

    def __init__(self, settings: Any, topic_filter: str = "cdb/#"):
        self.settings = settings
        self.topic_filter = topic_filter
        self._records: List[DispatchedRecord] = []
        self._all_typed_records: List[DispatchedRecord] = []
        self.seen_event_types: Set[CdbEventType] = set()
        self.handler_errors: List[str] = []
        self._lock = threading.Condition()
        self._stop_tail = threading.Event()
        self._tail_thread: Optional[threading.Thread] = None
        self._process: Optional[subprocess.Popen[str]] = None
        self._pub_client: Optional[Any] = None

        self._temp_dir = tempfile.TemporaryDirectory(prefix="cdb-mqtt-acceptance-")
        temp_root = Path(self._temp_dir.name)
        self.handlers_dir = temp_root / "handlers"
        self.handlers_dir.mkdir(parents=True, exist_ok=True)
        self.events_file = temp_root / "dispatched-events.jsonl"
        self.events_file.touch()
        self.audit_file = temp_root / "machine-design-audit.jsonl"
        self.config_file = temp_root / "config.yaml"
        self.lock_file = temp_root / "cdb-mqtt.lock"
        self.process_log_file = temp_root / "cdb-mqtt.log"

        if EXAMPLE_HANDLERS_DIR.exists():
            for handler_file in EXAMPLE_HANDLERS_DIR.glob("*.py"):
                if not handler_file.name.startswith("_"):
                    shutil.copy2(handler_file, self.handlers_dir / handler_file.name)

        (self.handlers_dir / "acceptance_recorder_handler.py").write_text(
            RECORDER_HANDLER_SOURCE, encoding="utf-8"
        )

        portal_url = os.getenv("CDB_TEST_PORTAL_URL", "http://localhost:8080/cdb")
        config_data = {
            "global": {
                "cdb_url": portal_url,
            },
            "handlers": {
                "AcceptanceRecordingHandler": {
                    "events_file": str(self.events_file),
                },
                "MachineDesignAuditHandler": {
                    "audit_file": str(self.audit_file),
                },
            },
        }
        self.config_file.write_text(json.dumps(config_data, indent=2), encoding="utf-8")

    def _build_subprocess_env(self) -> Dict[str, str]:
        env = os.environ.copy()
        py_paths = [
            str(PACKAGE_DIR / "src"),
            str(BASE_BROKER_SRC),
            str(PYTHON_CLIENT_API_DIR),
            str(PYTHON_CLIENT_CLI_DIR),
        ]
        existing = env.get("PYTHONPATH")
        if existing:
            py_paths.append(existing)
        env["PYTHONPATH"] = os.pathsep.join(py_paths)
        return env

    def _start_publisher(self) -> None:
        try:
            import paho.mqtt.client as mqtt
        except ImportError as ex:
            raise MqttUnavailable(f"paho-mqtt is not installed ({ex})") from ex

        client_id = f"cdb-mqtt-acceptance-pub-{uuid.uuid4().hex[:8]}"
        callback_api = getattr(mqtt, "CallbackAPIVersion", None)
        if callback_api is not None:
            client = mqtt.Client(callback_api.VERSION2, client_id=client_id)
        else:
            client = mqtt.Client(client_id=client_id)
        if self.settings.username:
            client.username_pw_set(self.settings.username, self.settings.password)

        connected = threading.Event()
        failure: List[str] = []

        def on_connect(
            _client: Any, _userdata: Any, _flags: Any, reason_code: Any, _props: Any = None
        ) -> None:
            if _is_failure(reason_code):
                failure.append(
                    f"broker {self.settings.describe()} refused connection: {reason_code}"
                )
            connected.set()

        client.on_connect = on_connect
        try:
            client.connect(self.settings.host, self.settings.port, keepalive=30)
        except (OSError, ValueError) as ex:
            raise MqttUnavailable(
                f"cannot connect to broker {self.settings.describe()}: {ex}"
            ) from ex

        client.loop_start()
        if not connected.wait(self.settings.timeout):
            client.loop_stop()
            raise MqttUnavailable(
                f"no CONNACK from broker {self.settings.describe()} within "
                f"{self.settings.timeout}s"
            )
        if failure:
            client.loop_stop()
            raise MqttUnavailable(failure[0])

        self._pub_client = client

    def _tail_events_file(self) -> None:
        with self.events_file.open("r", encoding="utf-8") as f:
            while not self._stop_tail.is_set():
                line = f.readline()
                if not line:
                    time.sleep(0.01)
                    continue
                line = line.strip()
                if not line:
                    continue
                self._ingest_line(line)

    def _ingest_line(self, line: str) -> None:
        data = json.loads(line)
        kind = data.get("kind")
        if kind == "error":
            with self._lock:
                self.handler_errors.append(data.get("error", "Unknown handler error"))
                self._lock.notify_all()
            return

        if kind == "typed":
            event_type = CdbEventType(data["event_type"])
            model_cls = CDB_EVENT_MODELS[event_type]
            typed_event = model_cls.model_validate(data["payload"])
            record = DispatchedRecord(
                topic=data["topic"],
                payload=data["payload"],
                received_at=time.monotonic(),
                event_type=event_type,
                handler_method=data["handler_method"],
                typed_event=typed_event,
            )
            with self._lock:
                self._records.append(record)
                self._all_typed_records.append(record)
                self.seen_event_types.add(event_type)
                self._lock.notify_all()
            return

        if kind == "generic":
            record = DispatchedRecord(
                topic=data["topic"],
                payload=data["payload"],
                received_at=time.monotonic(),
            )
            with self._lock:
                self._records.append(record)
                self._lock.notify_all()

    def start(self) -> "CdbBrokerCollector":
        self._start_publisher()
        env = self._build_subprocess_env()

        # Verify ``cdb-mqtt list-handlers`` discovers our handlers from --handlers-dir
        list_res = subprocess.run(
            [
                sys.executable,
                "-m",
                "cdb_mqtt.cli",
                "list-handlers",
                "--handlers-dir",
                str(self.handlers_dir),
            ],
            env=env,
            capture_output=True,
            text=True,
            timeout=15,
            check=False,
        )
        if list_res.returncode != 0 or "AcceptanceRecordingHandler" not in list_res.stdout:
            self.stop()
            raise RuntimeError(
                f"cdb-mqtt list-handlers failed (rc={list_res.returncode}):\n"
                f"stdout: {list_res.stdout}\nstderr: {list_res.stderr}"
            )

        client_id = f"cdb-mqtt-acceptance-{uuid.uuid4().hex[:8]}"
        cmd = [
            sys.executable,
            "-m",
            "cdb_mqtt.cli",
            "start",
            "--broker-host",
            self.settings.host,
            "--broker-port",
            str(self.settings.port),
            "--client-id",
            client_id,
            "--topic",
            self.topic_filter,
            "--handlers-dir",
            str(self.handlers_dir),
            "--config",
            str(self.config_file),
            "--lock-file",
            str(self.lock_file),
            "--log-level",
            "DEBUG",
        ]
        if self.settings.username:
            cmd.extend(["--username", self.settings.username])
        if self.settings.password:
            cmd.extend(["--password", self.settings.password])

        log_fh = self.process_log_file.open("w", encoding="utf-8")
        self._process = subprocess.Popen(
            cmd,
            env=env,
            stdout=log_fh,
            stderr=subprocess.STDOUT,
            text=True,
        )
        log_fh.close()

        self._tail_thread = threading.Thread(
            target=self._tail_events_file,
            name="cdb-mqtt-acceptance-tail",
            daemon=True,
        )
        self._tail_thread.start()

        # Wait until ``cdb-mqtt start`` connects, subscribes, and processes a probe message.
        deadline = time.monotonic() + self.settings.timeout
        ready = False
        while time.monotonic() < deadline:
            if self._process.poll() is not None:
                logs = self.process_log_file.read_text(encoding="utf-8")
                self.stop()
                raise MqttUnavailable(
                    f"cdb-mqtt start exited prematurely (rc={self._process.returncode}):\n{logs}"
                )
            try:
                self.probe(timeout=0.25)
                ready = True
                break
            except AssertionError:
                pass

        if not ready:
            logs = self.process_log_file.read_text(encoding="utf-8")
            self.stop()
            raise MqttUnavailable(
                f"cdb-mqtt start did not respond to broker probe within "
                f"{self.settings.timeout}s:\n{logs}"
            )

        with self._lock:
            self._records.clear()
        return self

    def stop(self) -> None:
        self._stop_tail.set()
        if self._tail_thread is not None:
            self._tail_thread.join(timeout=2.0)
            self._tail_thread = None

        if self._process is not None:
            if self._process.poll() is None:
                self._process.terminate()
                try:
                    self._process.wait(timeout=5.0)
                except subprocess.TimeoutExpired:
                    self._process.kill()
                    self._process.wait(timeout=2.0)
            self._process = None

        if self._pub_client is not None:
            try:
                self._pub_client.disconnect()
            finally:
                self._pub_client.loop_stop()
                self._pub_client = None

        self._temp_dir.cleanup()

    def probe(self, timeout: Optional[float] = None) -> Dict[str, Any]:
        """Round-trip a probe message through the broker and the ``cdb-mqtt start`` process."""
        assert self._pub_client is not None
        topic = f"cdb/test-probe/{uuid.uuid4().hex}"
        marker = uuid.uuid4().hex
        self._pub_client.publish(topic, json.dumps({"probe": marker}), qos=1)
        record = self._wait(topic, lambda payload: payload.get("probe") == marker, timeout)
        if record is None:
            raise AssertionError(
                f"Probe message on {topic} was not echoed back by cdb-mqtt via broker "
                f"{self.settings.describe()}"
            )
        return record.payload

    def drain(self) -> List[DispatchedRecord]:
        """Flush all pending broker deliveries through ``cdb-mqtt`` and clear the buffer."""
        if (
            self._pub_client is not None
            and self._process is not None
            and self._process.poll() is None
        ):
            self.probe(timeout=self.settings.timeout)
        with self._lock:
            drained = self._records
            self._records = []
        return drained

    def messages(self) -> List[DispatchedRecord]:
        with self._lock:
            return list(self._records)

    def typed_records_for(self, event_type: CdbEventType) -> List[DispatchedRecord]:
        with self._lock:
            return [r for r in self._all_typed_records if r.event_type == event_type]

    def wait_for(
        self,
        topic: str,
        predicate: Optional[Callable[[Dict[str, Any]], bool]] = None,
        timeout: Optional[float] = None,
        description: Optional[str] = None,
    ) -> Dict[str, Any]:
        record = self._wait(topic, predicate, timeout)
        if record is None:
            raise AssertionError(self._timeout_message(topic, description, timeout))
        self._verify_typed_record(record)
        return record.payload

    def assert_no_message(
        self,
        topic: str,
        predicate: Optional[Callable[[Dict[str, Any]], bool]] = None,
        within: float = 1.0,
        description: Optional[str] = None,
    ) -> None:
        record = self._wait(topic, predicate, within)
        if record is not None:
            suffix = f" ({description})" if description else ""
            raise AssertionError(f"Unexpected MQTT message{suffix}: {record.summary()}")

    def _verify_typed_record(self, record: DispatchedRecord) -> None:
        if self.handler_errors:
            raise AssertionError(f"Handler errors occurred: {self.handler_errors}")
        expected_type = CdbEventType(record.topic)
        assert record.event_type == expected_type
        assert record.handler_method == expected_type.handler_method_name
        assert record.typed_event is not None
        assert isinstance(record.typed_event, CDB_EVENT_MODELS[expected_type])
        event_ts = getattr(record.typed_event, "event_timestamp", None)
        assert event_ts is not None and event_ts.tzinfo is not None

    def _wait(
        self,
        topic: str,
        predicate: Optional[Callable[[Dict[str, Any]], bool]],
        timeout: Optional[float],
    ) -> Optional[DispatchedRecord]:
        wait_timeout = self.settings.timeout if timeout is None else timeout
        deadline = time.monotonic() + wait_timeout
        with self._lock:
            while True:
                for index, record in enumerate(self._records):
                    if record.topic != topic:
                        continue
                    if predicate is None or predicate(record.payload):
                        del self._records[index]
                        return record
                remaining = deadline - time.monotonic()
                if remaining <= 0:
                    return None
                self._lock.wait(remaining)

    def _timeout_message(
        self, topic: str, description: Optional[str], timeout: Optional[float]
    ) -> str:
        wait_timeout = self.settings.timeout if timeout is None else timeout
        received = self.messages()
        desc = f" matching {description}" if description else ""
        lines = [
            f"No MQTT message on {topic}{desc} within {wait_timeout}s "
            f"from broker {self.settings.describe()} (via cdb-mqtt CLI)."
        ]
        if self.handler_errors:
            lines.append(f"Handler errors: {self.handler_errors}")
        if received:
            lines.append(f"Captured but unmatched messages ({len(received)}):")
            lines.extend("  " + r.summary() for r in received[-10:])
        else:
            lines.append(f"No messages were captured on {self.topic_filter}.")
        if any(r.topic == topic for r in received):
            lines.append(
                f"Messages arrived on {topic} but none matched; compare the payloads "
                "above with the test's expectations."
            )
        elif PUBLISH_FAILURE_HINT:
            lines.append(PUBLISH_FAILURE_HINT)
        return "\n".join(lines)


_SHARED_COLLECTOR: Optional[CdbBrokerCollector] = None


def get_or_start_shared_collector() -> CdbBrokerCollector:
    global _SHARED_COLLECTOR
    if _SHARED_COLLECTOR is not None:
        return _SHARED_COLLECTOR
    if MqttTestSettings is None:
        raise unittest.SkipTest("mqtt_test_support not importable")
    settings = MqttTestSettings.from_env()
    if not settings.enabled:
        raise unittest.SkipTest("MQTT tests disabled (CDB_TEST_MQTT=false)")
    try:
        _SHARED_COLLECTOR = CdbBrokerCollector(settings).start()
        return _SHARED_COLLECTOR
    except MqttUnavailable as ex:
        raise unittest.SkipTest(f"MQTT broker unavailable, skipping: {ex}") from ex


def tearDownModule() -> None:
    global _SHARED_COLLECTOR
    if _SHARED_COLLECTOR is not None:
        _SHARED_COLLECTOR.stop()
        _SHARED_COLLECTOR = None


@pytest.mark.acceptance
class TestCdbBrokerApiAcceptance(_BaseApiMqttTest):
    """
    Triggers all 16 REST-API-accessible CDB events and verifies that the ``cdb-mqtt start``
    CLI process parses and dispatches every one as its typed Pydantic model.
    """

    __test__ = True
    SAMPLE_IMAGE_PATH = str(PYTHON_CLIENT_DIR / "data" / "AnlLogo.png")
    SAMPLE_DOC_PATH = str(PYTHON_CLIENT_DIR / "data" / "CdbSchema-v3.0-3.pdf")

    @classmethod
    def setUpClass(cls) -> None:
        if _API_IMPORT_ERROR is not None:
            raise unittest.SkipTest(
                f"CDB Python API client or test suite not available: {_API_IMPORT_ERROR}"
            )
        super().setUpClass()
        portal_url = os.getenv("CDB_TEST_PORTAL_URL")
        if portal_url:
            from CdbApiFactory import CdbApiFactory

            cls.factory = CdbApiFactory(portal_url)
        cls.mqtt = get_or_start_shared_collector()

    @classmethod
    def tearDownClass(cls) -> None:
        # Collector lifecycle is managed at module level so Selenium tests share it.
        pass

    def test_zzz_all_api_event_types_dispatched_as_typed_models(self) -> None:
        collector: CdbBrokerCollector = self.mqtt  # type: ignore[assignment]
        self.assertIsNotNone(collector._process)
        assert collector._process is not None
        self.assertIsNone(collector._process.poll(), msg="cdb-mqtt CLI process is not running")
        self.assertEqual(collector.handler_errors, [])
        missing = API_EVENT_TYPES - collector.seen_event_types
        self.assertEqual(
            missing,
            set(),
            msg=f"API acceptance tests did not trigger CdbEventType topics: {missing}",
        )
        for event_type in API_EVENT_TYPES:
            records = collector.typed_records_for(event_type)
            self.assertTrue(
                len(records) > 0,
                msg=f"Expected at least one typed record for {event_type.value}",
            )
            for rec in records:
                self.assertIsInstance(rec.typed_event, CDB_EVENT_MODELS[event_type])

        if EXAMPLE_HANDLERS_DIR.exists():
            self.assertTrue(
                collector.audit_file.exists(),
                msg="MachineDesignAuditHandler did not create audit JSONL file",
            )
            audit_lines = [
                json.loads(line)
                for line in collector.audit_file.read_text().splitlines()
                if line.strip()
            ]
            self.assertTrue(len(audit_lines) > 0, msg="Expected machine design audit records")


@pytest.mark.acceptance
@pytest.mark.mqtt
class TestCdbBrokerSeleniumAcceptance(_BaseSeleniumMachineDesignTest):
    """
    Triggers machine-design template instantiation, trash, and restore via Selenium
    and verifies ``CdbMachineTrashEvent`` and template ``CdbMachineAddEvent`` dispatch
    through the ``cdb-mqtt start`` CLI process.
    """

    __test__ = True
    DATA_PATH = str(SELENIUM_DIR / "data-dw")

    # Hide redundant UI tests already covered by TestCdbBrokerApiAcceptance so pytest
    # does not collect or report them as skipped.
    test_machine_add_parent_and_child = None  # type: ignore[assignment]
    test_machine_assign_link_and_move = None  # type: ignore[assignment]

    @classmethod
    def setUpClass(cls) -> None:
        skip_selenium = os.getenv("CDB_TEST_MQTT_SKIP_SELENIUM", "").strip().lower()
        if skip_selenium in ("true", "1", "t", "y", "yes", "on"):
            raise unittest.SkipTest(
                "Selenium acceptance tests skipped (CDB_TEST_MQTT_SKIP_SELENIUM=true)"
            )
        if _SELENIUM_IMPORT_ERROR is not None:
            raise unittest.SkipTest(f"Selenium test suite not available: {_SELENIUM_IMPORT_ERROR}")
        cls.mqtt = get_or_start_shared_collector()

    @classmethod
    def tearDownClass(cls) -> None:
        pass

    def test_machine_add_from_template(self) -> None:
        super().test_machine_add_from_template()
        collector: CdbBrokerCollector = self.mqtt  # type: ignore[assignment]
        template_adds: List[CdbMachineAddEvent] = [
            rec.typed_event
            for rec in collector.typed_records_for(CdbEventType.MACHINE_DESIGN_ADD)
            if isinstance(rec.typed_event, CdbMachineAddEvent)
            and rec.typed_event.source_template is not None
        ]
        self.assertTrue(
            len(template_adds) >= 2,
            msg="Expected CdbMachineAddEvent models with source_template populated",
        )
        for event in template_adds:
            self.assertIsNotNone(event.template_applied_to_machine)
            self.assertEqual(event.template_created_machine_count, 2)

    def test_machine_trash_and_restore(self) -> None:
        collector: CdbBrokerCollector = self.mqtt  # type: ignore[assignment]
        trash_before = len(collector.typed_records_for(CdbEventType.MACHINE_DESIGN_TRASH))
        restore_before = len(collector.typed_records_for(CdbEventType.MACHINE_DESIGN_RESTORE))

        super().test_machine_trash_and_restore()

        trash_records = collector.typed_records_for(CdbEventType.MACHINE_DESIGN_TRASH)[
            trash_before:
        ]
        restore_records = collector.typed_records_for(CdbEventType.MACHINE_DESIGN_RESTORE)[
            restore_before:
        ]
        self.assertTrue(len(trash_records) >= 2)
        self.assertTrue(len(restore_records) >= 2)

        for rec in trash_records[:2]:
            assert isinstance(rec.typed_event, CdbMachineTrashEvent)
            self.assertEqual(rec.typed_event.operation, CdbOperation.TRASH)
            self.assertEqual(rec.typed_event.affected_machine_count, 2)

        for rec in restore_records[:2]:
            assert isinstance(rec.typed_event, CdbMachineTrashEvent)
            self.assertEqual(rec.typed_event.operation, CdbOperation.RESTORE)
            self.assertEqual(rec.typed_event.affected_machine_count, 2)

        if API_EVENT_TYPES.issubset(collector.seen_event_types):
            self.assertEqual(
                collector.seen_event_types,
                set(CdbEventType),
                msg="Expected all 18 CdbEventType topics to be dispatched across the suite",
            )
