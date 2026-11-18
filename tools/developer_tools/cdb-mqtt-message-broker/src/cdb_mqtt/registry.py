"""Event registry binding each CdbEventType topic to its payload model."""

from typing import Dict, Type

from cdb_bely_message_broker_base import EventRegistry
from pydantic import BaseModel

from cdb_mqtt.events import CdbEventType
from cdb_mqtt.models import (
    CdbEntityEvent,
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
)

CDB_EVENT_MODELS: Dict[CdbEventType, Type[BaseModel]] = {
    CdbEventType.ENTITY_ADD: CdbEntityEvent,
    CdbEventType.ENTITY_UPDATE: CdbEntityEvent,
    CdbEventType.ENTITY_DELETE: CdbEntityEvent,
    CdbEventType.ITEM_PROPERTY_ADD: CdbItemPropertyEvent,
    CdbEventType.ITEM_PROPERTY_UPDATE: CdbItemPropertyEvent,
    CdbEventType.ITEM_PROPERTY_DELETE: CdbItemPropertyEvent,
    CdbEventType.ITEM_LOG_ADD: CdbItemLogEvent,
    CdbEventType.ITEM_LOG_UPDATE: CdbItemLogEvent,
    CdbEventType.ITEM_LOG_DELETE: CdbItemLogEvent,
    CdbEventType.ITEM_LOG_ATTACHMENT_ADD: CdbItemLogAttachmentEvent,
    CdbEventType.MACHINE_DESIGN_ADD: CdbMachineAddEvent,
    CdbEventType.MACHINE_DESIGN_ASSIGNED_ITEM_UPDATE: CdbMachineAssignedItemEvent,
    CdbEventType.MACHINE_DESIGN_MOVE: CdbMachineMoveEvent,
    CdbEventType.MACHINE_DESIGN_REPRESENTED_ELEMENT_UPDATE: CdbMachineRepresentedElementEvent,
    CdbEventType.MACHINE_DESIGN_TRASH: CdbMachineTrashEvent,
    CdbEventType.MACHINE_DESIGN_RESTORE: CdbMachineTrashEvent,
    CdbEventType.ITEM_LOCATION_UPDATE: CdbItemLocationEvent,
    CdbEventType.INVENTORY_ASSEMBLY_ELEMENT_UPDATE: CdbInventoryAssemblyElementEvent,
}

CDB_EVENT_REGISTRY = EventRegistry.from_enum(CdbEventType, CDB_EVENT_MODELS)
