/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            entity.setImportIsTemplate(itemName.contains("{"));
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
    
    @Override
    protected List<InputColumnModel> initializeInputColumns_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        List<InputColumnModel> cols = new ArrayList<>();
        
        for (int i = 0 ; i < actualColumnCount ; ++i) {
            cols.add(new InputColumnModel(i, "Name " + i, false, "Machine design hierarchy column " + i));
        }
        
        return cols;
    }
    
    @Override
    protected List<InputHandler> initializeInputHandlers_(
            int actualColumnCount, 
            Map<Integer, String> headerValueMap) {
        
        List<InputHandler> specs = new ArrayList<>();
        
        specs.add(new NameHandler(0, actualColumnCount - 1, 128));
            
        return specs;
    }
    
    @Override
    protected List<OutputColumnModel> initializeTableViewColumns_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        List<OutputColumnModel> columns = new ArrayList<>();
        
        columns.add(new OutputColumnModel("Name", "name"));
        columns.add(new OutputColumnModel("Is Template", "importIsTemplateString"));
        columns.add(new OutputColumnModel("Container Id", "importContainerString"));
        columns.add(new OutputColumnModel("Parent Path", "importPath"));
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
        return false;
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
        
        ImportInfo itemInfo = itemInfoMap.get(item);
        if (itemInfo == null) {
            String msg = "helper error for " + item.getName();
            LOGGER.debug(methodLogName + msg);
        }
        
        int itemIndentLevel = itemInfo.indentLevel;
        
        // find parent for this item
        ItemDomainMachineDesign itemParent = null;
        if (itemIndentLevel > 1) {
            // indent level 1 is top-level, so parent is null
            
            // find parent at previous indent level
            itemParent = parentIndentMap.get(itemIndentLevel - 1);
            if (itemParent != null) {
                System.out.println("parent for: " + item.getName() + " is: " + itemParent.getName());
            }
        }        
         
        // set current item as last parent at its indent level
        parentIndentMap.put(itemIndentLevel, item);
        
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