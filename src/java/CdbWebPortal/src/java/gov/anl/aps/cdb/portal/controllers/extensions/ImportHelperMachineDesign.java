/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

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
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author craig
 */
public class ImportHelperMachineDesign extends ImportHelperBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {

    private static final Logger LOGGER = LogManager.getLogger(ImportHelperMachineDesign.class.getName());

    protected static String completionUrlValue = "/views/itemDomainMachineDesign/list?faces-redirect=true";
    
    protected Map<String, ItemDomainMachineDesign> itemByNameMap = new HashMap<>();
    protected Map<String, TreeNode> treeNodeMap = new HashMap<>();
    
    @Override
    protected List<InputColumnModel> initializeInputColumns_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        List<InputColumnModel> cols = new ArrayList<>();
        
        cols.add(new InputColumnModel(0, "Is Template", true, "Specifies whether this item is a template (true or false)."));
        cols.add(new InputColumnModel(1, "Container Id", false, "Numeric ID of CDB machine design item that contains this new machine design hierarchy."));
        cols.add(new InputColumnModel(2, "Parent Path", false, "Path to new machine design item within container (/ separated). If path is empty, new item will be created directly in specified container."));
        cols.add(new InputColumnModel(3, "Name", true, "Machine design item name."));
        cols.add(new InputColumnModel(4, "Project", true, "Numeric ID or name of CDB project."));
        cols.add(new InputColumnModel(5, "Alt Name", false, "Alternate machine design item name."));
        cols.add(new InputColumnModel(6, "Description", false, "Textual description of machine design item."));
        cols.add(new InputColumnModel(7, "Assigned Catalog Item Id", false, "Numeric ID of assigned catalog item."));
        cols.add(new InputColumnModel(8, "Assigned Inventory Item Id", false, "Numeric ID of assigned inventory item."));
        cols.add(new InputColumnModel(9, "Location", false, "Numeric Id or name of CDB location item, for top-level non-template items only."));

        return cols;
    }
    
    @Override
    protected List<InputHandler> initializeInputHandlers_(
            int actualColumnCount, 
            Map<Integer, String> headerValueMap) {
        
        List<InputHandler> specs = new ArrayList<>();
        
        specs.add(new ImportHelperBase.BooleanInputHandler(0, "setImportIsTemplate"));
        specs.add(new ImportHelperBase.IdRefInputHandler(1, "setImportContainerItem", ItemDomainMachineDesignController.getInstance(), ItemDomainMachineDesign.class));
        specs.add(new ImportHelperBase.StringInputHandler(2, "setImportPath", 700));
        specs.add(new ImportHelperBase.StringInputHandler(3, "setName", 128));
        specs.add(new ImportHelperBase.IdOrNameRefInputHandler(4, "setProjectValue", ItemProjectController.getInstance(), ItemProject.class, null));
        specs.add(new ImportHelperBase.StringInputHandler(5, "setAlternateName", 32));
        specs.add(new ImportHelperBase.StringInputHandler(6, "setDescription", 256));
        specs.add(new ImportHelperBase.IdOrNameRefInputHandler(7, "setImportAssignedCatalogItem", ItemDomainCatalogController.getInstance(), ItemDomainCatalog.class, null));
        specs.add(new ImportHelperBase.IdRefInputHandler(8, "setImportAssignedInventoryItem", ItemDomainInventoryController.getInstance(), ItemDomainInventory.class));
        specs.add(new ImportHelperBase.IdOrNameRefInputHandler(9, "setImportLocationItem", ItemDomainLocationController.getInstance(), ItemDomainLocation.class, null));

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
        return true;
    }

    /**
     * Set parent/child relationships between items, and create new item at
     * specified path within container item.  The path contains the names of 
     * the parent nodes between the new item and the container item, separated by
     * Unix style slashes.
     */
    @Override
    protected ValidInfo postParseRow(ItemDomainMachineDesign item) {
        
        String methodLogName = "postParseRow() ";
        ItemDomainMachineDesign parentItem = null;
        boolean isValid = true;
        boolean isValidLocation = true;
        boolean isValidAssignedItem = true;
        String validString = "";
        
        // find parent item
        String path = item.getImportPath();
        if ((path == null) || (path.isEmpty())) {
            // no path is specified, see if there is a container machine design item
            ItemDomainMachineDesign container = item.getImportContainerItem();
            if (container == null) {
                // new item is a top-level machine design with no parent
                String msg = "creating top-level machine design item: " + item.getName();
                LOGGER.debug(methodLogName + msg);
                parentItem = null;
            } else {
                parentItem = container;
            }
            
        } else {
            // path is specified
            String[] nodeNames = path.split("/");
            if (nodeNames.length == 0) {
                // path is invalid after tokenization
                String msg = "invalid path specified: " + path;
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
                
            } else {
                // path contains valid tokens, find parent whose name matches
                // last token
                String parentNodeName = nodeNames[nodeNames.length - 1].trim();
                parentItem = itemByNameMap.get(parentNodeName);
                if (parentItem == null) {
                    // no item with that name
                    String msg = "no parent with name: " + parentNodeName;
                    LOGGER.info(methodLogName + msg);
                    validString = appendToString(validString, msg);
                    isValid = false;
                }
            }
        }
        
        // check for template restrictions
        if (item.getIsItemTemplate()) {
            
            if ((item.getImportAssignedCatalogItem() != null) || 
                    (item.getImportAssignedInventoryItem() != null)) {
                // template not allowed to have assigned catalog/inventory
                String msg = "Template cannot have assigned catalog/inventory item";
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
            if (parentItem != null) {
                
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
        
        if (parentItem != null) {
            
            if (!Objects.equals(item.getIsItemTemplate(), parentItem.getIsItemTemplate())) {
                // parent and child must both be templates or both not be
                String msg = "parent and child must both be templates or both not be templates";
                LOGGER.info(methodLogName + msg);
                validString = appendToString(validString, msg);
                isValid = false;
            }
        }
                        
        // establish parent/child relationship, set location info etc
        item.applyImportValues(parentItem, isValidAssignedItem, isValidLocation);
        
        // update tree view with item and parent
        updateTreeView(item, parentItem);
        
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