/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public class LocationDetailsHandler extends RefInputHandler {
    
    public static final String HEADER_LOCATION_DETAILS = "Location Details";
    public static final String KEY_LOCATION_DETAILS = "locationDetails";

    public LocationDetailsHandler() {
        super(HEADER_LOCATION_DETAILS);
    }

    @Override
    public ValidInfo handleInput(
            Row row,
            Map<Integer, String> cellValueMap,
            Map<String, Object> rowMap) {

        boolean isValid = true;
        String validString = "";

        String parsedValue = cellValueMap.get(getColumnIndex());
        if ((parsedValue != null) && (!parsedValue.isEmpty())) {
            // ignore word "parent"
            if (!parsedValue.equalsIgnoreCase("parent")) {
                rowMap.put(KEY_LOCATION_DETAILS, parsedValue);
            }
        }

        return new ValidInfo(isValid, validString);
    }

    @Override
    public ValidInfo updateEntity(Map<String, Object> rowMap, CdbEntity entity) {

        boolean isValid = true;
        String validString = "";

        LocatableItem item = null;
        if (!(entity instanceof LocatableItem)) {
            isValid = false;
            validString = "Item must be LocatableItem to use LocationDetailsHandler.";
            return new ValidInfo(isValid, validString);
        } else {
            item = (LocatableItem) entity;
        }

        String locationDetails = (String) rowMap.get(KEY_LOCATION_DETAILS);        
        String currentLocationDetails = item.getImportLocationDetails();
        
        if (locationDetails != null && !locationDetails.isEmpty() && (item.getIsItemTemplate())) {
            // template not allowed to have location details
            isValid = false;
            validString = "Template item cannot have location details.";
            return new ValidInfo(isValid, validString);
        }

        boolean changedLocationDetails =
                (locationDetails != null && currentLocationDetails == null) 
                || (locationDetails == null && currentLocationDetails != null) 
                || (locationDetails != null && currentLocationDetails != null && !locationDetails.equals(currentLocationDetails));
        
        if (changedLocationDetails) {             
            
            // check if we are allowed to change location
            if (!LocatableItemController.getInstance().locationEditable(item)) {
                isValid = false;
                Item location = item.getActiveLocation();
                validString
                        = "Item location details cannot be modified, it is part of "
                        + location.getDomain().getName() + " item: " + location.getName();
            }

            // set location details regardless if valid, so that it appears as a diff in validation table
            item.setImportLocationDetails(locationDetails);
        }

        return new ValidInfo(isValid, validString);
    }

}
