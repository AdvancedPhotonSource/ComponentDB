/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.primefaces.event.DragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainMachineDesignController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignController extends ItemController<ItemDomainMachineDesign, ItemDomainMachineDesignFacade, ItemDomainMachineDesignSettings> {

    public final static String controllerNamed = "itemDomainMachineDesignController";

    // <editor-fold defaultstate="collapsed" desc="Element edit variables ">
    private Boolean createCatalogElement = null;
    private Boolean machineDesignItemCreateFromTemplate = null;
    private Item inventoryForElement = null;
    private Item catalogForElement = null;
    private Item originalForElement = null;    
    protected DataModel installedInventorySelectionForCurrentElement;
    protected DataModel machineDesignTemplatesSelectionList;
    private DataModel topLevelMachineDesignSelectionList;
    private List<KeyValueObject> machineDesignNameList = null;
    private List<String> nameParts = null;
    private String machineDesignName = null;
    private boolean displayAssignToDataTable = false;
    private boolean displayCreateItemElementContent = false;
    private boolean displayCreateMachineDesignFromTemplateContent = false;
    // </editor-fold>

    private List<String> selectedListDisplayOptions = null;    
    
    // <editor-fold defaultstate="collapsed" desc="Dual list view configuration variables ">
    private TreeNode selectedItemInListTreeTable = null;
    private TreeNode lastExpandedNode = null;

    private TreeNode machineDesignTreeRootTreeNode = null;

    private boolean displayListConfigurationView = false;
    private boolean displayAddMachineDesignListConfigurationPanel = true;
    private boolean displayAddCatalogItemListConfigurationPanel = true;
    private boolean displayAssignInventoryItemListConfigurationPanel = true;
    private boolean displayCreateMachineDesignForTemplateElementPlaceholder = true;

    private List<ItemDomainCatalog> catalogItemsDraggedAsChildren = null;
    private TreeNode newCatalogItemsInMachineDesignModel = null;

    // </editor-fold>
    
    @EJB
    ItemDomainMachineDesignFacade itemDomainMachineDesignFacade;

    @EJB
    RelationshipTypeFacade relationshipTypeFacade;

    public ItemDomainMachineDesign getInstance() {
        return (ItemDomainMachineDesign) SessionUtility.findBean(controllerNamed);
    }

    public boolean getCurrentHasInventoryItem() {
        return !isCurrentItemTemplate();
    }  

    public boolean isItemMachineDesignAndTemplate(Item item) {
        if (item instanceof ItemDomainMachineDesign) {
            return ((ItemDomainMachineDesign) item).getIsItemTemplate();
        }

        return false;
    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
        machineDesignTreeRootTreeNode = null;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Dual list view configuration implementation ">

    private void setTreeNodeTypeMachineDesignTreeList(TreeNode treeNode) {
        Object data = treeNode.getData();
        ItemElement ie = (ItemElement) data;
        Item item = ie.getContainedItem();
        String domain = item.getDomain().getName();
        int itemDomainId = item.getDomain().getId();
        String defaultDomainAssignment = domain.replace(" ", "");
        if (isItemMachineDesignAndTemplate(item)) {
            defaultDomainAssignment += "Template";
        }

        if (treeNode.getParent().getData() == null) {
            treeNode.setType(defaultDomainAssignment);
            return;
        } else {
            Object parentData = treeNode.getParent().getData();
            ItemElement parentIe = (ItemElement) parentData;
            Item parentItem = parentIe.getContainedItem();
            int parentDomainId = parentItem.getDomain().getId();

            if (isItemMachineDesignAndTemplate(item)) {
                if (isItemMachineDesignAndTemplate(parentItem)) {
                    // parent is template -- default name is correct
                    defaultDomainAssignment += "Member";
                } else {
                    // parent is machine desing 
                    defaultDomainAssignment += "Placeholder";
                }
            } else if (parentDomainId == ItemDomainName.CATALOG_ID
                    || parentDomainId == ItemDomainName.INVENTORY_ID) {
                // Sub item of a catalog or an inventory 
                defaultDomainAssignment += "Member";
            } else if (parentDomainId == ItemDomainName.MACHINE_DESIGN_ID) {
                if (itemDomainId == ItemDomainName.MACHINE_DESIGN_ID) {
                    // machine design sub item of a machine design 
                    defaultDomainAssignment += "Member";
                } else if (itemDomainId == ItemDomainName.CATALOG_ID) {
                    // catalog sub item of a machine design 
                    if (isItemMachineDesignAndTemplate(parentItem)) {
                        // catalog sub item of a machine design template
                        defaultDomainAssignment += "TemplateMember";
                    }
                }
            }
            treeNode.setType(defaultDomainAssignment);
        }
    }

    public TreeNode getMachineDesignTreeRootTreeNode() {
        if (machineDesignTreeRootTreeNode == null) {
            machineDesignTreeRootTreeNode = new DefaultTreeNode();
            List<ItemDomainMachineDesign> itemsWithoutParents
                    = getItemsWithoutParents();

            for (Item item : itemsWithoutParents) {
                if (!item.getIsItemTemplate()) {
                    ItemElement element = new ItemElement();
                    element.setContainedItem(item);
                    TreeNode parent = new DefaultTreeNode(element);
                    machineDesignTreeRootTreeNode.getChildren().add(parent);
                    parent.setParent(machineDesignTreeRootTreeNode);
                    setTreeNodeTypeMachineDesignTreeList(parent);
                    expandTreeChildren(parent);
                }
            }
        }
        return machineDesignTreeRootTreeNode;
    }

    private void expandTreeChildren(TreeNode treeNode) {
        Object data = treeNode.getData();
        ItemElement ie = (ItemElement) data;
        Item item = ie.getContainedItem();

        boolean parentIsTemplate = isItemMachineDesignAndTemplate(item);

        if (item != null) {
            for (ItemElement itemElement : item.getItemElementDisplayList()) {
                TreeNode newTreeNode = new DefaultTreeNode(itemElement);
                Item containedItem = itemElement.getContainedItem();

                treeNode.getChildren().add(newTreeNode);
                newTreeNode.setParent(treeNode);

                if (containedItem != null) {
                    setTreeNodeTypeMachineDesignTreeList(newTreeNode);
                    boolean skipExpansion = false;
                    if (!parentIsTemplate) {
                        if (isItemMachineDesignAndTemplate(containedItem)) {
                            // Template within a non-template (Placeholder)
                            skipExpansion = true;
                        }
                    }
                    if (!skipExpansion) {
                        expandTreeChildren(newTreeNode);
                    }
                } else {
                    newTreeNode.setType("Blank");
                }

            }
        }
    }

    public TreeNode getSelectedItemInListTreeTable() {
        return selectedItemInListTreeTable;
    }

    public void setSelectedItemInListTreeTable(TreeNode selectedItemInListTreeTable) {
        this.selectedItemInListTreeTable = selectedItemInListTreeTable;
    }

    public boolean isSelectedItemInListTreeViewWriteable() {
        if (selectedItemInListTreeTable != null) {
            ItemElement itemElement = (ItemElement) selectedItemInListTreeTable.getData();
            Item containedItem = itemElement.getContainedItem();
            LoginController instance = LoginController.getInstance();
            return instance.isEntityWriteable(containedItem.getEntityInfo());

        }
        return false;
    }

    public boolean isParentOfSelectedItemInListTreeViewWriteable() {
        if (selectedItemInListTreeTable != null) {
            ItemElement itemElement = (ItemElement) selectedItemInListTreeTable.getData();
            Item containedItem = itemElement.getParentItem();
            if (containedItem != null) {
                LoginController instance = LoginController.getInstance();
                return instance.isEntityWriteable(containedItem.getEntityInfo());
            }
        }
        return false;
    }

    public String showDetailsForCurrentSelectedTreeNode() {
        ItemDomainMachineDesign item = getItemFromSelectedItemInTreeTable();

        if (item != null) {
            setCurrent(item);
            return view();
        }

        SessionUtility.addErrorMessage("Error", "Cannot load details for a non machine design.");
        return null;
    }

    public void resetListConfigurationVariables() {
        displayListConfigurationView = false;
        displayAddMachineDesignListConfigurationPanel = false;
        displayAddCatalogItemListConfigurationPanel = false;
        displayAssignInventoryItemListConfigurationPanel = false;
        displayCreateMachineDesignForTemplateElementPlaceholder = false;
        catalogItemsDraggedAsChildren = null;
        newCatalogItemsInMachineDesignModel = null;
    }

    public boolean isDisplayListConfigurationView() {
        return displayListConfigurationView;
    }

    public boolean isDisplayAddMachineDesignListConfigurationPanel() {
        return displayAddMachineDesignListConfigurationPanel;
    }

    public boolean isDisplayAddCatalogItemListConfigurationPanel() {
        return displayAddCatalogItemListConfigurationPanel;
    }

    public boolean isDisplayAssignInventoryItemListConfigurationPanel() {
        return displayAssignInventoryItemListConfigurationPanel;
    }

    public boolean isDisplayCreateMachineDesignForTemplateElementPlaceholder() {
        return displayCreateMachineDesignForTemplateElementPlaceholder;
    }

    private void updateCurrentUsingSelectedItemInTreeTable() {
        setCurrent(getItemFromSelectedItemInTreeTable());
    }

    private ItemDomainMachineDesign getItemFromSelectedItemInTreeTable() {
        ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();
        Item item = element.getContainedItem();

        if (item instanceof ItemDomainMachineDesign) {
            return (ItemDomainMachineDesign) item;
        }

        return null;
    }

    public void detachSelectedItemFromHierarchyInDualView() {
        ItemElement element = (ItemElement) selectedItemInListTreeTable.getData();

        Item containedItem = element.getContainedItem();
        Integer detachedDomainId = containedItem.getDomain().getId();

        ItemElementController instance = ItemElementController.getInstance();

        instance.destroy(element);

        selectedItemInListTreeTable = selectedItemInListTreeTable.getParent();

        resetListDataModel();

        expandToSpecificTreeNode(selectedItemInListTreeTable);
        if (detachedDomainId == ItemDomainName.MACHINE_DESIGN_ID) {            
            for (TreeNode node : getMachineDesignTreeRootTreeNode().getChildren()) {
                ItemElement ie = (ItemElement) node.getData();
                Item ci = ie.getContainedItem();
                if (containedItem.equals(ci)) {
                    node.setSelected(true);
                    selectedItemInListTreeTable = node;
                    break; 
                }
            }
        }
    }

    public void deleteSelectedMachineDesignItemFromDualView() {
        updateCurrentUsingSelectedItemInTreeTable();

        destroy();
    }

    public void prepareCreateMachineDesignForDualViewTemplateElementPlaceholder() {
        updateCurrentUsingSelectedItemInTreeTable();
        currentEditItemElement = (ItemElement) selectedItemInListTreeTable.getData();

        prepareCreateMachineDesignFromTemplate();

        displayListConfigurationView = true;
        displayCreateMachineDesignForTemplateElementPlaceholder = true;
    }

    public void createMachineDesignForDualViewTemplatePlaceholder(String successCreatePlaceholder) {
        boolean success = createMachineDesignFromTemplate(successCreatePlaceholder);

        if (success) {
            expandToSpecificTreeNode(selectedItemInListTreeTable);
            resetListConfigurationVariables();
        }
    }

    public void prepareAssignInventoryMachineDesignListConfiguration() {
        currentEditItemElement = (ItemElement) selectedItemInListTreeTable.getData();
        catalogForElement = currentEditItemElement.getCatalogItem();

        prepareUpdateInstalledInventoryItem();

        displayAssignInventoryItemListConfigurationPanel = true;
        displayListConfigurationView = true;
    }

    public void assignInventoryMachineDesignListConfiguration() {
        updateInstalledInventoryItem();

        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();
    }

    public void unassignInventoryMachineDesignListConfiguration() {
        ItemElement itemElement = (ItemElement) selectedItemInListTreeTable.getData();

        Item containedItem = itemElement.getContainedItem();

        if (containedItem instanceof ItemDomainInventory) {
            itemElement.setContainedItem(containedItem.getDerivedFromItem());

            ItemElementController itemElementController = ItemElementController.getInstance();
            itemElementController.setCurrent(itemElement);
            itemElementController.update();
        }

        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();

    }

    public void prepareAddNewMachineDesignListConfiguration() {
        updateCurrentUsingSelectedItemInTreeTable();

        displayListConfigurationView = true;
        displayAddMachineDesignListConfigurationPanel = true;

        prepareCreateSingleItemElementSimpleDialog();

        createCatalogElement = false;
    }

    public void prepareAddNewMachineDesignTemplateListConfiguration(String finalStepCommand) {
        prepareAddNewMachineDesignListConfiguration();
        machineDesignItemCreateFromTemplate = false;

        machineDesignItemCreateFromTemplateChange(finalStepCommand);
    }

    public void completeAddNewMachineDesignListConfiguration() {
        resetListConfigurationVariables();
    }

    public String saveTreeListMachineDesignItem() {
        ItemElement ref = currentEditItemElement;
        saveCreateSingleItemElementSimpleDialog();

        for (ItemElement element : current.getItemElementDisplayList()) {
            if (element.getName().equals(ref.getName())) {
                ref = element;
                break;
            }
        }

        expandToSelectedTreeNodeAndSelectChildItemElement(ref);

        return list();
    }

    private void expandToSelectedTreeNodeAndSelectChildItemElement(ItemElement element) {
        expandToSpecificTreeNode(selectedItemInListTreeTable);

        for (TreeNode treeNode : lastExpandedNode.getChildren()) {
            if (treeNode.getData().equals(element)) {
                selectedItemInListTreeTable = treeNode;
                treeNode.setSelected(true);
                break;
            }
        }
    }

    private void expandToSpecificTreeNodeAndSelect(TreeNode treeNode) {
        expandToSpecificTreeNode(treeNode);
        selectedItemInListTreeTable = lastExpandedNode;
        lastExpandedNode.setSelected(true);
    }

    private void expandToSelectedTreeNodeAndSelect() {
        expandToSpecificTreeNodeAndSelect(selectedItemInListTreeTable);
    }

    private void expandToSpecificTreeNode(TreeNode treeNode) {
        lastExpandedNode = null;

        if (treeNode.getParent() != null) {
            // No need to get top most parent. 
            if (treeNode.getParent().getParent() != null) {
                expandToSpecificTreeNode(treeNode.getParent());
            }
        }
        if (lastExpandedNode == null) {
            lastExpandedNode = getMachineDesignTreeRootTreeNode();
        }

        ItemElement itemElement = (ItemElement) treeNode.getData();
        Item item = itemElement.getContainedItem();

        for (TreeNode ittrTreeNode : lastExpandedNode.getChildren()) {
            ItemElement element = (ItemElement) ittrTreeNode.getData();
            Item ittrItem = element.getContainedItem();
            if (item.equals(ittrItem)) {
                ittrTreeNode.setExpanded(true);
                lastExpandedNode = ittrTreeNode;
                break;
            }
        }
    }

    public void prepareAddNewCatalogListConfiguration() {
        updateCurrentUsingSelectedItemInTreeTable();

        displayListConfigurationView = true;
        displayAddCatalogItemListConfigurationPanel = true;
    }

    public String completeAddNewCatalogListConfiguration() {
        for (Item item : catalogItemsDraggedAsChildren) {
            ItemElement newItemElement = createItemElement(current);

            newItemElement.setContainedItem(item);

            prepareAddItemElement(current, newItemElement);
        }

        update();

        expandToSelectedTreeNodeAndSelect();

        return list();
    }

    public void onItemDrop(DragDropEvent ddEvent) {
        ItemDomainCatalog catalogItem = ((ItemDomainCatalog) ddEvent.getData());

        if (catalogItemsDraggedAsChildren == null) {
            catalogItemsDraggedAsChildren = new ArrayList<>();
        }

        if (newCatalogItemsInMachineDesignModel == null) {
            newCatalogItemsInMachineDesignModel = new DefaultTreeNode();
            TreeNode parent = new DefaultTreeNode(getCurrent());
            newCatalogItemsInMachineDesignModel.getChildren().add(parent);
            parent.setExpanded(true);
            lastExpandedNode = parent;
        }
        TreeNode newCatalogNode = new DefaultTreeNode(catalogItem);
        lastExpandedNode.getChildren().add(newCatalogNode);

        catalogItemsDraggedAsChildren.add(catalogItem);
    }

    public List<ItemDomainCatalog> getCatalogItemsDraggedAsChildren() {
        return catalogItemsDraggedAsChildren;
    }

    public TreeNode getNewCatalogItemsInMachineDesignModel() {
        return newCatalogItemsInMachineDesignModel;
    }
    
    // </editor-fold>    
    
    public boolean verifyValidTemplateName(String templateName, boolean printMessage) {
        boolean validTitle = false;
        if (templateName.contains("{")) {
            int openBraceIndex = templateName.indexOf("{");
            int closeBraceIndex = templateName.indexOf("}");
            if (openBraceIndex < closeBraceIndex) {
                validTitle = true;
            }
        }
        if (!validTitle && printMessage) {
            SessionUtility.addWarningMessage(
                    "Template names require parameters",
                    "Place parements within {} in template name. Example: 'templateName {paramName}'");

        }

        return validTitle;
    }

    public String prepareCreateTemplate() {
        String createRedirect = super.prepareCreate();

        ItemDomainMachineDesign current = getCurrent();
        String templateEntityTypeName = EntityTypeName.template.getValue();
        EntityType templateEntityType = entityTypeFacade.findByName(templateEntityTypeName);
        try {
            current.setEntityTypeList(new ArrayList<>());
        } catch (CdbException ex) {
            Logger.getLogger(ItemDomainMachineDesignController.class.getName()).log(Level.SEVERE, null, ex);
        }
        current.getEntityTypeList().add(templateEntityType);

        return createRedirect;

    }        

    // <editor-fold defaultstate="collapsed" desc="Element creation implementation ">   
    // <editor-fold defaultstate="collapsed" desc="Functionality">
    public void machineDesignElementAddTypeSelectionChange(String intermediateStepCommand, String finalStepCommand) {
        if (!createCatalogElement) {
            if (isCurrentItemTemplate()) {
                machineDesignItemCreateFromTemplate = false;
                machineDesignItemCreateFromTemplateChange(finalStepCommand);
                return;
            }
        }

        SessionUtility.executeRemoteCommand(intermediateStepCommand);
    }

    public void machineDesignItemCreateFromTemplateChange(String finalStepCommand) {
        if (!machineDesignItemCreateFromTemplate) {
            // Create New 
            ItemDomainMachineDesign newMachineDesign = createEntityInstance();

            if (isCurrentItemTemplate()) {
                try {
                    List<EntityType> entityTypeList = new ArrayList<>();
                    EntityType templateEntity = entityTypeFacade.findByName(EntityTypeName.template.getValue());
                    entityTypeList.add(templateEntity);
                    newMachineDesign.setEntityTypeList(entityTypeList);
                } catch (CdbException ex) {
                    Logger.getLogger(ItemDomainMachineDesignController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            currentEditItemElement.setContainedItem(newMachineDesign);

            SessionUtility.executeRemoteCommand(finalStepCommand);
        }
    }

    public void newMachineDesignElementContainedItemValueChanged() {
        String name = currentEditItemElement.getContainedItem().getName();
        if (!name.equals("")) {
            if (isCurrentItemTemplate()) {
                if (!verifyValidTemplateName(name, true)) {
                    currentEditItemElementSaveButtonEnabled = false;
                    return;
                }
            }
            if (itemDomainMachineDesignFacade.findByName(name).size() != 0) {
                SessionUtility.addWarningMessage("Non-unique name", "Please change the name and try again");
                currentEditItemElementSaveButtonEnabled = false;
            } else {
                currentEditItemElementSaveButtonEnabled = true;
            }
        } else {
            currentEditItemElementSaveButtonEnabled = false;
        }

    }

    public void setMachineDesignItemCreateFromTemplate(Boolean machineDesignItemCreateFromTemplate) {
        this.machineDesignItemCreateFromTemplate = machineDesignItemCreateFromTemplate;
    }

    public void updateInstalledInventoryItem() {
        boolean updateNecessary = false;
        Item currentContainedItem = currentEditItemElement.getContainedItem();

        if (inventoryForElement != null) {
            if (currentContainedItem.equals(inventoryForElement)) {
                SessionUtility.addInfoMessage("No update", "Inventory selected is same as before");
            } else if (verifyValidUnusedInventoryItem(inventoryForElement)) {
                updateNecessary = true;
                currentEditItemElement.setContainedItem(inventoryForElement);
            }
        } else if (currentContainedItem.getDomain().getId() == ItemDomainName.INVENTORY_ID) {
            // Item is unselected, select catalog item
            updateNecessary = true;
            currentEditItemElement.setContainedItem(currentContainedItem.getDerivedFromItem());
        } else {
            SessionUtility.addInfoMessage("No update", "Inventory item not selected");
        }

        if (updateNecessary) {
            ItemElementController itemElementController = ItemElementController.getInstance();
            itemElementController.setCurrent(currentEditItemElement);
            itemElementController.update();
        }

        resetItemElementEditVariables();
    }

    private boolean verifyValidUnusedInventoryItem(Item inventoryItem) {
        for (ItemElement itemElement : inventoryItem.getItemElementMemberList()) {
            Item item = itemElement.getParentItem();
            if (item instanceof ItemDomainMachineDesign) {
                SessionUtility.addWarningMessage("Inventory item used",
                        "Inventory item cannot be saved, used in: " + item.toString());
                return false;
            }
        }

        return true;

    }

    @Override
    public void cancelCreateSingleItemElementSimpleDialog() {
        super.cancelCreateSingleItemElementSimpleDialog();
        resetItemElementEditVariables();
    }

    public void prepareCreateMachineDesignFromTemplate() {
        resetItemElementEditVariables();
        displayCreateMachineDesignFromTemplateContent = true;
        templateToCreateNewItem = currentEditItemElement.getMachineDesignItem();
        generateTemplateForElementMachineDesignNameVars();
    }

    public boolean createMachineDesignFromTemplate(String onSucess) {
        try {
            if (!allValuesForTitleGenerationsFilledIn()) {
                SessionUtility.addWarningMessage("Missing data", "Please fill in all name parameters");
                return false;
            }

            createMachineDesignFromTemplateForEditItemElement();
            ItemDomainMachineDesign newItem = (ItemDomainMachineDesign) currentEditItemElement.getContainedItem();
            // Create item
            performCreateOperations(newItem, false, true);

            // Update element 
            ItemElementController instance = ItemElementController.getInstance();
            instance.setCurrent(currentEditItemElement);
            instance.update();

            SessionUtility.executeRemoteCommand(onSucess);
            return true;
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        } catch (CloneNotSupportedException ex) {
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }

        return false;
    }

    public void prepareUpdateInstalledInventoryItem() {
        resetItemElementEditVariables();
        displayAssignToDataTable = true;
        catalogForElement = currentEditItemElement.getCatalogItem();
    }

    public DataModel getInstalledInventorySelectionForCurrentElement() {
        if (installedInventorySelectionForCurrentElement == null) {
            if (catalogForElement != null) {
                List<Item> derivedFromItemList = catalogForElement.getDerivedFromItemList();
                installedInventorySelectionForCurrentElement = new ListDataModel(derivedFromItemList);
            }

        }
        return installedInventorySelectionForCurrentElement;
    }

    public DataModel getMachineDesignTemplatesSelectionList() {
        if (machineDesignTemplatesSelectionList == null) {
            List<ItemDomainMachineDesign> machineDesignTemplates = itemDomainMachineDesignFacade.getMachineDesignTemplates();
            machineDesignTemplatesSelectionList = new ListDataModel(machineDesignTemplates);
        }
        return machineDesignTemplatesSelectionList;
    }

    public DataModel getTopLevelMachineDesignSelectionList() {
        if (topLevelMachineDesignSelectionList == null) {
            List<ItemDomainMachineDesign> itemsWithoutParents = getItemsWithoutParents();
            List<ItemElement> itemElementMemberList = current.getItemElementMemberList();

            if (itemElementMemberList != null) {
                if (itemElementMemberList.size() == 0) {
                    // current item has no parents
                    itemsWithoutParents.remove(current);
                } else {
                    // Be definition machine design item should only have one parent
                    Item parentItem = null;

                    while (itemElementMemberList.size() != 0) {
                        ItemElement parentElement = itemElementMemberList.get(0);
                        parentItem = parentElement.getParentItem();

                        itemElementMemberList = parentItem.getItemElementMemberList();
                    }

                    itemsWithoutParents.remove(parentItem);
                }
            }

            removeTemplatesFromList(itemsWithoutParents);

            topLevelMachineDesignSelectionList = new ListDataModel(itemsWithoutParents);
        }
        return topLevelMachineDesignSelectionList;
    }

    private void removeTemplatesFromList(List<ItemDomainMachineDesign> itemList) {
        String templateEntityName = EntityTypeName.template.getValue();
        EntityType templateEntityType = entityTypeFacade.findByName(templateEntityName);

        int index = 0;
        while (index < itemList.size()) {
            Item item = itemList.get(index);
            if (item.getEntityTypeList().contains(templateEntityType)) {
                itemList.remove(index);
            } else {
                index++;
            }
        }
    }

    private void removeMachineDesignFromList(List<ItemDomainMachineDesign> itemList) {
        String templateEntityName = EntityTypeName.template.getValue();
        EntityType templateEntityType = entityTypeFacade.findByName(templateEntityName);

        int index = 0;
        while (index < itemList.size()) {
            Item item = itemList.get(index);
            // Does not contain template entity type
            if (!item.getEntityTypeList().contains(templateEntityType)) {
                itemList.remove(index);
            } else {
                index++;
            }
        }
    }

    public void resetItemElementEditVariables() {
        currentEditItemElementSaveButtonEnabled = false;
        displayAssignToDataTable = false;
        displayCreateItemElementContent = false;
        displayCreateMachineDesignFromTemplateContent = false;

        installedInventorySelectionForCurrentElement = null;
        createCatalogElement = null;
        machineDesignItemCreateFromTemplate = null;
        inventoryForElement = null;
        catalogForElement = null;
        inventoryForElement = null;
        templateToCreateNewItem = null;
        machineDesignTemplatesSelectionList = null;
        topLevelMachineDesignSelectionList = null;
        machineDesignNameList = null;
        machineDesignName = null;
        nameParts = null;
    }

    @Override
    public void prepareCreateSingleItemElementSimpleDialog() {
        super.prepareCreateSingleItemElementSimpleDialog();
        resetItemElementEditVariables();
        displayCreateItemElementContent = true;
    }

    public void verifyItemElementContainedItemSelection() {
        if (currentEditItemElement != null) {
            if (createCatalogElement) {
                if (catalogForElement != null || inventoryForElement != null) {
                    currentEditItemElementSaveButtonEnabled = true;
                }
            } else if (!createCatalogElement) {
                // Machine design
                if (machineDesignItemCreateFromTemplate == null) {
                    if (currentEditItemElement.getContainedItem() != null) {
                        currentEditItemElementSaveButtonEnabled = true;
                    }
                } else if (machineDesignItemCreateFromTemplate) {
                    if (templateToCreateNewItem != null) {
                        generateTemplateForElementMachineDesignNameVars();
                    }
                }
            }
        }
    }

    private void generateTemplateForElementMachineDesignNameVars() {
        String name = templateToCreateNewItem.getName();
        int firstVar = name.indexOf('{');
        int secondVar;

        machineDesignNameList = new ArrayList<>();
        nameParts = new ArrayList<>();

        while (firstVar != -1) {
            nameParts.add(name.substring(0, firstVar));
            name = name.substring(firstVar);
            secondVar = name.indexOf('}');

            String key = name.substring(1, secondVar);

            KeyValueObject keyValue = new KeyValueObject(key);

            machineDesignNameList.add(keyValue);

            name = name.substring(secondVar + 1);

            firstVar = name.indexOf('{');
        }
        generateMachineDesignName();
    }

    public void titleGenerationValueChange() {
        generateMachineDesignName();

        currentEditItemElementSaveButtonEnabled = allValuesForTitleGenerationsFilledIn();
    }

    private boolean allValuesForTitleGenerationsFilledIn() {
        for (KeyValueObject keyValue : machineDesignNameList) {
            if (keyValue.getValue() == null || keyValue.getValue().equals("")) {
                return false;
            }
        }
        return true;
    }

    public void generateMachineDesignName() {
        machineDesignName = "";
        for (int i = 0; i < nameParts.size(); i++) {
            machineDesignName += nameParts.get(i);
            KeyValueObject keyValue = machineDesignNameList.get(i);

            if (keyValue.getValue() != null && !keyValue.getValue().equals("")) {
                machineDesignName += keyValue.getValue();
            } else {
                machineDesignName += "{" + keyValue.getKey() + "}";
            }
        }
    }

    @Override
    public void beforeValidateItemElement() throws CloneNotSupportedException, CdbException {
        super.beforeValidateItemElement();
        if (createCatalogElement) {
            originalForElement = currentEditItemElement.getContainedItem();
            if (inventoryForElement != null) {
                if (verifyValidUnusedInventoryItem(inventoryForElement)) {
                    currentEditItemElement.setContainedItem(inventoryForElement);
                } else {
                    throw new CdbException("Inventory item selected has already been used.");
                }
            } else if (catalogForElement != null) {
                currentEditItemElement.setContainedItem(catalogForElement);
            }
        } else if (!createCatalogElement) {
            if (machineDesignItemCreateFromTemplate == null) {

            } else if (machineDesignItemCreateFromTemplate) {
                createMachineDesignFromTemplateForEditItemElement();
            }

            ItemDomainMachineDesign containedItem = (ItemDomainMachineDesign) currentEditItemElement.getContainedItem();
            checkItem(containedItem);
        }
    }

    private void createMachineDesignFromTemplateForEditItemElement() throws CdbException, CloneNotSupportedException {
        cloneProperties = true;
        cloneCreateItemElementPlaceholders = false;

        ItemDomainMachineDesign clone = (ItemDomainMachineDesign) templateToCreateNewItem.clone();
        cloneCreateItemElements(clone, templateToCreateNewItem, true);
        clone.setName(machineDesignName);

        addCreatedFromTemplateRelationshipToItem(clone);
        
        // All template relationships actions completed, no need to redo them. 
        templateToCreateNewItem = null; 

        clone.setEntityTypeList(new ArrayList<>());
        currentEditItemElement.setContainedItem(clone);
    }

    @Override
    public void failedValidateItemElement() {
        super.failedValidateItemElement();
        currentEditItemElement.setContainedItem(originalForElement);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Accessors">
    public boolean isDisplayCreateItemElementContent() {
        return displayCreateItemElementContent;
    }

    public boolean isDisplayAssignToDataTable() {
        return displayAssignToDataTable;
    }

    public Boolean getCreateCatalogElement() {
        return createCatalogElement;
    }

    public void setCreateCatalogElement(Boolean createCatalogElement) {
        this.createCatalogElement = createCatalogElement;
    }

    public Boolean getMachineDesignItemCreateFromTemplate() {
        return machineDesignItemCreateFromTemplate;
    }

    public String getMachineDesignName() {
        return machineDesignName;
    }

    public List<KeyValueObject> getMachineDesignNameList() {
        return machineDesignNameList;
    }

    public Item getInventoryForElement() {
        return inventoryForElement;
    }

    public void setInventoryForElement(Item inventoryForElement) {
        this.inventoryForElement = inventoryForElement;
    }

    public Item getCatalogForElement() {
        return catalogForElement;
    }

    public void setCatalogForElement(Item catalogForElement) {
        this.catalogForElement = catalogForElement;
    }

    public boolean isDisplayCreateMachineDesignFromTemplateContent() {
        return displayCreateMachineDesignFromTemplateContent;
    }

    // </editor-fold>    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Base class overrides">               
    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }
    
    @Override
    public void processPreRenderList() {
        super.processPreRenderList();

        resetListConfigurationVariables();

        if (selectedListDisplayOptions == null) {
            selectedListDisplayOptions = new ArrayList<>();
            selectedListDisplayOptions.add("t");
            selectedListDisplayOptions.add("m");
        }
    }

    @Override
    public void createListDataModel() {
        if (selectedListDisplayOptions.size() == 0) {
            setListDataModel(new ListDataModel());
        } else if (!selectedListDisplayOptions.contains("c")) {
            // No children
            List<ItemDomainMachineDesign> itemsWithoutParents = getItemsWithoutParents();
            if (!selectedListDisplayOptions.contains("t")) {
                // remove templates 
                removeTemplatesFromList(itemsWithoutParents);
            } else if (!selectedListDisplayOptions.contains("m")) {
                // remove machine desings 
                removeMachineDesignFromList(itemsWithoutParents);
            }

            setListDataModel(new ListDataModel(itemsWithoutParents));

        } else // with Children 
        {
            if (selectedListDisplayOptions.size() == 1) {
                // Only children is selected 
                setListDataModel(new ListDataModel());
            } else {
                List<ItemDomainMachineDesign> itemList = getItemList();
                if (!selectedListDisplayOptions.contains("t")) {
                    // remove templates                
                    removeTemplatesFromList(itemList);
                } else if (!selectedListDisplayOptions.contains("m")) {
                    removeMachineDesignFromList(itemList);
                }
                setListDataModel(new ListDataModel(itemList));
            }
        }

    }

    @Override
    protected void checkItem(ItemDomainMachineDesign item) throws CdbException {
        super.checkItem(item);

        if (item.getIsItemTemplate()) {
            if (!verifyValidTemplateName(item.getName(), false)) {
                throw new CdbException("Place parements within {} in template name. Example: 'templateName {paramName}'");
            }
        }
    }

    @Override
    protected void resetVariablesForCurrent() {
        super.resetVariablesForCurrent();       

        resetItemElementEditVariables();
    }

    @Override
    protected ItemDomainMachineDesign instenciateNewItemDomainEntity() {
        return new ItemDomainMachineDesign();
    }

    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignSettings(this);
    }

    @Override
    protected ItemDomainMachineDesignFacade getEntityDbFacade() {
        return itemDomainMachineDesignFacade;
    }

    @Override
    public String getEntityTypeName() {
        return "itemMachineDesign";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Machine Design Item";
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.machineDesign.getValue();
    }
    
    public boolean getRenderItemElementList() {
        if (getEntityDisplayItemElements()) {
            return true; 
        }
        
        return false;
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false;
    }
    
    @Override
    public boolean getEntityDisplayTemplates() {
        return true;
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        return "machineDesign";
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // </editor-fold>
    
    public List<String> getSelectedListDisplayOptions() {
        return selectedListDisplayOptions;
    }

    public void setSelectedListDisplayOptions(List<String> selectedListDisplayOptions) {
        this.selectedListDisplayOptions = selectedListDisplayOptions;
        listDataModel = null;
    }

}
