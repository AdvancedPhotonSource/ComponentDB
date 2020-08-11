/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author djarosz
 */
public abstract class ItemMultiEditLocatableItemController extends ItemMultiEditController {

    private static final Logger logger = LogManager.getLogger(Item.class.getName());
    
    protected boolean updateLocation = false;
    protected boolean updateLocationDetails = false; 
    protected String toggledLocationEditViewUUID = null; 
    
    public boolean isUpdateLocation() {
        return updateLocation;
    }

    public void setUpdateLocation(boolean updateLocation) {
        this.toggledLocationEditViewUUID = null; 
        this.updateLocation = updateLocation;
    }

    public boolean isUpdateLocationDetails() {
        return updateLocationDetails;
    }

    public void setUpdateLocationDetails(boolean updateLocationDetails) {
        this.updateLocationDetails = updateLocationDetails;
    }

    public String getToggledLocationEditViewUUID() {
        return toggledLocationEditViewUUID;
    }

    public void setToggledLocationEditViewUUID(String toggledLocationEditViewUUID) {
        this.toggledLocationEditViewUUID = toggledLocationEditViewUUID;
    }

}
