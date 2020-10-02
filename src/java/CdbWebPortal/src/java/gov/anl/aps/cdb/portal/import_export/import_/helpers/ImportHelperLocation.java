/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemTypeController;
import gov.anl.aps.cdb.portal.controllers.UserGroupController;
import gov.anl.aps.cdb.portal.controllers.UserInfoController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InputColumnInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.InputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.OutputColumnModel;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.HierarchyHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.IntegerInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.RefInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.StringInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
public class ImportHelperLocation 
        extends HierarchicalImportHelperBase<ItemDomainLocation, ItemDomainLocationController> {
    
    private static final Logger LOGGER = LogManager.getLogger(ImportHelperLocation.class.getName());
    
    private static final String KEY_NAME = "name";
    private static final String KEY_INDENT = "indentLevel";
    private static final String KEY_PARENT = "importParentItem";
    private static final String KEY_QR = "qrId";
    private static final String KEY_TYPE = "itemTypeString";
    private static final String KEY_SORT_ORDER = "importSortOrder";
    
    private static final String HEADER_PARENT = "Parent Location ID";
    private static final String HEADER_BASE_LEVEL = "Level";
    private static final String HEADER_QR = "QR ID";
    private static final String HEADER_TYPE = "Type ID";
    private static final String HEADER_DESCRIPTION = "Description";
    private static final String HEADER_SORT_ORDER = "Sort Order";
    private static final String HEADER_USER = "Owner User";
    private static final String HEADER_GROUP = "Owner Group";
    
    private Map<String, InputColumnInfo> columnInfoMap = null;
    private Map<Integer, ItemDomainLocation> parentIndentMap = new HashMap<>();
    private Map<Integer, Integer> childCountMap = new HashMap<>();
    
    private int itemCount = 0;
    
    private void initColumnInfoMap() {
        
        columnInfoMap = new HashMap<>();
        
        columnInfoMap.put(HEADER_PARENT, new InputColumnInfo(
                HEADER_PARENT, 
                false, 
                "CDB ID or name of parent location item.  Can only be provided for level 0 item. Name must be unique and prefixed with '#'."));
        
        columnInfoMap.put(HEADER_BASE_LEVEL, new InputColumnInfo(
                HEADER_BASE_LEVEL, 
                false, 
                "Name hierarchy column."));
        
        columnInfoMap.put(HEADER_QR, new InputColumnInfo(
                HEADER_QR, 
                false, 
                "QR ID of location (9 digit number)."));
        
        columnInfoMap.put(HEADER_TYPE, new InputColumnInfo(
                HEADER_TYPE, 
                false, 
                "CDB ID or name of location type. Name must be prefixed with '#'."));
        
        columnInfoMap.put(HEADER_DESCRIPTION, new InputColumnInfo(
                HEADER_DESCRIPTION, 
                false, 
                "Textual description of location."));
        
        columnInfoMap.put(HEADER_SORT_ORDER, new InputColumnInfo(
                HEADER_SORT_ORDER, 
                false, 
                "Sort order within parent item."));
        
        columnInfoMap.put(HEADER_USER, new InputColumnInfo(
                HEADER_USER, 
                false, 
                "CDB ID or name of owner user. Name must be unique and prefixed with '#'."));
        
        columnInfoMap.put(HEADER_GROUP, new InputColumnInfo(
                HEADER_GROUP, 
                false, 
                "CDB ID or name of owner group. Name must be unique and prefixed with '#'."));
        
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
        
        InputColumnInfo colInfo = getColumnInfoMap().get(HEADER_PARENT);
        inputColumns.add(new InputColumnModel(0, HEADER_PARENT, colInfo.isRequired, colInfo.description));
        
        colInfo = getColumnInfoMap().get(HEADER_BASE_LEVEL);
        inputColumns.add(new InputColumnModel(1, HEADER_BASE_LEVEL + " 0", true, colInfo.description + " 0"));
        inputColumns.add(new InputColumnModel(2, HEADER_BASE_LEVEL + " 1", false, colInfo.description + " 1"));
        inputColumns.add(new InputColumnModel(3, HEADER_BASE_LEVEL + " 2", false, colInfo.description + " 2"));
        
        colInfo = getColumnInfoMap().get(HEADER_QR);
        inputColumns.add(new InputColumnModel(4, HEADER_QR, colInfo.isRequired, colInfo.description));
        
        colInfo = getColumnInfoMap().get(HEADER_TYPE);
        inputColumns.add(new InputColumnModel(5, HEADER_TYPE, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_DESCRIPTION);
        inputColumns.add(new InputColumnModel(6, HEADER_DESCRIPTION, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_SORT_ORDER);
        inputColumns.add(new InputColumnModel(7, HEADER_SORT_ORDER, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_USER);
        inputColumns.add(new InputColumnModel(8, HEADER_USER, colInfo.isRequired, colInfo.description));

        colInfo = getColumnInfoMap().get(HEADER_GROUP);
        inputColumns.add(new InputColumnModel(9, HEADER_GROUP, colInfo.isRequired, colInfo.description));

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
        
        for (Map.Entry<Integer, String> entry : headerValueMap.entrySet()) {
            
            int columnIndex = entry.getKey();
            String columnHeader = entry.getValue().trim();
            
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

                    case HEADER_PARENT:
                        colInfo = getColumnInfoMap().get(HEADER_PARENT);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        inputHandlers.add(new RefInputHandler(columnIndex, HEADER_PARENT, KEY_PARENT, "setImportParentItem", ItemDomainLocationController.getInstance(), ItemDomainLocation.class, "", false, true));
                        break;

                    case HEADER_QR:
                        colInfo = getColumnInfoMap().get(HEADER_QR);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        inputHandlers.add(new IntegerInputHandler(columnIndex, HEADER_QR, KEY_QR, "setQrId"));
                        break;

                    case HEADER_TYPE:
                        colInfo = getColumnInfoMap().get(HEADER_TYPE);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        inputHandlers.add(new RefInputHandler(columnIndex, HEADER_TYPE, KEY_TYPE, "setItemType", ItemTypeController.getInstance(), ItemType.class, ItemDomainName.location.getValue(), false, true));
                        break;

                    case HEADER_DESCRIPTION:
                        colInfo = getColumnInfoMap().get(HEADER_DESCRIPTION);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        inputHandlers.add(new StringInputHandler(columnIndex, HEADER_DESCRIPTION, "description", "setDescription", 256));
                        break;

                    case HEADER_SORT_ORDER:
                        colInfo = getColumnInfoMap().get(HEADER_SORT_ORDER);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        inputHandlers.add(new IntegerInputHandler(columnIndex, HEADER_SORT_ORDER, KEY_SORT_ORDER, "setImportSortOrder"));
                        break;

                    case HEADER_USER:
                        colInfo = getColumnInfoMap().get(HEADER_USER);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        inputHandlers.add(new RefInputHandler(columnIndex, HEADER_USER, "ownerUserName", "setOwnerUser", UserInfoController.getInstance(), UserInfo.class, "", false, true));
                        break;

                    case HEADER_GROUP:
                        colInfo = getColumnInfoMap().get(HEADER_GROUP);
                        inputColumns.add(new InputColumnModel(columnIndex, columnHeader, colInfo.isRequired, colInfo.description));
                        inputHandlers.add(new RefInputHandler(columnIndex, HEADER_GROUP, "ownerUserGroupName", "setOwnerUserGroup", UserGroupController.getInstance(), UserGroup.class, "", false, true));
                        break;

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
        outputColumns.add(new OutputColumnModel(0, "Parent Item", "importParentItemString"));
        outputColumns.add(new OutputColumnModel(1, "Parent Path", "importPath"));
        outputColumns.add(new OutputColumnModel(2, "Name", "name"));
        outputColumns.add(new OutputColumnModel(3, "QR", "qrId"));
        outputColumns.add(new OutputColumnModel(4, "Type", "itemTypeString"));
        outputColumns.add(new OutputColumnModel(5, "Description", "description"));
        outputColumns.add(new OutputColumnModel(6, "Sort Order", "importSortOrder"));
        outputColumns.add(new OutputColumnModel(7, "Owner User", "ownerUserName"));
        outputColumns.add(new OutputColumnModel(8, "Owner Group", "ownerUserGroupName"));
        
        return new ValidInfo(isValid, validString);
    }
    
    @Override
    public ItemDomainLocationController getEntityController() {
        return ItemDomainLocationController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Location Template";
    }
    
    @Override
    protected void reset_() {
        columnInfoMap = null;
        parentIndentMap = new HashMap<>();
        childCountMap = new HashMap<>();
        itemCount = 0;
    }
    
    @Override
    protected ItemDomainLocation getItemParent(ItemDomainLocation item) {
        return null;
    }
    
    @Override
    protected String getItemName(ItemDomainLocation item) {
        return item.getName();
    }
    
    @Override
    protected List<ItemDomainLocation> getItemChildren(ItemDomainLocation item) {
        List<ItemElement> children = item.getItemElementDisplayList();
        return children.stream()
                .map((child) -> (ItemDomainLocation) child.getContainedItem())
                .collect(Collectors.toList());
    }
            
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        String methodLogName = "createEntityInstance() ";
        boolean isValid = true;
        String validString = "";

        ItemDomainLocation item = null;
        ItemDomainLocation itemParent = null;

        item = getEntityController().createEntityInstance();

        item.setName((String) rowMap.get(KEY_NAME));
        if ((item.getName() == null) || (item.getName().isEmpty())) {
            // didn't find a non-empty name column for this row
            isValid = false;
            validString = "name columns are all empty";
            LOGGER.info(methodLogName + validString);
            return new CreateInfo(item, isValid, validString);
        }

        // find parent for this item
        
        if (!rowMap.containsKey(KEY_INDENT)) {
            // return because we need this value to continue
            isValid = false;
            validString = "missing indent level map entry";
            LOGGER.info(methodLogName + validString);
            return new CreateInfo(item, isValid, validString);
        }        
        int itemIndentLevel = (int) rowMap.get(KEY_INDENT);
        
        int defaultSortOrder = 1; // default sort order for top-level item
        
        ItemDomainLocation itemContainer
                = (ItemDomainLocation) rowMap.get(KEY_PARENT);
        
        if (itemIndentLevel > 1) {

            // not allowed to specify parent for non level 0 item
            if (itemContainer != null) {
                String msg = "Can only specify existing parent for level 0 item";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
            }

            // find parent at previous indent level
            itemParent = parentIndentMap.get(itemIndentLevel - 1);
            
            // calculate sort order
            Integer numSiblings = childCountMap.get(itemIndentLevel - 1);
            if (numSiblings == null) {
                numSiblings = 0;
            }
            defaultSortOrder = numSiblings + 1;
            childCountMap.put(itemIndentLevel - 1, defaultSortOrder);

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
                ItemDomainLocation pathItem = parentIndentMap.get(i);
                path = path + pathItem.getName() + "/ ";
            }
            item.setImportPath(path);

        } else {
            // this is either a top-level item, or a parent item is explicitly specified for it
            
            if (itemContainer == null) {
                // new item is a top-level item with no parent
                String msg = "creating top-level item: " + item.getName();
                LOGGER.debug(methodLogName + msg);
                itemParent = null;
            } else {
                itemParent = itemContainer;
            }
        }
        
        itemCount = itemCount + 1;

        UserInfo user = (UserInfo) rowMap.get(KEY_USER);
        UserGroup group = (UserGroup) rowMap.get(KEY_GROUP);
        Integer intSortOrder = (Integer)rowMap.get(KEY_SORT_ORDER);
        if (intSortOrder == null) {
            intSortOrder = defaultSortOrder;
            item.setImportSortOrder(defaultSortOrder);
        }
        Float sortOrder = (intSortOrder).floatValue();
        String childItemElementName = String.valueOf(intSortOrder);

        if (itemParent != null) {
            
            // validate name and sort order are unique for specified parent item
            List<ItemElement> ieList = itemParent.getFullItemElementList();
            for (ItemElement ie : ieList) {
                if ((ie.getSortOrder() != null) && (ie.getSortOrder().equals(sortOrder))) {
                    String msg = "duplicate sort order for existing child location";
                    LOGGER.info(methodLogName + msg);
                    validString = appendToString(validString, msg);
                    isValid = false;
                }
                if ((ie.getName() != null) && (ie.getName().equals(childItemElementName))) {
                    String msg = "duplicate item element name for existing child location";
                    LOGGER.info(methodLogName + msg);
                    validString = appendToString(validString, msg);
                    isValid = false;
                }
            }
            
            item.setImportChildParentRelationship(
                    itemParent, childItemElementName, sortOrder, user, group);
        }
        
        // set current item as last parent at its indent level
        parentIndentMap.put(itemIndentLevel, item);
        
        // reset child count map for current level
        childCountMap.put(itemIndentLevel, 0);

        // update tree view with item and parent
        updateTreeView(item, itemParent, false);

        return new CreateInfo(item, isValid, validString);
    }

    protected String getCustomSummaryDetails() {
        
        String summaryDetails = "";
        
        if (itemCount > 0) {
            String nontemplateItemDetails = itemCount + " regular items";
            if (summaryDetails.isEmpty()) {
                summaryDetails = nontemplateItemDetails;
            } else {
                summaryDetails = summaryDetails + ", " + nontemplateItemDetails;
            }
        }
        
        return summaryDetails;
    }
}
