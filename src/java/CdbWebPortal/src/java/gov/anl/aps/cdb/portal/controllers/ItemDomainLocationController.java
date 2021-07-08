/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainLocationSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainLocationControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperLocation;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainLocationFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.FilterViewItemHierarchySelection;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemHierarchyCache;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

@Named(ItemDomainLocationController.controllerNamed)
@SessionScoped
public class ItemDomainLocationController extends ItemController<ItemDomainLocationControllerUtility, ItemDomainLocation, ItemDomainLocationFacade, ItemDomainLocationSettings> {

    private final String DOMAIN_TYPE_NAME = ItemDomainName.location.getValue();
    private static final String DOMAIN_NAME = "Location";
    public static final String controllerNamed = "itemDomainLocationController";

    private static final Logger logger = LogManager.getLogger(ItemDomainLocationController.class.getName());

    private TreeNode locationsWithInventoryItemsRootNode;

    private TreeNode locationsWithInventoryItemAssemblyRootNode;

    private TreeNode selectedLocationTreeNode;

    private FilterViewItemHierarchySelection filterViewLocationSelection = null;

    private boolean renderLocationSelectionDialog = false;
    private boolean renderLocationInplaceEditTieredMenu = false;       

    @EJB
    DomainFacade domainFacade;

    @EJB
    ItemDomainLocationFacade itemDomainLocationFacade;

    public ItemDomainLocationController() {
        super();
    }   

    public static ItemDomainLocationController getInstance() {        
        return (ItemDomainLocationController) findDomainController(DOMAIN_NAME);        
    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
        getLocatableItemController().resetCachedLocationValues();

        locationsWithInventoryItemsRootNode = null;
        locationsWithInventoryItemAssemblyRootNode = null;
    }

    public FilterViewItemHierarchySelection getFilterViewLocationSelection() {
        if (filterViewLocationSelection == null) {
            List<Item> locationTopLevel = (List<Item>) (List<?>) getItemsWithoutParents();
            filterViewLocationSelection = new FilterViewItemHierarchySelection(locationTopLevel);
        }
        return filterViewLocationSelection;
    }

    public List<FilterViewItemHierarchySelection> getFilterViewLocationSelectionList() {
        return getFilterViewLocationSelection().getFilterViewSelectionHierarchyList();
    }

    public ItemDomainLocation getFilterViewLocationLastSelection() {
        return (ItemDomainLocation) getFilterViewLocationSelection().getLowesetSelectionMade();
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return false;
    }
    
    @Override
    public boolean isShowCloneCreateItemElementsPlaceholdersOption() {
        return false; 
    }

    /**
     * Get a list of items that are located somewhere down the hierarchy of
     * another item.
     *
     * @param locationItem
     * @return
     */
    public static List<ItemDomainInventory> getAllItemsLocatedInHierarchy(ItemDomainLocation locationItem) {
        List<Item> itemList = new ArrayList<>();
        return (List<ItemDomainInventory>) (List<?>) getAllItemsLocatedInHierarchy(itemList, locationItem);
    }

    /**
     * Recursive helper method for fetching items located somewhere down in the
     * hierarchy of another item.
     *
     * @param itemList
     * @param locationItem
     * @return
     */
    private static List<Item> getAllItemsLocatedInHierarchy(List<Item> itemList, ItemDomainLocation locationItem) {
        String relationshipToLocation = ItemElementRelationshipTypeNames.itemLocation.getValue();
        boolean isLocationItemFirst = false;
        List<Item> itemsInLocation = ItemUtility.getItemsRelatedToItem(locationItem, relationshipToLocation, isLocationItemFirst);
        itemList.addAll(itemsInLocation);

        List<ItemElement> itemElementList = locationItem.getItemElementDisplayList();
        if (itemElementList != null) {
            for (ItemElement itemElement : itemElementList) {
                ItemDomainLocation containedItem = (ItemDomainLocation) itemElement.getContainedItem();
                if (containedItem != null) {
                    getAllItemsLocatedInHierarchy(itemList, containedItem);
                }
            }
        }

        return itemList;
    }

    public TreeNode getLocationsWithInventoryItemsRootNode() {
        if (locationsWithInventoryItemsRootNode == null) {
            // Location with inventory items is now the same pointer as items with no parents...
            locationsWithInventoryItemsRootNode = super.getItemsWithNoParentsRootNode();
            // New tree must be generated for items with no parents
            itemsWithNoParentsRootNode = null;
            addInventoryItemsToLocationTree(locationsWithInventoryItemsRootNode);
        }

        return locationsWithInventoryItemsRootNode;
    }

    public TreeNode getLocationsWithInventoryItemAssemblyRootNode() throws CdbException {
        if (locationsWithInventoryItemAssemblyRootNode == null) {
            // locations with inventory item assebmby is now same pointer as locations with inventory items... 
            locationsWithInventoryItemAssemblyRootNode = getLocationsWithInventoryItemsRootNode();
            // Locations with items root node need to be recreated. 
            locationsWithInventoryItemsRootNode = null;
            addAssemblyTreeToLocationTree(locationsWithInventoryItemAssemblyRootNode);

        }
        return locationsWithInventoryItemAssemblyRootNode;
    }

    private void addAssemblyTreeToLocationTree(TreeNode currentNode) throws CdbException {
        List<TreeNode> nodeChildren = currentNode.getChildren();
        if (nodeChildren != null) {
            for (TreeNode childNode : currentNode.getChildren()) {
                addAssemblyTreeToLocationTree(childNode);
            }
        }

        Object data = currentNode.getData();

        if (data != null) {
            // Data is expected to be an item. 
            if (data instanceof Item) {
                Item nodeItem = (Item) data;
                String inventoryDomainName = ItemDomainName.inventory.getValue();
                if (nodeItem.getDomain().getName().equals(inventoryDomainName)) {
                    addValidAssemblyItemsToInventoryItemNode(currentNode, nodeItem);
                }
            } else {
                throw new CdbException("locationTreeNode is not of type item.");
            }
        }
    }

    private void addValidAssemblyItemsToInventoryItemNode(TreeNode treeNode, Item inventoryItem) {
        // Check if item is an assembly. 
        List<ItemElement> itemElements = inventoryItem.getItemElementDisplayList();
        if (itemElements != null && !itemElements.isEmpty()) {
            for (ItemElement assemblyItemElement : itemElements) {
                addValidAssemblyItemsToInventoryItemNode(treeNode, assemblyItemElement);
            }
        }
    }

    private void addValidAssemblyItemsToInventoryItemNode(TreeNode assemblyNode, ItemElement assemblyItemElement) {
        Item containedItem = assemblyItemElement.getContainedItem();
        if (containedItem != null) {
            ItemElement parentItemSelfElement = containedItem.getSelfElement();
            List<ItemElementRelationship> itemElementRelationshipList = parentItemSelfElement.getItemElementRelationshipList();
            ItemElementRelationship locationRelationship = findLocationItemElementRelationship(itemElementRelationshipList);
            if (locationRelationship == null || locationRelationship.getSecondItemElement() == null) {
                // No location specified -- Valid for location assembly tree. 
                TreeNode newAssemblyNode = ItemUtility.createNewTreeNode(containedItem, assemblyNode);

                // Check if parent item is assembly.
                addValidAssemblyItemsToInventoryItemNode(newAssemblyNode, containedItem);
            }
        }
    }

    private void addInventoryItemsToLocationTree(TreeNode locationTreeNode) {
        List<TreeNode> locationChildrenNodes = locationTreeNode.getChildren();
        for (TreeNode childLocatonNode : locationChildrenNodes) {
            addInventoryItemsToLocationTree(childLocatonNode);

            Item locationItem = (Item) childLocatonNode.getData();
            if (locationItem != null) {
                addLocationRelationshipsToParentTreeNode(locationItem, childLocatonNode);
            }
        }
    }

    private static ItemElementRelationship findLocationItemElementRelationship(List<ItemElementRelationship> itemElementRelationshipList) {
        String locationRelationshipTypeName = ItemElementRelationshipTypeNames.itemLocation.getValue();
        for (ItemElementRelationship ier : itemElementRelationshipList) {
            if (ier.getRelationshipType().getName().equals(locationRelationshipTypeName)) {
                return ier;
            }
        }
        return null;
    }

    public static void addLocationRelationshipsToParentTreeNode(Item item, TreeNode parentTreeNode) {
        String locationRelationshipName = ItemElementRelationshipTypeNames.itemLocation.getValue();
        boolean isItemFirst = false;
        List<Item> itemList = ItemUtility.getItemsRelatedToItem(item, locationRelationshipName, isItemFirst);

        for (Item inventoryItem : itemList) {
            TreeNode currentTreeNode = ItemUtility.createNewTreeNode(inventoryItem, parentTreeNode);
            // TODO handle circular location reference
            addLocationRelationshipsToParentTreeNode(inventoryItem, currentTreeNode);
        }
    }

    public TreeNode getSelectedLocationTreeNode() {
        return selectedLocationTreeNode;
    }

    public void setSelectedLocationTreeNode(TreeNode selectedLocationTreeNode) {
        this.selectedLocationTreeNode = selectedLocationTreeNode;
    }

    public Item getItemFromSelectedLocationTreeNode() {
        if (selectedLocationTreeNode != null) {
            return (Item) selectedLocationTreeNode.getData();
        }

        return null;
    }

    public void resetListDataModelAndSetSelectionLocatinTreeNodeByItem(Item item) {
        resetListDataModel();
        setSelectedLocationTreeNodeByItem(item);
    }

    public void setSelectedLocationTreeNodeByItem(Item item) {
        if (item != null) {
            // check selected node.. 
            TreeNode root = getItemsWithNoParentsRootNode();
            ItemUtility.setExpandedSelectedOnAllChildren(root, false, false);

            selectedLocationTreeNode = ItemUtility.findTreeNodeWithItem(item, root);
            if (selectedLocationTreeNode != null) {
                selectedLocationTreeNode.setSelected(true);
                ItemUtility.expandTreeBranch(selectedLocationTreeNode);
            }
        } else {
            selectedLocationTreeNode = null;
        }
    }

    public DefaultMenuModel getParentSelectionMenuModel(String updateTarget) {
        ItemDomainLocation current = getCurrent();
        DefaultMenuModel parentSelectionMenuModel = current.getParentSelectionMenuModel();
        if (parentSelectionMenuModel == null) {
            LocatableItemController instance = LocatableItemController.getInstance();
            List<ItemHierarchyCache> locationItemHierarchyCaches = instance.getLocationItemHierarchyCaches();

            ItemDomainLocation parentItem = getCurrent().getParentItem();
            String parentItemName = "Top Level"; 
            List<Item> activeLocation = new ArrayList<>();
            if (parentItem != null) {
                ItemDomainLocation ittrParentItem = parentItem;
                parentItemName = parentItem.getName(); 
                while (ittrParentItem != null) {
                    activeLocation.add(ittrParentItem);
                    ittrParentItem = ittrParentItem.getParentItem();
                }
            }

            parentSelectionMenuModel = ItemElementUtility.generateItemSelectionMenuModel(
                    locationItemHierarchyCaches,
                    parentItemName,
                    controllerNamed,
                    "updateParentForCurrent",
                    activeLocation,
                    null,
                    updateTarget,
                    updateTarget);
            current.setParentSelectionMenuModel(parentSelectionMenuModel);
        }
        return parentSelectionMenuModel;
    }

    public void updateParentForCurrent(Item newParent) {
        updateParentForItem(getCurrent(), newParent);
        
        ItemDomainLocation current = getCurrent();
        DefaultMenuModel parentSelectionMenuModel = current.getParentSelectionMenuModel();
        
        DefaultSubMenu topNode = (DefaultSubMenu) parentSelectionMenuModel.getElements().get(0);
        topNode.setLabel(newParent.getName());
    }
    
    public void updateParentForItem(ItemDomainLocation item, Item newParentItem) {
        UserInfo user = SessionUtility.getUser();
        ItemDomainLocationControllerUtility controllerUtility = getControllerUtility();
        try {
            controllerUtility.updateParentForItem(item, newParentItem, user);
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            logger.error(ex);
        }
    }    

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
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
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        return "content";
    }

    @Override
    public String getDefaultDomainName() {
        return DOMAIN_TYPE_NAME;
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false;
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return null;
    }   

    @Override
    protected ItemDomainLocationFacade getEntityDbFacade() {
        return itemDomainLocationFacade;
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    protected ItemDomainLocationSettings createNewSettingObject() {
        return new ItemDomainLocationSettings(this);
    }

    public boolean isRenderLocationSelectionDialog() {
        return renderLocationSelectionDialog;
    }

    public void setRenderLocationSelectionDialog(boolean renderLocationSelectionDialog) {
        this.renderLocationSelectionDialog = renderLocationSelectionDialog;
    }

    public boolean isRenderLocationInplaceEditTieredMenu() {
        return renderLocationInplaceEditTieredMenu;
    }

    public void setRenderLocationInplaceEditTieredMenu(boolean renderLocationInplaceEditTieredMenu) {
        this.renderLocationInplaceEditTieredMenu = renderLocationInplaceEditTieredMenu;
    }

    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    protected DomainImportExportInfo initializeDomainImportInfo() {

        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        formatInfo.add(new ImportExportFormatInfo("Hierarchical Location Format", ImportHelperLocation.class));

        String completionUrl = "/views/itemDomainLocation/list?faces-redirect=true";

        return new DomainImportExportInfo(formatInfo, completionUrl);
    }

    @Override
    protected ItemDomainLocationControllerUtility createControllerUtilityInstance() {
        return new ItemDomainLocationControllerUtility(); 
    }
}
