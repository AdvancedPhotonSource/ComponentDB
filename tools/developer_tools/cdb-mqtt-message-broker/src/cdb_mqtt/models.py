"""
Data models for ComponentDB MQTT events.

Each model mirrors the Jackson serialization of a ``gov.anl.aps.cdb.mqtt`` class in
the portal. Ids are strings (as published); nullable Java fields are Optional.
"""

from typing import Dict, List, Optional

from cdb_bely_message_broker_base import EventModel, JavaDateTime
from pydantic import Field

from cdb_mqtt.events import CdbOperation

# Snapshots


class CdbEntityTypeSnapshot(EventModel):
    """Entity type assigned to an item."""

    id: Optional[int] = None
    name: Optional[str] = None


class CdbItemSnapshot(EventModel):
    """Scalar snapshot of an item (catalog, inventory, machine design, location, ...)."""

    id: Optional[str] = None
    domain: Optional[str] = None
    domain_id: Optional[int] = Field(None, alias="domainId")
    description: Optional[str] = None
    entity_types: List[CdbEntityTypeSnapshot] = Field(default_factory=list, alias="entityTypes")


class CdbItemElementSnapshot(EventModel):
    """Snapshot of an item element (assembly/represented element)."""

    id: Optional[str] = None
    name: Optional[str] = None
    parent_item: Optional[CdbItemSnapshot] = Field(None, alias="parentItem")
    contained_item: Optional[CdbItemSnapshot] = Field(None, alias="containedItem")


class CdbPropertySnapshot(EventModel):
    """Snapshot of a property value."""

    property_value_id: Optional[str] = Field(None, alias="propertyValueId")
    property_type_id: Optional[str] = Field(None, alias="propertyTypeId")
    property_type_name: Optional[str] = Field(None, alias="propertyTypeName")
    tag: Optional[str] = None
    value: Optional[str] = None
    text: Optional[str] = None
    units: Optional[str] = None
    description: Optional[str] = None
    metadata: Dict[str, Optional[str]] = Field(default_factory=dict)
    text_omitted: bool = Field(False, alias="textOmitted")


class CdbLogSnapshot(EventModel):
    """Snapshot of a log entry."""

    log_id: Optional[str] = Field(None, alias="logId")
    text: Optional[str] = None
    text_omitted: bool = Field(False, alias="textOmitted")
    entered_on_date_time: Optional[JavaDateTime] = Field(None, alias="enteredOnDateTime")
    effective_from_date_time: Optional[JavaDateTime] = Field(None, alias="effectiveFromDateTime")
    effective_to_date_time: Optional[JavaDateTime] = Field(None, alias="effectiveToDateTime")
    entered_by_username: Optional[str] = Field(None, alias="enteredByUsername")
    log_topic_id: Optional[str] = Field(None, alias="logTopicId")
    log_topic_name: Optional[str] = Field(None, alias="logTopicName")


class CdbAttachmentSnapshot(EventModel):
    """Attachment metadata; file contents are never included."""

    name: Optional[str] = None
    original_filename: Optional[str] = Field(None, alias="originalFilename")


# Events


class CdbEventBase(EventModel):
    """Fields common to all CDB events (Java ``CdbMqttEvent``)."""

    event_timestamp: JavaDateTime = Field(alias="eventTimestamp")
    triggered_by_username: Optional[str] = Field(None, alias="triggeredByUsername")


class CdbItemEventBase(CdbEventBase):
    """Events describing an item by id, type and description."""

    item_id: Optional[str] = Field(None, alias="itemId")
    item_type: Optional[str] = Field(None, alias="itemType")
    item_description: Optional[str] = Field(None, alias="itemDescription")


class CdbEntityEvent(CdbEventBase):
    """Generic entity add/update/delete (``cdb/entity/*``)."""

    operation: CdbOperation
    entity_id: Optional[str] = Field(None, alias="entityId")
    entity_type: Optional[str] = Field(None, alias="entityType")
    entity_description: Optional[str] = Field(None, alias="entityDescription")


class CdbItemPropertyEvent(CdbItemEventBase):
    """Property value add/update/delete (``cdb/item/property/*``)."""

    operation: CdbOperation
    old_property: Optional[CdbPropertySnapshot] = Field(None, alias="oldProperty")
    new_property: Optional[CdbPropertySnapshot] = Field(None, alias="newProperty")
    text_diff: Optional[str] = Field(None, alias="textDiff")
    diff_truncated: bool = Field(False, alias="diffTruncated")


class CdbItemLogEvent(CdbItemEventBase):
    """Log entry add/update/delete (``cdb/item/log/*``)."""

    operation: CdbOperation
    old_log: Optional[CdbLogSnapshot] = Field(None, alias="oldLog")
    new_log: Optional[CdbLogSnapshot] = Field(None, alias="newLog")
    text_diff: Optional[str] = Field(None, alias="textDiff")
    diff_truncated: bool = Field(False, alias="diffTruncated")


class CdbItemLogAttachmentEvent(CdbItemEventBase):
    """Attachment added to a log entry (``cdb/item/log/attachment/add``)."""

    operation: CdbOperation = CdbOperation.ADD
    log_id: Optional[str] = Field(None, alias="logId")
    attachment: CdbAttachmentSnapshot


class CdbMachineAddEvent(CdbEventBase):
    """New machine design, possibly created from a template (``cdb/machine-design/add``)."""

    machine: CdbItemSnapshot
    parent: Optional[CdbItemSnapshot] = None
    source_template: Optional[CdbItemSnapshot] = Field(None, alias="sourceTemplate")
    template_applied_to_machine: Optional[CdbItemSnapshot] = Field(
        None, alias="templateAppliedToMachine"
    )
    template_created_machine_count: Optional[int] = Field(None, alias="templateCreatedMachineCount")


class CdbMachineAssignedItemEvent(CdbEventBase):
    """Assigned item or installed flag changed (``cdb/machine-design/assigned-item/update``)."""

    machine: CdbItemSnapshot
    old_assigned_item: Optional[CdbItemSnapshot] = Field(None, alias="oldAssignedItem")
    new_assigned_item: Optional[CdbItemSnapshot] = Field(None, alias="newAssignedItem")
    old_installed: bool = Field(False, alias="oldInstalled")
    new_installed: bool = Field(False, alias="newInstalled")


class CdbMachineMoveEvent(CdbEventBase):
    """Machine moved within the hierarchy (``cdb/machine-design/move``)."""

    machine: CdbItemSnapshot
    old_parent: Optional[CdbItemSnapshot] = Field(None, alias="oldParent")
    new_parent: CdbItemSnapshot = Field(alias="newParent")


class CdbMachineRepresentedElementEvent(CdbEventBase):
    """Represented element changed (``cdb/machine-design/represented-element/update``)."""

    machine: CdbItemSnapshot
    old_represented_element: Optional[CdbItemElementSnapshot] = Field(
        None, alias="oldRepresentedElement"
    )
    new_represented_element: Optional[CdbItemElementSnapshot] = Field(
        None, alias="newRepresentedElement"
    )
    source_template: Optional[CdbItemSnapshot] = Field(None, alias="sourceTemplate")


class CdbMachineTrashEvent(CdbEventBase):
    """Machine trashed or restored (``cdb/machine-design/trash`` / ``restore``)."""

    operation: CdbOperation
    affected_machine: CdbItemSnapshot = Field(alias="affectedMachine")
    requested_hierarchy_root: CdbItemSnapshot = Field(alias="requestedHierarchyRoot")
    affected_machine_count: int = Field(alias="affectedMachineCount")


class CdbItemLocationEvent(CdbEventBase):
    """Item location changed (``cdb/item/location/update``)."""

    item: CdbItemSnapshot
    old_location: Optional[CdbItemSnapshot] = Field(None, alias="oldLocation")
    new_location: Optional[CdbItemSnapshot] = Field(None, alias="newLocation")
    old_location_details: Optional[str] = Field(None, alias="oldLocationDetails")
    new_location_details: Optional[str] = Field(None, alias="newLocationDetails")


class CdbInventoryAssemblyElementEvent(CdbEventBase):
    """Assembly element assigned or cleared (``cdb/inventory/assembly-element/update``)."""

    inventory_assembly: CdbItemSnapshot = Field(alias="inventoryAssembly")
    assembly_element: CdbItemElementSnapshot = Field(alias="assemblyElement")
    catalog_element: Optional[CdbItemElementSnapshot] = Field(None, alias="catalogElement")
    old_assigned_inventory_item: Optional[CdbItemSnapshot] = Field(
        None, alias="oldAssignedInventoryItem"
    )
    new_assigned_inventory_item: Optional[CdbItemSnapshot] = Field(
        None, alias="newAssignedInventoryItem"
    )
