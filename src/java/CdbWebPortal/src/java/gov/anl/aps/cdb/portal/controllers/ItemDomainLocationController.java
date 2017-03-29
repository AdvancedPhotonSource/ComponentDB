/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainLocationFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import gov.anl.aps.cdb.portal.view.objects.FilterViewItemHierarchySelection;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

@Named("itemDomainLocationController")
@SessionScoped
public class ItemDomainLocationController extends ItemController<ItemDomainLocation, ItemDomainLocationFacade> {

    private final String ENTITY_TYPE_NAME = "Location";
    private final String DOMAIN_TYPE_NAME = ItemDomainName.location.getValue();
    private static final String DOMAIN_NAME = "Location";
    private final String ACTIVE_LOCATION_MENU_ITEM_STYLE = "activeLocationMenuItem";

    private static final Logger logger = Logger.getLogger(ItemDomainLocationController.class.getName());

    private TreeNode locationsWithInventoryItemsRootNode;

    private TreeNode locationsWithInventoryItemAssemblyRootNode;

    private TreeNode selectedLocationTreeNode;

    private FilterViewItemHierarchySelection filterViewLocationSelection = null;

    @EJB
    DomainFacade domainFacade;
    
    @EJB
    ItemDomainLocationFacade itemDomainLocationFacade; 

    public ItemDomainLocationController() {
        super();
        displayNumberOfItemsPerPage = 25;
        displayDescription = true;
        displayItemListTreeView = true;
    }

    public static ItemDomainLocationController getInstance() {
        return (ItemDomainLocationController) findDomainController(DOMAIN_NAME);
    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
        locationsWithInventoryItemsRootNode = null;
        locationsWithInventoryItemAssemblyRootNode = null;
    }

    public FilterViewItemHierarchySelection getFilterViewLocationSelection() {
        if (filterViewLocationSelection == null) {
            List<Item> locationTopLevel = (List<Item>) (List<?>)getItemsWithoutParents();
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

    /**
     * Get a list of items that are located somewhere down the hierarchy of
     * another item.
     *
     * @param locationItem
     * @return
     */
    public static List<ItemDomainInventory> getAllItemsLocatedInHierarchy(ItemDomainLocation locationItem) {
        List<Item> itemList = new ArrayList<>();
        return (List<ItemDomainInventory>)(List<?>)getAllItemsLocatedInHierarchy(itemList, locationItem);
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
    
    /**
     * Generates a tree of location nodes in the form of MenuModel meant to be
     * used as a model in a tiered menu. Could be used in other menus.
     *
     * @param baseNodeName - String that will be displayed on the initial
     * submenu.
     * @param setLocationController - [Null accepted] Controller to update item
     * location.
     * @param setLocationMethod - [Null accepted] Method in the location
     * controller to be called for menuitem command.
     * @param lowestLocation - [Null accepted] If provided a location selected
     * style will be applied to the location that lead to the lowest location.
     * @return
     */
    public DefaultMenuModel generateLocationMenuModel(String baseNodeName, String setLocationController, String setLocationMethod, ItemDomainLocation lowestLocation) {
        DefaultMenuModel generatedMenuModel = new DefaultMenuModel();
        List<ItemDomainLocation> locationHierarchyList = null;
        if (lowestLocation != null) {
            locationHierarchyList = generateLocationHierarchyList(lowestLocation);
        }
        DefaultSubMenu defaultSubMenu;
        defaultSubMenu = new DefaultSubMenu(baseNodeName);
        generatedMenuModel.addElement(defaultSubMenu);
        generateLocationMenuModel(defaultSubMenu, getItemsWithNoParentsRootNode(), setLocationController, setLocationMethod, locationHierarchyList);

        return generatedMenuModel;
    }

    /**
     * Recursive method generates a menu model for locations based on given
     * location root tree node.
     *
     * @param locationSubmenu - SubMenu to which child tree nodes will be
     * converted to.
     * @param locationTreeNode - Location TreeNode branch
     * @param setLocationController - [Null accepted] Controller to update item
     * location.
     * @param setLocationMethod - [Null accepted] Method in the location
     * controller to be called for menuitem command.
     * @param locationHierarchy - Apply location selected style to menu items in
     * the list.
     */
    private void generateLocationMenuModel(DefaultSubMenu locationSubmenu, TreeNode locationTreeNode, String setLocationController, String setLocationMethod, List<ItemDomainLocation> locationHierarchy) {
        if (locationTreeNode.getData() != null) {
            ItemDomainLocation locationItem = (ItemDomainLocation) locationTreeNode.getData();
            boolean applyLocationActiveStyle = false; 
            if (locationHierarchy != null) {
                if (locationHierarchy.contains(locationItem)) {
                    applyLocationActiveStyle = true; 
                }
            }
            if (locationTreeNode.getChildCount() > 0) {
                DefaultSubMenu childLocationSubMenu;
                childLocationSubMenu = new DefaultSubMenu(locationItem.getName());
                if (applyLocationActiveStyle) {
                    childLocationSubMenu.setStyleClass(ACTIVE_LOCATION_MENU_ITEM_STYLE); 
                    if (locationHierarchy.indexOf(locationItem) != locationHierarchy.size() -1) {
                        // Still more items in hierarchy no need to highlight two reps of same item. 
                        applyLocationActiveStyle = false; 
                    }
                }
                locationSubmenu.addElement(childLocationSubMenu);
                addLocationMenuItemToSubmenu(childLocationSubMenu, locationItem, setLocationController, setLocationMethod, applyLocationActiveStyle);
                for (TreeNode childLocationTreeNode : locationTreeNode.getChildren()) {
                    generateLocationMenuModel(childLocationSubMenu, childLocationTreeNode, setLocationController, setLocationMethod, locationHierarchy);
                }
            } else {
                addLocationMenuItemToSubmenu(locationSubmenu, locationItem, setLocationController, setLocationMethod, applyLocationActiveStyle);
            }
        } else // root node 
        if (locationTreeNode.getChildCount() > 0) {
            for (TreeNode childLocationTreeNode : locationTreeNode.getChildren()) {
                generateLocationMenuModel(locationSubmenu, childLocationTreeNode, setLocationController, setLocationMethod, locationHierarchy);
            }
        }
    }

    /**
     * Create a MenuItem for the location provided and insert into the SubMenu
     * provided. Apply additional necessary attributes based on the input
     * parameters.
     *
     * @param submenu
     * @param locationItem
     * @param setLocationController - [Null accepted] Controller to update item
     * location.
     * @param setLocationMethod - [Null accepted] Method in the location
     * controller to be called for menuitem command.
     * @param applayActiveLocationStyle - Apply location selected style to menu items in
     * the list.
     */
    private void addLocationMenuItemToSubmenu(DefaultSubMenu submenu, ItemDomainLocation locationItem, String setLocationController, String setLocationMethod, boolean applayActiveLocationStyle) {
        DefaultMenuItem locationMenuItem = new DefaultMenuItem();
        locationMenuItem.setValue(locationItem.getName());

        if (applayActiveLocationStyle) {
            locationMenuItem.setStyleClass(ACTIVE_LOCATION_MENU_ITEM_STYLE);
        }

        if (setLocationController != null) {
            String onClick = "#{" + setLocationController + ".";
            onClick += setLocationMethod + "(";
            String findLocationMethod = "itemDomainLocationController.findById(" + locationItem.getId() + ")";
            onClick += findLocationMethod + ")}";
            locationMenuItem.setCommand(onClick);
            locationMenuItem.setUpdate("@form");
        }
        submenu.addElement(locationMenuItem);
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
            TreeNode root = getLocationsWithInventoryItemsRootNode();
            setExpandedSelectedOnAllChildren(root, false, false);

            selectedLocationTreeNode = findTreeNodeWithItem(item, root);
            if (selectedLocationTreeNode != null) {
                selectedLocationTreeNode.setSelected(true);
                expandTreeBranch(selectedLocationTreeNode);
            }
        } else {
            selectedLocationTreeNode = null;
        }
    }

    public static List<ItemDomainLocation> generateLocationHierarchyList(ItemDomainLocation lowestLocationItem) {
        if (lowestLocationItem != null) {
            List<ItemDomainLocation> itemHerarchyList = new ArrayList<>();
            ItemDomainLocation currentLowestItem = lowestLocationItem;
            ItemElement currentLowestLocationSelfElement;

            itemHerarchyList.add(0, lowestLocationItem);

            // Check if item is linked using location relationship
            currentLowestLocationSelfElement = currentLowestItem.getSelfElement();
            List<ItemElementRelationship> relationshipList = currentLowestLocationSelfElement.getItemElementRelationshipList();

            // Keep track of iterated items to avoid a circular reference 
            List<ItemElement> locationElementList = new ArrayList<>();
            locationElementList.add(currentLowestLocationSelfElement);
            while (relationshipList != null && !relationshipList.isEmpty()) {
                relationshipList = currentLowestLocationSelfElement.getItemElementRelationshipList();
                ItemElementRelationship locationRelationship = findLocationItemElementRelationship(relationshipList);
                if (locationRelationship != null) {
                    currentLowestLocationSelfElement = locationRelationship.getSecondItemElement();
                    if (locationElementList.contains(currentLowestLocationSelfElement)) {
                        logger.warn("Circular reference occured in location relationship check. "
                                + "For lowest location item: " + lowestLocationItem.toString());
                        break;
                    } else {
                        locationElementList.add(currentLowestLocationSelfElement);
                    }
                    itemHerarchyList.add(0, (ItemDomainLocation) currentLowestLocationSelfElement.getParentItem());
                }
            }

            // No longer needed
            locationElementList = null;

            // Use the location herarchy relationship to complete the tree. 
            currentLowestItem = (ItemDomainLocation) currentLowestLocationSelfElement.getParentItem();
            while (currentLowestItem != null) {
                if (currentLowestItem != null) {
                    List<ItemElement> itemElementMembershipList = currentLowestItem.getItemElementMemberList();
                    if (itemElementMembershipList.size() > 0) {
                        ItemElement memberOfItemElement = itemElementMembershipList.get(0);
                        currentLowestItem = (ItemDomainLocation) memberOfItemElement.getParentItem();
                        itemHerarchyList.add(0, currentLowestItem);
                    } else {
                        currentLowestItem = null;
                    }
                }
            }
            return itemHerarchyList;
        }
        return null;
    }

    /**
     * Function generates a treebranch for a specific location (single item on
     * each branch)
     *
     * @param lowestLocationItem lowest desired item of the branch.
     * @return root of tree branch that has item passed at lowest level of
     * branch.
     */
    public static TreeNode generateLocationNodeTreeBranch(ItemDomainLocation lowestLocationItem) {
        if (lowestLocationItem != null) {
            List<ItemDomainLocation> itemHierarchyList = generateLocationHierarchyList(lowestLocationItem);

            if (itemHierarchyList != null) {
                TreeNode rootTreeNode = new DefaultTreeNode(null, null);
                TreeNode prevNode = rootTreeNode;

                for (Item item : itemHierarchyList) {
                    prevNode = ItemUtility.createNewTreeNode(item, prevNode);
                }
                
                // Allow for quick view full location. 
                setExpandedSelectedOnAllChildren(rootTreeNode, true, false);
                if (rootTreeNode.getChildCount() > 0) {
                    rootTreeNode.getChildren().get(0).setExpanded(false);
                }
                

                return rootTreeNode;
            }
        }

        return null;
    }

    public static String generateLocatonHierarchyString(ItemDomainLocation lowestLocationItem) {
        if (lowestLocationItem != null) {
            List<ItemDomainLocation> itemHierarchyList = generateLocationHierarchyList(lowestLocationItem);

            if (itemHierarchyList != null) {
                String result = "";
                for (ItemDomainLocation item : itemHierarchyList) {
                    result += item.getName();
                    // Still more items to load.
                    if (itemHierarchyList.indexOf(item) != itemHierarchyList.size() - 1) {
                        result += " ➜ ";
                    }
                }
                return result;
            }
        }

        return null;
    }

    private void expandTreeBranch(TreeNode childNode) {
        TreeNode parentNode = childNode.getParent();
        if (parentNode != null) {
            parentNode.setExpanded(true);
            expandTreeBranch(parentNode);
        }
    }

    private static void setExpandedSelectedOnAllChildren(TreeNode node, Boolean expanded, Boolean selected) {
        if (expanded != null) {
            node.setExpanded(expanded);
        }
        if (selected != null) {
            node.setSelected(selected);
        }
        for (TreeNode childNode : node.getChildren()) {
            setExpandedSelectedOnAllChildren(childNode, expanded, selected);
        }
    }

    private TreeNode findTreeNodeWithItem(Item item, TreeNode node) {
        Item nodeItem = (Item) node.getData();

        if (nodeItem != null && nodeItem.equals(item)) {
            return node;
        }

        for (TreeNode childNode : node.getChildren()) {
            TreeNode foundNode = findTreeNodeWithItem(item, childNode);
            if (foundNode != null) {
                return foundNode;
            }
        }

        return null;
    }

    @Override
    public boolean getEntityDisplayItemIdentifier1() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemType() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        return false;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return true;
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
        return false;
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
    public String getItemIdentifier1Title() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemIdentifier2Title() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        return "content";
    }

    @Override
    public String getEntityTypeName() {
        return "location";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Location";
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
    public boolean getEntityDisplayItemProject() {
        return false;
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
    protected ItemDomainLocation instenciateNewItemDomainEntity() {
        return new ItemDomainLocation();
    }

    @Override
    protected ItemDomainLocationFacade getEntityDbFacade() {
        return itemDomainLocationFacade; 
    }

}
