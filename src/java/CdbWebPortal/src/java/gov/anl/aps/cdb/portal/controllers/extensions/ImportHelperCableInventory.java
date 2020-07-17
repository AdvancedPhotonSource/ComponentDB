/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
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
public class ImportHelperCableInventory extends ImportHelperBase<ItemDomainCableInventory, ItemDomainCableInventoryController> {


    protected static String completionUrlValue = "/views/itemDomainCableInventory/list?faces-redirect=true";
    
    private static final Logger LOGGER = LogManager.getLogger(ImportHelperMachineDesign.class.getName());
    
    private static final String KEY_NAME = "name";
    private static final String KEY_CATALOG_ITEM = "catalogItem";
    private static final String AUTO_VALUE = "auto";
    
    private Map<ItemDomainCableCatalog, Integer> newItemCountMap = new HashMap<>();
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new IdOrNameRefColumnSpec(0, "Cable Catalog Item", KEY_CATALOG_ITEM, "setCatalogItem", true, "ID or name of cable catalog item for inventory unit", ItemDomainCableCatalogController.getInstance(), ItemDomainCableCatalog.class, null));
        specs.add(new StringColumnSpec(1, "Name", KEY_NAME, "", true, "Name for inventory unit.", 64));
        specs.add(new IntegerColumnSpec(2, "QR ID", "qrId", "setQrId", false, "Integer QR id of inventory unit."));
        specs.add(new StringColumnSpec(3, "Description", "description", "setDescription", false, "Description of inventory unit.", 256));
        specs.add(new IdOrNameRefColumnSpec(4, "Project", "itemProjectString", "setProject", true, "Numeric ID of CDB project.", ItemProjectController.getInstance(), ItemProject.class, ""));
        specs.add(new StringColumnSpec(5, "Length", "length", "setLength", false, "Installed length of cable inventory unit.", 256));
        
        return specs;
    } 
   
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public ItemDomainCableInventoryController getEntityController() {
        return ItemDomainCableInventoryController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Cable Inventory Template";
    }
    
    @Override
    protected void reset_() {
        newItemCountMap = new HashMap<>();
    }
    
    private String generateItemName(
            ItemDomainCableInventory item,
            Map<String, Object> rowMap) {
        
        ItemDomainCableCatalog catalogItem = 
                (ItemDomainCableCatalog)rowMap.get(KEY_CATALOG_ITEM);
        
        if (!newItemCountMap.containsKey(catalogItem)) {
            newItemCountMap.put(catalogItem, 0);
        }
        
        int newItemCount = newItemCountMap.get(catalogItem) + 1;
        newItemCountMap.put(catalogItem, newItemCount);
        
        return getEntityController().generateItemName(item, catalogItem, newItemCount);
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        ItemDomainCableInventory item = getEntityController().createEntityInstance();
        
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

        return new CreateInfo(item, true, "");
    }  

    protected boolean ignoreDuplicates() {
        return false;
    }
}
