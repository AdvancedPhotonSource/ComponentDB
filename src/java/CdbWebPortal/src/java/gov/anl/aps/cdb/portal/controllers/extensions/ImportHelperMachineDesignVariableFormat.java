/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author craig
 */
public class ImportHelperMachineDesignVariableFormat extends ImportHelperBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {
    
    public class NameHandler extends ColumnRangeInputHandler {
        
        protected int maxLength = 0;
        
        public NameHandler(int firstIndex, int lastIndex, int maxLength) {
            super(firstIndex, lastIndex);
            this.maxLength = maxLength;
        }
        
        public int getMaxLength() {
            return maxLength;
        }

        public ValidInfo handleInput(
                Row row,
                Map<Integer, String> cellValueMap,
                ItemDomainMachineDesign entity) {
            
            boolean isValid = true;
            String validString = "";
            
            int currentIndentLevel = 1;
            int itemIndentLevel = 0;
            String itemName = null;
            for (int colIndex = getFirstColumnIndex();
                    colIndex <= getLastColumnIndex();
                    colIndex++) {
                
                String parsedValue = cellValueMap.get(colIndex);
                if ((parsedValue != null) && (!parsedValue.isEmpty())) {
                    if (itemName != null) {
                        // invalid, we have a value in 2 columns
                        isValid = false;
                        validString = "found name value in multiple columns";
                        return new ValidInfo(isValid, validString);
                    } else {
                        itemName = parsedValue;
                        itemIndentLevel = currentIndentLevel;
                    }
                }
                
                currentIndentLevel = currentIndentLevel + 1;                
            }
            
            if (itemName == null) {
                // didn't find a non-empty name column for this row
                isValid = false;
                validString = "name columns are all empty";
                return new ValidInfo(isValid, validString);
            }
            
            // check column length is valid
            if ((getMaxLength() > 0) && (itemName.length() > getMaxLength())) {
                isValid = false;
                validString = appendToString(validString, 
                        "Invalid name, length exceeds " + getMaxLength());
                return new ValidInfo(isValid, validString);
            }
            
            // set item info
            entity.setName(itemName);
            ImportInfo itemInfo = new ImportInfo();
            itemInfo.indentLevel = itemIndentLevel;
            itemInfoMap.put(entity, itemInfo);
                
            return new ValidInfo(isValid, validString);
        }
    }
    
    public class ImportInfo {
        
        public int indentLevel;
        
    }
    

    private static final Logger LOGGER = LogManager.getLogger(ImportHelperMachineDesignVariableFormat.class.getName());

    protected static String completionUrlValue = "/views/itemDomainMachineDesign/list?faces-redirect=true";
    
    protected Map<String, ItemDomainMachineDesign> itemByNameMap = new HashMap<>();
    protected Map<String, TreeNode> treeNodeMap = new HashMap<>();
    protected Map<ItemDomainMachineDesign, ImportInfo> itemInfoMap = new HashMap<>();
    protected Map<Integer, ItemDomainMachineDesign> parentIndentMap = new HashMap<>();
    
    private ItemProject projectItem = null;
    
    private ItemProject getProjectItem() {
        if (projectItem == null) {
            try {
                projectItem = ItemProjectController.getInstance().findUniqueByName("APS-U Production", "");
            } catch (CdbException ex) {
                LOGGER.error("getProjectItem() exception retriveing project: " + ex);
            }
        }
        return projectItem;
    }
    
    @Override
    protected InitializeInfo initialize_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        boolean isValid = true;
        String validString = "";
        
        List<InputColumnModel> inputColumns = new ArrayList<>();
        List<InputHandler> handlers = new ArrayList<>();
        List<OutputColumnModel> outputColumns = new ArrayList<>();
                
        boolean foundLevel = false;
        int firstLevelIndex = -1;
        int lastLevelIndex = -1;
        
        for (Entry<Integer, String> entry : headerValueMap.entrySet()) {
            
            int columnIndex = entry.getKey();
            String columnHeader = entry.getValue();
            
            // check to see if this is a "level" column
            if (columnHeader.startsWith("Level")) {
                inputColumns.add(new InputColumnModel(columnIndex, columnHeader, false, "Machine design hierarchy column " + columnHeader));
                foundLevel = true;
                if (firstLevelIndex == -1) {
                    firstLevelIndex = columnIndex;
                }
                lastLevelIndex = columnIndex;
            }
            
            switch (columnHeader) {
                
                case "Parent ID":
                    inputColumns.add(new InputColumnModel(columnIndex, columnHeader, false, "CDB ID of parent machine design item."));
                    handlers.add(new IdRefInputHandler(columnIndex, "setImportContainerItem", ItemDomainMachineDesignController.getInstance(), ItemDomainMachineDesign.class));
                    break;
                    
                case "Machine Design Alternate Name":
                    inputColumns.add(new InputColumnModel(columnIndex, columnHeader, false, "Alternate machine design item name."));
                    handlers.add(new StringInputHandler(columnIndex, "setAlternateName", 32));
                    break;
                    
                case "Machine Design Item Description":
                    inputColumns.add(new InputColumnModel(columnIndex, columnHeader, false, "Textual description of machine design item."));
                    handlers.add(new ImportHelperBase.StringInputHandler(columnIndex, "setDescription", 256));
                    break;
                    
                case "Assigned Catalog/Inventory Item":
                    inputColumns.add(new InputColumnModel(columnIndex, columnHeader, false, "CDB ID of assigned catalog or inventory item."));
                    // TODO: one handler for both catalog and inventory, setter method, etc - custom handler?
                    handlers.add(new ImportHelperBase.IdOrNameRefInputHandler(columnIndex, "setImportAssignedCatalogItem", ItemDomainCatalogController.getInstance(), ItemDomainCatalog.class, null));
                    break;
                    
                case "Location":
                    inputColumns.add(new InputColumnModel(columnIndex, columnHeader, false, "CDB ID or name of CDB location item."));
                    // TODO: custom handler to ignore "parent" etc?
                    handlers.add(new ImportHelperBase.IdOrNameRefInputHandler(8, "setImportLocationItem", ItemDomainLocationController.getInstance(), ItemDomainLocation.class, null));
                    break;
                    
                case "Project ID":
                    inputColumns.add(new InputColumnModel(columnIndex, columnHeader, false, "CDB ID or name of item project."));
                    // TODO: handler for project id
                    break;
                    
                case "Is Template?":
                    inputColumns.add(new InputColumnModel(columnIndex, columnHeader, false, "TRUE if item is template, false otherwise."));
                    handlers.add(new BooleanInputHandler(columnIndex, "setImportIsTemplate"));
                    break;
                    
                default:
                    // TODO: unexpected column, complain
            }
        }
        
        // add handler for multiple "level" columns
        handlers.add(new NameHandler(firstLevelIndex, lastLevelIndex, 128));
        
        // output columns are fixed
        // TODO: what about parent item?
        outputColumns.add(new OutputColumnModel("Name", "name"));
        outputColumns.add(new OutputColumnModel("Is Template", "importIsTemplateString"));
        outputColumns.add(new OutputColumnModel("Project", "itemProjectString"));
        outputColumns.add(new OutputColumnModel("Alt Name", "alternateName"));
        outputColumns.add(new OutputColumnModel("Description", "description"));
        outputColumns.add(new OutputColumnModel("Parent Item", "importContainerString"));
        outputColumns.add(new OutputColumnModel("Assigned Catalog Item Id", "importAssignedCatalogItemString"));
        outputColumns.add(new OutputColumnModel("Assigned Inventory Item Id", "importAssignedInventoryItemString"));
        outputColumns.add(new OutputColumnModel("Location", "importLocationItemString"));
        
        ValidInfo validInfo = new ValidInfo(isValid, validString);
        InitializeInfo initInfo = new InitializeInfo(inputColumns, handlers, outputColumns, validInfo);
                
        return initInfo;
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public ItemDomainMachineDesignController getEntityController() {
        return ItemDomainMachineDesignController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Machine Design Template";
    }
    
    @Override
    protected ItemDomainMachineDesign createEntityInstance() {
        return getEntityController().createEntityInstance();
    }
    
    @Override
    protected void reset_() {
        itemByNameMap = new HashMap<>();
        treeNodeMap = new HashMap<>();
    }
    
    /**
     * Specifies whether the subclass will provide a tree view.  Default is false,
     * subclass should override to customize.
     */
    public boolean hasTreeView() {
        return true;
    }

    /**
     * Set parent/child relationships between items, and create new item at
     * specified path within container item.  The path contains the names of 
     * the parent nodes between the new item and the container item, separated by
     * Unix style slashes.
     */
    @Override
    protected ImportHelperBase.ValidInfo postParseRow(ItemDomainMachineDesign item) {

        String methodLogName = "postParseRow() ";

        boolean isValid = true;
        String validString = "";
        
        boolean isValidLocation = true;
        boolean isValidAssignedItem = true;
        
        ImportInfo itemInfo = itemInfoMap.get(item);
        if (itemInfo == null) {
            String msg = "No ImportInfo found for item";
            LOGGER.info(methodLogName + msg);
            validString = appendToString(validString, msg);
            isValid = false;
        }
        
        // TODO: don't :-)
        // set hardwired project for now
        item.setProjectValue(getProjectItem());
                
        // find parent for this item
        int itemIndentLevel = itemInfo.indentLevel;
        ItemDomainMachineDesign itemParent = null;
        if (itemIndentLevel > 1) {
            
            // not allowed to specify parent for non level 0 item
            if (item.getImportContainerItem() != null) {
                String msg = "Can only specify existing parent for level 0 item";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
            }
            
            // find parent at previous indent level
            itemParent = parentIndentMap.get(itemIndentLevel - 1);
            
        } else {
            // this is either a top-level item, or a parent item is explicitly specified for it
            ItemDomainMachineDesign container = item.getImportContainerItem();
            if (container == null) {
                // new item is a top-level machine design with no parent
                String msg = "creating top-level machine design item: " + item.getName();
                LOGGER.debug(methodLogName + msg);
                itemParent = null;
            } else {
                itemParent = container;
            }
        }
        
        // check for template restrictions
        if (item.getIsItemTemplate()) {
            
            if ((item.getImportAssignedInventoryItem() != null)) {
                
                // template not allowed to have assigned inventory
                String msg = "Template cannot have assigned inventory item";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
                isValidAssignedItem = false;
            }
            
            if (item.getImportLocationItem() != null) {
                // template not allowed to have location
                String msg = "Template cannot have location item";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
                isValidLocation = false;
            }
            
        } else {
            if (itemParent != null) {
                
                if (item.getImportLocationItem() != null) {
                    // only top-level non-template item can have location
                    String msg = "Only top-level item can have location";
                    LOGGER.info(methodLogName + msg);
                    validString = appendToString(validString, msg);
                    isValid = false;
                    isValidLocation = false;
                }
                
            } else {
            
                if ((item.getImportAssignedCatalogItem() != null)
                        || (item.getImportAssignedInventoryItem() != null)) {
                    // top-level item cannot have assigned item
                    String msg = "Top-level item cannot have assigned catalog/inventory item";
                    LOGGER.info(methodLogName + msg);
                    validString = appendToString(validString, msg);
                    isValid = false;
                    isValidAssignedItem = false;
                }
            }
        }
        
        if (itemParent != null) {

            if (!Objects.equals(item.getIsItemTemplate(), itemParent.getIsItemTemplate())) {
                // parent and child must both be templates or both not be
                String msg = "parent and child must both be templates or both not be templates";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
            }
        }
                                
        // establish parent/child relationship, set location info etc
        item.applyImportValues(itemParent, isValidAssignedItem, isValidLocation);
                 
        // set current item as last parent at its indent level
        parentIndentMap.put(itemIndentLevel, item);
        
        // update tree view with item and parent
        updateTreeView(item, itemParent);
        
        // add entry to name map for new item
        itemByNameMap.put(item.getName(), item);
               
        return new ValidInfo(isValid, validString);
    }

    protected void updateTreeView(ItemDomainMachineDesign item, 
            ItemDomainMachineDesign parent) {
        
        TreeNode itemNode = new DefaultTreeNode(item);
        itemNode.setExpanded(true);
        treeNodeMap.put(item.getName(), itemNode);
        
        if (parent != null) {
            TreeNode parentNode = treeNodeMap.get(parent.getName());
            if (parentNode != null) {
                // parent tree node already exists so add child to it
                parentNode.getChildren().add(itemNode);
                
            } else {
                // parent tree node doesn't exist, so create new tree nodes for
                // parent and its ancestors, and add child to parent
                parentNode = new DefaultTreeNode(parent);
                parentNode.setExpanded(true);
                parentNode.getChildren().add(itemNode);
                treeNodeMap.put(parent.getName(), parentNode);
                ItemDomainMachineDesign ancestor = parent.getParentMachineDesign();
                TreeNode childNode = parentNode;
                while (ancestor != null) {
                    TreeNode ancestorNode = new DefaultTreeNode(ancestor);
                    ancestorNode.setExpanded(true);
                    treeNodeMap.put(ancestor.getName(), ancestorNode);
                    ancestorNode.getChildren().add(childNode);
                    ancestor = ancestor.getParentMachineDesign();
                    childNode = ancestorNode;
                }
                rootTreeNode.getChildren().add(childNode);
            }
            
        } else {
            // top level machine design item, so add to root tree node
            rootTreeNode.getChildren().add(itemNode);
        }
        
    }
}