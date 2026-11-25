"""
CDB MQTT Message Broker - Pluggable Python framework for handling ComponentDB MQTT events.

Built on cdb-bely-message-broker-base. Provides:
- CdbEventType for every topic published by the CDB portal
- Type-safe pydantic models for all CDB event payloads
- CdbMQTTHandler base class with typed handle_<event> methods
- ``cdb-mqtt`` CLI
"""

__version__ = "1.0.0"

from cdb_bely_message_broker_base import MQTTMessage  # noqa: E402

from cdb_mqtt.config import CdbConfigManager, CdbGlobalConfig  # noqa: E402
from cdb_mqtt.events import CdbEventType, CdbOperation  # noqa: E402
from cdb_mqtt.models import (  # noqa: E402
    CdbAttachmentSnapshot,
    CdbEntityEvent,
    CdbEntityTypeSnapshot,
    CdbEventBase,
    CdbInventoryAssemblyElementEvent,
    CdbItemElementSnapshot,
    CdbItemEventBase,
    CdbItemLocationEvent,
    CdbItemLogAttachmentEvent,
    CdbItemLogEvent,
    CdbItemPropertyEvent,
    CdbItemSnapshot,
    CdbLogSnapshot,
    CdbMachineAddEvent,
    CdbMachineAssignedItemEvent,
    CdbMachineMoveEvent,
    CdbMachineRepresentedElementEvent,
    CdbMachineTrashEvent,
    CdbPropertySnapshot,
)
from cdb_mqtt.mqtt_client import CdbMQTTClient  # noqa: E402
from cdb_mqtt.plugin import CdbMQTTHandler, CdbPluginManager  # noqa: E402
from cdb_mqtt.registry import CDB_EVENT_MODELS, CDB_EVENT_REGISTRY  # noqa: E402

__all__ = [
    "CDB_EVENT_MODELS",
    "CDB_EVENT_REGISTRY",
    "CdbAttachmentSnapshot",
    "CdbConfigManager",
    "CdbEntityEvent",
    "CdbEntityTypeSnapshot",
    "CdbEventBase",
    "CdbEventType",
    "CdbGlobalConfig",
    "CdbInventoryAssemblyElementEvent",
    "CdbItemElementSnapshot",
    "CdbItemEventBase",
    "CdbItemLocationEvent",
    "CdbItemLogAttachmentEvent",
    "CdbItemLogEvent",
    "CdbItemPropertyEvent",
    "CdbItemSnapshot",
    "CdbLogSnapshot",
    "CdbMQTTClient",
    "CdbMQTTHandler",
    "CdbMachineAddEvent",
    "CdbMachineAssignedItemEvent",
    "CdbMachineMoveEvent",
    "CdbMachineRepresentedElementEvent",
    "CdbMachineTrashEvent",
    "CdbOperation",
    "CdbPluginManager",
    "CdbPropertySnapshot",
    "MQTTMessage",
]
