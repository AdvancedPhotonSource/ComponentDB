/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Scalar snapshot of a completed machine assigned-item change. */
public final class CdbMachineAssignedItemEvent extends CdbMqttEvent {

    private final CdbItemSnapshot machine;
    private final CdbItemSnapshot oldAssignedItem;
    private final CdbItemSnapshot newAssignedItem;
    private final boolean oldInstalled;
    private final boolean newInstalled;

    public CdbMachineAssignedItemEvent(ItemDomainMachineDesign machine,
            Item oldAssignedItem, Item newAssignedItem,
            boolean oldInstalled, boolean newInstalled, UserInfo user) {
        super(CdbMqttTopic.MACHINE_DESIGN_ASSIGNED_ITEM_UPDATE, user);
        if (machine == null) {
            throw new IllegalArgumentException("machine is required");
        }
        this.machine = new CdbItemSnapshot(machine);
        this.oldAssignedItem = oldAssignedItem == null ? null : new CdbItemSnapshot(oldAssignedItem);
        this.newAssignedItem = newAssignedItem == null ? null : new CdbItemSnapshot(newAssignedItem);
        this.oldInstalled = oldInstalled;
        this.newInstalled = newInstalled;
    }

    public CdbItemSnapshot getMachine() { return machine; }
    public CdbItemSnapshot getOldAssignedItem() { return oldAssignedItem; }
    public CdbItemSnapshot getNewAssignedItem() { return newAssignedItem; }
    public boolean isOldInstalled() { return oldInstalled; }
    public boolean isNewInstalled() { return newInstalled; }
}
