/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignInventoryController;
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import static gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperBase.KEY_USER;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IntegerColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperMachineInventory extends ImportHelperBase<ItemDomainMachineDesign, ItemDomainMachineDesignInventoryController> {
    
    private static final String KEY_TEMPLATE = "importMdItem";
    private static final String KEY_NAME = "name";
    private static final String KEY_LOCATION = "locationItem";
    protected static final String KEY_STATUS = "inventoryStatusValue";
    private static final String AUTO_VALUE = "auto";
    
    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new IdOrNameRefColumnSpec(
                "MD Template", 
                KEY_TEMPLATE, 
                "setImportMdItem", 
                "ID or name of CDB machine design template. Name must be unique and prefixed with '#'.", 
                null, null,
                ColumnModeOptions.rCREATErUPDATE(), 
                ItemDomainMachineDesignController.getInstance(), 
                ItemDomainMachineDesign.class, 
                null));
        
        specs.add(new StringColumnSpec(
                "Name", 
                KEY_NAME, 
                "", 
                "Name for inventory unit (use 'auto' to generate automatically).", 
                null,
                ColumnModeOptions.rCREATErUPDATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                "Alt Name", 
                "alternateName", 
                "setAlternateName", 
                "Alternate name for inventory unit.", 
                null,
                ColumnModeOptions.oCREATEoUPDATE(), 
                128));
        
        specs.add(new IntegerColumnSpec(
                "QR ID", 
                "qrId", 
                "setQrId", 
                "Integer QR id of inventory unit.", 
                null, null,
                ColumnModeOptions.oCREATEoUPDATE()));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                "setDescription", 
                "Description of inventory unit.", 
                null,
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Status", 
                KEY_STATUS, 
                "setInventoryStatusValue", 
                "Status of inventory item.", 
                null,
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(locationColumnSpec());
        specs.add(locationDetailsColumnSpec());
        specs.add(projectListColumnSpec());
        specs.add(ownerUserColumnSpec());
        specs.add(ownerGroupColumnSpec());
        
        return specs;
    } 
   
    @Override
    public ItemDomainMachineDesignInventoryController getEntityController() {
        return ItemDomainMachineDesignInventoryController.getInstance();
    }

    @Override
    public String getFilenameBase() {
        return "Machine Inventory";
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {

        String methodLogName = "createEntityFromTemplateInstantiation() ";
        boolean isValid = true;
        String validString = "";
        
        // validate status value is in set of allowed values
        String itemStatus = (String) rowMap.get(KEY_STATUS);
        if (itemStatus != null && !itemStatus.isEmpty()) {
            List<AllowedPropertyValue> allowedValues = 
                    getEntityController().getInventoryStatusPropertyType().getAllowedPropertyValueList();
            boolean found = false;
            for (AllowedPropertyValue value : allowedValues) {
                if (itemStatus.equals(value.getValue())) {
                    found = true;
                    break;
                }                    
            }
            if (!found) {
                // illegal status value
                isValid = false;
                validString = "invalid status value: " + itemStatus;
            }
        }
        
        UserInfo user = (UserInfo) rowMap.get(KEY_USER);
        UserGroup group = (UserGroup) rowMap.get(KEY_GROUP);

        ItemDomainMachineDesign template = (ItemDomainMachineDesign) rowMap.get(KEY_TEMPLATE);
        ItemDomainMachineDesign item = null;
        if (template != null) {
            item = getEntityController().performPrepareCreateInventoryFromTemplate(template, user, group);
        }
        
        if (item == null) {
            isValid = false;
            validString = "controller failed to create inventory item";
            item = getEntityController().createEntityInstance();
        } else {
            // prepare to update location info?
            LocatableItemController.getInstance().setItemLocationInfo(item);
            
            // override item name if not "auto"
            String itemName = (String) rowMap.get(KEY_NAME);
            if (!itemName.equals(AUTO_VALUE)) {
                item.setName(itemName);
            }
        }
        
        return new CreateInfo(item, isValid, validString);
    }
    
}
