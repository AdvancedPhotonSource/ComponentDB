"""
Plugin base classes for ComponentDB MQTT handlers.
"""

from typing import Any, Optional, Type

from cdb_bely_message_broker_base import MQTTHandler, PluginManager

from cdb_mqtt.registry import CDB_EVENT_REGISTRY


class CdbMQTTHandler(MQTTHandler):
    """
    Base class for ComponentDB MQTT handlers.

    Subscribes to all CDB topics (``cdb/#``) by default. Implement any of the
    typed event methods below; other messages go to ``handle_generic(message)``.

    Supported event handlers:
        - handle_entity_add / _update / _delete(event: CdbEntityEvent)
        - handle_item_property_add / _update / _delete(event: CdbItemPropertyEvent)
        - handle_item_log_add / _update / _delete(event: CdbItemLogEvent)
        - handle_item_log_attachment_add(event: CdbItemLogAttachmentEvent)
        - handle_machine_design_add(event: CdbMachineAddEvent)
        - handle_machine_design_assigned_item_update(event: CdbMachineAssignedItemEvent)
        - handle_machine_design_move(event: CdbMachineMoveEvent)
        - handle_machine_design_represented_element_update(
              event: CdbMachineRepresentedElementEvent)
        - handle_machine_design_trash / _restore(event: CdbMachineTrashEvent)
        - handle_item_location_update(event: CdbItemLocationEvent)
        - handle_inventory_assembly_element_update(event: CdbInventoryAssemblyElementEvent)

    Example:
        class MyHandler(CdbMQTTHandler):
            async def handle_item_log_add(self, event: CdbItemLogEvent) -> None:
                self.logger.info(f"Log added to {event.item_description}")

    ``self.api_factory`` is a ``CdbApiFactory`` when an API URL is configured.
    """

    default_topic_pattern = "cdb/#"
    event_registry = CDB_EVENT_REGISTRY


class CdbPluginManager(PluginManager):
    """PluginManager that loads only CdbMQTTHandler subclasses."""

    def __init__(
        self,
        api_factory=None,
        config_manager: Optional[Any] = None,
        handler_base: Type[MQTTHandler] = CdbMQTTHandler,
    ):
        super().__init__(
            api_factory=api_factory, config_manager=config_manager, handler_base=handler_base
        )
