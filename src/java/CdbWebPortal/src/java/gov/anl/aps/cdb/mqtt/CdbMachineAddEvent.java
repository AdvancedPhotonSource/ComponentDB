/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.anl.aps.cdb.mqtt.metadata.MachineAddChange;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 * Scalar snapshot of a newly persisted machine-design node. Template fields are
 * omitted unless the node was cloned from a template.
 */
public final class CdbMachineAddEvent extends CdbMqttEvent {

    private final CdbItemSnapshot machine;
    private final CdbItemSnapshot parent;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final CdbItemSnapshot sourceTemplate;
    // New top node of the instantiation, or an existing machine a template was assigned to.
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final CdbItemSnapshot templateAppliedToMachine;
    // Add events published for the same template application.
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer templateCreatedMachineCount;

    public CdbMachineAddEvent(ItemDomainMachineDesign machine,
            ItemDomainMachineDesign parent, UserInfo user) {
        this(new MachineAddChange(machine, parent), user);
    }

    public CdbMachineAddEvent(MachineAddChange change, UserInfo user) {
        super(CdbMqttTopic.MACHINE_DESIGN_ADD, user);
        if (change == null || change.getMachine() == null) {
            throw new IllegalArgumentException("machine is required");
        }
        this.machine = new CdbItemSnapshot(change.getMachine());
        this.parent = snapshot(change.getParent());
        this.sourceTemplate = snapshot(change.getSourceTemplate());
        this.templateAppliedToMachine = snapshot(change.getTemplateAppliedToMachine());
        this.templateCreatedMachineCount = change.getTemplateCreatedMachineCount();
    }

    private static CdbItemSnapshot snapshot(ItemDomainMachineDesign item) {
        return item == null ? null : new CdbItemSnapshot(item);
    }

    public CdbItemSnapshot getMachine() { return machine; }
    public CdbItemSnapshot getParent() { return parent; }
    public CdbItemSnapshot getSourceTemplate() { return sourceTemplate; }
    public CdbItemSnapshot getTemplateAppliedToMachine() { return templateAppliedToMachine; }
    public Integer getTemplateCreatedMachineCount() { return templateCreatedMachineCount; }
}
