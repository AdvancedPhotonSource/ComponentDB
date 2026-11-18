"""Tests for generic models and the event registry."""

from datetime import datetime, timedelta, timezone
from enum import Enum
from typing import Optional

import pytest
from pydantic import Field, ValidationError

from cdb_bely_message_broker_base.events import EventRegistry, EventSpec, handler_method_name
from cdb_bely_message_broker_base.models import EventModel, JavaDateTime, MQTTMessage


class SampleEvent(EventModel):
    event_timestamp: JavaDateTime = Field(alias="eventTimestamp")
    item_id: str = Field(alias="itemId")
    note: Optional[str] = None


class SampleType(Enum):
    ITEM_LOG_ADD = "app/item/log/add"
    NO_MODEL = "app/none"


def test_mqtt_message_defaults():
    message = MQTTMessage(topic="a/b", payload={"x": 1})
    assert message.raw_payload == ""


@pytest.mark.parametrize(
    "value",
    [
        "2026-05-01T12:00:00.000+0000",
        "2026-05-01T12:00:00.000+00:00",
        "2026-05-01T12:00:00Z",
        1777636800000,
    ],
)
def test_java_datetime_formats(value):
    event = SampleEvent.model_validate({"eventTimestamp": value, "itemId": "1"})
    assert event.event_timestamp == datetime(2026, 5, 1, 12, 0, tzinfo=timezone.utc)


def test_java_datetime_non_utc_offset():
    event = SampleEvent.model_validate(
        {"eventTimestamp": "2026-05-01T07:00:00.000-0500", "itemId": "1"}
    )
    assert event.event_timestamp.utcoffset() == timedelta(hours=-5)


def test_event_model_aliases_and_extra_fields():
    event = SampleEvent.model_validate(
        {"eventTimestamp": "2026-05-01T12:00:00Z", "itemId": "7", "unknownField": True}
    )
    assert event.item_id == "7"
    assert SampleEvent(event_timestamp=event.event_timestamp, item_id="8").item_id == "8"


def test_event_model_missing_required_field():
    with pytest.raises(ValidationError):
        SampleEvent.model_validate({"itemId": "1"})


def test_handler_method_name():
    assert handler_method_name(SampleType.ITEM_LOG_ADD) == "handle_item_log_add"


def test_registry_from_enum_skips_members_without_model():
    registry = EventRegistry.from_enum(SampleType, {SampleType.ITEM_LOG_ADD: SampleEvent})

    assert len(registry) == 1
    assert "app/item/log/add" in registry
    assert registry.from_topic("app/none") is None
    spec = registry.from_topic("app/item/log/add")
    assert spec == EventSpec("app/item/log/add", "handle_item_log_add", SampleEvent)


def test_registry_parse():
    registry = EventRegistry()
    spec = registry.register("app/x", SampleEvent, "handle_x")
    message = MQTTMessage(topic="app/x", payload={"eventTimestamp": 0, "itemId": "1"})

    event = registry.parse(spec, message)

    assert isinstance(event, SampleEvent)
    assert [s.topic for s in registry] == ["app/x"]


def test_registry_rejects_duplicate_topic():
    registry = EventRegistry()
    registry.register("app/x", SampleEvent, "handle_x")
    with pytest.raises(ValueError):
        registry.register("app/x", SampleEvent, "handle_y")
