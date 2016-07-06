package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.InventoryBillOfMaterialItemStates;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.DomainHandlerFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.InventoryBillOfMaterialItem;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.DefaultTreeNode;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author djarosz
 */
@Named("itemDomainInventoryController")
@SessionScoped
public class ItemDomainInventoryController extends ItemController {

    private final String DOMAIN_TYPE_NAME = "Inventory";
    private final String LOCATION_RELATIONSHIP_TYPE_NAME = "Location";
    private final String DEFAULT_DOMAIN_NAME = "Inventory";
    private final String DOMAIN_HANDLER_NAME = "Inventory";
    private final String DERIVED_ITEM_DOMAIN_HANDLER_NAME = "Catalog";

    private final String ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP = "itemElementInstantiation";

    /*
     * Controller specific settings
     */
    private static final String DisplayCreatedByUserSettingTypeKey = "ComponentInstance.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "ComponentInstance.List.Display.CreatedOnDateTime";
    private static final String DisplayDescriptionSettingTypeKey = "ComponentInstance.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "ComponentInstance.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "ComponentInstance.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "ComponentInstance.List.Display.LastModifiedOnDateTime";
    private static final String DisplayLocationDetailsSettingTypeKey = "ComponentInstance.List.Display.LocationDetails";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentInstance.List.Display.NumberOfItemsPerPage";
    private static final String DisplayOwnerUserSettingTypeKey = "ComponentInstance.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "ComponentInstance.List.Display.OwnerGroup";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "ComponentInstance.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "ComponentInstance.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "ComponentInstance.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "ComponentInstance.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "ComponentInstance.List.Display.PropertyTypeId5";
    private static final String DisplayQrIdSettingTypeKey = "ComponentInstance.List.Display.QrId";
    private static final String DisplaySerialNumberSettingTypeKey = "ComponentInstance.List.Display.SerialNumber";
    private static final String DisplayRowExpansionSettingTypeKey = "ComponentInstance.List.Display.RowExpansion";
    private static final String LoadRowExpansionPropertyValueSettingTypeKey = "ComponentInstance.List.Load.RowExpansionPropertyValue";
    private static final String FilterByComponentSettingTypeKey = "ComponentInstance.List.FilterBy.Component";
    private static final String FilterByCreatedByUserSettingTypeKey = "ComponentInstance.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "ComponentInstance.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "ComponentInstance.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "ComponentInstance.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "ComponentInstance.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByLocationSettingTypeKey = "ComponentInstance.List.FilterBy.Location";
    private static final String FilterByLocationDetailsSettingTypeKey = "ComponentInstance.List.FilterBy.LocationDetails";
    private static final String FilterByOwnerUserSettingTypeKey = "ComponentInstance.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "ComponentInstance.List.FilterBy.OwnerGroup";
    private static final String FilterByPropertyValue1SettingTypeKey = "ComponentInstance.List.FilterBy.PropertyValue1";
    private static final String FilterByPropertyValue2SettingTypeKey = "ComponentInstance.List.FilterBy.PropertyValue2";
    private static final String FilterByPropertyValue3SettingTypeKey = "ComponentInstance.List.FilterBy.PropertyValue3";
    private static final String FilterByPropertyValue4SettingTypeKey = "ComponentInstance.List.FilterBy.PropertyValue4";
    private static final String FilterByPropertyValue5SettingTypeKey = "ComponentInstance.List.FilterBy.PropertyValue5";
    private static final String FilterByTagSettingTypeKey = "ComponentInstance.List.FilterBy.Tag";
    private static final String FilterByQrIdSettingTypeKey = "ComponentInstance.List.FilterBy.QrId";
    private static final String FilterBySerialNumberSettingTypeKey = "ComponentInstance.List.FilterBy.SerialNumber";
    private static final String DisplayListPageHelpFragmentSettingTypeKey = "ComponentInstance.Help.ListPage.Display.Fragment";
    private static final String FilterByPropertiesAutoLoadTypeKey = "ComponentInstance.List.AutoLoad.FilterBy.Properties";

    private static final Logger logger = Logger.getLogger(ItemDomainInventoryController.class.getName());

    private Boolean displayLocationDetails = null;
    private Boolean displaySerialNumber = null;

    private String filterByComponent = null;
    private String filterByLocation = null;
    private String filterByLocationDetails = null;
    private String filterBySerialNumber = null;
    private String filterByTag = null;

    private Integer qrIdViewParam = null;

    private List<PropertyValue> filteredPropertyValueList = null;

    //Variables used for creation of new inventory item. 
    private List<Item> newItemsToAdd = null;
    private TreeNode currentItemBOMListTree = null;
    private TreeNode selectedItemBOMTreeNode = null;

    @EJB
    private ItemFacade itemFacade;

    @EJB
    private DomainFacade domainFacade;

    @EJB
    private DomainHandlerFacade domainHandlerFacade;

    @EJB
    private ItemElementRelationshipFacade itemElementRelationshipFacade;

    @EJB
    private RelationshipTypeFacade relationshipTypeFacade;

    public ItemDomainInventoryController() {
        super();
    }

    public Boolean getDisplayLocationDetails() {
        return displayLocationDetails;
    }

    public void setDisplayLocationDetails(Boolean displayLocationDetails) {
        this.displayLocationDetails = displayLocationDetails;
    }

    @Override
    public String getDisplayListPageHelpFragmentSettingTypeKey() {
        return DisplayListPageHelpFragmentSettingTypeKey;
    }

    public Boolean getDisplaySerialNumber() {
        return displaySerialNumber;
    }

    public void setDisplaySerialNumber(Boolean displaySerialNumber) {
        this.displaySerialNumber = displaySerialNumber;
    }

    public TreeNode getLocationRelationshipTree(Item inventoryItem) {
        if (inventoryItem.getLocationTree() == null) {
            setItemLocationInfo(inventoryItem);
        }
        return inventoryItem.getLocationTree();
    }

    private ItemElementRelationship findItemLocationRelationship(Item item) {
        // Support items that have not yet been saved to db. 
        if (item.getSelfElement().getId() != null) {
            return itemElementRelationshipFacade.findItemElementRelationshipByNameAndItemElementId(LOCATION_RELATIONSHIP_TYPE_NAME, item.getSelfElement().getId());
        } else {
            return null;
        }
    }

    @Override
    public ListDataModel getDomainListDataModel(EntityType entityType) {
        List<Item> itemList = itemFacade.findByDomainAndDerivedEntityTypeOrderByQrId(getListDomainName(), entityType.getName());
        return new ListDataModel(itemList);
    }

    @Override
    public ListDataModel getDomainListDataModel() {
        List<Item> itemList = itemFacade.findByDomainOrderByQrId(getListDomainName());
        return new ListDataModel(itemList);
    }

    @Override
    public List<EntityType> getFilterableEntityTypes() {
        return domainHandlerFacade.findByName(DERIVED_ITEM_DOMAIN_HANDLER_NAME).getAllowedEntityTypeList();
    }

    public String getLocationRelationshipDetails(Item inventoryItem) {
        if (inventoryItem.getLocationDetails() == null) {
            setItemLocationInfo(inventoryItem);
        }

        return inventoryItem.getLocationDetails();
    }

    public Item getLocation(Item inventoryItem) {
        if (inventoryItem.getLocation() == null) {
            setItemLocationInfo(inventoryItem);
        }
        return inventoryItem.getLocation();
    }

    public void setItemLocationInfo(Item inventoryItem) {
        TreeNode locationTree = inventoryItem.getLocationTree();

        if (locationTree == null) {
            locationTree = new DefaultTreeNode("Root", null);
            List<String> locationHierarchyStringList = new LinkedList<>();

            ItemElementRelationship itemElementRelationship;
            itemElementRelationship = findItemLocationRelationship(inventoryItem);

            if (itemElementRelationship != null) {
                ItemElement locationSelfItemElement = itemElementRelationship.getSecondItemElement();
                if (locationSelfItemElement != null) {
                    inventoryItem.setLocation(locationSelfItemElement.getParentItem());
                }
                inventoryItem.setLocationDetails(itemElementRelationship.getRelationshipDetails());

                while (locationSelfItemElement != null) {
                    Item locationParentItem = locationSelfItemElement.getParentItem();
                    String locationName = locationParentItem.getName();
                    locationHierarchyStringList.add(0, locationName);
                    List<ItemElement> memberList = locationParentItem.getItemElementMemberList();
                    if (memberList.size() > 0) {
                        locationSelfItemElement = memberList.get(0);
                    } else {
                        locationSelfItemElement = null;
                    }
                }
            }

            TreeNode prevNode = null;
            for (String locationName : locationHierarchyStringList) {
                if (prevNode == null) {
                    // Add first node 
                    prevNode = new DefaultTreeNode(locationName, locationTree);
                } else {
                    TreeNode newNode = new DefaultTreeNode(locationName, prevNode);
                    prevNode = newNode;
                }
            }
            inventoryItem.setLocationTree(locationTree);
        }

    }

    private RelationshipType getLocationRelationshipType() {
        return relationshipTypeFacade.findByName(LOCATION_RELATIONSHIP_TYPE_NAME);
    }

    @Override
    public String prepareCreate() {
        ItemController derivedItemController = getItemDerivedFromDomainController();
        if (derivedItemController != null) {
            derivedItemController.getSelectedObjectAndResetDataModel();
            derivedItemController.clearListFilters();
            derivedItemController.setFilteredObjectList(null);
        }        

        String createResult = super.prepareCreate();

        return createResult;
    }
    
    public void createSaveFromDialog(String onSuccessCommand) {
        String result = create(); 
        if (result != null) {
            RequestContext.getCurrentInstance().execute(onSuccessCommand);
        }        
    }
    
    public void createCancelFromDialog() {
        if (getCurrent() != null) {
            Item catalogItem = getCurrent().getDerivedFromItem(); 
            if (catalogItem != null) {
                catalogItem.getDerivedFromItemList().remove(getCurrent());
            }
            setCurrent(null);
        }
    }

    @Override
    public void prepareAddItemDerivedFromItem(Item item) {
        super.prepareAddItemDerivedFromItem(item);
        prepareBillOfMaterialsForCurrentItem();
    }

    @Override
    public String getNextStepForCreateItemWizard(FlowEvent event) {
        if (getCurrent().getDerivedFromItem() == null) {
            SessionUtility.addWarningMessage("No Catalog Item Selected", "Please select a catalog item.");
            return ItemCreateWizardSteps.derivedFromItemSelection.getValue();
        }

        String nsEvent = event.getNewStep();

        if (nsEvent.equals(ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP)) {
            prepareBillOfMaterialsForCurrentItem();
        }

        if (nsEvent.equals(ItemCreateWizardSteps.reviewSave.getValue())) {
            try {
                addItemElementsFromBillOfMaterials(getCurrent());
            } catch (CdbException ex) {
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                return event.getOldStep();
            }
        }

        return super.getNextStepForCreateItemWizard(event);
    }
    
    public void prepareBillOfMaterialsForCurrentItem() {
        // Prepare bill of materials if not yet done so. 
        InventoryBillOfMaterialItem iBom = new InventoryBillOfMaterialItem(getCurrent());
        InventoryBillOfMaterialItem.setBillOfMaterialsListForItem(getCurrent(), iBom);
    }

    @Override
    public String prepareEdit(Item inventoryItem) {
        setCurrent(inventoryItem);
        resetBOMSupportVariables();
        prepareBillOfMaterialsForCurrentItem(); 
        return super.prepareEdit(inventoryItem);
    } 

    @Override
    public void setCurrentDerivedFromItem(Item derivedFromItem) {
        if (getCurrent().getDerivedFromItem() == derivedFromItem) {
            //No need to set it is currently the same catalog item.
            return;
        }
        super.setCurrentDerivedFromItem(derivedFromItem);
        resetBOMSupportVariables();

        // Add current item to list
        newItemsToAdd.add(getCurrent());
    }

    public void resetBOMSupportVariables() {
        // All variables will be genererated when needed. 
        if (getCurrent() != null) {
            getCurrent().setInventoryDomainBillOfMaterialList(null);
            getCurrent().setContainedInBOM(null);
        }
        newItemsToAdd = new ArrayList<>();
        currentItemBOMListTree = null;
        selectedItemBOMTreeNode = null;

    }

    @Override
    public String getLastCreateWizardStep() {
        return ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP;
    }

    public TreeNode getCurrentItemBOMListTree() {
        if (currentItemBOMListTree == null) {
            currentItemBOMListTree = buildTreeNodeFromParentItem(getCurrent().getContainedInBOM());
        }

        return currentItemBOMListTree;
    }

    private TreeNode buildTreeNodeFromParentItem(InventoryBillOfMaterialItem startingBOM) {
        return buildTreeNodeFromParentItem(null, null, startingBOM);
    }

    private TreeNode buildTreeNodeFromParentItem(TreeNode root, TreeNode parent, InventoryBillOfMaterialItem nextBOM) {
        // The tree needs to be created. 
        if (root == null) {
            root = new DefaultTreeNode(null, null);
            parent = new DefaultTreeNode(nextBOM, root);
            parent.setExpanded(true);
            parent.setType(nextBOM.getState());
        }

        /*
        if (updateSelectedPointer) {
            if (parent.getData() == selectedItemBOMTreeNode.getData()) {
                selectedItemBOMTreeNode = parent; 
                updateSelectedPointer = false; 
            }
        }
         */
        if (nextBOM.getInventoryItem() != null) {
            List<InventoryBillOfMaterialItem> nextItemBOMList;
            nextItemBOMList = nextBOM.getInventoryItem().getInventoryDomainBillOfMaterialList();

            if (nextItemBOMList != null && nextItemBOMList.isEmpty() == false) {
                for (InventoryBillOfMaterialItem iBOM : nextItemBOMList) {
                    TreeNode newNode = new DefaultTreeNode(iBOM, parent);
                    newNode.setExpanded(true);
                    newNode.setType(iBOM.getState());
                    buildTreeNodeFromParentItem(root, newNode, iBOM);
                }
            }
        }

        return root;

    }

    public void addNewChildrenToCurrentSelection() {
        buildTreeNodeFromParentItem(currentItemBOMListTree,
                selectedItemBOMTreeNode,
                (InventoryBillOfMaterialItem) selectedItemBOMTreeNode.getData());
    }

    public TreeNode getSelectedItemBOMTreeNode() {
        return selectedItemBOMTreeNode;
    }

    public void setSelectedItemBOMTreeNode(TreeNode selectedItemBOMTreeNode) {
        if (selectedItemBOMTreeNode == null) {
            // Tree will set to null on every form update. 
            return;
        }
        
        // Clear selection in case same type of component item is in parts list.
        selectedObject = null;
        
        // Clear Filters
        clearListFilters();
        filteredObjectList = null; 
        
        this.selectedItemBOMTreeNode = selectedItemBOMTreeNode;
        
    }

    public void addItemElementsFromBillOfMaterials(Item item) throws CdbException {
        // Bill of materials list.
        List<InventoryBillOfMaterialItem> bomItems = item.getInventoryDomainBillOfMaterialList();
        
        for (InventoryBillOfMaterialItem bomItem : bomItems) {
            // Check if current catalog item element already has an item element defined. 
            ItemElement catalogItemElement = bomItem.getCatalogItemElement(); 
            ItemElement currentInventoryItemElement = null; 
            for (ItemElement inventoryItemElement: item.getFullItemElementList()) {
                if (inventoryItemElement.getDerivedFromItemElement() == catalogItemElement) {
                    currentInventoryItemElement = inventoryItemElement; 
                    logger.debug("Updating element " + currentInventoryItemElement + " to item " + item);        
                    break; 
                }
            }            
            
            if (currentInventoryItemElement == null) {
                currentInventoryItemElement = new ItemElement();
                currentInventoryItemElement.init(item, bomItem.getCatalogItemElement());
                item.getFullItemElementList().add(currentInventoryItemElement);
                logger.debug("Creating instance adding element " + currentInventoryItemElement + " to item " + item);
            }

            // User has specified to create a new item for this bill of materials item. 
            String currentBomState = bomItem.getState();
            if (currentBomState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())
                    || currentBomState.equals(InventoryBillOfMaterialItemStates.existingItem.getValue())) {
                if (bomItem.getInventoryItem() == null) {
                    
                    String actionWord = "defined"; 
                    if (currentBomState.equals(InventoryBillOfMaterialItemStates.existingItem.getValue())) {
                        actionWord = "selected";
                    }
                    
                    throw new CdbException("An item for: " + bomItem.getCatalogItemElement().getName() + " is not " + actionWord + ".");
                }

                Item inventoryItem = bomItem.getInventoryItem();

                // No need to do that for existing items. 
                if (currentBomState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
                    addItemElementsFromBillOfMaterials(inventoryItem);
                    currentInventoryItemElement.setContainedItem(inventoryItem);

                } else if (currentBomState.equals(InventoryBillOfMaterialItemStates.existingItem.getValue())) {
                    if (currentInventoryItemElement.getContainedItem() == inventoryItem == false) {
                        currentInventoryItemElement.setContainedItem(itemFacade.find(inventoryItem.getId()));
                    }
                }
            } else if (currentBomState.equals(InventoryBillOfMaterialItemStates.placeholder.getValue())) {
                currentInventoryItemElement.setContainedItem(null);
            }

            // Use permissions defined in parent of the item for the item element. 
            updateItemElementPermissionsToItem(currentInventoryItemElement, bomItem.getParentItemInstance());
        }

    }

    public void updateItemElementPermissionsToItem(ItemElement itemElement, Item item) {
        EntityInfo entityInfo = item.getEntityInfo();

        itemElement.getEntityInfo().setOwnerUser(entityInfo.getOwnerUser());
        itemElement.getEntityInfo().setOwnerUserGroup(entityInfo.getOwnerUserGroup());
        itemElement.getEntityInfo().setIsGroupWriteable(entityInfo.getIsGroupWriteable());
    }

    public boolean isRenderBomPlaceholder(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.placeholder.getValue());
        }
        return false;
    }

    public boolean isRenderBomExisting(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.existingItem.getValue());
        }
        return false;
    }

    public boolean isRenderBomEdit(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.newItem.getValue());
        }
        return false;
    }

    public boolean isRenderItemBom(Item item) {
        return item.getInventoryDomainBillOfMaterialList() != null
                && item.getInventoryDomainBillOfMaterialList().isEmpty() == false;
    }

    /**
     * Show full bill of materials based on top level item being added.
     *
     * @return
     */
    public boolean isRenderCurrentItemFullBOM() {
        if (getCurrent() == null) {
            return false;
        }
        return isRenderItemBom(getCurrent());
    }

    public void changeBillOfMaterialsState(InventoryBillOfMaterialItem bomItem, String previousState) {
        // Update type of selected tree node. 
        selectedItemBOMTreeNode.setType(bomItem.getState());

        if (!previousState.equals(bomItem.getState())) {
            if (previousState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
                Item newItem = bomItem.getInventoryItem();

                // The current item will not be defined. it has no children.                 
                selectedItemBOMTreeNode.getChildren().clear();
                
                bomItem.setInventoryItem(null);

                if (newItem != null) {
                    for (int i = 0; i < newItemsToAdd.size(); i++) {
                        if (newItemsToAdd.get(i) == newItem) {
                            newItemsToAdd.remove(i);

                            break;
                        }
                    }
                }
            } else if (InventoryBillOfMaterialItemStates.newItem.getValue().equals(bomItem.getState())) {
                ItemElement catalogItemElement = bomItem.getCatalogItemElement();
                Item catalogItem = catalogItemElement.getContainedItem();

                Item newInventoryItem = createEntityInstance();
                newItemsToAdd.add(newInventoryItem);

                newInventoryItem.setDerivedFromItem(catalogItem);
                InventoryBillOfMaterialItem.setBillOfMaterialsListForItem(newInventoryItem, bomItem);

                bomItem.setInventoryItem(newInventoryItem);

                // The tree needs to be updated.
                addNewChildrenToCurrentSelection();                
            }
        }
    }

    public void updatePermissionOnAllNewPartsIfNeeded() {
        if (isApplyPermissionToAllNewPartsForCurrent()) {
            for (Item item : newItemsToAdd) {
                setPermissionsForItemToCurrentItem(item);
            }
        }
    }

    public boolean isApplyPermissionToAllNewPartsForCurrent() {
        return getCurrent().getContainedInBOM().isApplyPermissionToAllNewParts();
    }

    private void setPermissionsForItemToCurrentItem(Item item) {
        if (item != getCurrent()) {
            // Set the permissions to equal. 
            EntityInfo entityInfo = getCurrent().getEntityInfo();
            item.getEntityInfo().setOwnerUser(entityInfo.getOwnerUser());
            item.getEntityInfo().setOwnerUserGroup(entityInfo.getOwnerUserGroup());
            item.getEntityInfo().setIsGroupWriteable(entityInfo.getIsGroupWriteable());
        }
    }

    public List<Item> getNewItemsToAdd() {
        return newItemsToAdd;
    }

    @Override
    public String getItemElementContainedItemText(ItemElement instanceItemElement) {
        if (instanceItemElement.getContainedItem() == null) {
            if (instanceItemElement.getDerivedFromItemElement().getContainedItem() != null) {
                return "No instance of " + instanceItemElement.getDerivedFromItemElement().getContainedItem().getName() + " defined";
            } else {
                return "Catalog item: " + instanceItemElement.getDerivedFromItemElement().getParentItem().getName() + " has no defined item.";
            }
        }

        Item containedItem = instanceItemElement.getContainedItem();
        String containedText = "Instance of " + containedItem.getDerivedFromItem().getName();

        if (containedItem.getQrId() != null) {
            containedText += " (" + containedItem.getQrIdDisplay() + ")";
        }

        return containedText;
    }

    public boolean isCurrentHasPartsToDisplay() {
        if (getCurrent().getInventoryDomainBillOfMaterialList() != null) {
            return getCurrent().getInventoryDomainBillOfMaterialList().isEmpty() == false;
        }
        return false;
    }

    @Override
    public void prepareEntityInsert(Item item) throws CdbException {
        if (item.getDerivedFromItem() == null) {
            throw new CdbException("Please specify " + getDerivedFromItemTitle());
        }
        
        super.prepareEntityInsert(item);

        checkNewItemsToAdd();

        // Clear new item elements for new items. In case a previous insert failed. 
        for (Item itemToAdd : newItemsToAdd) {
            if (isItemExistInDb(itemToAdd) == false) {
                //Make sure newest version of display list is fetched.
                ItemElement selfElement = itemToAdd.getSelfElement(); 
                itemToAdd.getFullItemElementList().clear();
                itemToAdd.getFullItemElementList().add(selfElement); 
                //Make sure display list is updated to reflect changes. 
                item.resetItemElementDisplayList();
            }
        }

        updatePermissionOnAllNewPartsIfNeeded();

        addItemElementsFromBillOfMaterials(item);
    }
    
    private void checkNewItemsToAdd() throws CdbException {
        Item item = getCurrent();
        for (Item newItem : newItemsToAdd) {
            // item is checked by default. 
            if (item != newItem) {
                checkItem(newItem);
            }
            updateItemLocation(item);
        }       
        
        // Cross check the nonadded items. 
        checkUniquenessBetweenNewItemsToAdd();
    }

    @Override
    public String getItemMembmershipPartIdentifier(Item item) {
        String result = item.getDerivedFromItem().getName(); 
        
        //Tag to help user identify the item
        String tag = item.getItemIdentifier2(); 
        if (tag != null && !tag.isEmpty()) {
            result += " - " + tag; 
        }
        
        return result; 
    }

    @Override
    protected void checkItem(Item item) throws CdbException {
        // Require tag be specified 
        if (item.getItemIdentifier2() == null || item.getItemIdentifier2().isEmpty()) {
            throw new CdbException("No " + getItemIdentifier2Title() + " has been specified for " + itemDomainToString(item));
        }
        
        super.checkItem(item); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void checkUniquenessBetweenNewItemsToAdd() throws CdbException {
        for (int i = 0; i < newItemsToAdd.size(); i++) {
            for (int j = newItemsToAdd.size() -1; j > -1; j--) {
                if (i == j) {
                    break; 
                }
                Item itemA = newItemsToAdd.get(i);
                Item itemB = newItemsToAdd.get(j); 
                
                String itemCompareString = itemA.getContainedInBOM().toString() + " and " + itemB.getContainedInBOM().toString(); 
                
                if (itemA.getQrId() != null && itemB.getQrId() != null) {
                    if (itemA.getQrId().equals(itemB.getQrId())) {                        
                        throw new CdbException(itemCompareString + " have QrId: " + itemA.getQrIdDisplay()); 
                    }
                }
                
                if (itemA.getDerivedFromItem() == itemB.getDerivedFromItem()) {
                    if (itemA.getItemIdentifier1().equals(itemB.getItemIdentifier1())
                            && itemA.getItemIdentifier2().equals(itemB.getItemIdentifier2())) {
                        throw new CdbException(itemCompareString + " have same combination of " +
                                getItemIdentifier1Title() + " and " + getItemIdentifier2Title()); 
                    }
                }
            }
        }
        
    }
    
    /**
     * Counts new items that will be added for a certain catalog item. 
     * 
     * @param catalogItem
     * @return count
     */
    public int getNewItemCount(Item catalogItem) {
        int count = 0; 
        for (Item item: newItemsToAdd) {
            if (item.getDerivedFromItem() == catalogItem) {
                count ++; 
            }
        }
        return count; 
    }

    @Override
    public void prepareEntityUpdate(Item item) throws CdbException {
        updateItemLocation(item);
        super.prepareEntityUpdate(item); 
        checkNewItemsToAdd();
        
        addItemElementsFromBillOfMaterials(item);
    }

    private void updateItemLocation(Item item) {
        // Determie updating of location relationship. 
        Item existingItem = null;
        ItemElementRelationship itemElementRelationship = null;

        if (item.getId() != null) {
            existingItem = itemFacade.findById(item.getId());
            // Item is not new
            if (existingItem != null) {
                setItemLocationInfo(existingItem);
                itemElementRelationship = findItemLocationRelationship(item);
            }
        }

        Boolean newItemWithNewLocation = (existingItem == null
                && (item.getLocation() != null || (item.getLocationDetails() != null && !item.getLocationDetails().isEmpty())));

        Boolean locationDifferentOnCurrentItem = false;

        if (existingItem != null) {
            // Empty String should be the same as null for comparison puposes. 
            String existingLocationDetails = existingItem.getLocationDetails();
            String newLocationDetails = item.getLocationDetails();

            if (existingLocationDetails != null && existingLocationDetails.isEmpty()) {
                existingLocationDetails = null;
            }
            if (newLocationDetails != null && newLocationDetails.isEmpty()) {
                newLocationDetails = null;
            }

            locationDifferentOnCurrentItem = ((!Objects.equals(existingItem.getLocation(), item.getLocation())
                    || !Objects.equals(existingLocationDetails, newLocationDetails)));
        }

        if (newItemWithNewLocation || locationDifferentOnCurrentItem) {

            if (item.getLocation() != null) {
                logger.debug("Updating location for Item " + item.toString()
                        + " to: " + item.getLocation().getName());
            } else if (item.getLocationDetails() != null) {
                logger.debug("Updating location details for Item " + item.toString()
                        + " to: " + item.getLocationDetails());
            }

            if (itemElementRelationship == null) {
                itemElementRelationship = new ItemElementRelationship();
                itemElementRelationship.setRelationshipType(getLocationRelationshipType());
                itemElementRelationship.setFirstItemElement(item.getSelfElement());
                List<ItemElementRelationship> itemElementRelationshipList = item.getSelfElement().getItemElementRelationshipList();
                if (itemElementRelationshipList == null) {
                    itemElementRelationshipList = new ArrayList<>();
                    item.getSelfElement().setItemElementRelationshipList(itemElementRelationshipList);
                }
                itemElementRelationshipList.add(itemElementRelationship);
            }

            List<ItemElementRelationship> itemElementRelationshipList = item.getSelfElement().getItemElementRelationshipList();

            Integer locationIndex = itemElementRelationshipList.indexOf(itemElementRelationship);

            if (item.getLocation() != null && item.getLocation().getSelfElement() != null) {
                itemElementRelationshipList.get(locationIndex).setSecondItemElement(item.getLocation().getSelfElement());
            }
            itemElementRelationshipList.get(locationIndex).setRelationshipDetails(item.getLocationDetails());
        }
    }

    @Override
    public void processEditRequestParams() {
        super.processEditRequestParams();
        setItemLocationInfo(getCurrent());
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public Boolean getDisplayItemIdentifier1() {
        return displaySerialNumber;
    }

    @Override
    public String getFilterByItemIdentifier1() {
        return filterBySerialNumber;
    }

    @Override
    public void setFilterByItemIdentifier1(String filterByItemIdentifier1) {
        this.filterBySerialNumber = filterByItemIdentifier1;
    }

    @Override
    public String getFilterByItemIdentifier2() {
        return filterByTag;
    }

    @Override
    public void setFilterByItemIdentifier2(String filterByItemIdentifier2) {
        this.filterByTag = filterByItemIdentifier2;
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);
        if (settingTypeMap == null) {
            return;
        }

        logger.debug("Updating list settings from setting type defaults");

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLocationDetails = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationDetailsSettingTypeKey).getDefaultValue());

        displayQrId = Boolean.parseBoolean(settingTypeMap.get(DisplayQrIdSettingTypeKey).getDefaultValue());
        displaySerialNumber = Boolean.parseBoolean(settingTypeMap.get(DisplaySerialNumberSettingTypeKey).getDefaultValue());

        displayRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayRowExpansionSettingTypeKey).getDefaultValue());
        loadRowExpansionPropertyValues = Boolean.parseBoolean(settingTypeMap.get(LoadRowExpansionPropertyValueSettingTypeKey).getDefaultValue());

        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());

        filterByComponent = settingTypeMap.get(FilterByComponentSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();

        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();

        filterByLocation = settingTypeMap.get(FilterByLocationSettingTypeKey).getDefaultValue();
        filterByLocationDetails = settingTypeMap.get(FilterByLocationDetailsSettingTypeKey).getDefaultValue();
        filterByQrId = settingTypeMap.get(FilterByQrIdSettingTypeKey).getDefaultValue();
        filterBySerialNumber = settingTypeMap.get(FilterBySerialNumberSettingTypeKey).getDefaultValue();
        filterByTag = settingTypeMap.get(FilterByTagSettingTypeKey).getDefaultValue();

        filterByPropertyValue1 = settingTypeMap.get(FilterByPropertyValue1SettingTypeKey).getDefaultValue();
        filterByPropertyValue2 = settingTypeMap.get(FilterByPropertyValue2SettingTypeKey).getDefaultValue();
        filterByPropertyValue3 = settingTypeMap.get(FilterByPropertyValue3SettingTypeKey).getDefaultValue();
        filterByPropertyValue4 = settingTypeMap.get(FilterByPropertyValue4SettingTypeKey).getDefaultValue();
        filterByPropertyValue5 = settingTypeMap.get(FilterByPropertyValue5SettingTypeKey).getDefaultValue();
        filterByPropertiesAutoLoad = Boolean.parseBoolean(settingTypeMap.get(FilterByPropertiesAutoLoadTypeKey).getDefaultValue());

        displayListPageHelpFragment = Boolean.parseBoolean(settingTypeMap.get(DisplayListPageHelpFragmentSettingTypeKey).getDefaultValue());

        resetDomainEntityPropertyTypeIdIndexMappings();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        super.updateSettingsFromSessionUser(sessionUser);
        if (sessionUser == null) {
            return;
        }

        logger.debug("Updating list settings from session user");

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        displayLocationDetails = sessionUser.getUserSettingValueAsBoolean(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);
        displayQrId = sessionUser.getUserSettingValueAsBoolean(DisplayQrIdSettingTypeKey, displayQrId);
        displaySerialNumber = sessionUser.getUserSettingValueAsBoolean(DisplaySerialNumberSettingTypeKey, displaySerialNumber);

        displayRowExpansion = sessionUser.getUserSettingValueAsBoolean(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        loadRowExpansionPropertyValues = sessionUser.getUserSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);

        displayPropertyTypeId1 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        filterByComponent = sessionUser.getUserSettingValueAsString(FilterByComponentSettingTypeKey, filterByComponent);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByLocation = sessionUser.getUserSettingValueAsString(FilterByLocationSettingTypeKey, filterByLocation);
        filterByLocationDetails = sessionUser.getUserSettingValueAsString(FilterByLocationDetailsSettingTypeKey, filterByLocationDetails);
        filterByQrId = sessionUser.getUserSettingValueAsString(FilterByQrIdSettingTypeKey, filterByQrId);
        filterBySerialNumber = sessionUser.getUserSettingValueAsString(FilterBySerialNumberSettingTypeKey, filterBySerialNumber);
        filterByTag = sessionUser.getUserSettingValueAsString(FilterByTagSettingTypeKey, filterByTag);

        filterByPropertyValue1 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        filterByPropertyValue2 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        filterByPropertyValue3 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        filterByPropertyValue4 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        filterByPropertyValue5 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        filterByPropertiesAutoLoad = sessionUser.getUserSettingValueAsBoolean(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        displayListPageHelpFragment = sessionUser.getUserSettingValueAsBoolean(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);

        resetDomainEntityPropertyTypeIdIndexMappings();

    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, Object> filters = dataTable.getFilters();
        filterByComponent = (String) filters.get("component.name");
        filterByLocation = (String) filters.get("location.name");
        filterByLocationDetails = (String) filters.get("locationDetails");
        filterByQrId = (String) filters.get("qrId");
        filterBySerialNumber = (String) filters.get("serialNumber");
        filterByTag = (String) filters.get("tag");

        filterByPropertyValue1 = (String) filters.get("propertyValue1");
        filterByPropertyValue2 = (String) filters.get("propertyValue2");
        filterByPropertyValue3 = (String) filters.get("propertyValue3");
        filterByPropertyValue4 = (String) filters.get("propertyValue4");
        filterByPropertyValue5 = (String) filters.get("propertyValue5");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        super.saveSettingsForSessionUser(sessionUser);
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        sessionUser.setUserSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        sessionUser.setUserSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        sessionUser.setUserSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        sessionUser.setUserSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        sessionUser.setUserSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        sessionUser.setUserSettingValue(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);
        sessionUser.setUserSettingValue(DisplayQrIdSettingTypeKey, displayQrId);
        sessionUser.setUserSettingValue(DisplaySerialNumberSettingTypeKey, displaySerialNumber);

        sessionUser.setUserSettingValue(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        sessionUser.setUserSettingValue(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);

        sessionUser.setUserSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        sessionUser.setUserSettingValue(FilterByComponentSettingTypeKey, filterByComponent);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        sessionUser.setUserSettingValue(FilterByLocationSettingTypeKey, filterByLocation);
        sessionUser.setUserSettingValue(FilterByLocationDetailsSettingTypeKey, filterByLocationDetails);
        sessionUser.setUserSettingValue(FilterByQrIdSettingTypeKey, filterByQrId);
        sessionUser.setUserSettingValue(FilterBySerialNumberSettingTypeKey, filterBySerialNumber);
        sessionUser.setUserSettingValue(FilterByTagSettingTypeKey, filterByTag);

        sessionUser.setUserSettingValue(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        sessionUser.setUserSettingValue(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        sessionUser.setUserSettingValue(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        sessionUser.setUserSettingValue(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        sessionUser.setUserSettingValue(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        sessionUser.setUserSettingValue(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        sessionUser.setUserSettingValue(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);

    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByComponent = null;
        filterByLocation = null;
        filterByLocationDetails = null;
        filterByQrId = null;
        filterBySerialNumber = null;
        filterByTag = null;
        filterByPropertyValue1 = null;
        filterByPropertyValue2 = null;
        filterByPropertyValue3 = null;
        filterByPropertyValue4 = null;
        filterByPropertyValue5 = null;
    }

    @Override
    public String getEntityTypeName() {
        return "componentInstance";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Inventory Item";
    }

    @Override
    public boolean getEntityDisplayItemIdentifier1() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemType() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        return false;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return true;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return true;
    }

    @Override
    public String getItemIdentifier1Title() {
        return "Serial Number";
    }

    @Override
    public String getItemIdentifier2Title() {
        return "Tag";
    }

    @Override
    public String getDerivedFromItemTitle() {
        return "Catalog Item";
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public String getStyleName() {
        return "inventory";
    }

    @Override
    public Domain getDefaultDomain() {
        return domainFacade.findByName(DEFAULT_DOMAIN_NAME);
    }

    @Override
    public String getDomainHandlerName() {
        return DOMAIN_HANDLER_NAME;
    }

    @Override
    public String getListDomainName() {
        return DOMAIN_TYPE_NAME;
    }

    @Override
    public String getItemDerivedFromDomainHandlerName() {
        return DERIVED_ITEM_DOMAIN_HANDLER_NAME;
    }

}
