"""
Event types for ComponentDB MQTT events.

Mirrors ``gov.anl.aps.cdb.mqtt.CdbMqttTopic`` and ``CdbMqttOperation`` in the portal.
"""

from enum import Enum


class CdbEventType(Enum):
    """
    All topics published by the ComponentDB portal.

    The handler method for each member is ``handle_<member_name_lower>``, e.g.
    ``ITEM_LOG_ADD`` -> ``handle_item_log_add(event: CdbItemLogEvent)``.
    """

    # Generic entity events (Java: ADD, UPDATE, DELETE)
    ENTITY_ADD = "cdb/entity/add"
    ENTITY_UPDATE = "cdb/entity/update"
    ENTITY_DELETE = "cdb/entity/delete"

    # Item property value events
    ITEM_PROPERTY_ADD = "cdb/item/property/add"
    ITEM_PROPERTY_UPDATE = "cdb/item/property/update"
    ITEM_PROPERTY_DELETE = "cdb/item/property/delete"

    # Item log events
    ITEM_LOG_ADD = "cdb/item/log/add"
    ITEM_LOG_UPDATE = "cdb/item/log/update"
    ITEM_LOG_DELETE = "cdb/item/log/delete"
    ITEM_LOG_ATTACHMENT_ADD = "cdb/item/log/attachment/add"

    # Machine design events
    MACHINE_DESIGN_ADD = "cdb/machine-design/add"
    MACHINE_DESIGN_ASSIGNED_ITEM_UPDATE = "cdb/machine-design/assigned-item/update"
    MACHINE_DESIGN_MOVE = "cdb/machine-design/move"
    MACHINE_DESIGN_REPRESENTED_ELEMENT_UPDATE = "cdb/machine-design/represented-element/update"
    MACHINE_DESIGN_TRASH = "cdb/machine-design/trash"
    MACHINE_DESIGN_RESTORE = "cdb/machine-design/restore"

    # Inventory / location events
    ITEM_LOCATION_UPDATE = "cdb/item/location/update"
    INVENTORY_ASSEMBLY_ELEMENT_UPDATE = "cdb/inventory/assembly-element/update"

    def __str__(self) -> str:
        """Return the topic for this event type."""
        return self.value

    @property
    def handler_method_name(self) -> str:
        """Handler method name, e.g. "handle_item_log_add"."""
        return f"handle_{self.name.lower()}"


class CdbOperation(str, Enum):
    """Operation reported in event payloads."""

    ADD = "add"
    UPDATE = "update"
    DELETE = "delete"
    TRASH = "trash"
    RESTORE = "restore"
