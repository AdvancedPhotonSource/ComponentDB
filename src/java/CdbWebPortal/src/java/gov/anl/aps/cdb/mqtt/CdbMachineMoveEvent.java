/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Scalar snapshot of a completed machine move. */
public final class CdbMachineMoveEvent extends CdbMqttEvent {

    private final CdbItemSnapshot machine;
    private final CdbItemSnapshot oldParent;
    private final CdbItemSnapshot newParent;

    public CdbMachineMoveEvent(ItemDomainMachineDesign machine,
            ItemDomainMachineDesign oldParent, ItemDomainMachineDesign newParent,
            UserInfo user) {
        super(CdbMqttTopic.MACHINE_DESIGN_MOVE, user);
        if (machine == null || newParent == null) {
            throw new IllegalArgumentException("machine and new parent are required");
        }
        this.machine = new CdbItemSnapshot(machine);
        this.oldParent = oldParent == null ? null : new CdbItemSnapshot(oldParent);
        this.newParent = new CdbItemSnapshot(newParent);
    }

    public CdbItemSnapshot getMachine() { return machine; }
    public CdbItemSnapshot getOldParent() { return oldParent; }
    public CdbItemSnapshot getNewParent() { return newParent; }
}
