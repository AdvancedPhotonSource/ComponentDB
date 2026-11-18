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
    private final ItemDomainMachineDesign sourceTemplate;
    private final ItemDomainMachineDesign templateAppliedToMachine;
    private final Integer templateCreatedMachineCount;

    public MachineAddChange(ItemDomainMachineDesign machine, ItemDomainMachineDesign parent) {
        this(machine, parent, null, null, null);
    }

    public MachineAddChange(ItemDomainMachineDesign machine, ItemDomainMachineDesign parent,
            ItemDomainMachineDesign sourceTemplate,
            ItemDomainMachineDesign templateAppliedToMachine,
            Integer templateCreatedMachineCount) {
        this.machine = machine;
        this.parent = parent;
        this.sourceTemplate = sourceTemplate;
        this.templateAppliedToMachine = templateAppliedToMachine;
        this.templateCreatedMachineCount = templateCreatedMachineCount;
    }

    public ItemDomainMachineDesign getMachine() { return machine; }
    public ItemDomainMachineDesign getParent() { return parent; }
    public ItemDomainMachineDesign getSourceTemplate() { return sourceTemplate; }
    public ItemDomainMachineDesign getTemplateAppliedToMachine() { return templateAppliedToMachine; }
    public Integer getTemplateCreatedMachineCount() { return templateCreatedMachineCount; }
}
