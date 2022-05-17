/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.RefObjectManager;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
/**
 * Using a custom handler for location so we can ignore the word "parent" in a
 * column that otherwise expects location item id's. We could use the standard
 * IdRef handler if we didn't need to worry about "parent".
 */
public class LocationHandler extends RefInputHandler {

    public static final String HEADER_LOCATION = "Location";
    public static final String KEY_LOCATION = "location";

    public LocationHandler() {
        super(HEADER_LOCATION);
    }

    @Override
    public ValidInfo handleInput(
            Row row,
            Map<Integer, String> cellValueMap,
            Map<String, Object> rowMap) {

        boolean isValid = true;
        String validString = "";

        String parsedValue = cellValueMap.get(getColumnIndex());

        ItemDomainLocation itemLocation = null;
        if ((parsedValue != null) && (!parsedValue.isEmpty())) {
            // location is specified

            // ignore word "parent"
            if (!parsedValue.equalsIgnoreCase("parent")) {
                
                ItemDomainLocationController controller = ItemDomainLocationController.getInstance();
                
                if (parsedValue.charAt(0) == '#') {
                    // lookup by name
                    
                    if (parsedValue.length() > 1) {
                        RefObjectManager mgr = getObjectManager(controller, null);
                        CdbEntity entity = null;
                        String name = parsedValue.substring(1);
                        try {
                            entity = mgr.getObjectWithName(name, getDomainNameFilter());
                        } catch (CdbException ex) {
                            isValid = false;
                            validString = "Exception looking up by name: "
                                    + name + " for column: " + getColumnName();
                        }
                        
                        if (entity == null) {
                            isValid = false;
                            validString = "Unable to find object for: " + getColumnName() + " with name: " + name;
                        } else {
                            itemLocation = (ItemDomainLocation) entity;
                        }
                        
                    } else {
                        isValid = false;
                        validString = "No name specified following '#' in column: " + getColumnName();
                    }
                    
                } else {
                    // lookup by id
                    
                    int id;
                    try {
                        id = Integer.valueOf(parsedValue);
                        itemLocation = controller.findById(id);
                        if (itemLocation == null) {
                            String msg = "Unable to find object for: " + getColumnName()
                                    + " with id: " + parsedValue;
                            isValid = false;
                            validString = msg;
                        }
                    } catch (NumberFormatException ex) {
                        String msg = "Invalid id number: " + parsedValue + " for column: " + getColumnName();
                        isValid = false;
                        validString = msg;
                    }
                }
                
                if (itemLocation != null) {
                    rowMap.put(KEY_LOCATION, itemLocation);
                }
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
            validString = "Item must be LocatableItem to use LocationHandler.";
            return new ValidInfo(isValid, validString);
        } else {
            item = (LocatableItem) entity;
        }
                
        ItemDomainLocation itemLocation = (ItemDomainLocation) rowMap.get(KEY_LOCATION);
        ItemDomainLocation currentItemLocation = item.getImportLocationItem();

        if (itemLocation != null && (item.getIsItemTemplate())) {
            // template not allowed to have location
            isValid = false;
            validString = "Template item cannot have assigned location.";
            return new ValidInfo(isValid, validString);
        }
        
        boolean changedLocation = 
                (itemLocation != null && currentItemLocation == null) 
                || (itemLocation == null && currentItemLocation != null) 
                || (itemLocation != null && currentItemLocation != null && !itemLocation.getId().equals(currentItemLocation.getId()));
        
        if (changedLocation) {
            
            // check if we are allowed to change location
            if (!LocatableItemController.getInstance().locationEditable(item)) {
                isValid = false;
                Item location = item.getActiveLocation();
                validString
                        = "Item location cannot be modified, it is part of "
                        + location.getDomain().getName() + " item: " + location.getName();
            }

            // update location whether or not it's valid so it appears as a diff in validation table
            item.setImportLocationItem(itemLocation);
        }

        return new ValidInfo(isValid, validString);
    }

}
