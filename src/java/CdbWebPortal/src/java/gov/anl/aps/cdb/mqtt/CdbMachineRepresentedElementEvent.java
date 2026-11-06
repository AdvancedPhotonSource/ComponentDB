/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Scalar snapshot of a completed represented-element change. */
public final class CdbMachineRepresentedElementEvent extends CdbMqttEvent {

    private final CdbItemSnapshot machine;
    private final CdbItemElementSnapshot oldRepresentedElement;
    private final CdbItemElementSnapshot newRepresentedElement;
    private final CdbItemSnapshot sourceTemplate;

    public CdbMachineRepresentedElementEvent(ItemDomainMachineDesign machine,
            ItemElement oldRepresentedElement, ItemElement newRepresentedElement,
            UserInfo user) {
        this(machine, oldRepresentedElement, newRepresentedElement, null, user);
    }

    public CdbMachineRepresentedElementEvent(ItemDomainMachineDesign machine,
            ItemElement oldRepresentedElement, ItemElement newRepresentedElement,
            ItemDomainMachineDesign sourceTemplate, UserInfo user) {
        super(CdbMqttTopic.MACHINE_DESIGN_REPRESENTED_ELEMENT_UPDATE, user);
        if (machine == null) {
            throw new IllegalArgumentException("machine is required");
        }
        this.machine = new CdbItemSnapshot(machine);
        this.oldRepresentedElement = oldRepresentedElement == null
                ? null : new CdbItemElementSnapshot(oldRepresentedElement);
        this.newRepresentedElement = newRepresentedElement == null
                ? null : new CdbItemElementSnapshot(newRepresentedElement);
        this.sourceTemplate = sourceTemplate == null ? null : new CdbItemSnapshot(sourceTemplate);
    }

    public CdbItemSnapshot getMachine() { return machine; }
    public CdbItemElementSnapshot getOldRepresentedElement() { return oldRepresentedElement; }
    public CdbItemElementSnapshot getNewRepresentedElement() { return newRepresentedElement; }
    public CdbItemSnapshot getSourceTemplate() { return sourceTemplate; }
}
