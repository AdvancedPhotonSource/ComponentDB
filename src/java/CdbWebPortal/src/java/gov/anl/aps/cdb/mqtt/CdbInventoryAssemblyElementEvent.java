/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Scalar snapshot of a completed inventory assembly-element assignment. */
public final class CdbInventoryAssemblyElementEvent extends CdbMqttEvent {

    private final CdbItemSnapshot inventoryAssembly;
    private final CdbItemElementSnapshot assemblyElement;
    private final CdbItemElementSnapshot catalogElement;
    private final CdbItemSnapshot oldAssignedInventoryItem;
    private final CdbItemSnapshot newAssignedInventoryItem;

    public CdbInventoryAssemblyElementEvent(ItemDomainInventoryBase inventoryAssembly,
            ItemElement assemblyElement, Item oldAssignedInventoryItem,
            Item newAssignedInventoryItem, UserInfo user) {
        super(CdbMqttTopic.INVENTORY_ASSEMBLY_ELEMENT_UPDATE, user);
        if (inventoryAssembly == null || assemblyElement == null) {
            throw new IllegalArgumentException("inventory assembly and element are required");
        }
        this.inventoryAssembly = new CdbItemSnapshot(inventoryAssembly);
        this.assemblyElement = new CdbItemElementSnapshot(assemblyElement);
        ItemElement sourceElement = assemblyElement.getDerivedFromItemElement();
        catalogElement = sourceElement == null ? null : new CdbItemElementSnapshot(sourceElement);
        this.oldAssignedInventoryItem = oldAssignedInventoryItem == null
                ? null : new CdbItemSnapshot(oldAssignedInventoryItem);
        this.newAssignedInventoryItem = newAssignedInventoryItem == null
                ? null : new CdbItemSnapshot(newAssignedInventoryItem);
    }

    public CdbItemSnapshot getInventoryAssembly() { return inventoryAssembly; }
    public CdbItemElementSnapshot getAssemblyElement() { return assemblyElement; }
    public CdbItemElementSnapshot getCatalogElement() { return catalogElement; }
    public CdbItemSnapshot getOldAssignedInventoryItem() { return oldAssignedInventoryItem; }
    public CdbItemSnapshot getNewAssignedInventoryItem() { return newAssignedInventoryItem; }
}
