/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.SingleColumnInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.BooleanColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.CustomColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.FloatColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.NameHierarchyColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public class ImportHelperMachineHierarchy 
        extends ImportHelperHierarchyBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {
    
    /**
     * Using a custom handler so that we can use catalog or inventory item id's
     * in a single column.  There is not a way to do this with IdRef handler, as
     * it needs a particular controller instance to use for the lookup.  The
     * query we need here is on the ItemFacade.
     */
    private class AssignedItemHandler extends SingleColumnInputHandler {
        
        public AssignedItemHandler() {
            super(HEADER_ASSIGNED_ITEM);
        }
        
        @Override
        public ValidInfo handleInput(
                Row row,
                Map<Integer, String> cellValueMap,
                Map<String, Object> rowMap) {
            
            boolean isValid = true;
            String validString = "";
            
            String parsedValue = cellValueMap.get(getColumnIndex());
            
            Item assignedItem = null;
            if ((parsedValue != null) && (!parsedValue.isEmpty())) {
                // assigned item is specified
                
                int id;
                try {
                    id = Integer.valueOf(parsedValue);
                    assignedItem = ItemFacade.getInstance().findById(id);
                    if (assignedItem == null) {
                        String msg = "Unable to find object for: " + getColumnName()
                                + " with id: " + parsedValue;
                        isValid = false;
                        validString = msg;
                        LOGGER.info("AssignedItemHandler.handleInput() " + msg);
                    }
                    rowMap.put(KEY_ASSIGNED_ITEM, assignedItem);
                    
                } catch (NumberFormatException ex) {
                    String msg = "Invalid id number: " + parsedValue + " for column: " + getColumnName();
                    isValid = false;
                    validString = msg;
                    LOGGER.info("AssignedItemHandler.handleInput() " + msg);  
                }                
            }

            return new ValidInfo(isValid, validString);
        }
    }
    
    /**
     * Using a custom handler for location so we can ignore the word "parent"
     * in a column that otherwise expects location item id's.  We could use the
     * standard IdRef handler if we didn't need to worry about "parent".
     */
    private class LocationHandler extends SingleColumnInputHandler {
        
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
                    int id;
                    try {
                        id = Integer.valueOf(parsedValue);
                        itemLocation = ItemDomainLocationController.getInstance().findById(id);
                        if (itemLocation == null) {
                            String msg = "Unable to find object for: " + getColumnName()
                                    + " with id: " + parsedValue;
                            isValid = false;
                            validString = msg;
                            LOGGER.info("LocationHandler.handleInput() " + msg);

                        } else {
                            // set location
                            rowMap.put(KEY_LOCATION, itemLocation);
                        }

                    } catch (NumberFormatException ex) {
                        String msg = "Invalid id number: " + parsedValue + " for column: " + getColumnName();
                        isValid = false;
                        validString = msg;
                        LOGGER.info("LocationHandler.handleInput() " + msg);
                    }              
                }
            }

            return new ValidInfo(isValid, validString);
        }
    }
    
    private static final Logger LOGGER = LogManager.getLogger(ImportHelperMachineHierarchy.class.getName());
    
    private static final String KEY_NAME = "name";
    private static final String KEY_INDENT = "indentLevel";
    private static final String KEY_ASSIGNED_ITEM = "assignedItem";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_PARENT = "importMdItem";
    private static final String KEY_IS_TEMPLATE = "importIsTemplateString";
    private static final String KEY_SORT_ORDER = "importSortOrder";
    
    private static final String HEADER_PARENT = "Parent ID";
    private static final String HEADER_BASE_LEVEL = "Level";
    private static final String HEADER_ALT_NAME = "Machine Design Alternate Name";
    private static final String HEADER_DESCRIPTION = "Machine Design Item Description";
    private static final String HEADER_SORT_ORDER = "Sort Order";
    private static final String HEADER_ASSIGNED_ITEM = "Assigned Catalog/Inventory Item Description";
    private static final String HEADER_ASSIGNED_ITEM_ID = "Assigned Catalog/Inventory Item";
    private static final String HEADER_LOCATION = "Location";
    private static final String HEADER_TEMPLATE = "Is Template?";

    private Map<Integer, ItemDomainMachineDesign> parentIndentMap = new HashMap<>();
    
    private int nonTemplateItemCount = 0;
    private int templateItemCount = 0;
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new IdOrNameRefColumnSpec(
                HEADER_PARENT, 
                KEY_PARENT, 
                "setImportMdItem", 
                "CDB ID or name of parent machine design item.  Can only be provided for level 0 item. Name must be unique and prefixed with '#'.", 
                null,
                ColumnModeOptions.oCREATE(), 
                ItemDomainMachineDesignController.getInstance(), 
                ItemDomainMachineDesign.class, 
                ""));
        
        specs.add(new NameHierarchyColumnSpec(
                "Name hierarchy column", 
                ColumnModeOptions.oCREATE(),
                HEADER_BASE_LEVEL, 
                KEY_NAME, 
                KEY_INDENT, 
                3));
        
        specs.add(new StringColumnSpec(
                HEADER_ALT_NAME, 
                "alternateName", 
                "setAlternateName", 
                "Alternate machine design item name.", 
                null,
                ColumnModeOptions.oCREATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                HEADER_DESCRIPTION, 
                "description", 
                "setDescription", 
                "Textual description of machine design item.", 
                null,
                ColumnModeOptions.oCREATE(), 
                256));
        
        specs.add(new FloatColumnSpec(
                HEADER_SORT_ORDER, 
                KEY_SORT_ORDER, 
                "setImportSortOrder", 
                "Sort order within parent item (as decimal), defaults to order in input sheet.", 
                null,
                ColumnModeOptions.oCREATE()));
        
        specs.add(new StringColumnSpec(
                HEADER_ASSIGNED_ITEM, 
                "importAssignedItemDescription", 
                "setImportAssignedItemDescription", 
                "Textual description of machine design item.", 
                null,
                ColumnModeOptions.oCREATE(), 
                256));

        AssignedItemHandler assignedItemHandler = new AssignedItemHandler();
        
        specs.add(new CustomColumnSpec(
                HEADER_ASSIGNED_ITEM_ID, 
                "importAssignedItemString", 
                "CDB ID or name of assigned catalog or inventory item. Name must be unique and prefixed with '#'.", 
                null,
                ColumnModeOptions.oCREATE(), 
                assignedItemHandler));
        
        LocationHandler locationHandler = new LocationHandler();
        
        specs.add(new CustomColumnSpec(
                HEADER_LOCATION, 
                "importLocationItemString", 
                "CDB ID or name of CDB location item (use of word 'parent' allowed for documentation purposes, it is ignored). Name must be unique and prefixed with '#'.", 
                null,
                ColumnModeOptions.oCREATE(), 
                locationHandler));

        specs.add(locationDetailsColumnSpec());
        
        specs.add(new BooleanColumnSpec(
                HEADER_TEMPLATE, 
                KEY_IS_TEMPLATE, 
                "setImportIsTemplate", 
                "True/yes if item is template, false/no otherwise.", 
                null,
                ColumnModeOptions.rCREATE()));
        
        specs.add(projectListColumnSpec());
        specs.add(ownerUserColumnSpec());
        specs.add(ownerGroupColumnSpec());
        
        return specs;
    }
        
    @Override
    public ItemDomainMachineDesignController getEntityController() {
        return ItemDomainMachineDesignController.getInstance();
    }

    @Override
    public String getFilenameBase() {
        return "Machine Hierarchy";
    }
    
    @Override
    protected void reset_() {
        super.reset();
        nonTemplateItemCount = 0;
        templateItemCount = 0;
    }
    
    @Override
    protected ItemDomainMachineDesign getItemParent(ItemDomainMachineDesign item) {
        return null;
    }
    
    @Override
    protected String getItemName(ItemDomainMachineDesign item) {
        return item.getName();
    }
    
    @Override
    protected List<ItemDomainMachineDesign> getItemChildren(ItemDomainMachineDesign item) {
        List<ItemElement> children = item.getItemElementDisplayList();
        return children.stream()
                .map((child) -> (ItemDomainMachineDesign) child.getContainedItem())
                .collect(Collectors.toList());
    }
            
    @Override
    protected ItemDomainMachineDesign newInstance_() {
        return getEntityController().createEntityInstance();
    }
    
    @Override
    protected String getKeyName_() {
        return KEY_NAME;
    }
            
    @Override
    protected String getKeyIndent_() {
        return KEY_INDENT;
    }
            
    @Override
    protected String getKeyParent_() {
        return KEY_PARENT;
    }
            
    @Override
    protected ValidInfo initEntityInstance_(
            ItemDomainMachineDesign item,
            ItemDomainMachineDesign itemParent,
            Map<String, Object> rowMap,
            String itemName,
            String itemPath,
            int itemSiblingNumber) {
        
        boolean isValid = true;
        String validString = "";

        boolean isValidAssignedItem = true;

        // determine sort order
        Float itemSortOrder = (Float) rowMap.get(KEY_SORT_ORDER);
        if ((itemSortOrder == null) && (itemParent != null)) {
            // get maximum sort order value for existing children
            Float maxSortOrder = itemParent.getMaxSortOrder();
            itemSortOrder = maxSortOrder + 1;
        }
        item.setImportSortOrder(itemSortOrder);
        
        item.setName(itemName);
        item.setImportPath(itemPath);
        
        // set uuid in case there are duplicate names
        String viewUUID = item.getViewUUID();
        item.setItemIdentifier2(viewUUID);

        // set flag indicating item is template
        Boolean itemIsTemplate = (Boolean) rowMap.get(KEY_IS_TEMPLATE);
        if (itemIsTemplate != null) {
            item.setImportIsTemplate(itemIsTemplate);
        } else {
            // return because we need this value to continue
            isValid = false;
            validString = ""; // we don't need a message because this is already flagged as invalid because it is a required column"
            return new ValidInfo(isValid, validString);
        }

        // set assigned item
        Item assignedItem = (Item) rowMap.get(KEY_ASSIGNED_ITEM);
        if (assignedItem != null) {
            if (assignedItem instanceof ItemDomainCatalog) {
                item.setImportAssignedCatalogItem((ItemDomainCatalog) assignedItem);
            } else if (assignedItem instanceof ItemDomainInventory) {
                item.setImportAssignedInventoryItem((ItemDomainInventory) assignedItem);
            } else {
                String msg = "Invalid object type for assigned item: " + assignedItem.getClass().getName();
                isValid = false;
                validString = msg;
            }
        }

        // set location
        ItemDomainLocation itemLocation = (ItemDomainLocation) rowMap.get(KEY_LOCATION);
        if (itemLocation != null) {
            item.setImportLocationItem(itemLocation);
        }

        if (item.getIsItemTemplate()) {
            // template item handling
            
            templateItemCount = templateItemCount + 1;

            if ((item.getImportAssignedInventoryItem() != null)) {

                // template not allowed to have assigned inventory
                String msg = "Template cannot have assigned inventory item";
                validString = appendToString(validString, msg);
                isValid = false;
                isValidAssignedItem = false;
            }

            if (item.getImportLocationItem() != null) {
                // template not allowed to have location
                String msg = "Template cannot have location item";
                validString = appendToString(validString, msg);
                isValid = false;
            }

        } else {            
            // non-template item handling
            nonTemplateItemCount = nonTemplateItemCount + 1;
        }

        if (itemParent != null) {
            // handling for all items with parent, template or non-template
            if (!Objects.equals(item.getIsItemTemplate(), itemParent.getIsItemTemplate())) {
                // parent and child must both be templates or both not be
                String msg = "parent and child must both be templates or both not be templates";
                validString = appendToString(validString, msg);
                isValid = false;
            }            
            item.setImportChildParentRelationship(itemParent, itemSortOrder);
        }
        
        if (isValidAssignedItem) {
            item.applyImportAssignedItem();
        }

        return new ValidInfo(isValid, validString);
    }

    protected String getCustomSummaryDetails() {
        
        String summaryDetails = "";
        
        if (templateItemCount > 0) {
            String templateItemDetails = templateItemCount + " template items";
            if (summaryDetails.isEmpty()) {
                summaryDetails = templateItemDetails;                        
            } else {
                summaryDetails = summaryDetails + ", " + templateItemDetails;
            }
        }
        
        if (nonTemplateItemCount > 0) {
            String nontemplateItemDetails = nonTemplateItemCount + " regular items";
            if (summaryDetails.isEmpty()) {
                summaryDetails = nontemplateItemDetails;
            } else {
                summaryDetails = summaryDetails + ", " + nontemplateItemDetails;
            }
        }
        
        return summaryDetails;
    }
}