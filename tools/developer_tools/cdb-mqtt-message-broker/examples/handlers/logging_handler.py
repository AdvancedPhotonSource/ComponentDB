"""
Example handler that logs ComponentDB item events.

Demonstrates typed event methods; anything without a specific method
(e.g. machine design events) falls through to ``handle_generic``.
"""

import json

from cdb_mqtt import (
    CdbEntityEvent,
    CdbInventoryAssemblyElementEvent,
    CdbItemLocationEvent,
    CdbItemLogAttachmentEvent,
    CdbItemLogEvent,
    CdbItemPropertyEvent,
    CdbMQTTHandler,
    MQTTMessage,
)


class LoggingHandler(CdbMQTTHandler):
    """Logs CDB item, property, log, location and assembly events."""

    async def handle_entity_add(self, event: CdbEntityEvent) -> None:
        self._log_entity(event)

    async def handle_entity_update(self, event: CdbEntityEvent) -> None:
        self._log_entity(event)

    async def handle_entity_delete(self, event: CdbEntityEvent) -> None:
        self._log_entity(event)

    async def handle_item_property_add(self, event: CdbItemPropertyEvent) -> None:
        self._log_property(event)

    async def handle_item_property_update(self, event: CdbItemPropertyEvent) -> None:
        self._log_property(event)

    async def handle_item_property_delete(self, event: CdbItemPropertyEvent) -> None:
        self._log_property(event)

    async def handle_item_log_add(self, event: CdbItemLogEvent) -> None:
        self._log_log(event)

    async def handle_item_log_update(self, event: CdbItemLogEvent) -> None:
        self._log_log(event)

    async def handle_item_log_delete(self, event: CdbItemLogEvent) -> None:
        self._log_log(event)

    async def handle_item_log_attachment_add(self, event: CdbItemLogAttachmentEvent) -> None:
        self.logger.info(
            f"[{event.triggered_by_username}] attachment "
            f"'{event.attachment.original_filename}' added to log {event.log_id} "
            f"of {event.item_description}"
        )

    async def handle_item_location_update(self, event: CdbItemLocationEvent) -> None:
        old = event.old_location.description if event.old_location else None
        new = event.new_location.description if event.new_location else None
        self.logger.info(
            f"[{event.triggered_by_username}] {event.item.description} moved: {old} -> {new}"
        )

    async def handle_inventory_assembly_element_update(
        self, event: CdbInventoryAssemblyElementEvent
    ) -> None:
        old = event.old_assigned_inventory_item
        new = event.new_assigned_inventory_item
        self.logger.info(
            f"[{event.triggered_by_username}] {event.inventory_assembly.description} "
            f"element '{event.assembly_element.name}': "
            f"{old.description if old else None} -> {new.description if new else None}"
        )

    async def handle_generic(self, message: MQTTMessage) -> None:
        self.logger.info(f"Event received on topic: {message.topic}")
        self.logger.debug(f"Payload: {json.dumps(message.payload, indent=2)}")

    def _log_entity(self, event: CdbEntityEvent) -> None:
        self.logger.info(
            f"[{event.triggered_by_username}] {event.operation.value} "
            f"{event.entity_type} {event.entity_id}: {event.entity_description}"
        )

    def _log_property(self, event: CdbItemPropertyEvent) -> None:
        prop = event.new_property or event.old_property
        old = event.old_property.value if event.old_property else None
        new = event.new_property.value if event.new_property else None
        self.logger.info(
            f"[{event.triggered_by_username}] property {event.operation.value} on "
            f"{event.item_description}: {prop.property_type_name if prop else None} "
            f"{old!r} -> {new!r}"
        )

    def _log_log(self, event: CdbItemLogEvent) -> None:
        log = event.new_log or event.old_log
        self.logger.info(
            f"[{event.triggered_by_username}] log {event.operation.value} on "
            f"{event.item_description} (log {log.log_id if log else None})"
        )
        if event.text_diff:
            self.logger.debug(f"Diff:\n{event.text_diff}")
