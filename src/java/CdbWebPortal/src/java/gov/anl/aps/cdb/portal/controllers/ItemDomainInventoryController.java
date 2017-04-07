/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.InventoryBillOfMaterialItemStates;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationshipHistory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementRelationshipUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.FilterViewResultItem;
import gov.anl.aps.cdb.portal.view.objects.InventoryBillOfMaterialItem;
import gov.anl.aps.cdb.portal.view.objects.InventoryItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

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
public class ItemDomainInventoryController extends ItemController<ItemDomainInventory, ItemDomainInventoryFacade> {

    private static final String DEFAULT_DOMAIN_NAME = "Inventory";
    private final String DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME = "Catalog";

    private final String ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP = "itemElementInstantiation";
    private final String ITEM_DOMAIN_LOCATION_CONTROLLER_NAME = "itemDomainLocationController";

    /*
     * Controller specific settings
     */
    private static final String DisplayItemElementListItemIdentifier1SettingTypeKey = "ItemDomainInventory.ItemElementList.Display.ItemIdentifier1"; 
    private static final String DisplayCreatedByUserSettingTypeKey = "ItemDomainInventory.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "ItemDomainInventory.List.Display.CreatedOnDateTime";
    private static final String DisplayDescriptionSettingTypeKey = "ItemDomainInventory.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "ItemDomainInventory.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "ItemDomainInventory.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "ItemDomainInventory.List.Display.LastModifiedOnDateTime";
    private static final String DisplayLocationDetailsSettingTypeKey = "ItemDomainInventory.List.Display.LocationDetails";
    private static final String DisplayLocationSettingTypeKey = "ItemDomainInventory.List.Display.Location";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ItemDomainInventory.List.Display.NumberOfItemsPerPage";
    private static final String DisplayOwnerUserSettingTypeKey = "ItemDomainInventory.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "ItemDomainInventory.List.Display.OwnerGroup";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "ItemDomainInventory.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "ItemDomainInventory.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "ItemDomainInventory.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "ItemDomainInventory.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "ItemDomainInventory.List.Display.PropertyTypeId5";
    private static final String DisplayQrIdSettingTypeKey = "ItemDomainInventory.List.Display.QrId";
    private static final String DisplaySerialNumberSettingTypeKey = "ItemDomainInventory.List.Display.SerialNumber";
    private static final String DisplayItemProjectSettingTypeKey = "ItemDomainInventory.List.Display.Project";   
    private static final String DisplayRowExpansionSettingTypeKey = "ItemDomainInventory.List.Display.RowExpansion";
    private static final String LoadRowExpansionPropertyValueSettingTypeKey = "ItemDomainInventory.List.Load.RowExpansionPropertyValue";
    private static final String AutoLoadListFilterValuesSettingTypeKey = "ItemDomainInventory.List.Load.FilterDataTable"; 
    private static final String FilterByComponentSettingTypeKey = "ItemDomainInventory.List.FilterBy.Component";
    private static final String FilterByCreatedByUserSettingTypeKey = "ItemDomainInventory.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "ItemDomainInventory.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "ItemDomainInventory.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "ItemDomainInventory.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "ItemDomainInventory.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByLocationSettingTypeKey = "ItemDomainInventory.List.FilterBy.Location";
    private static final String FilterByLocationDetailsSettingTypeKey = "ItemDomainInventory.List.FilterBy.LocationDetails";
    private static final String FilterByOwnerUserSettingTypeKey = "ItemDomainInventory.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "ItemDomainInventory.List.FilterBy.OwnerGroup";
    private static final String FilterByPropertyValue1SettingTypeKey = "ItemDomainInventory.List.FilterBy.PropertyValue1";
    private static final String FilterByPropertyValue2SettingTypeKey = "ItemDomainInventory.List.FilterBy.PropertyValue2";
    private static final String FilterByPropertyValue3SettingTypeKey = "ItemDomainInventory.List.FilterBy.PropertyValue3";
    private static final String FilterByPropertyValue4SettingTypeKey = "ItemDomainInventory.List.FilterBy.PropertyValue4";
    private static final String FilterByPropertyValue5SettingTypeKey = "ItemDomainInventory.List.FilterBy.PropertyValue5";
    private static final String FilterByTagSettingTypeKey = "ItemDomainInventory.List.FilterBy.Tag";
    private static final String FilterByQrIdSettingTypeKey = "ItemDomainInventory.List.FilterBy.QrId";
    private static final String FilterBySerialNumberSettingTypeKey = "ItemDomainInventory.List.FilterBy.SerialNumber";
    private static final String DisplayListPageHelpFragmentSettingTypeKey = "ItemDomainInventory.Help.ListPage.Display.Fragment";
    private static final String FilterByPropertiesAutoLoadTypeKey = "ItemDomainInventory.List.AutoLoad.FilterBy.Properties";
    private static final String DisplayListDataModelScopeSettingTypeKey = "ItemDomainInventory.List.Scope.Display";
    private static final String DisplayListDataModelScopePropertyTypeIdSettingTypeKey = "ItemDomainInventory.List.Scope.Display.PropertyTypeId";

    private static final Logger logger = Logger.getLogger(ItemDomainInventoryController.class.getName());

    private Boolean displayLocationDetails = null;
    private Boolean displayLocation = null;
    private Boolean displaySerialNumber = null;
    
    // Set externally from item element controller
    private Boolean displayItemElementListItemIdentifier1 = null; 

    private String filterByComponent = null;
    private String filterByLocation = null;
    private String filterByLocationDetails = null;
    private String filterBySerialNumber = null;
    private String filterByTag = null;

    private List<PropertyValue> filteredPropertyValueList = null;

    //Variables used for creation of new inventory item. 
    private List<ItemDomainInventory> newItemsToAdd = null;
    private TreeNode currentItemBOMListTree = null;
    private TreeNode selectedItemBOMTreeNode = null;
    private boolean showOptionalPartsInBom = false;
    private Boolean currentItemBOMTreeHasOptionalItems = null;

    protected ListDataModel filterViewLocationDataModel = null;
    protected ItemDomainLocation filterViewLocationItemLoaded = null;
    protected boolean filterViewLocationDataModelLoaded = false;

    private ItemDomainInventory lastInventoryItemRequestedLocationMenuModel = null;   

    @EJB
    private DomainFacade domainFacade;
    @EJB
    private ItemElementRelationshipFacade itemElementRelationshipFacade;

    @EJB
    private RelationshipTypeFacade relationshipTypeFacade;
    
    @EJB
    private ItemDomainInventoryFacade itemDomainInventoryFacade; 

    public ItemDomainInventoryController() {
        super();
        displayDerivedFromItem = false;
    }

    public static ItemDomainInventoryController getInstance() {
        return (ItemDomainInventoryController) findDomainController(DEFAULT_DOMAIN_NAME);
    }

    public Boolean getDisplayLocationDetails() {
        return displayLocationDetails;
    }

    public void setDisplayLocationDetails(Boolean displayLocationDetails) {
        this.displayLocationDetails = displayLocationDetails;
    }

    public Boolean getDisplayLocation() {
        return displayLocation;
    }

    public void setDisplayLocation(Boolean displayLocation) {
        this.displayLocation = displayLocation;
    }

    @Override
    public String getDisplayListPageHelpFragmentSettingTypeKey() {
        // No help fragment yet exists for cdb 3.0 Inventory. 
        return null;
        //return DisplayListPageHelpFragmentSettingTypeKey;
    }

    public TreeNode getLocationRelationshipTree(ItemDomainInventory inventoryItem) {
        if (inventoryItem.getLocationTree() == null) {
            setItemLocationInfo(inventoryItem);
        }
        return inventoryItem.getLocationTree();
    }

    private ItemElementRelationship findItemLocationRelationship(ItemDomainInventory item) {
        // Support items that have not yet been saved to db.
        if (item.getSelfElement().getId() != null) {
            return itemElementRelationshipFacade
                    .findItemElementRelationshipByNameAndItemElementId(ItemElementRelationshipTypeNames.itemLocation.getValue(),
                            item.getSelfElement().getId());
        } else {
            return null;
        }
    }

    @Override
    public List<ItemDomainInventory> getItemListWithProject(ItemProject itemProject) {
        String projectName = itemProject.getName();
        return itemDomainInventoryFacade.findByDomainAndProjectOrderByQrId(getDefaultDomainName(), projectName);
    }

    @Override
    public List<ItemDomainInventory> getItemList() {
        return itemDomainInventoryFacade.findByDomainOrderByQrId(getDefaultDomainName());
    }

    @Override
    public List<EntityType> getFilterableEntityTypes() {
        return getDefaultDomainDerivedFromDomain().getAllowedEntityTypeList();
    }

    public String getLocationRelationshipDetails(ItemDomainInventory inventoryItem) {
        if (inventoryItem.getLocationDetails() == null) {
            setItemLocationInfo(inventoryItem);
        }

        return inventoryItem.getLocationDetails();
    }

    public ItemDomainLocation getLocation(ItemDomainInventory inventoryItem) {
        if (inventoryItem.getLocation() == null) {
            setItemLocationInfo(inventoryItem);
        }
        return inventoryItem.getLocation();
    }

    public DefaultMenuModel getItemLocataionDefaultMenuModel(ItemDomainInventory item) {
        lastInventoryItemRequestedLocationMenuModel = item;
        if (item != null) {
            if (item.getLocationMenuModel() == null) {
                setItemLocationInfo(item, false, true);
                String locationString = item.getLocationString();
                DefaultMenuModel itemLocationMenuModel;
                ItemDomainLocationController itemDomainLocationController;
                itemDomainLocationController = ItemDomainLocationController.getInstance();
                String inventoryControllerName = getDomainControllerName(DEFAULT_DOMAIN_NAME);
                if (locationString == null || locationString.isEmpty()) {
                    locationString = "Select Location";
                }
                ItemDomainLocation lowestLocation = item.getLocation();

                itemLocationMenuModel = itemDomainLocationController.generateLocationMenuModel(locationString, inventoryControllerName, "updateLocationForLastRequestedMenuModel", lowestLocation);
                item.setLocationMenuModel(itemLocationMenuModel);
            }
            return item.getLocationMenuModel();
        }
        return null;
    }

    public void updateLocationForLastRequestedMenuModel(ItemDomainLocation locationItem) {
        if (lastInventoryItemRequestedLocationMenuModel != null) {
            updateLocationForItem(lastInventoryItemRequestedLocationMenuModel, locationItem, null);
        } else {
            SessionUtility.addErrorMessage("Error", "No current item.");
        }
    }

    public void updateLocationForItem(ItemDomainInventory item, ItemDomainLocation locationItem, String onSuccess) {
        if (item.equals(locationItem)) {
            SessionUtility.addErrorMessage("Error", "Cannot use the same location as this item.");
            return;
        }

        if (locationItem != null) {
            if (isInventoryDomainItem(locationItem)) {
                ItemDomainInventory itemToCheck = item;
                if (item.getId() == null) {
                    // new item is part of currents assembly. 
                    itemToCheck = getCurrent();
                    if (itemToCheck.equals(locationItem)) {
                        SessionUtility.addErrorMessage("Error", "Cannot use parent of assembly as a location.");
                        return;
                    }
                } else {
                    // Existent item.
                    // Check that current item is not placed in its own herarchy. 
                    List<ItemDomainLocation> locationHierarchyList = ItemDomainLocationController.generateLocationHierarchyList(locationItem);
                    if (locationHierarchyList != null) {
                        if (locationHierarchyList.contains(item)) {
                            SessionUtility.addErrorMessage("Error", "Item is part of the the desired location heriarchy.");
                            return;
                        }
                    }
                }

                // Check if item is part of assembly. 
                if (isItemInAssemblyTree(itemToCheck, locationItem)) {
                    SessionUtility.addErrorMessage("Error", "Item is already part of this assembly, cannot use selected location.");
                    return;
                }

            }
        }

        item.setLocation(locationItem);
        updateLocationTreeForItem(item);
        updateLocationStringForItem(item);
        // To be updated. 
        item.setLocationMenuModel(null);

        if (onSuccess != null) {
            RequestContext.getCurrentInstance().execute(onSuccess);
        }

    }

    private boolean isItemInAssemblyTree(Item currentItem, Item itemToLookFor) {
        return isItemInAssemblyTree(currentItem, itemToLookFor, true, true);
    }

    private boolean isItemInAssemblyTree(Item currentItem, Item itemToLookFor, boolean isNeedToMoveDown, boolean isNeedToMoveUp) {
        if (isNeedToMoveUp) {
            // Move up the assembly tree
            List<ItemElement> itemElementMembershipList = currentItem.getItemElementMemberList();
            if (itemElementMembershipList != null) {
                for (ItemElement itemElement : itemElementMembershipList) {
                    Item parentItem = itemElement.getParentItem();
                    if (parentItem != null) {
                        if (parentItem.equals(itemToLookFor)) {
                            return true;
                        }
                        if (isItemInAssemblyTree(parentItem, itemToLookFor, false, true)) {
                            return true;
                        }
                    }
                }
            }
        }
        if (isNeedToMoveDown) {
            // Move down the assembly tree
            List<ItemElement> itemElementList = currentItem.getItemElementDisplayList();
            if (itemElementList != null) {
                for (ItemElement itemElement : itemElementList) {
                    Item containedItem = itemElement.getContainedItem();
                    if (containedItem != null) {
                        if (containedItem.equals(itemToLookFor)) {
                            return true;
                        }
                        if (isItemInAssemblyTree(containedItem, itemToLookFor, true, false)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected void filterViewItemProjectChanged() {
        super.filterViewItemProjectChanged();
        filterViewLocationDataModelLoaded = false;
    }

    public boolean isFilterViewLocationDataModelNeedReloading(ItemDomainLocation newLocationItem) {
        if (filterViewLocationDataModel == null) {
            return true;
        }
        if (filterViewLocationDataModelLoaded) {
            return true;
        }
        if (filterViewLocationItemLoaded != null) {
            if (!filterViewLocationItemLoaded.equals(newLocationItem)) {
                return true;
            }
        } else // last is null but new is not. 
        {
            if (newLocationItem != null) {
                return true;
            }
        }
        return false;
    }

    public void updateFilterViewLocationDataModelReloadStatus(ItemDomainLocation lastLocationLoaded) {
        filterViewLocationDataModelLoaded = true;
        filterViewLocationItemLoaded = lastLocationLoaded;
    }

    public List<ItemElementRelationshipHistory> getItemLocationRelationshipHistory(Item item) {
        String locationRelationshipTypeName = ItemElementRelationshipTypeNames.itemLocation.getValue();

        List<ItemElementRelationship> locationRelationshipList = ItemUtility.getItemRelationshipList(item, locationRelationshipTypeName, true);

        if (locationRelationshipList.size() > 1) {
            SessionUtility.addErrorMessage("Error", "Item is part of two or more locations.");
        } else if (locationRelationshipList.size() == 1) {
            return locationRelationshipList.get(0).getItemElementRelationshipHistoryList();
        }

        return null;
    }

    public ListDataModel getFilterViewLocationDataModel() {
        ItemDomainLocation selection = getItemDomainLocationController().getFilterViewLocationLastSelection();
        if (isFilterViewLocationDataModelNeedReloading(selection)) {
            List<ItemDomainInventory> itemList = new ArrayList<>();
            ItemProject currentItemProject = getCurrentItemProject();
            if (selection != null) {
                itemList.addAll(ItemDomainLocationController.getAllItemsLocatedInHierarchy(selection));
                if (currentItemProject != null) {
                    List<Item> itemsToRemove = new ArrayList<>();
                    for (Item item : itemList) {
                        if (item.getItemProjectList().contains(currentItemProject)) {
                            continue;
                        }
                        itemsToRemove.add(item);
                    }
                    itemList.removeAll(itemsToRemove);
                }
            } else if (currentItemProject != null) {
                itemList = itemDomainInventoryFacade.findByFilterViewItemProjectAttributes(currentItemProject, getDefaultDomainName());
            }
            filterViewLocationDataModel = createFilterViewListDataModel(itemList);
        }

        return filterViewLocationDataModel;
    }

    @Override
    protected void prepareFilterViewResultItem(FilterViewResultItem fvio) {
        super.prepareFilterViewResultItem(fvio);
        Item inventoryItem = fvio.getItemObject();

        TreeNode rootTreeNode = ItemUtility.createNewTreeNode(inventoryItem, null);
        ItemDomainLocationController.addLocationRelationshipsToParentTreeNode(inventoryItem, rootTreeNode);
        if (rootTreeNode.getChildren().size() > 0) {
            fvio.addFilterViewItemExpansion(rootTreeNode, "Location For");
        }

    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
    }

    public ItemDomainLocationController getItemDomainLocationController() {
        Object bean = SessionUtility.findBean(ITEM_DOMAIN_LOCATION_CONTROLLER_NAME);
        return (ItemDomainLocationController) bean;
    }

    public boolean isInventoryDomainItem(Item item) {
        return item.getDomain().getName().equals(getDefaultDomainName());
    }

    /**
     * Using the current location set in item, generate a location tree.
     *
     * @param item
     */
    public void updateLocationTreeForItem(ItemDomainInventory item) {
        if (item != null) {
            ItemDomainLocation location = item.getLocation();
            item.setLocationTree(ItemDomainLocationController.generateLocationNodeTreeBranch(location));
        }
    }

    /**
     * Using the current location set in item, generate a location string.
     *
     * @param item
     */
    public void updateLocationStringForItem(ItemDomainInventory item) {
        if (item != null) {
            ItemDomainLocation locationItem = item.getLocation();
            item.setLocationString(ItemDomainLocationController.generateLocatonHierarchyString(locationItem));
        }
    }

    /**
     * Gets a location string for an item and loads it if necessary.
     *
     * @param item
     * @return
     */
    public String getLocationStringForItem(ItemDomainInventory item) {
        if (item != null) {
            if (item.getLocationString() == null) {
                loadLocationStringForItem(item);
                if (item.getLocationString() == null) {
                    // Avoid unecessary checks.
                    item.setLocationString("");
                }
            }
            return item.getLocationString();
        }
        return null;
    }

    /**
     * Load current item location information & set location string.
     *
     * @param inventoryItem
     */
    public void loadLocationStringForItem(ItemDomainInventory inventoryItem) {
        setItemLocationInfo(inventoryItem, false, true);
    }

    public void setItemLocationInfo(ItemDomainInventory inventoryItem) {
        setItemLocationInfo(inventoryItem, true, true);
    }

    public void setItemLocationInfo(ItemDomainInventory inventoryItem, boolean loadLocationTreeForItem, boolean loadLocationHierarchyString) {
        if (inventoryItem.getOriginalLocationLoaded() == false) {
            ItemDomainLocation itemLocationItem = inventoryItem.getLocation();

            if (itemLocationItem == null) {
                ItemElementRelationship itemElementRelationship;
                itemElementRelationship = findItemLocationRelationship(inventoryItem);

                if (itemElementRelationship != null) {
                    ItemElement locationSelfItemElement = itemElementRelationship.getSecondItemElement();
                    if (locationSelfItemElement != null) {
                        ItemDomainLocation locationItem = (ItemDomainLocation) locationSelfItemElement.getParentItem();
                        inventoryItem.setLocation(locationItem);
                        if (loadLocationTreeForItem) {
                            updateLocationTreeForItem(inventoryItem);
                        }
                        if (loadLocationHierarchyString) {
                            updateLocationStringForItem(inventoryItem);
                        }
                    }
                    inventoryItem.setLocationDetails(itemElementRelationship.getRelationshipDetails());
                }
            }
            inventoryItem.setOriginalLocationLoaded(true);
        }

    }

    private RelationshipType getLocationRelationshipType() {
        return relationshipTypeFacade.findByName(ItemElementRelationshipTypeNames.itemLocation.getValue());
    }

    @Override
    protected boolean isPreProcessListDataModelIterateNeeded() {
        boolean result = super.isPreProcessListDataModelIterateNeeded();

        if (!result) {
            return displayLocation;
        }

        return result;
    }

    @Override
    protected void processPreProcessIteratedDomainEntity(CdbDomainEntity entity) {
        super.processPreProcessIteratedDomainEntity(entity);

        if (displayLocation) {
            ItemDomainInventory item = (ItemDomainInventory) entity;
            loadLocationStringForItem(item);
        }
    }

    @Override
    public String prepareCreate() {
        ItemController derivedItemController = getDefaultDomainDerivedFromDomainController();
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
            ItemDomainCatalog catalogItem = getCurrent().getCatalogItem();
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
    protected String getCreateItemWizardMenuItemValue(ItemCreateWizardSteps step) {
        switch (step) {
            case basicInformation:
                return null;
            case classification:
                return null;
            case permissions:
                return null;
            case reviewSave:
                return null;
            default:
                break;
        }

        return super.getCreateItemWizardMenuItemValue(step);
    }

    @Override
    protected String getCreateItemWizardMenuItemCustomValue(String stepName) {
        if (stepName.equals(ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP)) {
            return "Bill of Materials";
        }

        return super.getCreateItemWizardMenuItemCustomValue(stepName);
    }

    @Override
    public MenuModel getCreateItemWizardStepsMenuModel() {
        if (createItemWizardStepsMenuModel == null) {
            // Create all of the standard menu items.
            super.getCreateItemWizardStepsMenuModel();

            DefaultMenuItem menuItem;
            String menuItemDisplayValue = getCreateItemWizardMenuItemCustomValue(ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP);
            menuItem = createMenuItemForCreateWizardSteps(menuItemDisplayValue, ITEM_CREATE_WIZARD_ITEM_ELEMENT_CREATE_STEP);

            createItemWizardStepsMenuModel.addElement(menuItem);

        }

        return createItemWizardStepsMenuModel;
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

        return super.getNextStepForCreateItemWizard(event);
    }

    public void prepareBillOfMaterialsForCurrentItem() {
        // Prepare bill of materials if not yet done so. 
        newItemsToAdd = new ArrayList<>();
        InventoryBillOfMaterialItem iBom = new InventoryBillOfMaterialItem(getCurrent());
        InventoryBillOfMaterialItem.setBillOfMaterialsListForItem(getCurrent(), iBom);
    }

    @Override
    public String prepareView(ItemDomainInventory item) {
        resetBOMSupportVariables();
        return super.prepareView(item); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String prepareEdit(ItemDomainInventory inventoryItem) {
        resetBOMSupportVariables();
        setCurrent(inventoryItem);
        return super.prepareEdit(inventoryItem);
    }

    public Boolean displayBOMEditButton() {
        if (current != null) {
            List<ItemElement> catalogItemElementDisplayList;
            if (current.getDerivedFromItem() != null) {
                catalogItemElementDisplayList = current.getDerivedFromItem().getItemElementDisplayList();
                return catalogItemElementDisplayList != null && catalogItemElementDisplayList.isEmpty() == false;
            }
        }

        return false;
    }

    public String saveEditBOMList() {
        return this.update();
    }

    public void prepareEditBOMForCurrent() {
        resetBOMSupportVariables();
        prepareBillOfMaterialsForCurrentItem();
    }

    @Override
    public void setCurrentDerivedFromItem(Item derivedFromItem) {
        if (getCurrent().getDerivedFromItem() == derivedFromItem) {
            //No need to set it is currently the same catalog item.
            return;
        }
        super.setCurrentDerivedFromItem(derivedFromItem);
        resetBOMSupportVariables();
        newItemsToAdd = new ArrayList<>();

        // Add current item to list
        newItemsToAdd.add(getCurrent());
    }

    public void resetBOMSupportVariables() {
        // All variables will be genererated when needed. 
        if (getCurrent() != null) {
            getCurrent().setInventoryDomainBillOfMaterialList(null);
            getCurrent().setContainedInBOM(null);
        }
        newItemsToAdd = null;
        currentItemBOMListTree = null;
        selectedItemBOMTreeNode = null;
        showOptionalPartsInBom = false;
        currentItemBOMTreeHasOptionalItems = null;

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

    public void addItemElementsFromBillOfMaterials(ItemDomainInventory item) throws CdbException {
        // Bill of materials list.
        List<InventoryBillOfMaterialItem> bomItems = item.getInventoryDomainBillOfMaterialList();

        if (bomItems != null) {
            for (InventoryBillOfMaterialItem bomItem : bomItems) {
                if (bomItem.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue())) {
                    continue;
                }

                // Check if current catalog item element already has an item element defined. 
                ItemElement catalogItemElement = bomItem.getCatalogItemElement();
                ItemElement currentInventoryItemElement = null;
                for (ItemElement inventoryItemElement : item.getFullItemElementList()) {
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

                    ItemDomainInventory inventoryItem = bomItem.getInventoryItem();

                    // No need to do that for existing items. 
                    if (currentBomState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
                        addItemElementsFromBillOfMaterials(inventoryItem);
                        currentInventoryItemElement.setContainedItem(inventoryItem);

                    } else if (currentBomState.equals(InventoryBillOfMaterialItemStates.existingItem.getValue())) {
                        if (currentInventoryItemElement.getContainedItem() == inventoryItem == false) {
                            currentInventoryItemElement.setContainedItem(itemDomainInventoryFacade.find(inventoryItem.getId()));
                        }
                    }
                } else if (currentBomState.equals(InventoryBillOfMaterialItemStates.placeholder.getValue())) {
                    currentInventoryItemElement.setContainedItem(null);
                }

                // Use permissions defined in parent of the item for the item element. 
                updateItemElementPermissionsToItem(currentInventoryItemElement, bomItem.getParentItemInstance());
            }
        }

    }

    public void updateItemElementPermissionsToItem(ItemElement itemElement, ItemDomainInventory item) {
        EntityInfo entityInfo = item.getEntityInfo();

        itemElement.getEntityInfo().setOwnerUser(entityInfo.getOwnerUser());
        itemElement.getEntityInfo().setOwnerUserGroup(entityInfo.getOwnerUserGroup());
        itemElement.getEntityInfo().setIsGroupWriteable(entityInfo.getIsGroupWriteable());
    }

    public boolean isRenderBomOptionalUnspecified(InventoryBillOfMaterialItem billOfMaterialsItem) {
        if (billOfMaterialsItem != null) {
            return billOfMaterialsItem.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue());
        }
        return false;
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

    public boolean isRenderItemBom(ItemDomainInventory item) {
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

    public void createOptionalBillOfMaterialsPart(InventoryBillOfMaterialItem bomItem) {
        bomItem.setState(InventoryBillOfMaterialItemStates.placeholder.getValue());
        currentItemBOMTreeHasOptionalItems = null; 
    }

    public void removeOptionalBillOfMaterialsPart(InventoryBillOfMaterialItem bomItem) {
        // Removal could only happen for optional items 
        if (bomItem.isOptional()) {
            bomItem.setState(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue());
            if (bomItem.getInventoryItemElement() != null) {
                ItemElement inventoryItemElement = bomItem.getInventoryItemElement();
                current.getFullItemElementList().remove(inventoryItemElement);
                ItemElementController.getInstance().destroy(inventoryItemElement);
            }
            clearSelectedOptionalElementsIfNeeded();
            currentItemBOMTreeHasOptionalItems = null; 
        }
    }

    public void toggleShowOptionalItems() {
        showOptionalPartsInBom = !showOptionalPartsInBom;
        clearSelectedOptionalElementsIfNeeded();
    }

    public Boolean getCurrentItemBOMTreeHasOptionalItems() {
        // TODO add support for optional elements in sub assamblies 
        if (current != null && currentItemBOMTreeHasOptionalItems == null) {
            currentItemBOMTreeHasOptionalItems = itemHasOptionalsInBOM(current); 
            if (!currentItemBOMTreeHasOptionalItems) {
                for (ItemDomainInventory item : newItemsToAdd) {
                    currentItemBOMTreeHasOptionalItems = itemHasOptionalsInBOM(item);
                    if (currentItemBOMTreeHasOptionalItems) {
                        return true; 
                    }
                }
            }

        }
        return currentItemBOMTreeHasOptionalItems;
    }

    private boolean itemHasOptionalsInBOM(ItemDomainInventory item) {
        List<InventoryBillOfMaterialItem> iBomiList = item.getInventoryDomainBillOfMaterialList();
        for (InventoryBillOfMaterialItem iBomi : iBomiList) {
            if (iBomi.isOptional()) {
                if (iBomi.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue())) {
                    return true;
                }
            }
        }
        return false; 
    }

    private void clearSelectedOptionalElementsIfNeeded() {
        if (showOptionalPartsInBom == false && selectedItemBOMTreeNode != null) {
            InventoryBillOfMaterialItem selectedBOM = (InventoryBillOfMaterialItem) selectedItemBOMTreeNode.getData();
            if (selectedBOM.isOptional()
                    && selectedBOM.getState().equals(InventoryBillOfMaterialItemStates.unspecifiedOptional.getValue())) {
                selectedItemBOMTreeNode.setSelected(false);
                selectedItemBOMTreeNode = null;
            }
        }
    }

    public boolean isShowOptionalPartsInBom() {
        return showOptionalPartsInBom;
    }

    public boolean isBOMInventoryItemElementInDB(InventoryBillOfMaterialItem bomItem) {
        ItemElement inventoryItemElement = bomItem.getInventoryItemElement();
        if (inventoryItemElement != null) {
            Integer itemElementId = inventoryItemElement.getId();
            return itemElementId != null;
        }
        return false;
    }

    public void changeBillOfMaterialsState(InventoryBillOfMaterialItem bomItem, String previousState) {
        // Update type of selected tree node. 
        selectedItemBOMTreeNode.setType(bomItem.getState());

        if (!previousState.equals(bomItem.getState())) {
            if (previousState.equals(InventoryBillOfMaterialItemStates.newItem.getValue())) {
                ItemDomainInventory newItem = bomItem.getInventoryItem();

                // The current item will not be defined. it has no children.                 
                selectedItemBOMTreeNode.getChildren().clear();

                bomItem.setInventoryItem(null);

                ItemDomainCatalog catalogItem = bomItem.getCatalogItem();
                if (catalogItem.getFullItemElementList().size() > 1) {
                    // Assembly may have optionals
                    currentItemBOMTreeHasOptionalItems = null;
                }

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
                ItemDomainCatalog catalogItem = (ItemDomainCatalog) catalogItemElement.getContainedItem();

                ItemDomainInventory newInventoryItem = createEntityInstance();
                newItemsToAdd.add(newInventoryItem);

                newInventoryItem.setDerivedFromItem(catalogItem);
                InventoryBillOfMaterialItem.setBillOfMaterialsListForItem(newInventoryItem, bomItem);

                bomItem.setInventoryItem(newInventoryItem);

                // The tree needs to be updated.
                addNewChildrenToCurrentSelection();

                if (catalogItem.getFullItemElementList().size() > 1) {
                    // Assembly may have optionals
                    currentItemBOMTreeHasOptionalItems = null;
                }
            }
        }
    }

    public void updatePermissionOnAllNewPartsIfNeeded() {
        if (isApplyPermissionToAllNewPartsForCurrent()) {
            for (ItemDomainInventory item : newItemsToAdd) {
                setPermissionsForItemToCurrentItem(item);
            }
        }
    }

    public boolean isApplyPermissionToAllNewPartsForCurrent() {
        return getCurrent().getContainedInBOM().isApplyPermissionToAllNewParts();
    }

    private void setPermissionsForItemToCurrentItem(ItemDomainInventory inventoryItem) {
        if (inventoryItem != getCurrent()) {
            // Set the permissions to equal. 
            EntityInfo entityInfo = getCurrent().getEntityInfo();
            inventoryItem.getEntityInfo().setOwnerUser(entityInfo.getOwnerUser());
            inventoryItem.getEntityInfo().setOwnerUserGroup(entityInfo.getOwnerUserGroup());
            inventoryItem.getEntityInfo().setIsGroupWriteable(entityInfo.getIsGroupWriteable());
        }
    }

    public List<ItemDomainInventory> getNewItemsToAdd() {
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

        ItemDomainInventory containedItem = (ItemDomainInventory) instanceItemElement.getContainedItem();

        return getItemDisplayString(containedItem);
    }

    public boolean isCurrentHasPartsToDisplay() {
        if (getCurrent().getInventoryDomainBillOfMaterialList() != null) {
            return getCurrent().getInventoryDomainBillOfMaterialList().isEmpty() == false;
        }
        return false;
    }

    @Override
    public void prepareEntityInsert(ItemDomainInventory item) throws CdbException {
        if (item.getDerivedFromItem() == null) {
            throw new CdbException("Please specify " + getDerivedFromItemTitle());
        }

        super.prepareEntityInsert(item);
        checkNewItemsToAdd();

        if (newItemsToAdd != null) {
            // Clear new item elements for new items. In case a previous insert failed. 
            for (ItemDomainInventory itemToAdd : newItemsToAdd) {
                if (isItemExistInDb(itemToAdd) == false) {
                    //Make sure newest version of display list is fetched.
                    ItemElement selfElement = itemToAdd.getSelfElement();
                    itemToAdd.getFullItemElementList().clear();
                    itemToAdd.getFullItemElementList().add(selfElement);
                    //Make sure display list is updated to reflect changes. 
                    item.resetItemElementDisplayList();
                }
            }

            clearItemElementsForItem(item);
            updatePermissionOnAllNewPartsIfNeeded();
            addItemElementsFromBillOfMaterials(item);
        }
    }

    private void clearItemElementsForItem(ItemDomainInventory item) {
        //Make sure newest version of display list is fetched.
        //Item should be updated using addItemElementsFromBillOfMaterials.
        ItemElement selfElement = item.getSelfElement();
        item.getFullItemElementList().clear();
        item.getFullItemElementList().add(selfElement);
        //Make sure display list is updated to reflect changes. 
        item.resetItemElementDisplayList();
    }

    private void checkNewItemsToAdd() throws CdbException {
        ItemDomainInventory item = getCurrent();
        if (newItemsToAdd != null && !newItemsToAdd.isEmpty()) {
            for (ItemDomainInventory newItem : newItemsToAdd) {
                // item is checked by default. 
                if (item != newItem) {
                    checkItem(newItem);
                }
                updateItemLocation(newItem);
            }
            // Cross check the nonadded items. 
            checkUniquenessBetweenNewItemsToAdd();
        } else {
            checkItem(item);
        }
        updateItemLocation(item);
    }

    public String getInventoryItemAssemblyRowExpansionDisplayString(ItemElement itemElement) {
        if (itemElement != null) {
            if (itemElement.getContainedItem() != null) {
                ItemDomainInventory inventoryItem = (ItemDomainInventory) itemElement.getContainedItem();                
                return getItemDisplayString(inventoryItem);
            }

            ItemDomainCatalog catalogItem = getCatalogItemForInventoryItemElement(itemElement);
            if (catalogItem != null) {
                return catalogItem.getName() + "- [ ]";
            } else {
                return "Undefined Part: " + itemElement.getDerivedFromItemElement().getName();
            }
        }
        return null;
    }

    public ItemDomainCatalog getCatalogItemForInventoryItemElement(ItemElement inventoryItemElement) {
        if (inventoryItemElement != null) {
            ItemElement derivedFromItemElement = inventoryItemElement.getDerivedFromItemElement();
            if (derivedFromItemElement.getContainedItem() != null) {
                return (ItemDomainCatalog) derivedFromItemElement.getContainedItem();
            }
        }
        return null;
    }

    @Override
    public String getItemDisplayString(ItemDomainInventory item) {
        if (item != null) {
            if (item.getDerivedFromItem() != null) {
                String result = item.getDerivedFromItem().getName();

                //Tag to help user identify the item
                String tag = item.getName();
                if (tag != null && !tag.isEmpty()) {
                    result += " - [" + tag + "]";
                }

                return result;
            } else {
                return "No inventory item defied";
            }
        }
        return null;
    }

    private void checkUniquenessBetweenNewItemsToAdd() throws CdbException {
        for (int i = 0; i < newItemsToAdd.size(); i++) {
            for (int j = newItemsToAdd.size() - 1; j > -1; j--) {
                if (i == j) {
                    break;
                }
                ItemDomainInventory itemA = newItemsToAdd.get(i);
                ItemDomainInventory itemB = newItemsToAdd.get(j);

                String itemCompareString = itemA.getContainedInBOM().toString() + " and " + itemB.getContainedInBOM().toString();

                if (itemA.getQrId() != null && itemB.getQrId() != null) {
                    if (itemA.getQrId().equals(itemB.getQrId())) {
                        throw new CdbException(itemCompareString + " have QrId: " + itemA.getQrIdDisplay());
                    }
                }

                if (itemA.getDerivedFromItem() == itemB.getDerivedFromItem()) {
                    if (itemA.getItemIdentifier1().equals(itemB.getItemIdentifier1())
                            && itemA.getName().equals(itemB.getName())) {
                        throw new CdbException(itemCompareString + " have same combination of "
                                + getItemIdentifier1Title() + " and " + getNameTitle());
                    }
                }
            }
        }

    }

    @Override
    public boolean isShowCloneCreateItemElementsPlaceholdersOption() {
        // Item elements should match the assembly. User has no control over that.
        return false;
    }

    @Override
    public String prepareCloneForItemToClone() {
        // Item elements should match the assembly. User has no control over that.
        cloneCreateItemElementPlaceholders = true;
        newItemsToAdd = null;

        return super.prepareCloneForItemToClone();
    }

    @Override
    public ItemElementConstraintInformation loadItemElementConstraintInformation(ItemElement itemElement) {
        return new InventoryItemElementConstraintInformation(itemElement);
    }

    /**
     * Counts new items that will be added for a certain catalog item.
     *
     * @param catalogItem
     * @return count
     */
    public int getNewItemCount(ItemDomainCatalog catalogItem) {
        int count = 0;
        for (ItemDomainInventory item : newItemsToAdd) {
            if (item.getDerivedFromItem() == catalogItem) {
                count++;
            }
        }
        if (getCurrent().getDerivedFromItem() == catalogItem) {
            count++;
        }
        return count;
    }

    @Override
    public void prepareEntityUpdate(ItemDomainInventory item) throws CdbException {
        super.prepareEntityUpdate(item);
        checkNewItemsToAdd();

        addItemElementsFromBillOfMaterials(item);
    }

    private void updateItemLocation(ItemDomainInventory item) {
        // Determie updating of location relationship. 
        ItemDomainInventory existingItem = null;
        ItemElementRelationship itemElementRelationship = null;

        if (item.getId() != null) {
            existingItem = itemDomainInventoryFacade.findById(item.getId());
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
            } else {
                // No location is set. 
                itemElementRelationshipList.get(locationIndex).setSecondItemElement(null);
            }
            itemElementRelationshipList.get(locationIndex).setRelationshipDetails(item.getLocationDetails());

            // Add Item Element relationship history record. 
            ItemElementRelationship ier = itemElementRelationshipList.get(locationIndex);
            ItemElementRelationshipHistory ierh;
            ierh = ItemElementRelationshipUtility.createItemElementHistoryRecord(
                    ier, (UserInfo) SessionUtility.getUser(), new Date());
            List<ItemElementRelationshipHistory> ierhList;
            ierhList = item.getSelfElement().getItemElementRelationshipHistoryList();
            if (ierhList == null) {
                ierhList = new ArrayList<>();
                item.getSelfElement().setItemElementRelationshipHistoryList(ierhList);
            }

            ierhList.add(ierh);
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
            return getCurrent().toString();
        }
        return "";
    }

    @Override
    public Boolean getDisplayItemIdentifier1() {
        return displaySerialNumber;
    }

    @Override
    public void setDisplayItemIdentifier1(Boolean displayItemIdentifier1) {
        this.displaySerialNumber = displayItemIdentifier1;
    }

    public Boolean getDisplaySerialNumber() {
        return displaySerialNumber;
    }

    public void setDisplaySerialNumber(Boolean displaySerialNumber) {
        this.displaySerialNumber = displaySerialNumber;
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
        displayLocation = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationSettingTypeKey).getDefaultValue());

        displayQrId = Boolean.parseBoolean(settingTypeMap.get(DisplayQrIdSettingTypeKey).getDefaultValue());
        displaySerialNumber = Boolean.parseBoolean(settingTypeMap.get(DisplaySerialNumberSettingTypeKey).getDefaultValue());
        displayItemProject = Boolean.parseBoolean(settingTypeMap.get(DisplayItemProjectSettingTypeKey).getDefaultValue());
        //displayItemEntityTypes = Boolean.parseBoolean(settingTypeMap.get(DisplayItemEntityTypeSettingTypeKey).getDefaultValue());

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

        displayListDataModelScope = settingTypeMap.get(DisplayListDataModelScopeSettingTypeKey).getDefaultValue();
        displayListDataModelScopePropertyTypeId = parseSettingValueAsInteger(settingTypeMap.get(DisplayListDataModelScopePropertyTypeIdSettingTypeKey).getDefaultValue());
        
        displayItemElementListItemIdentifier1 = Boolean.parseBoolean(settingTypeMap.get(DisplayItemElementListItemIdentifier1SettingTypeKey).getDefaultValue()); 
        autoLoadListFilterValues = Boolean.parseBoolean(settingTypeMap.get(AutoLoadListFilterValuesSettingTypeKey).getDefaultValue()); 

        resetDomainEntityPropertyTypeIdIndexMappings();
    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);
        if (settingEntity == null) {
            return;
        }

        logger.debug("Updating list settings from session user");

        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        displayLocationDetails = settingEntity.getSettingValueAsBoolean(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);
        displayLocation = settingEntity.getSettingValueAsBoolean(DisplayLocationSettingTypeKey, displayLocation);
        displayQrId = settingEntity.getSettingValueAsBoolean(DisplayQrIdSettingTypeKey, displayQrId);
        displaySerialNumber = settingEntity.getSettingValueAsBoolean(DisplaySerialNumberSettingTypeKey, displaySerialNumber);
        displayItemProject = settingEntity.getSettingValueAsBoolean(DisplayItemProjectSettingTypeKey, displayItemProject);
        //displayItemEntityTypes = settingEntity.getSettingValueAsBoolean(DisplayItemEntityTypeSettingTypeKey, displayItemEntityTypes);

        displayRowExpansion = settingEntity.getSettingValueAsBoolean(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        loadRowExpansionPropertyValues = settingEntity.getSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);

        displayPropertyTypeId1 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        filterByComponent = settingEntity.getSettingValueAsString(FilterByComponentSettingTypeKey, filterByComponent);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByOwnerUser = settingEntity.getSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = settingEntity.getSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = settingEntity.getSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = settingEntity.getSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = settingEntity.getSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = settingEntity.getSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByLocation = settingEntity.getSettingValueAsString(FilterByLocationSettingTypeKey, filterByLocation);
        filterByLocationDetails = settingEntity.getSettingValueAsString(FilterByLocationDetailsSettingTypeKey, filterByLocationDetails);
        filterByQrId = settingEntity.getSettingValueAsString(FilterByQrIdSettingTypeKey, filterByQrId);
        filterBySerialNumber = settingEntity.getSettingValueAsString(FilterBySerialNumberSettingTypeKey, filterBySerialNumber);
        filterByTag = settingEntity.getSettingValueAsString(FilterByTagSettingTypeKey, filterByTag);

        filterByPropertyValue1 = settingEntity.getSettingValueAsString(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        filterByPropertyValue2 = settingEntity.getSettingValueAsString(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        filterByPropertyValue3 = settingEntity.getSettingValueAsString(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        filterByPropertyValue4 = settingEntity.getSettingValueAsString(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        filterByPropertyValue5 = settingEntity.getSettingValueAsString(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        filterByPropertiesAutoLoad = settingEntity.getSettingValueAsBoolean(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        displayListPageHelpFragment = settingEntity.getSettingValueAsBoolean(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);

        displayListDataModelScope = settingEntity.getSettingValueAsString(DisplayListDataModelScopeSettingTypeKey, displayListDataModelScope);
        displayListDataModelScopePropertyTypeId = settingEntity.getSettingValueAsInteger(DisplayListDataModelScopePropertyTypeIdSettingTypeKey, displayListDataModelScopePropertyTypeId);
        
        displayItemElementListItemIdentifier1 = settingEntity.getSettingValueAsBoolean(DisplayItemElementListItemIdentifier1SettingTypeKey, displayItemElementListItemIdentifier1); 
        autoLoadListFilterValues = settingEntity.getSettingValueAsBoolean(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues); 

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
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        super.saveSettingsForSessionSettingEntity(settingEntity);
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        settingEntity.setSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        settingEntity.setSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        settingEntity.setSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        settingEntity.setSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        settingEntity.setSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        settingEntity.setSettingValue(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);
        settingEntity.setSettingValue(DisplayLocationSettingTypeKey, displayLocation);
        settingEntity.setSettingValue(DisplayQrIdSettingTypeKey, displayQrId);
        settingEntity.setSettingValue(DisplaySerialNumberSettingTypeKey, displaySerialNumber);
        settingEntity.setSettingValue(DisplayItemProjectSettingTypeKey, displayItemProject);
        //settingEntity.setSettingValue(DisplayItemEntityTypeSettingTypeKey, displayItemEntityTypes);

        settingEntity.setSettingValue(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        settingEntity.setSettingValue(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);

        settingEntity.setSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        settingEntity.setSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        settingEntity.setSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        settingEntity.setSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        settingEntity.setSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        settingEntity.setSettingValue(FilterByComponentSettingTypeKey, filterByComponent);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        settingEntity.setSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        settingEntity.setSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        settingEntity.setSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        settingEntity.setSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        settingEntity.setSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        settingEntity.setSettingValue(FilterByLocationSettingTypeKey, filterByLocation);
        settingEntity.setSettingValue(FilterByLocationDetailsSettingTypeKey, filterByLocationDetails);
        settingEntity.setSettingValue(FilterByQrIdSettingTypeKey, filterByQrId);
        settingEntity.setSettingValue(FilterBySerialNumberSettingTypeKey, filterBySerialNumber);
        settingEntity.setSettingValue(FilterByTagSettingTypeKey, filterByTag);

        settingEntity.setSettingValue(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        settingEntity.setSettingValue(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        settingEntity.setSettingValue(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        settingEntity.setSettingValue(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        settingEntity.setSettingValue(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        settingEntity.setSettingValue(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        settingEntity.setSettingValue(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);

        settingEntity.setSettingValue(DisplayListDataModelScopeSettingTypeKey, displayListDataModelScope);
        settingEntity.setSettingValue(DisplayListDataModelScopePropertyTypeIdSettingTypeKey, displayListDataModelScopePropertyTypeId);
        
        settingEntity.setSettingValue(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues);

    }   

    @Override
    public String getDisplayItemElementListItemIdentifier1Key() {
        return DisplayItemElementListItemIdentifier1SettingTypeKey; 
    } 
    
    @Override
    public Boolean getDisplayItemElementListItemIdentifier1() {
        return displayItemElementListItemIdentifier1; 
    }

    @Override
    public void setDisplayItemElementListItemIdentifier1(Boolean displayItemElementListItemIdentifier1) {
        this.displayItemElementListItemIdentifier1 = displayItemElementListItemIdentifier1;
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
        return false;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
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
    public String getNameTitle() {
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
    public String getDefaultDomainName() {
        return DEFAULT_DOMAIN_NAME;
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME;
    }

    @Override
    public String getItemIdentifier2Title() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return true;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    @Override
    public boolean isAllowedSetDerivedFromItemForCurrentItem() {
        if (getCurrent() != null) {
            return !getCurrent().isIsCloned();
        }

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
    protected ItemDomainInventory instenciateNewItemDomainEntity() {
        return new ItemDomainInventory(); 
    }

    @Override
    protected ItemDomainInventoryFacade getEntityDbFacade() {
        return itemDomainInventoryFacade; 
    }

}
