/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.EntityTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author darek
 */
public abstract class ItemDomainMachineDesignBaseControllerUtility extends ItemControllerUtility<ItemDomainMachineDesign, ItemDomainMachineDesignFacade> {

    private static final Logger logger = LogManager.getLogger(ItemDomainMachineDesignBaseControllerUtility.class.getName());

    EntityTypeFacade entityTypeFacade;

    public ItemDomainMachineDesignBaseControllerUtility() {
        super();
        entityTypeFacade = EntityTypeFacade.getInstance();
    }

    @Override
    protected boolean verifyItemNameCombinationUniqueness(Item item) {
        boolean unique = super.verifyItemNameCombinationUniqueness(item);

        // Ensure all machine designs are unique
        if (!unique) {
            String viewUUID = item.getViewUUID();
            item.setItemIdentifier2(viewUUID);
            unique = true;
        }

        return unique;
    }

    @Override
    public void checkItem(ItemDomainMachineDesign item) throws CdbException {
        super.checkItem(item);

        if (item.getIsItemTemplate()) {
            List<ItemElement> itemElementMemberList = item.getItemElementMemberList();
            if (itemElementMemberList == null || itemElementMemberList.isEmpty()) {
                // Item is not a child of another item. 
                if (!verifyValidTemplateName(item.getName())) {
                    throw new CdbException("Place parements within {} in template name. Example: 'templateName {paramName}'");
                }
            }
        }

        Item newAssignedItem = item.getAssignedItem();
        if (newAssignedItem != null) {
            if ((newAssignedItem instanceof ItemDomainCatalog || newAssignedItem instanceof ItemDomainInventory) == false) {
                throw new CdbException("The new assigned item must be either catalog or inventory item.");
            }

            Integer itemId = item.getId();
            if (itemId != null) {
                ItemDomainMachineDesign originalItem = findById(itemId);

                Item origAssignedItem = originalItem.getAssignedItem();

                if (origAssignedItem != null) {
                    ItemDomainCatalog catItem = null;
                    if (origAssignedItem instanceof ItemDomainInventory) {
                        catItem = ((ItemDomainInventory) origAssignedItem).getCatalogItem();
                    } else if (origAssignedItem instanceof ItemDomainCatalog) {
                        catItem = (ItemDomainCatalog) origAssignedItem;
                    }

                    if (newAssignedItem instanceof ItemDomainInventory) {
                        List<ItemDomainInventory> inventoryItemList = catItem.getInventoryItemList();
                        if (inventoryItemList.contains(newAssignedItem) == false) {
                            throw new CdbException("The new assigned inventory item must be of catalog item: " + catItem.getName() + ".");
                        }
                    }
                }
            }
        }
    }

    private boolean verifyValidTemplateName(String templateName) {
        boolean validTitle = false;
        if (templateName.contains("{")) {
            int openBraceIndex = templateName.indexOf("{");
            int closeBraceIndex = templateName.indexOf("}");
            if (openBraceIndex < closeBraceIndex) {
                validTitle = true;
            }
        }

        return validTitle;
    }

    /**
     * Used by import framework. Looks up entity by path.
     */
    @Override
    public ItemDomainMachineDesign findByPath(String path) throws CdbException {
        return findByPath_(path, ItemDomainMachineDesign::getParentMachineDesign);
    }

    @Override
    public boolean isEntityHasItemIdentifier2() {
        return false;
    }

    @Override
    public boolean isEntityHasQrId() {
        //TODO add a machine design template and inventory and override with false; 
        return true;
    }

    @Override
    public boolean isEntityHasName() {
        return true;
    }

    @Override
    public boolean isEntityHasProject() {
        return true;
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.machineDesign.getValue();
    }

    @Override
    protected ItemDomainMachineDesignFacade getItemFacadeInstance() {
        return ItemDomainMachineDesignFacade.getInstance();
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    protected ItemDomainMachineDesign instenciateNewItemDomainEntity() {
        return new ItemDomainMachineDesign();
    }

    public TreeNode getSearchResults(String searchString, boolean caseInsensitive) {
        LinkedList<SearchResult> searchResultList = this.performEntitySearch(searchString, caseInsensitive);
        return getHierarchicalSearchResults(searchResultList);
    }

    public TreeNode getHierarchicalSearchResults(LinkedList<SearchResult> searchResultList) {
        TreeNode searchResultsTreeNode;

        TreeNode rootTreeNode = new DefaultTreeNode();
        if (searchResultList != null) {
            for (SearchResult result : searchResultList) {
                result.setRowStyle(SearchResult.SEARCH_RESULT_ROW_STYLE);

                ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) result.getCdbEntity();

                ItemDomainMachineDesign parent = mdItem.getParentMachineDesign();

                TreeNode resultNode = new DefaultTreeNode(result);

                List<ItemDomainMachineDesign> parents = new ArrayList<>();

                while (parent != null) {
                    parents.add(parent);
                    parent = parent.getParentMachineDesign();
                }

                TreeNode currentRoot = rootTreeNode;

                // Combine common parents 
                parentSearch:
                for (int i = parents.size() - 1; i >= 0; i--) {
                    ItemDomainMachineDesign currentParent = parents.get(i);

                    List<TreeNode> children = currentRoot.getChildren();
                    for (TreeNode node : children) {
                        Object data = node.getData();
                        SearchResult searchResult = (SearchResult) data;
                        CdbEntity cdbEntity = searchResult.getCdbEntity();
                        ItemDomainMachineDesign itemResult = (ItemDomainMachineDesign) cdbEntity;

                        if (itemResult.equals(currentParent)) {
                            currentRoot = node;
                            continue parentSearch;
                        }
                    }

                    // Need to create parentNode
                    SearchResult parentResult = new SearchResult(currentParent, currentParent.getId(), currentParent.getName());
                    parentResult.addAttributeMatch("Reason", "Parent of Result");

                    TreeNode newRoot = new DefaultTreeNode(parentResult);
                    newRoot.setExpanded(true);
                    currentRoot.getChildren().add(newRoot);
                    currentRoot = newRoot;
                }

                currentRoot.getChildren().add(resultNode);

                List<ItemElement> childElements = mdItem.getItemElementDisplayList();

                for (ItemElement childElement : childElements) {
                    Item mdChild = childElement.getContainedItem();
                    SearchResult childResult = new SearchResult(mdChild, mdChild.getId(), mdChild.getName());
                    childResult.addAttributeMatch("Reason", "Child of result");

                    TreeNode resultChildNode = new DefaultTreeNode(childResult);
                    resultNode.getChildren().add(resultChildNode);
                }
            }
        }
        searchResultsTreeNode = rootTreeNode;
        return searchResultsTreeNode;
    }

    public void syncMachineDesignConnectors(ItemDomainMachineDesign item) {
        List<ItemConnector> itemConnectorList = item.getItemConnectorList();
        List<ItemConnector> connectorsFromAssignedCatalogItem = getConnectorsFromAssignedCatalogItem(item);

        if (connectorsFromAssignedCatalogItem == null) {
            return;
        }

        if (itemConnectorList.size() == 0) {
            // Sync all connectors into machine design
            for (ItemConnector cconnector : connectorsFromAssignedCatalogItem) {
                ItemConnector mdConnector = cloneConnectorForMachineDesign(cconnector, item);

                itemConnectorList.add(mdConnector);
            }
        } else {
            // Verify if any new connections were created on the catalog             
            if (connectorsFromAssignedCatalogItem != null) {

                catConnFor:
                for (ItemConnector catalogItemConn : connectorsFromAssignedCatalogItem) {
                    for (ItemConnector mdItemConn : itemConnectorList) {
                        Connector mdConnector = mdItemConn.getConnector();
                        Connector catConnector = catalogItemConn.getConnector();

                        if (mdConnector.equals(catConnector)) {
                            continue catConnFor;
                        }
                    }
                    ItemConnector mdConnector = cloneConnectorForMachineDesign(catalogItemConn, item);
                    itemConnectorList.add(mdConnector);
                }
            }
        }
    }

    private static List<ItemConnector> getConnectorsFromAssignedCatalogItem(ItemDomainMachineDesign item) {
        Item assignedItem = item.getAssignedItem();

        Item catalogItem = null;
        if (assignedItem instanceof ItemDomainInventory) {
            catalogItem = ((ItemDomainInventory) assignedItem).getCatalogItem();
        } else if (assignedItem instanceof ItemDomainCatalog) {
            catalogItem = assignedItem;
        }

        if (catalogItem != null) {
            return catalogItem.getItemConnectorList();
        }
        return null;
    }

    private ItemConnector cloneConnectorForMachineDesign(ItemConnector catalogConnector, ItemDomainMachineDesign mdItem) {
        ItemConnector mdConnector = new ItemConnector();

        mdConnector.setConnector(catalogConnector.getConnector());
        mdConnector.setItem(mdItem);

        return mdConnector;
    }

    @Override
    protected ItemElement createItemElement(ItemDomainMachineDesign item, EntityInfo entityInfo) {
        ItemElement newElement = super.createItemElement(item, entityInfo);

        Item parentItem = newElement.getParentItem();

        int elementSize = parentItem.getItemElementDisplayList().size();
        float sortOrder = elementSize;
        newElement.setSortOrder(sortOrder);

        return newElement;
    }

    public ItemDomainMachineDesign createEntityInstanceBasedOnParent(ItemDomainMachineDesign parentMachine, UserInfo sessionUser) throws CdbException {
        ItemDomainMachineDesign newItem = createEntityInstance(sessionUser);

        newItem.setItemProjectList(parentMachine.getItemProjectList());

        if (parentMachine.getIsItemTemplate()) {
            List<EntityType> entityTypeList = new ArrayList<>();
            EntityType templateEntity = entityTypeFacade.findByName(EntityTypeName.template.getValue());
            entityTypeList.add(templateEntity);
            newItem.setEntityTypeList(entityTypeList);
        }

        return newItem;
    }

    public ItemElement prepareMachinePlaceholder(ItemDomainMachineDesign parentMachine, UserInfo sessionUser) throws CdbException {
        ItemDomainMachineDesign newItem = createEntityInstanceBasedOnParent(parentMachine, sessionUser);

        ItemElement itemElement = createItemElement(parentMachine, sessionUser);
        itemElement.setContainedItem(newItem);

        return itemElement;
    }

    public ItemElement performMachineMove(ItemDomainMachineDesign newParent, ItemDomainMachineDesign child, UserInfo sessionUser) throws CdbException {
        if ((newParent instanceof ItemDomainMachineDesign && child instanceof ItemDomainMachineDesign) == false) {
            throw new CdbException("Both items provided must be of type machine design");
        }
        if ((newParent.getEntityTypeList().isEmpty() && child.getEntityTypeList().isEmpty()) == false) {
            throw new CdbException("Moving machines is currently only supported for standard machines.");
        }

        ItemElement currentItemElement = child.getParentMachineElement();

        // Continue to reassignment of parent.        
        if (currentItemElement != null) {
            String uniqueName = generateUniqueElementNameForItem(newParent);
            currentItemElement.setName(uniqueName);
            currentItemElement.setParentItem(newParent);
        } else {
            // Dragging in top level            
            currentItemElement = createItemElement(newParent, sessionUser);
            currentItemElement.setContainedItem(child);
        }

        prepareAddItemElement(newParent, currentItemElement);

        update(newParent, sessionUser);

        return currentItemElement;
    }

}
