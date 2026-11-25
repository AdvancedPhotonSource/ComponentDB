"""Tests for CDB handler dispatch and discovery."""

import logging
from typing import Any, List, Tuple

import pytest
from cdb_bely_message_broker_base import MQTTHandler

from cdb_mqtt import (
    CDB_EVENT_MODELS,
    CdbConfigManager,
    CdbEventType,
    CdbMQTTClient,
    CdbMQTTHandler,
    CdbPluginManager,
    MQTTMessage,
)
from tests.conftest import EXAMPLE_HANDLERS_DIR, load_event_payloads

PAYLOADS = load_event_payloads()
CDB_URL = "https://cdb.test/cdb/"


def _make_recorder(calls: List[Tuple[str, Any]]):
    """Build a handler class implementing every CDB event method."""

    def make_method(name: str):
        async def method(self, event):
            calls.append((name, event))

        return method

    methods = {t.handler_method_name: make_method(t.handler_method_name) for t in CdbEventType}
    return type("RecordingHandler", (CdbMQTTHandler,), methods)


class GenericOnlyHandler(CdbMQTTHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.messages: List[MQTTMessage] = []

    async def handle_generic(self, message: MQTTMessage) -> None:
        self.messages.append(message)


def _example_manager(audit_file) -> CdbPluginManager:
    config_manager = CdbConfigManager()
    config_manager.load_from_dict(
        {
            "global": {"cdb_url": CDB_URL},
            "handlers": {"MachineDesignAuditHandler": {"audit_file": str(audit_file)}},
        }
    )
    manager = CdbPluginManager(config_manager=config_manager)
    manager.load_handlers_from_directory(EXAMPLE_HANDLERS_DIR)
    return manager


def test_default_topic_pattern():
    handler = GenericOnlyHandler()
    assert handler.topic_pattern == "cdb/#"
    assert handler.topic_matches("cdb/machine-design/move")
    assert not handler.topic_matches("bely/logEntry/Add")


@pytest.mark.asyncio
@pytest.mark.parametrize("event_type", list(CdbEventType), ids=lambda t: t.value)
async def test_each_topic_dispatches_typed_event(event_type):
    calls: List[Tuple[str, Any]] = []
    manager = CdbPluginManager()
    manager.register_handler(_make_recorder(calls)())

    await manager.handle_message(
        MQTTMessage(topic=event_type.value, payload=PAYLOADS[event_type.value])
    )

    assert len(calls) == 1
    name, event = calls[0]
    assert name == event_type.handler_method_name
    assert isinstance(event, CDB_EVENT_MODELS[event_type])


@pytest.mark.asyncio
async def test_unimplemented_and_unknown_topics_go_to_generic():
    handler = GenericOnlyHandler()
    manager = CdbPluginManager()
    manager.register_handler(handler)

    await manager.handle_message(
        MQTTMessage(topic="cdb/entity/add", payload=PAYLOADS["cdb/entity/add"])
    )
    await manager.handle_message(MQTTMessage(topic="cdb/test-probe/abc", payload={"probe": 1}))
    await manager.handle_message(MQTTMessage(topic="bely/logEntry/Add", payload={}))

    assert [m.topic for m in handler.messages] == ["cdb/entity/add", "cdb/test-probe/abc"]


@pytest.mark.asyncio
async def test_invalid_payload_is_logged(caplog):
    calls: List[Tuple[str, Any]] = []
    manager = CdbPluginManager()
    manager.register_handler(_make_recorder(calls)())

    with caplog.at_level(logging.ERROR):
        await manager.handle_message(MQTTMessage(topic="cdb/machine-design/move", payload={}))

    assert calls == []
    assert "RecordingHandler" in caplog.text


def test_plugin_manager_handler_base():
    assert CdbPluginManager().handler_base is CdbMQTTHandler


def test_client_defaults():
    client = CdbMQTTClient(broker_host="localhost")
    assert client.client_id == "cdb-mqtt-client"
    assert isinstance(client.plugin_manager, CdbPluginManager)


def test_load_skips_non_cdb_handlers(tmp_path):
    (tmp_path / "handlers.py").write_text(
        "from cdb_bely_message_broker_base import MQTTHandler\n"
        "from cdb_mqtt import CdbMQTTHandler\n\n"
        "class MyCdbHandler(CdbMQTTHandler):\n    pass\n\n"
        "class OtherAppHandler(MQTTHandler):\n    pass\n"
    )
    manager = CdbPluginManager()
    manager.load_handlers_from_directory(tmp_path)

    assert [h.__class__.__name__ for h in manager.handlers] == ["MyCdbHandler"]
    assert all(isinstance(h, MQTTHandler) for h in manager.handlers)


def test_example_handlers_load_with_configuration(tmp_path):
    if not EXAMPLE_HANDLERS_DIR.exists():
        pytest.skip("examples not available")
    audit_file = tmp_path / "audit.jsonl"
    manager = _example_manager(audit_file)

    names = sorted(h.__class__.__name__ for h in manager.handlers)
    assert names == ["LoggingHandler", "MachineDesignAuditHandler"]
    audit = next(h for h in manager.handlers if h.__class__.__name__ == "MachineDesignAuditHandler")
    assert getattr(audit, "audit_file") == str(audit_file)
    assert audit.topic_pattern == "cdb/machine-design/#"


@pytest.mark.asyncio
async def test_example_handlers_process_every_fixture(tmp_path, caplog):
    if not EXAMPLE_HANDLERS_DIR.exists():
        pytest.skip("examples not available")
    audit_file = tmp_path / "audit.jsonl"
    manager = _example_manager(audit_file)

    with caplog.at_level(logging.ERROR):
        for topic, payload in PAYLOADS.items():
            await manager.handle_message(MQTTMessage(topic=topic, payload=payload))

    # Handler errors are logged, not raised.
    assert "Error in handler" not in caplog.text
    lines = audit_file.read_text().splitlines()
    machine_topics = [t for t in PAYLOADS if t.startswith("cdb/machine-design/")]
    assert len(lines) == len(machine_topics)
    assert f"{CDB_URL}views/itemDomainMachineDesign/view.xhtml?id=3003" in lines[0]
