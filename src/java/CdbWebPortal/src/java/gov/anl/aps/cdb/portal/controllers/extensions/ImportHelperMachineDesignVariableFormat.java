/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
    protected List<InputColumnModel> initializeInputColumns_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        List<InputColumnModel> cols = new ArrayList<>();
        
        // TODO: use actualColumnCount and headerValueMap to set up input columns
        // column indexes might change if path/name columns move to right side of sheet
        
        for (int i = 0 ; i < 4/*actualColumnCount*/ ; ++i) {
            cols.add(new InputColumnModel(i, "Name " + i, false, "Machine design hierarchy column " + i));
        }
        cols.add(new InputColumnModel(4, "Alt Name", false, "Alternate machine design item name."));
        cols.add(new InputColumnModel(5, "Description", false, "Textual description of machine design item."));
        cols.add(new InputColumnModel(6, "Assigned Catalog Item Id", false, "Numeric ID of assigned catalog item."));
        cols.add(new InputColumnModel(7, "Assigned Inventory Item Id", false, "Numeric ID of assigned inventory item."));
        cols.add(new InputColumnModel(8, "Location", false, "Numeric Id or name of CDB location item, for top-level non-template items only."));
        
        return cols;
    }
    
    @Override
    protected List<InputHandler> initializeInputHandlers_(
            int actualColumnCount, 
            Map<Integer, String> headerValueMap) {
        
        List<InputHandler> specs = new ArrayList<>();
        
        // TODO: rework once format is established, column indexes might change
        // if path/name columns move to right side of sheet
        
        specs.add(new NameHandler(0, 3/*actualColumnCount - 1*/, 128));
        specs.add(new ImportHelperBase.StringInputHandler(4, "setAlternateName", 32));
        specs.add(new ImportHelperBase.StringInputHandler(5, "setDescription", 256));
        specs.add(new ImportHelperBase.IdOrNameRefInputHandler(6, "setImportAssignedCatalogItem", ItemDomainCatalogController.getInstance(), ItemDomainCatalog.class, null));
        specs.add(new ImportHelperBase.IdRefInputHandler(7, "setImportAssignedInventoryItem", ItemDomainInventoryController.getInstance(), ItemDomainInventory.class));
        specs.add(new ImportHelperBase.IdOrNameRefInputHandler(8, "setImportLocationItem", ItemDomainLocationController.getInstance(), ItemDomainLocation.class, null));

            
        return specs;
    }
    
    @Override
    protected List<OutputColumnModel> initializeTableViewColumns_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        List<OutputColumnModel> columns = new ArrayList<>();
        
        columns.add(new OutputColumnModel("Name", "name"));
        columns.add(new OutputColumnModel("Is Template", "importIsTemplateString"));
        columns.add(new OutputColumnModel("Project", "itemProjectString"));
        columns.add(new OutputColumnModel("Alt Name", "alternateName"));
        columns.add(new OutputColumnModel("Description", "description"));
        columns.add(new OutputColumnModel("Assigned Catalog Item Id", "importAssignedCatalogItemString"));
        columns.add(new OutputColumnModel("Assigned Inventory Item Id", "importAssignedInventoryItemString"));
        columns.add(new OutputColumnModel("Location", "importLocationItemString"));

        return columns;
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
            String msg = "helper error for " + item.getName();
            LOGGER.debug(methodLogName + msg);
        }
        
        // TODO: don't :-)
        // set hardwired project for now
        item.setProjectValue(getProjectItem());
        
        // TODO: confirm approach for "is template"
        item.setImportIsTemplate(item.getName().contains("{"));
        
        int itemIndentLevel = itemInfo.indentLevel;
        
        // find parent for this item
        ItemDomainMachineDesign itemParent = null;
        if (itemIndentLevel > 1) {
            // indent level 1 is top-level, so parent is null
            
            // find parent at previous indent level
            itemParent = parentIndentMap.get(itemIndentLevel - 1);
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
        
        // establish parent/child relationship, set location info etc
        item.applyImportValues(itemParent, isValidAssignedItem, isValidLocation);
                 
        // set current item as last parent at its indent level
        parentIndentMap.put(itemIndentLevel, item);
        
        // update tree view with item and parent
        updateTreeView(item, itemParent);
        
        // add entry to name map for new item
        itemByNameMap.put(item.getName(), item);
               
        return new ValidInfo(true, "");
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