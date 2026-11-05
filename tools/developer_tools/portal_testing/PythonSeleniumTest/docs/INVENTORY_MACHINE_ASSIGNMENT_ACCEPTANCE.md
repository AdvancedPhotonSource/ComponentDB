# Inventory Machine Assignment Acceptance Coverage

Automated by `tests/test_inventory.py::InventoryTest::test_inventory_machine_assignment` using the test database fixtures.

## Automated scenarios

- Open Housing edit and verify the machine-selection dialog and Installed/Planned control.
- Assign inventory as **Planned**, reopen the dialog, and verify the selected machine and state persisted.
- Move assigned inventory to another machine and verify the destination becomes selected after refresh (source release and view refresh).
- Clear the assignment and verify no machine remains selected (inventory is replaced by its catalog on the machine).
- Expand `Another Top Level`, select `Inventory Assignment Test Destination`, confirm the different-catalog replacement, verify applicable progress steps complete, reopen to verify persistence, and clear for cleanup.

## Manual acceptance scenarios

These require fixture/user manipulation or an injected persistence failure and remain manual:

- Verify assignment is rejected unless the user can write the inventory, source machine, and destination machine.
- Force destination persistence to fail after source release and verify the source inventory assignment and Installed/Planned state are restored.

Logging is intentionally not asserted as acceptance behavior.
