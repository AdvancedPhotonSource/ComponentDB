/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Scalar snapshot of a newly persisted machine-design node. */
public final class CdbMachineAddEvent extends CdbMqttEvent {

    private final CdbItemSnapshot machine;
    private final CdbItemSnapshot parent;

    public CdbMachineAddEvent(ItemDomainMachineDesign machine,
            ItemDomainMachineDesign parent, UserInfo user) {
        super(CdbMqttTopic.MACHINE_DESIGN_ADD, user);
        if (machine == null) {
            throw new IllegalArgumentException("machine is required");
        }
        this.machine = new CdbItemSnapshot(machine);
        this.parent = parent == null ? null : new CdbItemSnapshot(parent);
    }

    public CdbItemSnapshot getMachine() { return machine; }
    public CdbItemSnapshot getParent() { return parent; }
}
