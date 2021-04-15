/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ImportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.beans.SourceFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperSource extends ImportHelperBase<Source, SourceController> {

    private static final String KEY_NAME = "name";
    
    private SourceFacade sourceFacade;
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        
        specs.add(new StringColumnSpec(
                "Name", 
                KEY_NAME, 
                "setName", 
                "Name of vendor/manufacturer", 
                "getName", 
                ColumnModeOptions.rCREATErUPDATE(), 
                64));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                "setDescription", 
                "Description of vendor/manufacturer", 
                "getDescription", 
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Contact Info", 
                "contactInfo", 
                "setContactInfo", 
                "Contact name and phone number etc", 
                "getContactInfo", 
                ColumnModeOptions.oCREATEoUPDATE(), 
                64));
        
        specs.add(new StringColumnSpec(
                "URL", 
                "url", 
                "setUrl", 
                "URL for vendor/manufacturer", 
                "getUrl", 
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        return specs;
    } 
   
    @Override
    public SourceController getEntityController() {
        return SourceController.getInstance();
    }
    
    private SourceFacade getSourceFacade() {
        if (sourceFacade == null) {
            sourceFacade = sourceFacade.getInstance();
        }
        return sourceFacade;
    }

    /**
     * Specifies whether helper supports updating existing instances.  Defaults
     * to false. Subclasses override to customize.
     */
    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public boolean supportsModeDelete() {
        return true;
    }

    @Override
    public String getFilenameBase() {
        return "Source";
    }
    
    @Override
    protected Source newInvalidUpdateInstance() {
        return new Source();
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        Source entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
    }  
    
//    @Override
//    protected CreateInfo retrieveEntityInstance(Map<String, Object> rowMap) {
//        
//        boolean isValid = true;
//        String validString = "";
//
//        // create item in case we don't find one by name or id, need to return an instance
//        Source invalidInstance = new Source();
//        Source item;
//        
//        // retrieve by id if specified
//        Integer itemId = (Integer) rowMap.get(KEY_EXISTING_ITEM_ID);
//        if (itemId != null) {
//            item = getEntityController().findById(itemId);
//            if (item == null) {
//                item = invalidInstance;
//                isValid = false;
//                validString = "Unable to retrieve existing item with id: " + itemId;
//            }
//        } else {
//            // retrieve by name
//            String itemName = (String) rowMap.get(KEY_NAME);
//            if (itemName != null) {
//                item = getSourceFacade().findByName(itemName);
//                if (item == null) {
//                    // no item found with specified name
//                    item = invalidInstance;
//                    isValid = false;
//                    validString = "No source object found with specified name: " + itemName;
//                }
//            } else {
//                item = invalidInstance;
//                isValid = false;
//                validString = "Must specify existing item id or name to update Source item.";
//            }
//        }
//               
//        return new CreateInfo(item, isValid, validString);
//    }  
}
