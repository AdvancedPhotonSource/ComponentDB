/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.metadata;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;

/** Pending pre-persistence context for a machine node that does not yet exist in the DB. */
public final class MachineAddChange {

    private final ItemDomainMachineDesign machine;
    private final ItemDomainMachineDesign parent;

    public MachineAddChange(ItemDomainMachineDesign machine, ItemDomainMachineDesign parent) {
        this.machine = machine;
        this.parent = parent;
    }

    public ItemDomainMachineDesign getMachine() { return machine; }
    public ItemDomainMachineDesign getParent() { return parent; }
}
