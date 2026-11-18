/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.metadata;

import gov.anl.aps.cdb.portal.model.db.entities.Item;

/** Pending pre-update state for an item-location change. */
public final class ItemLocationChange {

    private final Item oldLocation;
    private final String oldLocationDetails;
    private final Item newLocation;
    private final String newLocationDetails;

    public ItemLocationChange(Item oldLocation, String oldLocationDetails,
            Item newLocation, String newLocationDetails) {
        this.oldLocation = oldLocation;
        this.oldLocationDetails = oldLocationDetails;
        this.newLocation = newLocation;
        this.newLocationDetails = newLocationDetails;
    }

    public Item getOldLocation() { return oldLocation; }
    public String getOldLocationDetails() { return oldLocationDetails; }
    public Item getNewLocation() { return newLocation; }
    public String getNewLocationDetails() { return newLocationDetails; }
}
