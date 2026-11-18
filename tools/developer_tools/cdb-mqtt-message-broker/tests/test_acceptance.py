"""
End-to-end acceptance tests for ``cdb-mqtt-message-broker``.

Uses the ComponentDB REST API (and Selenium for UI-only machine-design trash/restore
and template instantiation) to trigger all 18 ``CdbEventType`` events on a live
portal instance and verifies that ``CdbMQTTClient``, ``CdbPluginManager``,
``CdbMQTTHandler``, and the example handlers receive and validate every event.

Excluded from default unit test runs via ``-m "not acceptance"`` in ``pytest.ini``.
Run with ``make test-acceptance`` or ``sbin/cdb_mqtt_test.sh``.
"""

from __future__ import annotations

import asyncio
import json
import os
import sys
import tempfile
import threading
import time
import unittest
import uuid
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Callable, Dict, List, Optional, Set, Tuple

import pytest
from pydantic import BaseModel

from cdb_mqtt import (
    CDB_EVENT_MODELS,
    CdbConfigManager,
    CdbEventType,
    CdbMachineAddEvent,
    CdbMachineTrashEvent,
    CdbMQTTClient,
    CdbMQTTHandler,
    CdbOperation,
    CdbPluginManager,
    MQTTMessage,
)
from tests.conftest import EXAMPLE_HANDLERS_DIR, REPO_ROOT

PYTHON_CLIENT_DIR = REPO_ROOT / "tools" / "developer_tools" / "python-client"
PYTHON_CLIENT_API_DIR = PYTHON_CLIENT_DIR / "packages" / "api"
PYTHON_CLIENT_CLI_DIR = PYTHON_CLIENT_DIR / "packages" / "cli"
PYTHON_CLIENT_TEST_DIR = PYTHON_CLIENT_DIR / "test"
SELENIUM_DIR = REPO_ROOT / "tools" / "developer_tools" / "portal_testing" / "PythonSeleniumTest"

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


@dataclass
class DispatchedRecord:
    """A single MQTT message processed through CdbMQTTClient and CdbPluginManager."""

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


def _build_recording_handler_class(
    on_typed_event: Callable[[CdbEventType, str, BaseModel, MQTTMessage], None],
    on_generic_message: Callable[[MQTTMessage], None],
):
    """Build a CdbMQTTHandler subclass implementing all 18 typed event handler methods."""

    def make_event_method(event_type: CdbEventType):
        method_name = event_type.handler_method_name

        async def _handle(self, event: BaseModel) -> None:
            msg = getattr(self, "_current_message", None)
            assert msg is not None, "Expected _current_message to be set during dispatch"
            on_typed_event(event_type, method_name, event, msg)

        _handle.__name__ = method_name
        return _handle

    async def dispatch(self, message: MQTTMessage) -> None:
        self._current_message = message
        try:
            await super(CdbMQTTHandler, self).dispatch(message)
        finally:
            self._current_message = None

    async def handle_generic(self, message: MQTTMessage) -> None:
        on_generic_message(message)

    attrs: Dict[str, Any] = {
        "dispatch": dispatch,
        "handle_generic": handle_generic,
    }
    for event_type in CdbEventType:
        attrs[event_type.handler_method_name] = make_event_method(event_type)

    return type("AcceptanceRecordingHandler", (CdbMQTTHandler,), attrs)


class CdbBrokerCollector:
    """
    Live subscriber backed by ``CdbMQTTClient``, ``CdbPluginManager``, and ``CdbMQTTHandler``.

    Drop-in replacement for ``mqtt_test_support.MqttEventCollector`` that routes every
    broker message through the ``cdb_mqtt`` plugin framework and validates the resulting
    Pydantic event models.
    """

    def __init__(self, settings: Any, topic_filter: str = "cdb/#"):
        self.settings = settings
        self.topic_filter = topic_filter
        self._records: List[DispatchedRecord] = []
        self._all_typed_records: List[DispatchedRecord] = []
        self.seen_event_types: Set[CdbEventType] = set()
        self.handler_errors: List[str] = []
        self._lock = threading.Condition()
        self._connected = threading.Event()
        self._subscribed = threading.Event()
        self._failure: Optional[str] = None

        self._temp_dir = tempfile.TemporaryDirectory(prefix="cdb-mqtt-acceptance-")
        self.audit_file = Path(self._temp_dir.name) / "machine-design-audit.jsonl"

        self.config_manager = CdbConfigManager()
        self.config_manager.load_from_dict(
            {
                "global": {
                    "cdb_url": os.getenv("CDB_TEST_PORTAL_URL", "http://localhost:8080/cdb"),
                },
                "handlers": {
                    "MachineDesignAuditHandler": {"audit_file": str(self.audit_file)},
                },
            }
        )

        self.plugin_manager = CdbPluginManager(config_manager=self.config_manager)
        recorder_cls = _build_recording_handler_class(
            self._record_typed_event,
            self._record_generic_message,
        )
        self.recording_handler = recorder_cls(global_config=self.config_manager.get_global_config())
        self.plugin_manager.register_handler(self.recording_handler)
        if EXAMPLE_HANDLERS_DIR.exists():
            self.plugin_manager.load_handlers_from_directory(EXAMPLE_HANDLERS_DIR)

        self._orig_handle_message = self.plugin_manager.handle_message
        self.plugin_manager.handle_message = self._checked_handle_message  # type: ignore[method-assign]

        self._loop: Optional[asyncio.AbstractEventLoop] = None
        self._loop_thread: Optional[threading.Thread] = None
        self.mqtt_client: Optional[CdbMQTTClient] = None

    async def _checked_handle_message(self, message: MQTTMessage) -> None:
        for handler in self.plugin_manager.get_handlers_for_topic(message.topic):
            try:
                await handler.dispatch(message)
            except Exception as ex:
                err = f"{handler.__class__.__name__} failed on {message.topic}: {ex}"
                with self._lock:
                    self.handler_errors.append(err)
                    self._lock.notify_all()
                raise

    def _record_typed_event(
        self,
        event_type: CdbEventType,
        method_name: str,
        event: BaseModel,
        message: MQTTMessage,
    ) -> None:
        expected_model = CDB_EVENT_MODELS[event_type]
        if not isinstance(event, expected_model):
            with self._lock:
                self.handler_errors.append(
                    f"Expected {expected_model.__name__} for {event_type.value}, "
                    f"got {type(event).__name__}"
                )
        record = DispatchedRecord(
            topic=message.topic,
            payload=message.payload,
            received_at=time.monotonic(),
            event_type=event_type,
            handler_method=method_name,
            typed_event=event,
        )
        with self._lock:
            self._records.append(record)
            self._all_typed_records.append(record)
            self.seen_event_types.add(event_type)
            self._lock.notify_all()

    def _record_generic_message(self, message: MQTTMessage) -> None:
        record = DispatchedRecord(
            topic=message.topic,
            payload=message.payload,
            received_at=time.monotonic(),
        )
        with self._lock:
            self._records.append(record)
            self._lock.notify_all()

    def start(self) -> "CdbBrokerCollector":
        self._loop = asyncio.new_event_loop()
        self._loop_thread = threading.Thread(
            target=self._loop.run_forever,
            name="cdb-mqtt-acceptance-loop",
            daemon=True,
        )
        self._loop_thread.start()

        client_id = f"cdb-mqtt-acceptance-{uuid.uuid4().hex[:8]}"
        self.mqtt_client = CdbMQTTClient(
            broker_host=self.settings.host,
            broker_port=self.settings.port,
            client_id=client_id,
            username=self.settings.username,
            password=self.settings.password,
            plugin_manager=self.plugin_manager,
        )
        if self.settings.username:
            self.mqtt_client.client.username_pw_set(self.settings.username, self.settings.password)
        self.mqtt_client._loop = self._loop

        orig_on_connect = self.mqtt_client._on_connect
        orig_on_subscribe = self.mqtt_client._on_subscribe

        def on_connect(
            client: Any, userdata: Any, flags: Any, reason_code: Any, properties: Any = None
        ) -> None:
            if _is_failure(reason_code):
                self._failure = (
                    f"broker {self.settings.describe()} refused connection: {reason_code}"
                )
            else:
                orig_on_connect(client, userdata, flags, reason_code, properties)
            self._connected.set()

        def on_subscribe(
            client: Any, userdata: Any, mid: Any, reason_codes: Any, properties: Any = None
        ) -> None:
            orig_on_subscribe(client, userdata, mid, reason_codes, properties)
            codes = reason_codes if isinstance(reason_codes, (list, tuple)) else [reason_codes]
            for code in codes:
                if _is_failure(code, subscribe=True):
                    self._failure = f"broker refused subscription to {self.topic_filter}: {code}"
            self._subscribed.set()

        self.mqtt_client.client.on_connect = on_connect  # type: ignore[assignment]
        self.mqtt_client.client.on_subscribe = on_subscribe  # type: ignore[assignment]
        self.mqtt_client.subscribe(self.topic_filter)

        try:
            self.mqtt_client.connect()
        except (OSError, ValueError) as ex:
            self.stop()
            raise MqttUnavailable(
                f"cannot connect to broker {self.settings.describe()}: {ex}"
            ) from ex

        self.mqtt_client.client.loop_start()
        if not self._connected.wait(self.settings.timeout):
            self.stop()
            raise MqttUnavailable(
                f"no CONNACK from broker {self.settings.describe()} within "
                f"{self.settings.timeout}s ({self._failure or 'timeout'})"
            )
        if self._failure:
            failure = self._failure
            self.stop()
            raise MqttUnavailable(failure)

        if not self._subscribed.wait(self.settings.timeout):
            self.stop()
            raise MqttUnavailable(
                f"no SUBACK for {self.topic_filter} within {self.settings.timeout}s"
            )
        if self._failure:
            failure = self._failure
            self.stop()
            raise MqttUnavailable(failure)

        return self

    def stop(self) -> None:
        if self.mqtt_client is not None:
            try:
                self.mqtt_client.disconnect()
            finally:
                self.mqtt_client.client.loop_stop()
                self.mqtt_client = None

        if self._loop is not None:
            self._loop.call_soon_threadsafe(self._loop.stop)
            if self._loop_thread is not None:
                self._loop_thread.join(timeout=5.0)
                self._loop_thread = None
            self._loop.close()
            self._loop = None

        self._temp_dir.cleanup()

    def probe(self, timeout: Optional[float] = None) -> Dict[str, Any]:
        """Round-trip a probe message through the broker and CdbMQTTClient.handle_generic."""
        assert self.mqtt_client is not None
        topic = f"cdb/test-probe/{uuid.uuid4().hex}"
        marker = uuid.uuid4().hex
        self.mqtt_client.client.publish(topic, json.dumps({"probe": marker}), qos=1)
        record = self._wait(topic, lambda payload: payload.get("probe") == marker, timeout)
        if record is None:
            raise AssertionError(
                f"Probe message on {topic} was not echoed back by broker "
                f"{self.settings.describe()}"
            )
        return record.payload

    def _flush_loop(self) -> None:
        """Wait for all pending broker deliveries and asyncio handler tasks to finish."""
        if self.mqtt_client is not None and self.mqtt_client.is_connected():
            topic = f"cdb/test-probe/{uuid.uuid4().hex}"
            marker = uuid.uuid4().hex
            self.mqtt_client.client.publish(topic, json.dumps({"probe": marker}), qos=1)
            self._wait(topic, lambda payload: payload.get("probe") == marker, self.settings.timeout)
        if self._loop is not None and self._loop.is_running():
            fut = asyncio.run_coroutine_threadsafe(asyncio.sleep(0), self._loop)
            fut.result(timeout=self.settings.timeout)

    def drain(self) -> List[DispatchedRecord]:
        self._flush_loop()
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
            f"from broker {self.settings.describe()} (via CdbMQTTClient)."
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
    Triggers all 16 REST-API-accessible CDB events and verifies that ``CdbMQTTClient``
    and ``CdbPluginManager`` parse and dispatch every one as its typed Pydantic model.
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
    through ``CdbMQTTClient`` and ``CdbPluginManager``.
    """

    __test__ = True
    DATA_PATH = str(SELENIUM_DIR / "data-dw")

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

    # Hide redundant UI tests already covered by TestCdbBrokerApiAcceptance so pytest
    # does not collect or report them as skipped.
    test_machine_add_parent_and_child = None  # type: ignore[assignment]
    test_machine_assign_link_and_move = None  # type: ignore[assignment]

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
