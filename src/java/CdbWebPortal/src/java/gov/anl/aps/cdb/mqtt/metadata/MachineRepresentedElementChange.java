/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.metadata;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;

/** Pending pre-update state for a machine represented-element change. */
public final class MachineRepresentedElementChange {

    private final ItemElement oldRepresentedElement;
    private final ItemDomainMachineDesign sourceTemplate;

    public MachineRepresentedElementChange(ItemElement oldRepresentedElement) {
        this(oldRepresentedElement, null);
    }

    public MachineRepresentedElementChange(ItemElement oldRepresentedElement,
            ItemDomainMachineDesign sourceTemplate) {
        this.oldRepresentedElement = oldRepresentedElement;
        this.sourceTemplate = sourceTemplate;
    }

    public ItemElement getOldRepresentedElement() { return oldRepresentedElement; }
    public ItemDomainMachineDesign getSourceTemplate() { return sourceTemplate; }
}
