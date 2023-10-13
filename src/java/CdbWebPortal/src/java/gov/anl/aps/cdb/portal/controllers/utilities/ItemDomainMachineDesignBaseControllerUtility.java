/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.beans.EntityTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainApp;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
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
            if ((newAssignedItem instanceof ItemDomainCatalog || newAssignedItem instanceof ItemDomainInventory || newAssignedItem instanceof ItemDomainApp) == false) {
                throw new CdbException("The new assigned item must be either catalog, inventory or app item.");
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

    public ItemElement performMachineMove(
            ItemDomainMachineDesign newParent, ItemDomainMachineDesign child, UserInfo sessionUser) throws CdbException {

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

        return currentItemElement;
    }

    public ItemElement performMachineMoveWithUpdate(
            ItemDomainMachineDesign newParent, ItemDomainMachineDesign child, UserInfo sessionUser) throws CdbException {
        ItemElement currentItemElement = performMachineMove(newParent, child, sessionUser);
        update(newParent, sessionUser);
        return currentItemElement;
    }

    /**
     * Function used to create a new machine from a template hierarchically.
     *
     * @param machineElement (if null then top level) parent element of the new
     * machine design created from template.
     * @param templateItem template used to create a new item.
     * @param user user creating the new machine design from template.
     * @param ownerGroup owner group of the new machine design created from
     * template
     * @param machineDesignNameList key value pairs for filling in parameters in
     * template name ex {varName}. Generate using
     * generateMachineDesignTemplateNameListRecursivelly()
     *
     * @throws CdbException
     * @throws CloneNotSupportedException
     */
    public ItemDomainMachineDesign createMachineDesignFromTemplateHierachically(ItemElement machineElement, ItemDomainMachineDesign templateItem, UserInfo user, UserGroup ownerGroup, List<KeyValueObject> machineDesignNameList) throws CdbException, CloneNotSupportedException {
        ItemDomainMachineDesign newMachine = createMachineDesignFromTemplate(machineElement, templateItem, user, ownerGroup, machineDesignNameList);
        createMachineDesignFromTemplateHierachically(newMachine, machineDesignNameList);

        return newMachine;
    }

    private void createMachineDesignFromTemplateHierachically(ItemDomainMachineDesign machineItem, List<KeyValueObject> machineDesignNameList) throws CdbException, CloneNotSupportedException {
        UserInfo ownerUser = machineItem.getOwnerUser();
        UserGroup ownerGroup = machineItem.getOwnerUserGroup();

        List<ItemElement> itemElementDisplayList = machineItem.getItemElementDisplayList();
        for (ItemElement ie : itemElementDisplayList) {
            ItemDomainMachineDesign templateRef = (ItemDomainMachineDesign) ie.getContainedItem();
            Boolean isItemTemplate = templateRef.getIsItemTemplate();
            if (isItemTemplate) {
                ItemDomainMachineDesign result = createMachineDesignFromTemplate(ie, templateRef, ownerUser, ownerGroup, machineDesignNameList);
                createMachineDesignFromTemplateHierachically(result, machineDesignNameList);
            }
        }
    }

    /**
     * Function used to create a new machine from a template.
     *
     * @param machineElement (if null then top level) parent element of the new
     * machine design created from template.
     * @param templateItem template used to create a new item.
     * @param user user creating the new machine design from template.
     * @param ownerGroup owner group of the new machine design created from
     * template
     * @param machineDesignNameList key value pairs for filling in parameters in
     * template name ex {varName}. Generate using
     * generateMachineDesignTemplateNameListRecursivelly()
     * @return
     * @throws CdbException
     * @throws CloneNotSupportedException
     */
    public ItemDomainMachineDesign createMachineDesignFromTemplate(
            ItemElement machineElement,
            ItemDomainMachineDesign templateItem,
            UserInfo user,
            UserGroup ownerGroup,
            List<KeyValueObject> machineDesignNameList) throws CdbException, CloneNotSupportedException {

        boolean cloneProperties = true;
        boolean cloneSources = false;
        boolean cloneCreateItemElementPlaceholders = false;

        ItemDomainMachineDesign newItem = (ItemDomainMachineDesign) templateItem.clone(user, ownerGroup, cloneProperties, cloneSources, cloneCreateItemElementPlaceholders);

        // Top level 
        if (machineElement != null) {
            machineElement.setContainedItem(newItem);
            newItem.appendItemElementMemberList(machineElement);
        }

        assignTemplateInfoToMd(newItem, templateItem, user, machineDesignNameList);

        // ensure uniqueness of template creation.
        String viewUUID = newItem.getViewUUID();
        newItem.setItemIdentifier2(viewUUID);
        newItem.setEntityTypeList(new ArrayList<>());

        return newItem;
    }

    public ValidInfo assignTemplateToItem(
            ItemDomainMachineDesign item,
            ItemDomainMachineDesign template,
            UserInfo ownerUser,
            List<KeyValueObject> machineDesignNameList) {

        boolean isValid = true;
        String validString = "";

        try {
            assignTemplateInfoToMd(item, template, ownerUser, machineDesignNameList);
            createMachineDesignFromTemplateHierachically(item, machineDesignNameList);
        } catch (CdbException | CloneNotSupportedException ex) {
            isValid = false;
            validString = ex.getMessage();
        }

        return new ValidInfo(isValid, validString);
    }

    private void assignTemplateInfoToMd(ItemDomainMachineDesign item,
            ItemDomainMachineDesign template,
            UserInfo ownerUser,
            List<KeyValueObject> machineDesignNameList) throws CdbException {

        setMachineDesginIdentifiersFromTemplateItem(template, item, machineDesignNameList);
        addCreatedFromTemplateRelationshipToItem(item, template);

        ItemElement representsCatalogElement = template.getRepresentsCatalogElement();
        if (representsCatalogElement == null) {
            Item assignedItem = template.getAssignedItem();
            updateAssignedItem(item, assignedItem, ownerUser);
        } else {
            ItemDomainMachineDesign parentMachineDesign = item.getParentMachineDesign();
            if (parentMachineDesign == null) {
                throw new CdbException("Representing machine elements require a parent machine with assignment of correct catalog item.");
            }
            Item assignedItem = parentMachineDesign.getAssignedItem();
            if (assignedItem == null) {
                throw new CdbException("Representing machine parent does not have assigned item but requires parent assembly of representing element.");
            }

            Item parentItem = representsCatalogElement.getParentItem();
            if (parentItem.equals(assignedItem) == false) {
                throw new CdbException("Parent of machine element does not have correct assigned item to fullfil the represented element.");
            }

            item.setRepresentsCatalogElement(representsCatalogElement);
        }

        cloneCreateItemElements(item, template, ownerUser, true, true, true);

        item.resetItemElementVars();
    }

    public void setMachineDesginIdentifiersFromTemplateItem(ItemDomainMachineDesign templateItem, ItemDomainMachineDesign mdItem, List<KeyValueObject> machineDesignNameList) {
        String machineDesignName = generateMachineDesignNameForTemplateItem(templateItem.getName(), machineDesignNameList);
        mdItem.setName(machineDesignName);
        String alternateName = generateMachineDesignNameForTemplateItem(templateItem.getItemIdentifier1(), machineDesignNameList);
        mdItem.setItemIdentifier1(alternateName);
    }

    public List<KeyValueObject> generateTemplateVarsForSelectedMdCreatedFromTemplate(ItemDomainMachineDesign mdCreatedFromTemplate) {
        List<KeyValueObject> machineDesignNameList = new ArrayList<>();

        generateTemplateVarsForSelectedMdCreatedFromTemplateRecursivelly(mdCreatedFromTemplate, machineDesignNameList);

        return machineDesignNameList;
    }

    private void generateTemplateVarsForSelectedMdCreatedFromTemplateRecursivelly(ItemDomainMachineDesign itemDomainMachineDesign, List<KeyValueObject> machineDesignNameList) {
        ItemDomainMachineDesign createdFromTemplate = (ItemDomainMachineDesign) itemDomainMachineDesign.getCreatedFromTemplate();

        if (createdFromTemplate != null) {
            generateMachineDesignTemplateNameVars(createdFromTemplate, machineDesignNameList);
        }

        List<ItemElement> itemElementDisplayList = itemDomainMachineDesign.getItemElementDisplayList();

        for (ItemElement itemElement : itemElementDisplayList) {
            ItemDomainMachineDesign containedItem = (ItemDomainMachineDesign) itemElement.getContainedItem();
            generateTemplateVarsForSelectedMdCreatedFromTemplateRecursivelly(containedItem, machineDesignNameList);
        }
    }

    public List<KeyValueObject> generateMachineDesignTemplateNameListRecursivelly(ItemDomainMachineDesign template) {
        List<KeyValueObject> machineDesignNameList = new ArrayList<>();

        generateMachineDesignTemplateNameListRecursivelly(template, machineDesignNameList);

        return machineDesignNameList;
    }

    private void generateMachineDesignTemplateNameListRecursivelly(ItemDomainMachineDesign template, List<KeyValueObject> machineDesignNameList) {
        generateMachineDesignTemplateNameVars(template, machineDesignNameList);

        for (ItemElement ie : template.getItemElementDisplayList()) {
            ItemDomainMachineDesign machineDesignTemplate = (ItemDomainMachineDesign) ie.getContainedItem();
            if (machineDesignTemplate != null) {
                generateMachineDesignTemplateNameListRecursivelly(machineDesignTemplate, machineDesignNameList);
            }
        }
    }

    public List<KeyValueObject> generateMachineDesignTemplateNameVars(ItemDomainMachineDesign template) {
        List<KeyValueObject> machineDesignNameList = new ArrayList<>();

        generateMachineDesignTemplateNameVars(template, machineDesignNameList);

        return machineDesignNameList;
    }

    private void generateMachineDesignTemplateNameVars(ItemDomainMachineDesign template, List<KeyValueObject> machineDesignNameList) {
        String name = template.getName();
        String alternateName = template.getItemIdentifier1();
        appendMachineDesignNameList(name, machineDesignNameList);
        appendMachineDesignNameList(alternateName, machineDesignNameList);
    }

    private void appendMachineDesignNameList(String templateIdentifier, List<KeyValueObject> machineDesignNameList) {
        if (templateIdentifier == null) {
            return;
        }
        int firstVar = templateIdentifier.indexOf('{');
        int secondVar;

        while (firstVar != -1) {
            templateIdentifier = templateIdentifier.substring(firstVar);
            secondVar = templateIdentifier.indexOf('}');

            String key = templateIdentifier.substring(1, secondVar);

            KeyValueObject keyValue = new KeyValueObject(key);

            if (machineDesignNameList.contains(keyValue) == false) {
                machineDesignNameList.add(keyValue);
            }

            templateIdentifier = templateIdentifier.substring(secondVar + 1);

            firstVar = templateIdentifier.indexOf('{');
        }
    }

    public String generateMachineDesignNameForTemplateItem(String templateIdentifierString, List<KeyValueObject> machineDesignNameList) {
        if (templateIdentifierString == null) {
            return templateIdentifierString;
        }

        if (machineDesignNameList != null) {
            for (KeyValueObject kv : machineDesignNameList) {
                if (kv.getValue() != null && !kv.getValue().equals("")) {
                    String originalText = "{" + kv.getKey() + "}";
                    templateIdentifierString = templateIdentifierString.replace(originalText, kv.getValue());
                }
            }
        }

        return templateIdentifierString;
    }

    /**
     * Function used to validate and complete a assignment of assigned item in
     * machine design
     *
     * @param mdItem machine design whose assigned item is updated.
     * @param newAssignment new assigned item
     * @param userInfo user doing the assignment
     * @throws gov.anl.aps.cdb.common.exceptions.CdbException
     */
    public void updateAssignedItem(ItemDomainMachineDesign mdItem, Item newAssignment, UserInfo userInfo) throws CdbException {
        updateAssignedItem(mdItem, newAssignment, userInfo, null);
    }

    /**
     * Function used to validate and complete a assignment of assigned item in
     * machine design
     *
     * @param mdItem machine design whose assigned item is updated.
     * @param newAssignment new assigned item
     * @param userInfo user doing the assignment
     * @param isInstalled planned/installed for inventory item. Invalid for
     * items other than inventory.
     * @throws CdbException
     */
    public void updateAssignedItem(ItemDomainMachineDesign mdItem, Item newAssignment, UserInfo userInfo, Boolean isInstalled) throws CdbException {
        if (newAssignment != null) {
            if (isInstalled != null && (newAssignment instanceof ItemDomainInventory) == false) {
                throw new CdbException("Is installed can only be set for inventory assignments.");
            }

            if (newAssignment instanceof ItemDomainInventory == false) {
                isInstalled = null;
            }
        } else {
            isInstalled = null;
        }

        if (isInstalled == null) {
            // Reset to default
            isInstalled = true;
        }

        // Ensure it is only installed in one place.
        if (newAssignment instanceof ItemDomainInventory) {
            for (ItemElement itemElement : newAssignment.getItemElementMemberList2()) {
                Item item = itemElement.getParentItem();
                if (item instanceof ItemDomainMachineDesign) {
                    // Allow updates for the same item. 
                    if (!mdItem.equals(item)) {
                        String exMessage = "Inventory item used. Inventory item cannot be saved, used in: " + item.toString();
                        if (item.getIsItemDeleted()) {
                            exMessage = exMessage + " (located in trash)";
                        }
                        throw new CdbException(exMessage);
                    }
                }
            }
        }

        ItemElement representsCatalogElement = mdItem.getRepresentsCatalogElement();
        if (representsCatalogElement != null) {
            throw new CdbException("Cannot update assigned item of machine design. Must update parent assembly for promotoed machine elements.");
        }

        // Verify that items need to be of specific catalog type.
        Item mustRepCatalog = null;
        if (mdItem.getItemElementDisplayList().size() > 0) {
            List<ItemElement> itemElementDisplayList = mdItem.getItemElementDisplayList();
            for (ItemElement ie : itemElementDisplayList) {
                ItemDomainMachineDesign containedMd = (ItemDomainMachineDesign) ie.getContainedItem();
                ItemElement repElement = containedMd.getRepresentsCatalogElement();
                if (repElement != null) {
                    mustRepCatalog = repElement.getParentItem();
                    break;
                }
            }
        }

        if (mustRepCatalog != null) {
            if (newAssignment == null) {
                // Clear can only be done to the catalog item.
                newAssignment = mustRepCatalog;
            }
            Item installCatalog = null;

            if (newAssignment instanceof ItemDomainInventory) {
                installCatalog = newAssignment.getDerivedFromItem();
            } else {
                installCatalog = newAssignment;
            }

            if (installCatalog.equals(mustRepCatalog) == false) {
                throw new CdbException("Existing assembly representing machine elements exist. The assigned item can only be of catalog type: " + mustRepCatalog);
            }
        }

        Item originalContainedItem = mdItem.getAssignedItem();
        mdItem.setAssignedItem(newAssignment);
        mdItem.setIsHoused(isInstalled);

        updateTemplateReferenceForAssignedItem(mdItem, originalContainedItem, newAssignment, userInfo);
    }

    /**
     * Updates assigned item on elements created from template if update is
     * valid.
     *
     * @param currentElement
     * @param newAssignedItem
     */
    private void updateTemplateReferenceForAssignedItem(
            ItemDomainMachineDesign mdItem,
            Item originalContainedItem,
            Item newAssignedItem, UserInfo userInfo) throws CdbException {

        if (mdItem.getIsItemTemplate()) {
            List<ItemDomainMachineDesign> itemsCreatedFromThisTemplateItem
                    = (List<ItemDomainMachineDesign>) (List<?>) mdItem.getItemsCreatedFromThisTemplateItem();

            for (ItemDomainMachineDesign item : itemsCreatedFromThisTemplateItem) {
                Item assignedItem = item.getAssignedItem();

                // Verify if in sync with template
                if (ObjectUtility.equals(originalContainedItem, assignedItem)) {
                    item.setAssignedItem(newAssignedItem);
                    mdItem.addItemToUpdate(item);
                }
            }
        }
    }

    public void updateRepresentingAssemblyElementForMachine(ItemDomainMachineDesign node, ItemElement element, boolean matchElementNamesForTemplateInstances) throws InvalidArgument, InvalidObjectState {
        node.clearItemsToUpdate();
        updateRepresentingAssemblyElementForMachine(node, element, matchElementNamesForTemplateInstances, false);
    }

    private void updateRepresentingAssemblyElementForMachine(ItemDomainMachineDesign node, ItemElement representedElement, boolean matchElementNamesForTemplateInstances, boolean updateFromTemplate) throws InvalidArgument, InvalidObjectState {
        // Ensure this can only be applied for standard machine elements and template items. 
        if (node.getEntityTypeList().size() == 1) {
            if (node.getIsItemTemplate() == false) {
                String entityTypeString = node.getEntityTypeString();
                throw new InvalidObjectState("Cannot apply representing element for " + entityTypeString + " type items.");
            }
        } else if (node.getEntityTypeList().size() > 1) {
            String entityTypeString = node.getEntityTypeString();
            throw new InvalidObjectState("Cannot apply representing element for " + entityTypeString + " type items.");
        }

        // Do not allow update for machine created from template.  
        if (updateFromTemplate == false) {
            Item template = node.getCreatedFromTemplate();
            if (template != null) {
                String errMessage = node.getName();
                errMessage += " is created from template " + template.getName();
                errMessage += ". Please update template to update representing element for this node.";
                throw new InvalidObjectState(errMessage);
            }
        }
        
        ItemDomainMachineDesign parentMachineDesign = node.getParentMachineDesign();
        if (representedElement != null && parentMachineDesign.getRepresentsCatalogElement() != null) {
            String errMessage = node.getName();
            errMessage += " has a parent " + parentMachineDesign.getName();
            errMessage += " with represented element reference.";
            throw new InvalidObjectState(errMessage);
        }

        List<ItemElement> elements = fetchElementsAvaiableForNodeRepresentation(node);

        // Updating of element 
        if (representedElement != null) {
            if (elements != null && elements.contains(representedElement)) {
                // Valid element. Update the element set. 
                if (node.getAssignedItem() != null) {
                    String errMessage = node.getName();
                    errMessage += " has an assigned item set.";
                    throw new InvalidObjectState(errMessage);
                }
            } else {
                boolean found = false;
                if (elements != null && matchElementNamesForTemplateInstances && updateFromTemplate) {
                    String elementName = representedElement.getName();
                    for (ItemElement assemblyElement : elements) {
                        if (assemblyElement.getName().equals(elementName)) {
                            found = true;
                            representedElement = assemblyElement;
                            break;
                        }
                    }
                }

                if (!found) {
                    throw new InvalidArgument("Element provided is not an available element for representation for node " + node.getName() + ".");
                }
            }
        } else {
            // No additional validation needed for clearing represented element. 
        }

        node.setRepresentsCatalogElement(representedElement);

        if (node.getIsItemTemplate()) {
            List<ItemDomainMachineDesign> itemsCreatedFromThisTemplateItem
                    = (List<ItemDomainMachineDesign>) (List<?>) node.getItemsCreatedFromThisTemplateItem();

            for (ItemDomainMachineDesign nodeFromTemplate : itemsCreatedFromThisTemplateItem) {
                updateRepresentingAssemblyElementForMachine(nodeFromTemplate, representedElement, matchElementNamesForTemplateInstances, true);
                node.addItemToUpdate(nodeFromTemplate);
            }

        }
    }

    public List<ItemElement> fetchElementsAvaiableForNodeRepresentation(ItemDomainMachineDesign node) {
        ItemDomainMachineDesign parentMachineDesign = node.getParentMachineDesign();

        List<ItemElement> availableElementsForNodeRepresentation = null;

        if (parentMachineDesign != null) {
            Item assignedCat = parentMachineDesign.getAssignedItem();
            if (assignedCat != null) {
                if (assignedCat.getDerivedFromItem() != null) {
                    assignedCat = assignedCat.getDerivedFromItem();
                }

                if (!assignedCat.getItemElementDisplayList().isEmpty()) {
                    availableElementsForNodeRepresentation = new ArrayList<>();
                    availableElementsForNodeRepresentation.addAll(assignedCat.getItemElementDisplayList());

                    // Remove any already assigned elements.
                    List<ItemElement> machineChildren = parentMachineDesign.getItemElementDisplayList();

                    for (ItemElement machineChild : machineChildren) {
                        ItemDomainMachineDesign containedItem = (ItemDomainMachineDesign) machineChild.getContainedItem();
                        ItemElement representingElement = containedItem.getRepresentsCatalogElement();
                        availableElementsForNodeRepresentation.remove(representingElement);
                    }
                }
            }
        }

        return availableElementsForNodeRepresentation;
    }
    
    public void unassignMachineTemplate(List<ItemElement> machineElementList, UserInfo updateUser) throws CdbException {
        List<ItemElementRelationship> relationshipsToDestroy = new ArrayList<>();        
        List<ItemDomainMachineDesign> machinesToUpdate = new ArrayList<>();
        ItemElementRelationshipControllerUtility ieru = new ItemElementRelationshipControllerUtility();
        
        for (ItemElement element : machineElementList) {
            boolean updateItem = false; 
            if (element.getDerivedFromItemElement() != null) {
                element.setDerivedFromItemElement(null);
                updateItem = true; 
            }
            ItemDomainMachineDesign containedItem = (ItemDomainMachineDesign) element.getContainedItem();
            ItemElementRelationship templateRelationship = containedItem.getCreatedFromTemplateRelationship();
            if (templateRelationship != null) { 
                updateItem = true; 
                relationshipsToDestroy.add(templateRelationship);
                List<ItemElement> childElements = containedItem.getItemElementDisplayList();

                for (ItemElement derivedElement : childElements) {
                    derivedElement.setDerivedFromItemElement(null);                
                }
            }
            if (updateItem) {
                machinesToUpdate.add(containedItem);         
            }
        }
        
        
        updateList(machinesToUpdate, updateUser);
        ieru.destroyList(relationshipsToDestroy, null, updateUser);
    }

}
