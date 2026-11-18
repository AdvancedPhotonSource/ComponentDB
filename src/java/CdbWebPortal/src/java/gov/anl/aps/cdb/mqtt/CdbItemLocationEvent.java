/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Scalar snapshot of a completed item-location change. */
public final class CdbItemLocationEvent extends CdbMqttEvent {

    private final CdbItemSnapshot item;
    private final CdbItemSnapshot oldLocation;
    private final CdbItemSnapshot newLocation;
    private final String oldLocationDetails;
    private final String newLocationDetails;

    public CdbItemLocationEvent(LocatableItem item,
            Item oldLocation, Item newLocation,
            String oldLocationDetails, String newLocationDetails, UserInfo user) {
        super(CdbMqttTopic.ITEM_LOCATION_UPDATE, user);
        if (item == null) {
            throw new IllegalArgumentException("item is required");
        }
        this.item = new CdbItemSnapshot(item);
        this.oldLocation = oldLocation == null ? null : new CdbItemSnapshot(oldLocation);
        this.newLocation = newLocation == null ? null : new CdbItemSnapshot(newLocation);
        this.oldLocationDetails = oldLocationDetails;
        this.newLocationDetails = newLocationDetails;
    }

    public CdbItemSnapshot getItem() { return item; }
    public CdbItemSnapshot getOldLocation() { return oldLocation; }
    public CdbItemSnapshot getNewLocation() { return newLocation; }
    public String getOldLocationDetails() { return oldLocationDetails; }
    public String getNewLocationDetails() { return newLocationDetails; }
}
