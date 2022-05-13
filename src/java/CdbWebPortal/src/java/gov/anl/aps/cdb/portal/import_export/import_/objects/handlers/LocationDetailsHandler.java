/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
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

        // set location details
        String locationDetails = (String) rowMap.get(KEY_LOCATION_DETAILS);
        if (locationDetails != null) {
            if (item.getMembershipLocation() != null) {
                // error if there is a membership location, because setLocationDetails() ignores change in that case
                isValid = false;
                validString = "'" + HEADER_LOCATION_DETAILS + "' cannot be specified if location is inherited from parent";
            }
            
            // set location details regardless if valid, so that it appears as a diff in validation table
            item.setImportLocationDetails(locationDetails);
        }

        return new ValidInfo(isValid, validString);
    }

}
