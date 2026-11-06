/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.metadata;

import gov.anl.aps.cdb.portal.model.db.entities.Item;

/** Pending pre-update state for a machine assigned-item change. */
public final class MachineAssignedItemChange {

    private final Item oldAssignedItem;
    private final boolean oldInstalled;

    public MachineAssignedItemChange(Item oldAssignedItem, boolean oldInstalled) {
        this.oldAssignedItem = oldAssignedItem;
        this.oldInstalled = oldInstalled;
    }

    public Item getOldAssignedItem() { return oldAssignedItem; }
    public boolean isOldInstalled() { return oldInstalled; }
}
