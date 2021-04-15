/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryBaseController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
public abstract class ImportHelperInventoryBase
        <InventoryEntityType extends ItemDomainInventoryBase, InventoryEntityControllerType extends ItemDomainInventoryBaseController> 
        extends ImportHelperBase<InventoryEntityType, InventoryEntityControllerType> {

    private static final Logger LOGGER = LogManager.getLogger(ImportHelperInventoryBase.class.getName());
    
    protected static final String KEY_CATALOG_ITEM = "catalogItem";
    protected static final String KEY_NAME = "name";
    protected static final String KEY_STATUS = "inventoryStatusValue";
    protected static final String AUTO_VALUE = "auto";
    
    private Map<ItemDomainCatalogBase, Integer> newItemCountMap = new HashMap<>();
    
    public StringColumnSpec statusColumnSpec(int colIndex) {
        return new StringColumnSpec(
                "Status", 
                KEY_STATUS, 
                "setInventoryStatusValue", 
                "Status of inventory item.", 
                null,
                ColumnModeOptions.oCREATEoUPDATE(), 
                256);
    }
    
    @Override
    protected void reset_() {
        newItemCountMap = new HashMap<>();
    }
    
    private String generateItemName(
            ItemDomainInventoryBase item,
            Map<String, Object> rowMap) {
        
        ItemDomainCatalogBase catalogItem = 
                (ItemDomainCatalogBase)rowMap.get(KEY_CATALOG_ITEM);
        
        if (!newItemCountMap.containsKey(catalogItem)) {
            newItemCountMap.put(catalogItem, 0);
        }
        
        int newItemCount = newItemCountMap.get(catalogItem) + 1;
        newItemCountMap.put(catalogItem, newItemCount);
        
        return getEntityController().generateItemName(item, catalogItem, newItemCount);
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        ItemDomainInventoryBase item = getEntityController().createEntityInstance();
        getEntityController().prepareEditInventoryStatus();
        
        String methodLogName = "createEntityInstance() ";
        boolean isValid = true;
        String validString = "";
        
        // get item name
        String itemName = (String) rowMap.get(KEY_NAME);
        if (itemName.equals(AUTO_VALUE)) {
            String autoName = generateItemName(item, rowMap);
            item.setName(autoName);
        } else {
            item.setName(itemName);
        }
        
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
                LOGGER.info(methodLogName + validString);
            }
        }

        return new CreateInfo(item, isValid, validString);
    }  

}
