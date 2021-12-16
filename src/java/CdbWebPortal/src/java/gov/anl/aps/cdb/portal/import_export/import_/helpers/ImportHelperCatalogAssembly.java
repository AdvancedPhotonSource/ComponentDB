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
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
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

    private static final String LABEL_PARENT_ITEM = "Catalog Item";

    private static final String KEY_PART_NAME = "importPartName";
    private static final String KEY_PARENT_ITEM = "importParentItem";
    private static final String KEY_CHILD_ITEM = "importChildItem";
    private static final String KEY_SORT_ORDER = "sortOrder";

    private static final String HEADER_SORT_ORDER = "Sort Order";
    
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());

        specs.add(new IdOrNameRefColumnSpec(
                LABEL_PARENT_ITEM, 
                KEY_PARENT_ITEM, 
                "", 
                "ID or name of parent catalog item. Name must be unique and prefixed with '#'.", 
                "getParentItem", 
                null,
                ColumnModeOptions.rCREATErUPDATE(), 
                ItemDomainCatalogController.getInstance(), 
                Item.class, 
                null));   
        
        specs.add(new StringColumnSpec(
                "Part Name", 
                KEY_PART_NAME, 
                "setImportPartName", 
                "Part name for assembly member.", 
                "getName",
                ColumnModeOptions.rCREATErUPDATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                "Part Description", 
                "importPartDescription", 
                "setImportPartDescription", 
                "Part description for assembly member.", 
                "getDescription",
                ColumnModeOptions.oCREATEoUPDATE(), 
                128));
        
        specs.add(new FloatColumnSpec(
                HEADER_SORT_ORDER, 
                KEY_SORT_ORDER, 
                "setSortOrder", 
                "Sort order within parent catalog item (as decimal), defaults to order in input sheet.", 
                "getSortOrder",
                null,
                ColumnModeOptions.oCREATEoUPDATE()));
        
        specs.add(new BooleanColumnSpec(
                "Part Required", 
                "importPartRequired", 
                "setImportPartRequired", 
                "True/yes if part is required.", 
                "getIsRequired",
                ColumnModeOptions.oCREATEoUPDATE()));
        
        specs.add(new StringColumnSpec(
                "Part Catalog Item Name (optional)", 
                "importChildItemName", 
                "setImportChildItemName", 
                "Catalog item name for part (for documentation purposes only, ignored by import).", 
                null,
                ColumnModeOptions.oCREATEoUPDATE(), 
                128));
        
        specs.add(new IdOrNameRefColumnSpec(
                "Part Catalog Item ID", 
                KEY_CHILD_ITEM, 
                "setImportChildItem", 
                "ID or name of catalog item for part. Name must be unique and prefixed with '#'.", 
                "getContainedItem", 
                null,
                ColumnModeOptions.rCREATErUPDATE(), 
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
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public boolean supportsModeDelete() {
        return true;
    }

    @Override
    protected String getCreateMessageTypeName() {
        return "catalog element";
    }
    
    @Override
    protected List<ItemElement> generateExportEntityList_() {
        List<ItemElement> entityList = new ArrayList<>();
        ItemDomainCatalogController controller = ItemDomainCatalogController.getInstance();
        List<ItemDomainCatalog> catalogList = controller.getExportEntityList();
        for (ItemDomainCatalog catalog : catalogList) {
            catalog = controller.reloadEntity(catalog);
            entityList.addAll(catalog.getItemElementDisplayList());
        }
        return entityList;
    }
    
    private ValidInfo validateElementInfo(
            ItemDomainCatalog parentItem, String name, Float sortOrder, Integer id, ItemDomainCatalog childItem) {
        
        boolean isValid = true;
        String validString = "";
        
        if (parentItem == null || childItem == null) {
            isValid = false;
            validString = "Parent and child catalog items must be specified";
            return new ValidInfo(isValid, validString);
        }
        
        // check that parent and child are different catalog items
        if (parentItem.getId().equals(childItem.getId())) {
            isValid = false;
            validString = appendToString(validString, "Parent and child catalog items must be different");
        }
        
        List<ItemElement> ieList = parentItem.getItemElementDisplayList();
        for (ItemElement ie : ieList) {
            if (ie.getId() != null && ie.getId().equals(id)) {
                // this is the same child element
                continue;
            }
            if ((ie.getSortOrder() != null) && (ie.getSortOrder().equals(sortOrder))) {
                String msg = "Duplicate sort order for existing child element";
                validString = appendToString(validString, msg);
                isValid = false;
            }
            if ((ie.getName() != null) && (ie.getName().equals(name))) {
                isValid = false;
                String msg = "Duplicate part name for existing child element";
                validString = appendToString(validString, msg);
            }
        }
        return new ValidInfo(isValid, validString);
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
        
        if (itemParentCatalogItem != null) {
            
            // validate child element (name, sort order, catalog item)
            String itemName = (String) rowMap.get(KEY_PART_NAME);
            ItemDomainCatalog itemPartCatalogItem = (ItemDomainCatalog) rowMap.get(KEY_CHILD_ITEM);
            ValidInfo validElementInfo = 
                    validateElementInfo(itemParentCatalogItem, itemName, itemSortOrder, null, itemPartCatalogItem);
            if (!validElementInfo.isValid()) {
                isValid = false;
                validString = appendToString(validString, validElementInfo.getValidString());
            }
            
            itemElement.setImportParentItem(itemParentCatalogItem, itemSortOrder, null, null);
        }
            
        return new CreateInfo(itemElement, isValid, validString);
    }
    
    @Override
    protected ValidInfo updateEntityInstance(ItemElement entity, Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validString = "";
        
        ItemDomainCatalog itemParentCatalogItem = (ItemDomainCatalog) rowMap.get(KEY_PARENT_ITEM);

        if (itemParentCatalogItem != null) {
            
            if (!itemParentCatalogItem.equals(entity.getParentItem())) {
                // catalog item cannot be changed in update
                isValid = false;
                validString = LABEL_PARENT_ITEM + " cannot be modified in update operation.";
            }
        
            // validate name and sort order unique for parent item
            Integer itemId = (Integer) rowMap.get(KEY_EXISTING_ITEM_ID);
            Float itemSortOrder = (Float) rowMap.get(KEY_SORT_ORDER);
            String itemName = (String) rowMap.get(KEY_PART_NAME);
            ItemDomainCatalog itemPartCatalogItem = (ItemDomainCatalog) rowMap.get(KEY_CHILD_ITEM);
            ValidInfo validElementInfo = 
                    validateElementInfo(itemParentCatalogItem, itemName, itemSortOrder, itemId, itemPartCatalogItem);
            if (!validElementInfo.isValid()) {
                isValid = false;
                validString = appendToString(validString, validElementInfo.getValidString());
            }
        }
        
        return new ValidInfo(isValid, validString);
    }

}
