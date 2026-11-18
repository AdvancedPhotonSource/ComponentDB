"""Tests for CDB event models against sample payloads."""

from datetime import datetime, timezone

import pytest
from pydantic import ValidationError

from cdb_mqtt import (
    CDB_EVENT_MODELS,
    CdbEntityEvent,
    CdbEventType,
    CdbInventoryAssemblyElementEvent,
    CdbItemLocationEvent,
    CdbItemLogAttachmentEvent,
    CdbItemLogEvent,
    CdbItemPropertyEvent,
    CdbMachineAddEvent,
    CdbMachineAssignedItemEvent,
    CdbMachineMoveEvent,
    CdbMachineRepresentedElementEvent,
    CdbMachineTrashEvent,
    CdbOperation,
)
from tests.conftest import load_event_payloads

PAYLOADS = load_event_payloads()
TIMESTAMP = datetime(2026, 11, 18, 16, 38, 6, 157000, tzinfo=timezone.utc)


@pytest.mark.parametrize("event_type", list(CdbEventType), ids=lambda t: t.value)
def test_every_fixture_parses(event_type):
    model = CDB_EVENT_MODELS[event_type]
    event = model.model_validate(PAYLOADS[event_type.value])
    assert event.event_timestamp.tzinfo is not None


def test_entity_event(event_payloads):
    event = CdbEntityEvent.model_validate(event_payloads["cdb/entity/add"])
    assert event.operation is CdbOperation.ADD
    assert event.entity_id == "1001"
    assert event.entity_type == "ItemDomainCatalog"
    assert event.triggered_by_username == "cdb"
    assert event.event_timestamp == TIMESTAMP


def test_jackson_compact_offset(event_payloads):
    event = CdbEntityEvent.model_validate(event_payloads["cdb/entity/update"])
    assert event.event_timestamp == TIMESTAMP


def test_null_triggered_by(event_payloads):
    event = CdbEntityEvent.model_validate(event_payloads["cdb/entity/delete"])
    assert event.triggered_by_username is None


def test_property_events(event_payloads):
    added = CdbItemPropertyEvent.model_validate(event_payloads["cdb/item/property/add"])
    assert added.old_property is None
    assert added.new_property is not None
    assert added.new_property.value == "SN-001"
    assert added.new_property.property_type_name == "Serial Number"

    updated = CdbItemPropertyEvent.model_validate(event_payloads["cdb/item/property/update"])
    assert updated.operation is CdbOperation.UPDATE
    assert updated.text_diff == "-old line\n+new line"
    assert updated.new_property is not None
    assert updated.new_property.text_omitted is True
    assert updated.new_property.metadata == {"source": "import", "reviewed": "yes"}
    assert updated.old_property is not None
    assert updated.old_property.metadata["reviewed"] is None

    deleted = CdbItemPropertyEvent.model_validate(event_payloads["cdb/item/property/delete"])
    assert deleted.new_property is None


def test_log_events(event_payloads):
    added = CdbItemLogEvent.model_validate(event_payloads["cdb/item/log/add"])
    assert added.new_log is not None
    assert added.new_log.log_id == "77"
    assert added.new_log.log_topic_name == "Installation"
    assert added.new_log.entered_on_date_time == datetime(
        2026, 11, 18, 16, 38, 5, tzinfo=timezone.utc
    )

    updated = CdbItemLogEvent.model_validate(event_payloads["cdb/item/log/update"])
    assert updated.old_log is not None and updated.new_log is not None
    assert updated.new_log.text is None
    assert updated.new_log.text_omitted is True
    assert updated.new_log.effective_to_date_time == datetime(2026, 12, 1, tzinfo=timezone.utc)
    assert updated.diff_truncated is False


def test_log_attachment_event(event_payloads):
    event = CdbItemLogAttachmentEvent.model_validate(event_payloads["cdb/item/log/attachment/add"])
    assert event.operation is CdbOperation.ADD
    assert event.log_id == "77"
    assert event.attachment.original_filename == "photo.jpg"


def test_log_attachment_operation_defaults_to_add(event_payloads):
    payload = dict(event_payloads["cdb/item/log/attachment/add"])
    del payload["operation"]
    assert CdbItemLogAttachmentEvent.model_validate(payload).operation is CdbOperation.ADD


def test_machine_add_event(event_payloads):
    event = CdbMachineAddEvent.model_validate(event_payloads["cdb/machine-design/add"])
    assert event.machine.id == "3003"
    assert event.machine.domain == "Machine Design"
    assert event.machine.domain_id == 6
    assert event.source_template is not None
    assert [t.name for t in event.source_template.entity_types] == ["Template"]
    assert event.template_created_machine_count == 3


def test_machine_add_event_without_template_fields(event_payloads):
    # Java omits the template fields (JsonInclude NON_NULL) for plain adds.
    payload = dict(event_payloads["cdb/machine-design/add"])
    for key in ("sourceTemplate", "templateAppliedToMachine", "templateCreatedMachineCount"):
        del payload[key]
    event = CdbMachineAddEvent.model_validate(payload)
    assert event.source_template is None
    assert event.template_created_machine_count is None


def test_machine_assigned_item_event(event_payloads):
    event = CdbMachineAssignedItemEvent.model_validate(
        event_payloads["cdb/machine-design/assigned-item/update"]
    )
    assert event.old_assigned_item is not None and event.old_assigned_item.domain == "Catalog"
    assert event.new_assigned_item is not None and event.new_assigned_item.id == "2002"
    assert (event.old_installed, event.new_installed) == (False, True)


def test_machine_move_event(event_payloads):
    event = CdbMachineMoveEvent.model_validate(event_payloads["cdb/machine-design/move"])
    assert event.old_parent is None
    assert event.new_parent.id == "3001"


def test_machine_move_requires_new_parent(event_payloads):
    payload = dict(event_payloads["cdb/machine-design/move"])
    del payload["newParent"]
    with pytest.raises(ValidationError):
        CdbMachineMoveEvent.model_validate(payload)


def test_machine_represented_element_event(event_payloads):
    event = CdbMachineRepresentedElementEvent.model_validate(
        event_payloads["cdb/machine-design/represented-element/update"]
    )
    assert event.old_represented_element is None
    assert event.new_represented_element is not None
    assert event.new_represented_element.name == "Coil"
    assert event.new_represented_element.parent_item is not None
    assert event.new_represented_element.parent_item.id == "1001"
    assert event.new_represented_element.contained_item is None


@pytest.mark.parametrize(
    "topic,operation",
    [
        ("cdb/machine-design/trash", CdbOperation.TRASH),
        ("cdb/machine-design/restore", CdbOperation.RESTORE),
    ],
)
def test_machine_trash_events(event_payloads, topic, operation):
    event = CdbMachineTrashEvent.model_validate(event_payloads[topic])
    assert event.operation is operation
    assert event.affected_machine.id == "3003"
    assert event.requested_hierarchy_root.id == "3000"
    assert event.affected_machine_count == 2


def test_item_location_event(event_payloads):
    event = CdbItemLocationEvent.model_validate(event_payloads["cdb/item/location/update"])
    assert event.item.id == "2002"
    assert event.old_location is not None and event.old_location.domain == "Location"
    assert event.new_location is None
    assert event.old_location_details == "Shelf A"


def test_inventory_assembly_element_event(event_payloads):
    event = CdbInventoryAssemblyElementEvent.model_validate(
        event_payloads["cdb/inventory/assembly-element/update"]
    )
    assert event.inventory_assembly.id == "2002"
    assert event.assembly_element.contained_item is not None
    assert event.assembly_element.contained_item.id == "2010"
    assert event.catalog_element is not None and event.catalog_element.id == "450"
    assert event.old_assigned_inventory_item is None
    assert event.new_assigned_inventory_item is not None


def test_unknown_fields_are_ignored(event_payloads):
    payload = dict(event_payloads["cdb/entity/add"], futureField={"x": 1})
    assert CdbEntityEvent.model_validate(payload).entity_id == "1001"


def test_invalid_operation_rejected(event_payloads):
    payload = dict(event_payloads["cdb/entity/add"], operation="explode")
    with pytest.raises(ValidationError):
        CdbEntityEvent.model_validate(payload)
