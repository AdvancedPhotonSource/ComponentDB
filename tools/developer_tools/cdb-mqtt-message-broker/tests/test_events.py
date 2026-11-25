"""Tests for CDB event types and the event registry."""

import re

import pytest

from cdb_mqtt import CDB_EVENT_MODELS, CDB_EVENT_REGISTRY, CdbEventType, CdbOperation
from tests.conftest import JAVA_MQTT_DIR, load_event_payloads


def test_every_event_type_has_a_model():
    assert set(CDB_EVENT_MODELS) == set(CdbEventType)
    assert len(CDB_EVENT_REGISTRY) == len(CdbEventType)


def test_registry_specs_match_enum():
    for event_type in CdbEventType:
        spec = CDB_EVENT_REGISTRY.from_topic(event_type.value)
        assert spec is not None
        assert spec.handler_method == event_type.handler_method_name
        assert spec.model is CDB_EVENT_MODELS[event_type]


def test_handler_method_names():
    assert CdbEventType.ITEM_LOG_ADD.handler_method_name == "handle_item_log_add"
    assert CdbEventType.ENTITY_ADD.handler_method_name == "handle_entity_add"
    assert str(CdbEventType.MACHINE_DESIGN_MOVE) == "cdb/machine-design/move"


def test_fixtures_cover_every_topic():
    assert set(load_event_payloads()) == {t.value for t in CdbEventType}


def test_operation_values():
    assert [o.value for o in CdbOperation] == ["add", "update", "delete", "trash", "restore"]


def test_topics_match_java_cdb_mqtt_topic():
    java_file = JAVA_MQTT_DIR / "CdbMqttTopic.java"
    if not java_file.exists():
        pytest.skip("ComponentDB Java sources not available")
    java_topics = set(re.findall(r'\w+\("(cdb/[^"]+)"\)', java_file.read_text()))
    assert java_topics == {t.value for t in CdbEventType}


def test_operations_match_java_cdb_mqtt_operation():
    java_file = JAVA_MQTT_DIR / "CdbMqttOperation.java"
    if not java_file.exists():
        pytest.skip("ComponentDB Java sources not available")
    java_values = set(re.findall(r'[A-Z]+\("(\w+)"\)', java_file.read_text()))
    assert java_values == {o.value for o in CdbOperation}
