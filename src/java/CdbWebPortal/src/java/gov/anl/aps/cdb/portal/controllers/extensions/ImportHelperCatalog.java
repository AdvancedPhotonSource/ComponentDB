/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
public class ImportHelperCatalog extends 
        HierarchicalImportHelperBase<ItemDomainCatalog, ItemDomainCatalogController> {

    private static final Logger LOGGER = LogManager.getLogger(ImportHelperCatalog.class.getName());
    
    private static final String KEY_NAME = "name";
    private static final String KEY_INDENT = "indentLevel";
    private static final String KEY_ASSIGNED_ITEM = "assignedItem";
    
    private static final String HEADER_BASE_LEVEL = "Level";
    private static final String HEADER_ALT_NAME = "Machine Design Alternate Name";
    private static final String HEADER_DESCRIPTION = "Machine Design Item Description";
    private static final String HEADER_ASSIGNED_ITEM = "Assigned Catalog/Inventory Item";
    private static final String HEADER_ASSIGNED_ITEM_ID = "Assigned Catalog/Inventory Item ID";
    private static final String HEADER_PROJECT = "Project ID";

    protected static String completionUrlValue = "/views/itemDomainCatalog/list?faces-redirect=true";

    private Map<String, InputColumnInfo> columnInfoMap = null;
    private Map<Integer, ItemDomainCatalog> parentIndentMap = new HashMap<>();

    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public ItemDomainCatalogController getEntityController() {
        return ItemDomainCatalogController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Catalog and Assemby Template";
    }
    
    @Override
    protected void reset_() {
        super.reset_();
        columnInfoMap = null;
        parentIndentMap = new HashMap<>();
    }
    
    private void initColumnInfoMap() {
        
        columnInfoMap = new HashMap<>();
        
        columnInfoMap.put(HEADER_BASE_LEVEL, new InputColumnInfo(
                HEADER_BASE_LEVEL, 
                false, 
                "Assembly hierarchy column level"));
        
        columnInfoMap.put(HEADER_ALT_NAME, new InputColumnInfo(
                HEADER_ALT_NAME, 
                false, 
                "Alternate item name."));
        
        columnInfoMap.put(HEADER_DESCRIPTION, new InputColumnInfo(
                HEADER_DESCRIPTION, 
                false, 
                "Textual description of item."));
        
        columnInfoMap.put(HEADER_ASSIGNED_ITEM, new InputColumnInfo(
                HEADER_ASSIGNED_ITEM, 
                false, 
                "Name of assigned catalog item (optional, for reference only)."));
        
        columnInfoMap.put(HEADER_ASSIGNED_ITEM_ID, new InputColumnInfo(
                HEADER_ASSIGNED_ITEM_ID, 
                false, 
                "CDB ID of assigned catalog item."));
        
        columnInfoMap.put(HEADER_PROJECT, new InputColumnInfo(
                HEADER_PROJECT, 
                true, 
                "CDB ID or name of item project."));
        
    }
    
    private Map<String, InputColumnInfo> getColumnInfoMap() {
        if (columnInfoMap == null) {
            initColumnInfoMap();
        }
        return columnInfoMap;
    }
    
    /**
     * Returns list of columns for download template file feature.  Because
     * initialize_() creates the columns dynamically after the spreadsheet file
     * is opened and read, the sample list of columns is hardwired here.
     * The number of "Level" columns is variable, and depends on the levels of
     * hierarchy to be added for a particular import.  Updates to input columns
     * must unfortunately be made in two places, here and in initialize_().
     */
    @Override
    protected List<InputColumnModel> getTemplateColumns() {
        
        List<InputColumnModel> inputColumns = new ArrayList<>();
        
        InputColumnInfo colInfo = getColumnInfoMap().get(HEADER_BASE_LEVEL);
        inputColumns.add(new InputColumnModel(0, HEADER_BASE_LEVEL + " 0", true, colInfo.description + " 0"));
        inputColumns.add(new InputColumnModel(1, HEADER_BASE_LEVEL + " 1", false, colInfo.description + " 1"));
        inputColumns.add(new InputColumnModel(2, HEADER_BASE_LEVEL + " 2", false, colInfo.description + " 2"));
        
        colInfo = getColumnInfoMap().get(HEADER_ALT_NAME);
        inputColumns.add(new InputColumnModel(3, HEADER_ALT_NAME, colInfo.isRequired, colInfo.description));
        
        colInfo = getColumnInfoMap().get(HEADER_DESCRIPTION);
        inputColumns.add(new InputColumnModel(4, HEADER_DESCRIPTION, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_ASSIGNED_ITEM);
        inputColumns.add(new InputColumnModel(5, HEADER_ASSIGNED_ITEM, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_ASSIGNED_ITEM_ID);
        inputColumns.add(new InputColumnModel(6, HEADER_ASSIGNED_ITEM_ID, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_PROJECT);
        inputColumns.add(new InputColumnModel(7, HEADER_PROJECT, colInfo.isRequired, colInfo.description));

        return inputColumns;
    }
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        return new ArrayList<>();
    }
    
    /**
     * Builds the input columns, handlers, and output columns for the helper
     * framework.  Note that because the number of "Level" columns is variable
     * the column layout is not fixed as is the case for some of the other
     * helpers.  Changes to input columns, therefore, must be made in two places,
     * here and in getTemplateColumns().
     */
    @Override
    protected ValidInfo initialize_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap,
            List<InputColumnModel> inputColumns,
            List<InputHandler> inputHandlers,
            List<OutputColumnModel> outputColumns) {
        
        String methodLogName = "initialize_() ";

        boolean isValid = true;
        String validString = "";
        
        boolean foundLevel = false;
        int firstLevelIndex = -1;
        int lastLevelIndex = -1;
        
        InputColumnInfo colInfo;
        
        for (Entry<Integer, String> entry : headerValueMap.entrySet()) {
            
            int columnIndex = entry.getKey();
            String columnHeader = entry.getValue();
            
            // check to see if this is a "level" column
            if (columnHeader.startsWith(HEADER_BASE_LEVEL)) {
                colInfo = getColumnInfoMap().get(HEADER_BASE_LEVEL);
                inputColumns.add(new InputColumnModel(columnIndex, columnHeader, false, colInfo.description));
                foundLevel = true;
                if (firstLevelIndex == -1) {
                    firstLevelIndex = columnIndex;
                }
                lastLevelIndex = columnIndex;
                
            } else {
            
                switch (columnHeader) {

//                    case HEADER_ALT_NAME:
//                        colInfo = getColumnInfoMap().get(HEADER_ALT_NAME);
//                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
//                        inputHandlers.add(new StringInputHandler(columnIndex, "alternateName", "setAlternateName", 128));
//                        break;
//
//                    case HEADER_DESCRIPTION:
//                        colInfo = getColumnInfoMap().get(HEADER_DESCRIPTION);
//                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
//                        inputHandlers.add(new StringInputHandler(columnIndex, "description", "setDescription", 256));
//                        break;
//
//                    case HEADER_ASSIGNED_ITEM:
//                        colInfo = getColumnInfoMap().get(HEADER_ASSIGNED_ITEM);
//                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
//                        break;
//
                    case HEADER_ASSIGNED_ITEM_ID:
                        colInfo = getColumnInfoMap().get(HEADER_ASSIGNED_ITEM_ID);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        inputHandlers.add(new IdOrNameRefInputHandler(columnIndex, "assignedCatalogItem", "setAssignedCatalogItem", ItemDomainCatalogController.getInstance(), ItemDomainCatalog.class, ""));
                        break;

//                    case HEADER_PROJECT:
//                        colInfo = getColumnInfoMap().get(HEADER_PROJECT);
//                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
//                        inputHandlers.add(new IdOrNameRefInputHandler(columnIndex, "project", "setProject", ItemProjectController.getInstance(), ItemProject.class, ""));
//                        break;

                    default:
                        // unexpected column found, so fail
                        isValid = false;
                        String msg = "found unexpected column header: " + columnHeader;
                        validString = msg;
                        LOGGER.info(methodLogName + msg);
                }
            }
        }
        
        if (!foundLevel) {
            // didn't find any "Level" columns, so fail
            isValid = false;
            String msg = "one or more 'Level' columns is required";
            validString = msg;
            LOGGER.info(methodLogName + msg);
            
        } else {
            // add handler for multiple "level" columns
            inputHandlers.add(new HierarchyHandler(
                    firstLevelIndex, lastLevelIndex, 128, KEY_NAME, KEY_INDENT));
        }
        
        // output columns are fixed
        outputColumns.add(new OutputColumnModel(0, "Parent Path", "importPath"));
        outputColumns.add(new OutputColumnModel(1, "Name", "name"));
//        outputColumns.add(new OutputColumnModel(2, "Alt Name", "alternateName"));
//        outputColumns.add(new OutputColumnModel(3, "Description", "description"));
//        outputColumns.add(new OutputColumnModel(4, "Assigned Catalog Item", "importAssignedCatalogItemString"));
//        outputColumns.add(new OutputColumnModel(5, "Assigned Inventory Item", "importAssignedInventoryItemString"));
//        outputColumns.add(new OutputColumnModel(6, "Project", "itemProjectString"));
        
        return new ValidInfo(isValid, validString);
    }
    
    @Override
    protected ItemDomainCatalog getItemParent(ItemDomainCatalog item) {
        return null;
    }
    
    @Override
    protected String getItemName(ItemDomainCatalog item) {
        return item.getName();
    }
    
    @Override
    protected List<ItemDomainCatalog> getItemChildren(ItemDomainCatalog item) {
        List<ItemElement> children = item.getItemElementDisplayList();
        return children.stream()
                .map((child) -> (ItemDomainCatalog) child.getContainedItem())
                .collect(Collectors.toList());
    }
            
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        String methodLogName = "createEntityForRegularItem() ";
        boolean isValid = true;
        String validString = "";

        boolean isValidLocation = true;
        boolean isValidAssignedItem = true;

        ItemDomainCatalog item = null;
        ItemDomainCatalog itemParent = null;
        
        // check if there is an assigned item
        ItemDomainCatalog assignedCatalogItem = (ItemDomainCatalog) rowMap.get(KEY_ASSIGNED_ITEM);

        if (assignedCatalogItem == null) {
            item = getEntityController().createEntityInstance();
            item.setName((String) rowMap.get(KEY_NAME));
            if ((item.getName() == null) || (item.getName().isEmpty())) {
                // didn't find a non-empty name column for this row
                isValid = false;
                validString = "name columns are all empty";
                LOGGER.info(methodLogName + validString);
                return new CreateInfo(item, isValid, validString);
            }        
        } else {
            item = assignedCatalogItem;
        }
        
        // find parent item
        int itemIndentLevel = (int) rowMap.get(KEY_INDENT);
        if (itemIndentLevel > 1) {

            // find parent at previous indent level
            itemParent = parentIndentMap.get(itemIndentLevel - 1);

            if (itemParent == null) {
                // should have a parent for this item in map
                String msg = "Unable to determine parent for item";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
            }

            // create "parent path" to display item hierarchy in validation table
            String path = "";
            for (int i = 1; i < itemIndentLevel; i++) {
                ItemDomainCatalog pathItem = parentIndentMap.get(i);
                path = path + pathItem.getName() + "/ ";
            }
            item.setImportPath(path);

        } else {
            // this is a top-level item
            String msg = "creating top-level machine design item: " + item.getName();
            LOGGER.debug(methodLogName + msg);
            itemParent = null;
        }
        
        // set current item as last parent at its indent level
        parentIndentMap.put(itemIndentLevel, item);

        // update tree view with item and parent
        updateTreeView(item, itemParent, false);

        return new CreateInfo(item, isValid, validString);
    }

}
