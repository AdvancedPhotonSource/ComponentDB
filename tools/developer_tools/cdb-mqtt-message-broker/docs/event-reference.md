# Event Reference

Every CDB event payload has these common fields:
- `eventTimestamp`: `event_timestamp: datetime`
- `triggeredByUsername`: `triggered_by_username: str | None`

The topic is never repeated in the payload. Python attributes are snake_case; JSON keys are camelCase. Ids are strings.

## Topics

| Topic | Handler method | Model |
|---|---|---|
| `cdb/entity/add` | `handle_entity_add` | `CdbEntityEvent` |
| `cdb/entity/update` | `handle_entity_update` | `CdbEntityEvent` |
| `cdb/entity/delete` | `handle_entity_delete` | `CdbEntityEvent` |
| `cdb/item/property/add` | `handle_item_property_add` | `CdbItemPropertyEvent` |
| `cdb/item/property/update` | `handle_item_property_update` | `CdbItemPropertyEvent` |
| `cdb/item/property/delete` | `handle_item_property_delete` | `CdbItemPropertyEvent` |
| `cdb/item/log/add` | `handle_item_log_add` | `CdbItemLogEvent` |
| `cdb/item/log/update` | `handle_item_log_update` | `CdbItemLogEvent` |
| `cdb/item/log/delete` | `handle_item_log_delete` | `CdbItemLogEvent` |
| `cdb/item/log/attachment/add` | `handle_item_log_attachment_add` | `CdbItemLogAttachmentEvent` |
| `cdb/machine-design/add` | `handle_machine_design_add` | `CdbMachineAddEvent` |
| `cdb/machine-design/assigned-item/update` | `handle_machine_design_assigned_item_update` | `CdbMachineAssignedItemEvent` |
| `cdb/machine-design/move` | `handle_machine_design_move` | `CdbMachineMoveEvent` |
| `cdb/machine-design/represented-element/update` | `handle_machine_design_represented_element_update` | `CdbMachineRepresentedElementEvent` |
| `cdb/machine-design/trash` | `handle_machine_design_trash` | `CdbMachineTrashEvent` |
| `cdb/machine-design/restore` | `handle_machine_design_restore` | `CdbMachineTrashEvent` |
| `cdb/item/location/update` | `handle_item_location_update` | `CdbItemLocationEvent` |
| `cdb/inventory/assembly-element/update` | `handle_inventory_assembly_element_update` | `CdbInventoryAssemblyElementEvent` |

The enum is `CdbEventType`, and the topic→model map is `CDB_EVENT_MODELS` / `CDB_EVENT_REGISTRY`.

## Events

Fields are listed by JSON key; `?` marks an optional field.

- **`CdbEntityEvent`**: `operation` (`add`/`update`/`delete`), `entityId?`, `entityType?`, `entityDescription?`.
- **`CdbItemPropertyEvent`**:
  - `operation`, `itemId?`, `itemType?`, `itemDescription?`
  - `oldProperty?` and `newProperty?`: `None` on add and delete, respectively.
  - `textDiff?`: set for long-text properties on update.
  - `diffTruncated`
- **`CdbItemLogEvent`**: `operation`, `itemId?`, `itemType?`, `itemDescription?`, `oldLog?`, `newLog?`, `textDiff?` (on update), `diffTruncated`.
- **`CdbItemLogAttachmentEvent`**: `operation` (`add`), `itemId?`, `itemType?`, `itemDescription?`, `logId?`, `attachment`.
- **`CdbMachineAddEvent`**: `machine`, `parent?`, `sourceTemplate?`, `templateAppliedToMachine?`, `templateCreatedMachineCount?`. The template fields are only present for template-created machines.
- **`CdbMachineAssignedItemEvent`**: `machine`, `oldAssignedItem?`, `newAssignedItem?`, `oldInstalled`, `newInstalled`.
- **`CdbMachineMoveEvent`**: `machine`, `oldParent?`, `newParent`.
- **`CdbMachineRepresentedElementEvent`**: `machine`, `oldRepresentedElement?`, `newRepresentedElement?`, `sourceTemplate?`.
- **`CdbMachineTrashEvent`**: `operation` (`trash`/`restore`), `affectedMachine`, `requestedHierarchyRoot`, `affectedMachineCount`. One event is published per affected machine.
- **`CdbItemLocationEvent`**: `item`, `oldLocation?`, `newLocation?`, `oldLocationDetails?`, `newLocationDetails?`.
- **`CdbInventoryAssemblyElementEvent`**: `inventoryAssembly`, `assemblyElement`, `catalogElement?`, `oldAssignedInventoryItem?`, `newAssignedInventoryItem?`.

## Snapshots

- **`CdbItemSnapshot`**: `id`, `domain`, `domainId`, `description`, `entityTypes: [CdbEntityTypeSnapshot]`.
- **`CdbEntityTypeSnapshot`**: `id`, `name`.
- **`CdbItemElementSnapshot`**: `id`, `name`, `parentItem?`, `containedItem?`.
- **`CdbPropertySnapshot`**:
  - `propertyValueId`, `propertyTypeId`, `propertyTypeName`
  - `tag`, `value`, `text`, `units`, `description`
  - `metadata: {str: str}`
  - `textOmitted`
- **`CdbLogSnapshot`**: `logId`, `text`, `textOmitted`, `enteredOnDateTime`, `effectiveFromDateTime`, `effectiveToDateTime`, `enteredByUsername`, `logTopicId`, `logTopicName`.
- **`CdbAttachmentSnapshot`**: `name`, `originalFilename`. File contents are never included.

Free text is capped at 16 KiB. `diffTruncated` is `true` when text or a diff was cut, and `textOmitted` is `true` when the full text was replaced by `textDiff`.
