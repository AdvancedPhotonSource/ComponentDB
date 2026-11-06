/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.metadata;

import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;

/** Pending pre-update state for a machine represented-element change. */
public final class MachineRepresentedElementChange {

    private final ItemElement oldRepresentedElement;

    public MachineRepresentedElementChange(ItemElement oldRepresentedElement) {
        this.oldRepresentedElement = oldRepresentedElement;
    }

    public ItemElement getOldRepresentedElement() { return oldRepresentedElement; }
}
