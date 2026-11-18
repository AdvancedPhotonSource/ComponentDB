"""
Example handler that audits ComponentDB machine design changes.

Demonstrates handler configuration (``audit_file``), the global ``cdb_url``
and narrowing the subscription with ``topic_pattern``.

Configuration (config.yaml):
    global:
      cdb_url: https://cdb.example.com/cdb
    handlers:
      MachineDesignAuditHandler:
        audit_file: /var/log/cdb-mqtt/machine-design-audit.jsonl
"""

import json
from typing import Any, Dict, Optional

from cdb_bely_message_broker_base import GlobalConfig
from cdb_mqtt import (
    CdbItemSnapshot,
    CdbMachineAddEvent,
    CdbMachineAssignedItemEvent,
    CdbMachineMoveEvent,
    CdbMachineRepresentedElementEvent,
    CdbMachineTrashEvent,
    CdbMQTTHandler,
)


class MachineDesignAuditHandler(CdbMQTTHandler):
    """Records machine design changes as JSON lines (or logs them)."""

    def __init__(
        self,
        api_factory=None,
        global_config: Optional[GlobalConfig] = None,
        audit_file: Optional[str] = None,
    ):
        super().__init__(api_factory=api_factory, global_config=global_config)
        self.audit_file = audit_file

    @property
    def topic_pattern(self) -> str:
        return "cdb/machine-design/#"

    async def handle_machine_design_add(self, event: CdbMachineAddEvent) -> None:
        details: Dict[str, Any] = {"parent": self._describe(event.parent)}
        if event.source_template:
            details["sourceTemplate"] = self._describe(event.source_template)
            details["templateCreatedMachineCount"] = event.template_created_machine_count
        self._record("add", event.machine, event.triggered_by_username, details)

    async def handle_machine_design_move(self, event: CdbMachineMoveEvent) -> None:
        details: Dict[str, Any] = {
            "oldParent": self._describe(event.old_parent),
            "newParent": self._describe(event.new_parent),
        }
        self._record("move", event.machine, event.triggered_by_username, details)

    async def handle_machine_design_assigned_item_update(
        self, event: CdbMachineAssignedItemEvent
    ) -> None:
        details: Dict[str, Any] = {
            "oldAssignedItem": self._describe(event.old_assigned_item),
            "newAssignedItem": self._describe(event.new_assigned_item),
            "oldInstalled": event.old_installed,
            "newInstalled": event.new_installed,
        }
        self._record("assigned-item", event.machine, event.triggered_by_username, details)

    async def handle_machine_design_represented_element_update(
        self, event: CdbMachineRepresentedElementEvent
    ) -> None:
        old = event.old_represented_element
        new = event.new_represented_element
        details: Dict[str, Any] = {
            "oldRepresentedElement": old.name if old else None,
            "newRepresentedElement": new.name if new else None,
        }
        self._record("represented-element", event.machine, event.triggered_by_username, details)

    async def handle_machine_design_trash(self, event: CdbMachineTrashEvent) -> None:
        self._record_trash(event)

    async def handle_machine_design_restore(self, event: CdbMachineTrashEvent) -> None:
        self._record_trash(event)

    def _record_trash(self, event: CdbMachineTrashEvent) -> None:
        details: Dict[str, Any] = {
            "requestedHierarchyRoot": self._describe(event.requested_hierarchy_root),
            "affectedMachineCount": event.affected_machine_count,
        }
        self._record(
            event.operation.value, event.affected_machine, event.triggered_by_username, details
        )

    def _describe(self, item: Optional[CdbItemSnapshot]) -> Optional[str]:
        return item.description if item else None

    def _machine_url(self, machine: CdbItemSnapshot) -> Optional[str]:
        cdb_url = self.global_config.public_url if self.global_config else None
        if not cdb_url or not machine.id:
            return None
        return f"{cdb_url.rstrip('/')}/views/itemDomainMachineDesign/view.xhtml?id={machine.id}"

    def _record(
        self,
        change: str,
        machine: CdbItemSnapshot,
        username: Optional[str],
        details: Dict[str, Any],
    ) -> None:
        entry = {
            "change": change,
            "machineId": machine.id,
            "machine": machine.description,
            "url": self._machine_url(machine),
            "user": username,
            **details,
        }
        if self.audit_file:
            with open(self.audit_file, "a") as f:
                f.write(json.dumps(entry) + "\n")
        else:
            self.logger.info(json.dumps(entry))
