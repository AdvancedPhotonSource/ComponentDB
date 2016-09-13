/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named("itemDomainLocationController")
@SessionScoped
public class ItemDomainLocationController extends ItemController {

    private final String ENTITY_TYPE_NAME = "Location";
    private final String DOMAIN_TYPE_NAME = ItemDomainName.location.getValue();
    private final String DOMAIN_HANDLER_NAME = "Location";

    private TreeNode locationsWithInventoryItemsRootNode;

    private TreeNode locationsWithInventoryItemAssemblyRootNode;

    private TreeNode selectedLocationTreeNode;

    @EJB
    ItemFacade itemFacade;

    @EJB
    DomainFacade domainFacade;

    public ItemDomainLocationController() {
        super();
        displayNumberOfItemsPerPage = 25;
        displayDescription = true;
        displayItemListTreeView = true;
    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
        locationsWithInventoryItemsRootNode = null;
        locationsWithInventoryItemAssemblyRootNode = null;
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
        for (TreeNode childNode : currentNode.getChildren()) {
            addAssemblyTreeToLocationTree(childNode);
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
                TreeNode newAssemblyNode = createNewTreeNode(containedItem, assemblyNode);

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

    private void addLocationRelationshipsToParentTreeNode(Item item, TreeNode parentTreeNode) {
        ItemElement selfElement = item.getSelfElement();

        // ItemElementRelationshipList1 is for when item is second (item located in secondItem). 
        // location is secondItem. 
        List<ItemElementRelationship> relationshipList = selfElement.getItemElementRelationshipList1();

        String locationRelationshipTypeName = ItemElementRelationshipTypeNames.itemLocation.getValue();
        for (ItemElementRelationship ier : relationshipList) {
            if (ier.getRelationshipType().getName().equals(locationRelationshipTypeName)) {
                ItemElement inventoryItemSelfElement = ier.getFirstItemElement();
                Item inventoryItem = inventoryItemSelfElement.getParentItem();
                TreeNode currentTreeNode = createNewTreeNode(inventoryItem, parentTreeNode);
                // TODO handle circular location reference
                addLocationRelationshipsToParentTreeNode(inventoryItem, currentTreeNode);
            }
        }
    }

    private static TreeNode createNewTreeNode(Item item, TreeNode parentTreeNode) {
        String treeNodeType = item.getDomain().getName();
        return new DefaultTreeNode(treeNodeType, item, parentTreeNode);
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
    
    public static List<Item> generateLocationHierarchyList(Item lowestLocationItem) {
        if (lowestLocationItem != null) {
            List<Item> itemHerarchyList = new ArrayList<>();
            Item currentLowestItem = lowestLocationItem;
            ItemElement currentLowestLocationSelfElement;

            itemHerarchyList.add(0, lowestLocationItem);

            // Check if item is linked using location relationship
            currentLowestLocationSelfElement = currentLowestItem.getSelfElement();
            List<ItemElementRelationship> relationshipList = currentLowestLocationSelfElement.getItemElementRelationshipList();
            while (relationshipList != null && !relationshipList.isEmpty()) {
                relationshipList = currentLowestLocationSelfElement.getItemElementRelationshipList();
                ItemElementRelationship locationRelationship = findLocationItemElementRelationship(relationshipList);
                if (locationRelationship != null) {
                    currentLowestLocationSelfElement = locationRelationship.getSecondItemElement();
                    itemHerarchyList.add(0, currentLowestLocationSelfElement.getParentItem());
                }
            }

            // Use the location herarchy relationship to complete the tree. 
            currentLowestItem = currentLowestLocationSelfElement.getParentItem();
            while (currentLowestItem != null) {
                if (currentLowestItem != null) {
                    List<ItemElement> itemElementMembershipList = currentLowestItem.getItemElementMemberList();
                    if (itemElementMembershipList.size() > 0) {
                        ItemElement memberOfItemElement = itemElementMembershipList.get(0);
                        currentLowestItem = memberOfItemElement.getParentItem();
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
    public static TreeNode generateLocationNodeTreeBranch(Item lowestLocationItem) {
        if (lowestLocationItem != null) {
            List<Item> itemHierarchyList = generateLocationHierarchyList(lowestLocationItem);

            if (itemHierarchyList != null) {
                TreeNode rootTreeNode = new DefaultTreeNode(null, null);
                TreeNode prevNode = rootTreeNode;

                for (Item item : itemHierarchyList) {
                    prevNode = createNewTreeNode(item, prevNode);
                }

                return rootTreeNode;
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

    private void setExpandedSelectedOnAllChildren(TreeNode node, Boolean expanded, Boolean selected) {
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
    public Domain getDefaultDomain() {
        return domainFacade.findByName(DOMAIN_TYPE_NAME);
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
    public String getDomainHandlerName() {
        return DOMAIN_HANDLER_NAME;
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
    public String getItemDerivedFromDomainHandlerName() {
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
    public String getDerivedDomainName() {
        return null;
    }

}
