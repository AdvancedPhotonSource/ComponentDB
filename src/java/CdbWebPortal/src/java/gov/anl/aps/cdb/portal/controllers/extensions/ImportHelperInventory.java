/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
public class ImportHelperInventory 
        extends ImportHelperBase<ItemDomainInventory, ItemDomainInventoryController> {

    protected static String completionUrlValue = "/views/itemDomainInventory/list?faces-redirect=true";
    
    private static final Logger LOGGER = LogManager.getLogger(ImportHelperInventory.class.getName());
    
    private static final String KEY_NAME = "name";
    private static final String KEY_STATUS = "inventoryStatusValue";
    private static final String KEY_CATALOG_ITEM = "catalogItem";
    private static final String AUTO_VALUE = "auto";
    
    private Map<ItemDomainCatalog, Integer> newItemCountMap = new HashMap<>();
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new ImportHelperBase.IdOrNameRefColumnSpec(0, "Catalog Item", KEY_CATALOG_ITEM, "setCatalogItem", true, "ID or name of catalog item for inventory unit", ItemDomainCatalogController.getInstance(), ItemDomainCatalog.class, null));
        specs.add(new ImportHelperBase.StringColumnSpec(1, "Tag", KEY_NAME, "", true, "Name for inventory unit.", 64));
        specs.add(new ImportHelperBase.IntegerColumnSpec(2, "QR ID", "qrId", "setQrId", false, "Integer QR id of inventory unit."));
        specs.add(new ImportHelperBase.StringColumnSpec(3, "Serial Number", "serialNumber", "setSerialNumber", false, "Inventory unit serial number.", 128));
        specs.add(new ImportHelperBase.StringColumnSpec(4, "Description", "description", "setDescription", false, "Description of inventory unit.", 256));
        specs.add(new ImportHelperBase.StringColumnSpec(5, "Status", KEY_STATUS, "setInventoryStatusValue", false, "Status of inventory item.", 256));
        specs.add(new ImportHelperBase.IdOrNameRefColumnSpec(6, "Location", "importLocationItemString", "setImportLocationItem", false, "Inventory unit location.", ItemDomainLocationController.getInstance(), ItemDomainLocation.class, ""));
        specs.add(new ImportHelperBase.StringColumnSpec(7, "Location Details", "locationDetails", "setLocationDetails", false, "Location details for inventory unit.", 256));
        specs.add(new ImportHelperBase.IdOrNameRefColumnSpec(8, "Project", "itemProjectString", "setProject", true, "Numeric ID of CDB project.", ItemProjectController.getInstance(), ItemProject.class, ""));
        
        return specs;
    } 
   
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public ItemDomainInventoryController getEntityController() {
        return ItemDomainInventoryController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Component Inventory Template";
    }
    
    @Override
    protected void reset_() {
        newItemCountMap = new HashMap<>();
    }
    
    private String generateItemName(
            ItemDomainInventory item,
            Map<String, Object> rowMap) {
        
        ItemDomainCatalog catalogItem = 
                (ItemDomainCatalog)rowMap.get(KEY_CATALOG_ITEM);
        
        if (!newItemCountMap.containsKey(catalogItem)) {
            newItemCountMap.put(catalogItem, 0);
        }
        
        int newItemCount = newItemCountMap.get(catalogItem) + 1;
        newItemCountMap.put(catalogItem, newItemCount);
        
        return getEntityController().generateItemName(item, catalogItem, newItemCount);
    }

    @Override
    protected ImportHelperBase.CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        ItemDomainInventory item = getEntityController().createEntityInstance();
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

        return new ImportHelperBase.CreateInfo(item, isValid, validString);
    }  

    protected boolean ignoreDuplicates() {
        return false;
    }
}
