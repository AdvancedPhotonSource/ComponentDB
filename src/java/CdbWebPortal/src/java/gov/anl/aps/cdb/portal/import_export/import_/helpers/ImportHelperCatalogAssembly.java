/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemElementController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.BooleanColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.FloatColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperCatalogAssembly extends ImportHelperBase<ItemElement, ItemElementController> {

    private static final String KEY_PART_NAME = "importPartName";
    private static final String KEY_PARENT_ITEM = "importParentItem";
    private static final String KEY_SORT_ORDER = "sortOrder";

    private static final String HEADER_SORT_ORDER = "Sort Order";
    
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new IdOrNameRefColumnSpec(
                "Catalog Item", 
                KEY_PARENT_ITEM, 
                "", 
                "ID or name of parent catalog item. Name must be unique and prefixed with '#'.", 
                null, null,
                ColumnModeOptions.rCREATE(), 
                ItemDomainCatalogController.getInstance(), 
                Item.class, 
                null));   
        
        specs.add(new StringColumnSpec(
                "Part Name", 
                KEY_PART_NAME, 
                "setImportPartName", 
                "Part name for assembly member.", 
                null,
                ColumnModeOptions.rCREATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                "Part Description", 
                "importPartDescription", 
                "setImportPartDescription", 
                "Part description for assembly member.", 
                null,
                ColumnModeOptions.oCREATE(), 
                128));
        
        specs.add(new FloatColumnSpec(
                HEADER_SORT_ORDER, 
                KEY_SORT_ORDER, 
                "setSortOrder", 
                "Sort order within parent catalog item (as decimal), defaults to order in input sheet.", 
                null,
                null,
                ColumnModeOptions.oCREATE()));
        
        specs.add(new BooleanColumnSpec(
                "Part Required", 
                "importPartRequired", 
                "setImportPartRequired", 
                "True/yes if part is required.", 
                null,
                ColumnModeOptions.oCREATE()));
        
        specs.add(new StringColumnSpec(
                "Part Catalog Item Name (optional)", 
                "importChildItemName", 
                "setImportChildItemName", 
                "Catalog item name for part (for documentation purposes only, ignored by import).", 
                null,
                ColumnModeOptions.oCREATE(), 
                128));
        
        specs.add(new IdOrNameRefColumnSpec(
                "Part Catalog Item ID", 
                "importChildItem", 
                "setImportChildItem", 
                "ID or name of catalog item for part. Name must be unique and prefixed with '#'.", 
                null, null,
                ColumnModeOptions.rCREATE(), 
                ItemDomainCatalogController.getInstance(), 
                Item.class, 
                null));        

        return specs;
    }
    
    @Override
    public ItemElementController getEntityController() {
        return ItemElementController.getInstance();
    }

    @Override
    public String getFilenameBase() {
        return "Catalog Assembly";
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validString = "";
        
        ItemElement itemElement = new ItemElement();
        
        ItemDomainCatalog itemParentCatalogItem = (ItemDomainCatalog) rowMap.get(KEY_PARENT_ITEM);
        
        // determine sort order
        Float itemSortOrder = (Float) rowMap.get(KEY_SORT_ORDER);
        if ((itemSortOrder == null) && (itemParentCatalogItem != null)) {
            // get maximum sort order value for existing children
            Float maxSortOrder = itemParentCatalogItem.getMaxSortOrder();
            itemSortOrder = maxSortOrder + 1;
        }
        itemElement.setSortOrder(itemSortOrder);
        
        // validate name and sort order unique for specified parent item
        String itemName = (String) rowMap.get(KEY_PART_NAME);
        if (itemParentCatalogItem != null) {
            // check for duplicate part name
            List<ItemElement> ieList = itemParentCatalogItem.getFullItemElementList();
            for (ItemElement ie : ieList) {
                if ((ie.getSortOrder() != null) && (ie.getSortOrder().equals(itemSortOrder))) {
                    String msg = "duplicate sort order for existing child location";
                    validString = appendToString(validString, msg);
                    isValid = false;
                }
                if ((ie.getName() != null) && (ie.getName().equals(itemName))) {
                    isValid = false;
                    String msg = "duplicate part name for existing item element";
                    validString = appendToString(validString, msg);
                }
            }
            itemElement.setImportParentItem(itemParentCatalogItem, itemSortOrder, null, null);
        }
            
        return new CreateInfo(itemElement, isValid, validString);
    }
    
}
