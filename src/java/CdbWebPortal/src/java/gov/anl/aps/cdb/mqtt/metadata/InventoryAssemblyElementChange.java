/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.metadata;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;

/** Pending pre-update state for an inventory assembly-element assignment. */
public final class InventoryAssemblyElementChange {

    private final ItemElement assemblyElement;
    private final Item oldAssignedInventoryItem;
    private final Item newAssignedInventoryItem;

    public InventoryAssemblyElementChange(ItemElement assemblyElement,
            Item oldAssignedInventoryItem, Item newAssignedInventoryItem) {
        this.assemblyElement = assemblyElement;
        this.oldAssignedInventoryItem = oldAssignedInventoryItem;
        this.newAssignedInventoryItem = newAssignedInventoryItem;
    }

    public ItemElement getAssemblyElement() { return assemblyElement; }
    public Item getOldAssignedInventoryItem() { return oldAssignedInventoryItem; }
    public Item getNewAssignedInventoryItem() { return newAssignedInventoryItem; }
}
