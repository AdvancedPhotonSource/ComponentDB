/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.common.utilities.CollectionUtility;
import gov.anl.aps.cdb.portal.constants.ItemDisplayListDataModelScope;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.PortalStyles;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.EntityTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.beans.ItemTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ListFacade;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.ListTbl;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.model.jsf.handlers.ImagePropertyTypeHandler;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public abstract class ItemController<ItemDomainEntity extends Item, ItemDomainEntityFacade extends ItemFacadeBase<ItemDomainEntity>, ItemSettingsObject extends ItemSettings> extends CdbDomainEntityController<ItemDomainEntity, ItemDomainEntityFacade, ItemSettingsObject> implements IItemController<ItemDomainEntity> {

    private static final Logger logger = Logger.getLogger(Item.class.getName());
    protected final String FAVORITES_LIST_NAME = "Favorites";
    protected final String PRIMARY_IMAGE_PROPERTY_METADATA_KEY = "Primary";    
    
    @EJB
    protected ItemElementFacade itemElementFacade;

    @EJB
    protected ItemTypeFacade itemTypeFacade;

    @EJB
    protected DomainFacade domainFacade;

    @EJB
    protected EntityTypeFacade entityTypeFacade;

    @EJB
    protected ItemCategoryFacade itemCategoryFacade;

    @EJB
    protected ListFacade listFacade;

    @EJB
    protected UserInfoFacade userInfoFacade;

    private List<Item> parentItemList;
    private int currentItemEntityHashCode;
        
    protected DataModel scopedListDataModel = null;     
    protected List<String> displayListDataModelScopeSelectionList = null;

    protected Integer qrIdViewParam = null;

    private TreeNode itemElementListTreeTableRootNode = null;

    private List<ItemDomainEntity> selectItemCandidateList;
    private List<Item> selectedItems;

    private List<ItemDomainEntity> selectItemElementItemCandidateList;

    protected DataModel itemsWithNoParentsListDataModel = null;
    protected TreeNode itemsWithNoParentsRootNode = null;

    private Domain selectionDomain;

    protected DataModel allowedChildItemSelectDataModel = null;

    protected List<ItemCategory> domainItemCategoryList = null;

    protected List<ItemType> availableItemTypesForCurrentItem = null;
    protected List<ItemCategory> lastKnownItemCategoryListForCurrentItem = null;
    protected ItemElement newItemElementForCurrent = null; 
    protected Boolean newItemElementForCurrentSaveButtonEnabled = false; 

    protected Boolean cloneProperties = false;
    protected Boolean cloneCreateItemElementPlaceholders = false;
    protected Boolean cloneSources = false;
    protected Item itemToClone;

    // Globalized item project functionality. 
    protected ItemProjectController itemProjectController = null;        
    protected SettingController settingController; 

    public ItemController() {
    }
    
    protected abstract ItemDomainEntity instenciateNewItemDomainEntity();
    
    /**
     * Get item create wizard controller for current controller. 
     * 
     * @return 
     */
    protected ItemCreateWizardController getItemCreateWizardController() {
        return null; 
    }

    public Domain getDefaultDomain() {
        Domain domain = domainFacade.findByName(getDefaultDomainName());
        if (domain == null) {
            domain = new Domain();
            domain.setName(getDefaultDomainName());
        }
        return domain; 
        
    }

    public String getNameTitle() {
        return "Name";
    }

    public String getItemItemTypeTitle() {
        return "Type";
    }

    public String getItemItemCategoryTitle() {
        return "Category";
    }

    @Override
    public final String getEntityEntityTypeName() {
        return getItemItemTypeTitle();
    }

    @Override
    public final String getEntityEntityCategoryName() {
        return getItemItemCategoryTitle();
    }

    public List<ItemDomainEntity> getItemListWithProject(ItemProject itemProject) {
        String projectName = itemProject.getName();
        return getEntityDbFacade().findByDomainAndProject(getDefaultDomainName(), projectName);
    }

    /**
     * Called from item project controller whenever item project changes.
     *
     */
    public void itemProjectChanged() {
        resetListDataModel();
    }

    /**
     * Fetch global item project selection from item project controller.
     *
     * @return currently selected item project.
     */
    protected ItemProject getCurrentItemProject() {
        // Item project should be initalized in the pre render list. 
        if (itemProjectController != null) {
            return itemProjectController.getCurrentItemProject();
        }

        logger.warn("Item project controller was not set properly.");
        return null;
    }

    @Override
    public void processPreRenderList() {
        super.processPreRenderList();
        filteredObjectList = null;

        // Check if itemProject registered. 
        if (itemProjectController == null) {
            itemProjectController = ItemProjectController.getInstance();
            itemProjectController.addItemControllerProjectChangeListener(this);
        }
    }

    public List<ItemDomainEntity> getItemList() {
        return getEntityDbFacade().findByDomain(getDefaultDomainName());
    }

    public List<ItemCategory> getDomainItemCategoryList() {
        if (domainItemCategoryList == null) {
            domainItemCategoryList = itemCategoryFacade.findByDomainName(this.getDefaultDomainName());
        }
        return domainItemCategoryList;
    }

    public SelectItem[] getDomainItemCategoryListForSelectOne() {
        return CollectionUtility.getSelectItems(getDomainItemCategoryList(), true);
    }

    @Override
    protected void resetVariablesForCurrent() {
        super.resetVariablesForCurrent();
        availableItemTypesForCurrentItem = null;
        lastKnownItemCategoryListForCurrentItem = null;
        newItemElementForCurrent = null; 
        newItemElementForCurrentSaveButtonEnabled = false; 
    }

    public List<ItemType> getAvailableItemTypesForCurrentItem() {
        Item currentItem = getCurrent();
        if (currentItem != null) {
            if (getEntityDisplayItemCategory()) {
                List<ItemCategory> itemCategoryList = currentItem.getItemCategoryList();

                if (lastKnownItemCategoryListForCurrentItem != null) {
                    if (lastKnownItemCategoryListForCurrentItem.size() != itemCategoryList.size()) {
                        availableItemTypesForCurrentItem = null;
                    } else {
                        for (ItemCategory itemCategory : lastKnownItemCategoryListForCurrentItem) {
                            if (itemCategoryList.contains(itemCategory) == false) {
                                availableItemTypesForCurrentItem = null;
                                break;
                            }
                        }
                    }
                } else {
                    availableItemTypesForCurrentItem = null;
                }
                lastKnownItemCategoryListForCurrentItem = itemCategoryList;

                if (availableItemTypesForCurrentItem == null) {
                    availableItemTypesForCurrentItem = getAvaiableTypesForItemCategoryList(itemCategoryList);
                    updateItemTypeListBasedOnNewAvailableTypes(availableItemTypesForCurrentItem, currentItem);
                }
            } else {
                // Item does not have item category to pick from
                availableItemTypesForCurrentItem = itemTypeFacade.findByDomainName(getDefaultDomainName());
            }
        }

        return availableItemTypesForCurrentItem;
    }

    private void updateItemTypeListBasedOnNewAvailableTypes(List<ItemType> avaiableItemTypes, Item item) {
        if (item.getItemTypeList() != null) {
            List<ItemType> itemItemTypeList = new ArrayList<>(item.getItemTypeList());

            for (ItemType itemType : itemItemTypeList) {
                if (avaiableItemTypes.contains(itemType) == false) {
                    item.getItemTypeList().remove(itemType);
                }
            }
        }
    }

    public List<ItemType> getAvaiableTypesForItemCategoryList(List<ItemCategory> itemCategoryList) {
        List<ItemType> avaiableItemTypes = new ArrayList<>();

        if (itemCategoryList != null) {
            for (ItemCategory itemCategory : itemCategoryList) {
                if (itemCategoryList.size() == 1) {
                    avaiableItemTypes.addAll(itemCategory.getItemTypeList());
                    break;
                } else {
                    for (ItemType itemType : itemCategory.getItemTypeList()) {
                        if (avaiableItemTypes.contains(itemType) == false) {
                            avaiableItemTypes.add(itemType);
                        }
                    }
                }
            }

            if (itemCategoryList.size() > 1) {
                // Alphabetical sort order needs to be re-applied. 
                Comparator<ItemType> itemTypesAlphabeticalComperitor;
                itemTypesAlphabeticalComperitor = new Comparator<ItemType>() {
                    @Override
                    public int compare(ItemType o1, ItemType o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                };
                avaiableItemTypes.sort(itemTypesAlphabeticalComperitor);
            }
        }

        return avaiableItemTypes;
    }

    public String getCurrentItemItemTypeEditString() {
        Item item = getCurrent();
        if (item != null) {
            if (isDisabledItemItemType()) {
                return "First Select " + getItemItemCategoryTitle();
            } else {
                return item.getEditItemTypeString(getItemItemTypeTitle());
            }
        }
        return "";
    }

    public boolean isDisabledItemItemType() {
        List<ItemType> avaiableItemTypesForCurrentItem = getAvailableItemTypesForCurrentItem();
        if (avaiableItemTypesForCurrentItem != null) {
            return avaiableItemTypesForCurrentItem.isEmpty();
        }
        return true;
    }   

    /**
     * Create a list data model with items of controller domain and item project
     * if specified.
     *
     * @return List data model for the particular domain.
     */
    private ListDataModel getUpdatedDomainListDataModel() {
        ItemProject currentItemProject = getCurrentItemProject();

        List<ItemDomainEntity> itemList;
        if (currentItemProject != null) {
            itemList = getItemListWithProject(currentItemProject);
        } else {
            itemList = getItemList();
        }

        return new ListDataModel(itemList);
    }

    public String getDomainPath(Domain domain) {
        return "/views/" + getEntityViewsDirectory(domain.getName());
    }

    @Override
    protected String getEntityViewsDirectory() {
        return getEntityViewsDirectory(getDefaultDomainName());
    }

    protected String getEntityViewsDirectory(String domainName) {
        return "itemDomain" + domainName;
    }

    @Override
    protected String getEntityApplicationViewPath() {
        return getDomainPath(getDefaultDomain());
    }

    @Override
    public boolean entityHasTypes() {
        return getEntityDisplayItemType();
    }

    @Override
    public boolean entityHasCategories() {
        return getEntityDisplayItemCategory();
    }

    @Override
    public String getEntityTypeTypeName() {
        return "itemType";
    }

    @Override
    public String getEntityTypeCategoryName() {
        return "itemCategory";
    }

    public Domain getDefaultDomainDerivedFromDomain() {
        return domainFacade.findByName(getDefaultDomainDerivedFromDomainName());
    }

    public Domain getDefaultDomainDerivedToDomain() {
        return domainFacade.findByName(getDefaultDomainDerivedToDomainName());
    }
    
    public final ItemController getSelectionController() {
        return this;
    }

    public static ItemController findDomainControllerForItem(Item item) {
        String domainName = item.getDomain().getName();
        return findDomainController(domainName);
    }

    public static ItemController findDomainController(String domainName) {
        if (domainName != null) {
            return (ItemController) SessionUtility.findBean(getDomainControllerName(domainName));
        }
        return null;
    }

    public static String getItemItemTypeTitleForDomain(Domain domain) {
        if (domain != null) {
            ItemController itemController = findDomainController(domain.getName());
            return itemController.getItemItemTypeTitle();
        }
        return null;
    }

    public static String getItemItemCategoryTitleForDomain(Domain domain) {
        if (domain != null) {
            ItemController itemController = findDomainController(domain.getName());
            return itemController.getItemItemCategoryTitle();
        }
        return null;
    }

    public static boolean getItemHasItemTypesForDomain(Domain domain) {
        if (domain != null) {
            return findDomainController(domain.getName()).entityHasTypes();
        }
        return false;
    }

    public static boolean getItemHasItemCategoriesForDomain(Domain domain) {
        if (domain != null) {
            return findDomainController(domain.getName()).entityHasCategories();
        }
        return false;
    }

    public String getDomainControllerName() {
        return getDomainControllerName(getDefaultDomainName());
    }

    protected static String getDomainControllerName(String domainName) {
        return "itemDomain" + domainName + "Controller";
    }

    public List<EntityType> getDomainAllowedEnityTypes() {
        Domain domain = getDefaultDomain();
        if (domain != null) {
            return domain.getAllowedEntityTypeList();
        }
        return null;
    }

    public List<EntityType> getFilterableEntityTypes() {
        return getDomainAllowedEnityTypes();
    }

    public SelectItem[] getEntityTypesForDataTableFilterSelectOne() {
        return CollectionUtility.getSelectItems(getFilterableEntityTypes(), true);
    }

    public boolean isEntityTypeEditable() {
        List<EntityType> allowedEntityType = getDomainAllowedEnityTypes();
        if (allowedEntityType != null && !allowedEntityType.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Domain getSelectionDomain() {
        return selectionDomain;
    }

    public void setSelectionDomain(Domain selectionDomain) {
        this.selectionDomain = selectionDomain;
    }

    @Override
    public DataModel createSelectDataModel() {
        selectDataModel = getUpdatedDomainListDataModel();
        return selectDataModel;
    }

    @Override
    public void createListDataModel() {
        setListDataModel(getUpdatedDomainListDataModel());
    }

    public List<ItemDomainEntity> getItemsWithoutParents() {
        return getEntityDbFacade().findByDomainWithoutParents(getDefaultDomainName());
    }

    public DataModel getItemsWithNoParentsListDataModel() {
        if (itemsWithNoParentsListDataModel == null) {
            itemsWithNoParentsListDataModel = new ListDataModel(getItemsWithoutParents());
        }
        return itemsWithNoParentsListDataModel;
    }

    public TreeNode getItemsWithNoParentsRootNode() {
        if (itemsWithNoParentsRootNode == null) {
            List<ItemDomainEntity> itemsWitNoParentsList = getItemsWithoutParents();
            itemsWithNoParentsRootNode = new DefaultTreeNode(null, null);

            for (Item item : itemsWitNoParentsList) {
                TreeNode itemRootTreeNode;
                try {
                    itemRootTreeNode = ItemElementUtility.createItemRoot(item);
                } catch (CdbException ex) {
                    SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                    itemsWithNoParentsRootNode = null;
                    return null;
                }
                itemsWithNoParentsRootNode.getChildren().add(itemRootTreeNode);
            }
        }

        return itemsWithNoParentsRootNode;
    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
        scopedListDataModel = null;
        itemsWithNoParentsListDataModel = null;
        itemsWithNoParentsRootNode = null;
        displayListDataModelScopeSelectionList = null;
    }

    public final DataModel getScopedListDataModel() {
        // Show all
        if (settingObject.getDisplayListDataModelScope().equals(ItemDisplayListDataModelScope.showAll.getValue())) {
            return getListDataModel();
        } else if (scopedListDataModel == null) {
            if (settingObject.getDisplayListDataModelScope().equals(ItemDisplayListDataModelScope.showItemsWithPropertyType.getValue())) {
                if (settingObject.getDisplayListDataModelScopePropertyTypeId() == null) {
                    return null;
                } else {
                    List<ItemDomainEntity> itemList;
                    ItemProject currentProject = getCurrentItemProject();
                    if (currentProject != null) {
                        itemList = getEntityDbFacade().getItemsWithPropertyTypeAndProject(
                                getDefaultDomainName(),
                                settingObject.getDisplayListDataModelScopePropertyTypeId(),
                                currentProject.getName());
                    } else {
                        itemList = getEntityDbFacade().getItemListWithPropertyType(
                                getDefaultDomainName(),
                                settingObject.getDisplayListDataModelScopePropertyTypeId());
                    }
                    scopedListDataModel = new ListDataModel(itemList);
                }
            } else {
                // Determine if currently viewed as group or user. 
                SettingEntity settingEntity = getSettingController().getCurrentSettingEntity();
                // All the remaining options require a setting entity loaded. 
                if (settingEntity == null) {
                    return getListDataModel();
                }

                // Show only favorites
                if (settingObject.getDisplayListDataModelScope().equals(ItemDisplayListDataModelScope.showFavorites.getValue())) {
                    List<ItemDomainEntity> itemList = getEntityDbFacade().getItemListContainedInList(getDefaultDomainName(), getFavoritesList());
                    scopedListDataModel = new ListDataModel(itemList);
                } else {
                    // Show owned or owned & favorites. 
                    boolean showOwnedAndFavorites = settingObject.getDisplayListDataModelScope().equals(ItemDisplayListDataModelScope.showOwnedPlusFavorites.getValue());
                    boolean showOwned = !showOwnedAndFavorites;

                    List<ItemDomainEntity> itemList = null;

                    if (showOwnedAndFavorites) {
                        if (getFavoritesList() != null) {
                            if (settingEntity instanceof UserInfo) {
                                itemList = getEntityDbFacade()
                                        .getItemListContainedInListOrOwnedByUser(getDefaultDomainName(), getFavoritesList(), (UserInfo) settingEntity);
                            } else if (settingEntity instanceof UserGroup) {
                                itemList = getEntityDbFacade()
                                        .getItemListContainedInListOrOwnedByGroup(getDefaultDomainName(), getFavoritesList(), (UserGroup) settingEntity);
                            }
                        } else {
                            // No favorites list will not show any results from query. 
                            showOwned = true;
                        }
                    }

                    if (showOwned) {
                        if (settingEntity instanceof UserInfo) {
                            itemList = getEntityDbFacade().getItemListOwnedByUser(getDefaultDomainName(), (UserInfo) settingEntity);
                        } else if (settingEntity instanceof UserGroup) {
                            itemList = getEntityDbFacade().getItemListOwnedByUserGroup(getDefaultDomainName(), (UserGroup) settingEntity);
                        }
                    }

                    scopedListDataModel = new ListDataModel(itemList);
                }
            }
        }
        if (scopedListDataModel == null) {
            //Nothing was populated into the list data model. 
            scopedListDataModel = new ListDataModel<>();
        }

        loadPreProcessListDataModelIfNeeded(scopedListDataModel);

        return scopedListDataModel;
    }
    
    public List<String> getDisplayListDataScopeSelectionList() {
        if (displayListDataModelScopeSelectionList == null) {
            displayListDataModelScopeSelectionList = new ArrayList<>();
            SettingEntity settingEntity = getSettingController().getCurrentSettingEntity();
            boolean settingEntityLoaded = (settingEntity != null);
            for (ItemDisplayListDataModelScope value : ItemDisplayListDataModelScope.values()) {
                if (!settingEntityLoaded) {
                    if (value.isSettingEntityRequired()) {
                        continue;
                    }
                }
                displayListDataModelScopeSelectionList.add(value.getValue());
            }
        }
        return displayListDataModelScopeSelectionList;
    }

    public void toggleItemInFavoritesList(Item item) {
        if (SessionUtility.getUser() == null) {
            SessionUtility.addErrorMessage("Error", "Cannot add item to favorites list no user logged in.");
            return;
        }

        ListTbl favoriteList = getFavoritesList();
        if (favoriteList == null) {
            favoriteList = createFavoritesList();
        }

        ItemElement favoriteItemElement = item.getSelfElement();
        List<ItemElement> favoriteItemElementList = favoriteList.getItemElementList();

        if (favoriteItemElementList.contains(favoriteItemElement)) {
            favoriteItemElementList.remove(favoriteItemElement);
            logger.debug(String.format("Removing %s to %s List", favoriteItemElement, FAVORITES_LIST_NAME));
        } else {
            favoriteItemElementList.add(item.getSelfElement());
            logger.debug(String.format("Adding %s to %s List", favoriteItemElement, FAVORITES_LIST_NAME));
        }

        // Update
        try {
            listFacade.edit(favoriteList);
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            logger.error(ex);
        }

    }

    public String getItemFavoritesIconStyle(Item item) {
        ListTbl favoritesList = getFavoritesList();
        if (favoritesList != null) {
            if (favoritesList.getItemElementList().contains(item.getSelfElement())) {
                return PortalStyles.favoritesOn.getValue();
            }
        }

        return PortalStyles.favoritesOff.getValue();
    }

    private List<ListTbl> getSettingEntityItemElementLists() {
        SettingEntity settingEntity = getSettingController().getCurrentSettingEntity();
        if (settingEntity != null) {
            return settingEntity.getItemElementLists();
        }
        return null;
    }

    private ListTbl getFavoritesList() {
        List<ListTbl> itemElementLists = getSettingEntityItemElementLists();
        if (itemElementLists != null) {
            for (ListTbl list : itemElementLists) {
                if (list.getName().equals(FAVORITES_LIST_NAME)) {
                    return list;
                }
            }
        }

        // List does not exist
        return null;
    }

    private ListTbl createFavoritesList() {
        ListTbl favoriteList = new ListTbl();

        // Link the setting entity and the new favorites list
        SettingEntity settingEntity = getSettingController().getCurrentSettingEntity();
        settingEntity.getItemElementLists().add(favoriteList);
        favoriteList.init(FAVORITES_LIST_NAME, settingEntity);

        logger.debug(String.format("Creating %s List for %s", FAVORITES_LIST_NAME, settingEntity));
        try {
            listFacade.create(favoriteList);
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            logger.error(ex);
        }

        return favoriteList;
    }

    public String getItemListPageTitle() {
        return getDisplayEntityTypeName() + " List";
    }

    private String getEntityTypeStyleName(String genEntityTypeName) {
        return genEntityTypeName.toLowerCase() + getDefaultDomainName();
    }

    public String getListStyleName() { 
        return getStyleName();

    }

    public String getCurrentItemStyleName() {
        return getStyleName();
    }

    public boolean currentHasChanged() {
        Item itemEntity = getCurrent();
        if (currentItemEntityHashCode != itemEntity.hashCode()) {
            currentItemEntityHashCode = hashCode();
            return true;
        }
        return false;
    }

    public Item getItem(java.lang.Integer id) {
        return getEntityDbFacade().find(id);
    }

    void prepareItemElementListTreeTable(Item item) {
        try {
            itemElementListTreeTableRootNode = ItemElementUtility.createItemElementRoot(item);
        } catch (CdbException ex) {
            logger.warn("Could not create item element list for tree view: " + ex.toString());
        }
    }

    public void prepareAddSource(Item item) {
        List<ItemSource> sourceList = item.getItemSourceList();
        ItemSource source = new ItemSource();
        source.setItem(item);
        sourceList.add(0, source);
    }

    public void saveSourceList() {
        update();
    }

    public void deleteSource(ItemSource itemSource) {
        Item item = getCurrent();
        List<ItemSource> itemSourceList = item.getItemSourceList();
        itemSourceList.remove(itemSource);
        updateOnRemoval();
    }
    
    public void prepareCreateSingleItemElementSimpleDialog() {
        Item item = getCurrent(); 
        if (item != null) {
            newItemElementForCurrent = createItemElement(getCurrent()); 
        }        
    }
    
    public void cancelCreateSingleItemElementSimpleDialog() {
        newItemElementForCurrent = null; 
        newItemElementForCurrentSaveButtonEnabled = false; 
    }
    
    public void saveCreateSingleItemElementSimpleDialog() {
        Item currentItem = getCurrent(); 
        if (currentItem != null) {
            prepareAddItemElement(getCurrent(), newItemElementForCurrent);
        }
        
        update();
        
        newItemElementForCurrent = null;
    }
    
    public void changeItemCreateSingleItemElementSimpleDialog() {
        newItemElementForCurrent.setContainedItem(null);
        newItemElementForCurrentSaveButtonEnabled = false; 
    }
    
    public void validateCreateSingleItemElementSimpleDialog(String onSuccessCommand, String errorSummary) {
        ItemDomainEntity item = getCurrent(); 
        try { 
            prepareAddItemElement(item, newItemElementForCurrent);                       
            checkItemElementsForItem(item);
            
            newItemElementForCurrentSaveButtonEnabled = true; 
            RequestContext.getCurrentInstance().execute(onSuccessCommand);                         
        } catch (CdbException ex) {            
            SessionUtility.addErrorMessage(errorSummary, ex.getErrorMessage()); 
        } finally {
            item.getFullItemElementList().remove(newItemElementForCurrent); 
            item.resetItemElementDisplayList();            
        }                        
    }
    
    public void deleteItemConnector(ItemConnector itemConnector) {
        Item item = getCurrent();
        
        ConnectorController connectorController = ConnectorController.getInstance(); 
        Connector connector = itemConnector.getConnector(); 
        if (connectorController.verifySafeRemovalOfConnector(connector)) {
            List<ItemConnector> itemConnectorList = item.getItemConnectorList(); 
            itemConnectorList.remove(itemConnector);
            ItemConnectorController.getInstance().destroy(itemConnector); 
        } else {
            // Generate a userfull message
            String message = "";
            List<ItemConnector> itemConnectorList = connector.getItemConnectorList();
            for (ItemConnector ittrConnector : itemConnectorList) {
                Item ittrItem = ittrConnector.getItem(); 
                if (ittrItem.equals(item) == false) {
                    if (ittrItem.getDomain().getName().equals(ItemDomainName.inventory.getValue())) {
                        message = "Please check connections on inventory item: " + ittrItem.getName(); 
                    }
                }
            }            
            
            SessionUtility.addErrorMessage("Error", "Cannot remove connector, check if it is used for connections in inventory. " + message);
        }
    }
    
    public void prepareAddItemConnector(Item item) {
        if (item != null) {
            ItemConnectorController itemConnectorController = ItemConnectorController.getInstance(); 
            ItemConnector itemConnector = itemConnectorController.createEntityInstance();             
            itemConnector.setItem(item);
            itemConnector = prepareItemConnectorForDomain(itemConnector); 
            item.getItemConnectorList().add(itemConnector);
            itemConnectorController.setCurrent(itemConnector);
        }
    }
    
    protected ItemConnector prepareItemConnectorForDomain(ItemConnector itemConnector) {        
        return itemConnector; 
    }
    
    public void revertItemConnectorListForCurrent() {
        Item item = getCurrent();
        if (item != null) {
            Item dbItem = findById(item.getId());
            item.setItemConnectorList(dbItem.getItemConnectorList());
        }
    }

    protected ItemElement createItemElement(ItemDomainEntity item) {        
        List<ItemElement> itemElementsDisplayList = item.getItemElementDisplayList();
        ItemElement itemElement = new ItemElement();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        itemElement.setEntityInfo(entityInfo);
        itemElement.setParentItem(item);        

        int elementNumber = itemElementsDisplayList.size() + 1;
        String elementNameSuffix = "E";
        String elementName = null;

        boolean unique = false;
        while (elementName == null) {
            String test = elementNameSuffix + elementNumber;
            if (itemElementsDisplayList.size() > 0) {
                for (ItemElement ittrItemElement : itemElementsDisplayList) {
                    if (ittrItemElement.getName().equalsIgnoreCase(test)) {
                        elementNumber++;
                        unique = false;
                        break;
                    } else {
                        unique = true;
                    }
                }
            } else {                
                unique = true; 
            }
            if (unique) {
                elementName = test;
            }
        }

        itemElement.setName(elementName);
        
        return itemElement;
    }
    
    protected void prepareAddItemElement(ItemDomainEntity item, ItemElement itemElement) {
        List<ItemElement> itemElementList = item.getFullItemElementList();
        List<ItemElement> itemElementsDisplayList = item.getItemElementDisplayList();                
        
        itemElementList.add(itemElement);
        itemElementsDisplayList.add(0, itemElement);
    }

    public void prepareAddItemElement(ItemDomainEntity item) {
        ItemElement itemElement = createItemElement(item);
        prepareAddItemElement(item, itemElement);
    }

    public void completeSuccessfulItemElementRemoval(ItemElement itemElement) {
        if (current != null) {
            Item parentItem = itemElement.getParentItem();
            if (current.equals(parentItem)) {
                removeItemElementFromItem(itemElement, current);
                // Remove from related items to prevent a readd to db. 
                List<ItemElement> derivedItemElements = itemElement.getDerivedFromItemElementList();
                for (ItemElement derivedItemElement : derivedItemElements) {
                    removeItemElementFromItem(derivedItemElement, derivedItemElement.getParentItem());
                }
            }
        }
    }

    public void completeSuccessfulItemElementUpdate(ItemElement itemElement) {
        if (current != null) {
            Item parentItem = itemElement.getParentItem();
            if (current.equals(parentItem)) {
                // Resaving the item could cause revivial of the element prior update due to various connections. 
                current = findById(current.getId());
            }
        }
    }

    public ItemElementConstraintInformation loadItemElementConstraintInformation(ItemElement itemElement) {
        return new ItemElementConstraintInformation(itemElement);
    }

    public void completeSucessfulDerivedFromItemCreation() {
        if (current != null) {
            // Item elements have outdated reference back to parent with new inventory item. 
            // Will allow user to see the latest constraints on item elements.          
            current = findById(current.getId());
        }
    }

    public ItemElement finalizeItemElementRequiredStatusChanged(ItemElement itemElement) throws CdbException {
        return itemElement;
    }

    private void removeItemElementFromItem(ItemElement itemElement, Item item) {
        List<ItemElement> itemElementList = item.getFullItemElementList();
        List<ItemElement> itemElementsDisplayList = item.getItemElementDisplayList();
        itemElementList.remove(itemElement);
        itemElementsDisplayList.remove(itemElement);
    }

    public void saveItemElementList() {
        update();
    }

    public List<ItemDomainEntity> getSelectItemCandidateList() {
        if (selectItemCandidateList == null) {
            selectItemCandidateList = getItemList();
        }
        return selectItemCandidateList;
    }

    public List<Item> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<Item> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public List<ItemDomainEntity> completeItem(String query) {
        List<Item> itemList = (List<Item>) (List<?>) getSelectItemCandidateList();
        return (List<ItemDomainEntity>) ItemUtility.filterItem(query, itemList);
    }   

    public ItemController getDefaultDomainDerivedFromDomainController() {
        return findDomainController(getDefaultDomainDerivedFromDomainName());
    }

    public ItemController getDefaultDomainDerivedToDomainController() {
        return findDomainController(getDefaultDomainDerivedToDomainName());
    }

    public Boolean isItemExistInDb(Item item) {
        Item dbItem = null;
        if (item.getId() != null) {
            dbItem = getEntityDbFacade().find(item.getId());
        }

        return dbItem != null;
    }

    public void setCurrentDerivedFromItem(Item derivedFromItem) {
        getCurrent().setDerivedFromItem(derivedFromItem);
    }

    public void checkItemProject(Item item) throws CdbException {
        if (item.getItemProjectList() == null || item.getItemProjectList().isEmpty()) {
            throw new CdbException("Project for item " + itemDomainToString(item) + " must be specified.");
        }
    }   

    @Override
    public String prepareCreate() {        
        // Reset create wizard variables. 
        ItemCreateWizardController itemCreateWizardController = getItemCreateWizardController();
        if (itemCreateWizardController != null) {
            itemCreateWizardController.resetCreateItemWizardVariables();
        }
        return super.prepareCreate();
    }

    public String getItemDisplayString(ItemDomainEntity item) {
        return item.toString();
    }

    public String getItemMembmershipPartIdentifier(ItemDomainEntity item) {
        return getItemDisplayString(item);
    }

    public boolean isDisplayRowExpansionForItem(Item item) {
        Boolean displayRowExpansion = settingObject.getDisplayRowExpansion();
        if (displayRowExpansion != null && displayRowExpansion) {
            if (item != null) {
                return isDisplayRowExpansionLogs(item)
                        || isDisplayRowExpansionProperties(item)
                        || isDisplayRowExpansionAssembly(item);
            }
        }

        return false;
    }

    public boolean isDisplayRowExpansionAssembly(Item item) {
        if (item != null) {
            if (getEntityDisplayItemElements()) {
                List<ItemElement> itemElementsList = item.getItemElementDisplayList();
                return itemElementsList != null && !itemElementsList.isEmpty();
            }
        }
        return false;
    }

    public boolean isDisplayRowExpansionItemsDerivedFromItem(Item item) {
        if (item != null) {
            if (getEntityDisplayItemsDerivedFromItem()) {
                List<Item> itemsDerivedFromItem = item.getDerivedFromItemList();
                if (itemsDerivedFromItem != null) {
                    return !itemsDerivedFromItem.isEmpty();
                }
            }
        }
        return false;
    }

    @Override
    public boolean isDisplayRowExpansionProperties(CdbDomainEntity item) {
        if (getEntityDisplayItemProperties()) {
            return super.isDisplayRowExpansionProperties(item);
        }
        return false;
    }

    @Override
    public boolean isDisplayRowExpansionLogs(CdbDomainEntity item) {
        if (getEntityDisplayItemLogs()) {
            return super.isDisplayRowExpansionLogs(item);
        }
        return false;
    }

    public List<ItemDomainEntity> getSelectItemElementItemCandidateList() {
        if (selectItemElementItemCandidateList == null) {
            logger.debug("Preparing Item element candiate list for user.");
            selectItemElementItemCandidateList = new ArrayList<>();
            selectItemElementItemCandidateList = getItemList();
        }

        return selectItemElementItemCandidateList;
    }

    public DataModel getAllowedChildItemSelectDataModel() {
        if (allowedChildItemSelectDataModel == null) {
            allowedChildItemSelectDataModel = new ListDataModel(getSelectItemElementItemCandidateList());
        }
        return allowedChildItemSelectDataModel;
    }

    @Override
    public void resetSelectDataModel() {
        super.resetSelectDataModel();
        allowedChildItemSelectDataModel = null;
    }

    public Item getSelectedObjectAndReset() {
        Item selectedItem = getSelectedObject();
        selectedObject = null;
        return selectedItem;
    }

    public List<Item> completeItemElementItem(String queryString) {
        List<Item> candiateList = (List<Item>) getSelectItemElementItemCandidateList(); 
        return ItemUtility.filterItem(queryString, candiateList);
    }

    /**
     * Adds an item and sets the derived from item. Meant to be called from the
     * item domain controller of new item.
     *
     * @param derivedFromItem
     */
    public void prepareAddItemDerivedFromItem(Item derivedFromItem) {
        List<Item> itemDerivedFromItemList = derivedFromItem.getDerivedFromItemList();

        ItemDomainEntity newItemDerivedFromItem = instenciateNewItemDomainEntity();

        newItemDerivedFromItem.init(getDefaultDomain());

        itemDerivedFromItemList.add(0, newItemDerivedFromItem);

        current = newItemDerivedFromItem;

        setCurrentDerivedFromItem(derivedFromItem);
    }

    public void saveItemDerivedFromItemList() {
        Item item = getCurrent();
        List<Item> itemDerivedFromItemList = item.getDerivedFromItemList();
        if (itemDerivedFromItemList != null) {
            UserInfo createdByUser = (UserInfo) SessionUtility.getUser();
            Date createdOnDateTime = new Date();

            for (Item itemDerivedFromItem : itemDerivedFromItemList) {
                // If id is null, this is a new component instance; check its properties
                if (itemDerivedFromItem.getId() == null) {
                    itemDerivedFromItem.updateDynamicProperties(createdByUser, createdOnDateTime);
                }
            }
        }
        update();
    }

    public void deleteItemDerivedFromItem(ItemDomainEntity itemDerivedFromItem) {
        ItemDomainEntity item = (ItemDomainEntity) itemDerivedFromItem.getDerivedFromItem();
        List<Item> itemDerivedFromItemList = item.getDerivedFromItemList();
        itemDerivedFromItemList.remove(itemDerivedFromItem);
        setCurrent(item);
        updateOnRemoval();
    }

    protected ItemDomainEntity cloneProperties(ItemDomainEntity clonedItem, ItemDomainEntity cloningFrom) {
        List<PropertyValue> cloningFromPropertyValueList = cloningFrom.getPropertyValueList();

        if (cloningFromPropertyValueList != null) {
            List<PropertyValue> newItemPropertyValueList = new ArrayList<>();

            Date enteredOnDateTime = new Date();
            UserInfo enteredByUser = (UserInfo) SessionUtility.getUser();

            for (PropertyValue propertyValue : cloningFromPropertyValueList) {
                PropertyValue newPropertyValue = new PropertyValue();
                newPropertyValue.setPropertyType(propertyValue.getPropertyType());
                newPropertyValue.setValue(propertyValue.getValue());
                newPropertyValue.setTag(propertyValue.getTag());
                newPropertyValue.setUnits(propertyValue.getUnits());
                newPropertyValue.setDescription(propertyValue.getDescription());
                newPropertyValue.setEnteredOnDateTime(enteredOnDateTime);
                newPropertyValue.setEnteredByUser(enteredByUser);

                newItemPropertyValueList.add(newPropertyValue);
            }

            clonedItem.setPropertyValueList(newItemPropertyValueList);
        }

        return clonedItem;
    }

    protected ItemDomainEntity cloneSources(ItemDomainEntity clonedItem, ItemDomainEntity cloningFrom) {
        List<ItemSource> cloningFromSourceList = cloningFrom.getItemSourceList();

        if (cloningFromSourceList != null) {
            List<ItemSource> newItemSourceList = new ArrayList<>();

            for (ItemSource itemSource : cloningFromSourceList) {
                ItemSource newItemSource = new ItemSource();

                newItemSource.setItem(clonedItem);
                newItemSource.setSource(itemSource.getSource());
                newItemSource.setPartNumber(itemSource.getPartNumber());
                newItemSource.setCost(itemSource.getCost());
                newItemSource.setDescription(itemSource.getDescription());
                newItemSource.setIsVendor(itemSource.getIsVendor());
                newItemSource.setIsManufacturer(itemSource.getIsManufacturer());
                newItemSource.setContactInfo(itemSource.getContactInfo());
                newItemSource.setUrl(itemSource.getUrl());

                newItemSourceList.add(newItemSource);
            }

            clonedItem.setItemSourceList(newItemSourceList);
        }

        return clonedItem;
    }

    protected ItemDomainEntity cloneCreateItemElements(ItemDomainEntity clonedItem, ItemDomainEntity cloningFrom) {
        return cloneCreateItemElements(clonedItem, cloningFrom, false);
    }

    protected ItemDomainEntity cloneCreateItemElements(ItemDomainEntity clonedItem, ItemDomainEntity cloningFrom, boolean addContained) {
        List<ItemElement> cloningFromItemElementList = cloningFrom.getItemElementDisplayList();

        if (cloningFromItemElementList != null) {
            for (ItemElement itemElement : cloningFromItemElementList) {
                ItemElement newItemElement = new ItemElement();

                if (itemElement.getDerivedFromItemElement() != null) {
                    newItemElement.init(clonedItem, itemElement.getDerivedFromItemElement());
                } else {
                    newItemElement.init(clonedItem);
                }

                if (addContained) {
                    newItemElement.setContainedItem(itemElement.getContainedItem());
                }

                newItemElement.setName(itemElement.getName());

                clonedItem.getFullItemElementList().add(newItemElement);
            }
        }

        return clonedItem;
    }

    public ItemDomainEntity completeClone(ItemDomainEntity clonedItem, Integer cloningFromItemId) {
        ItemDomainEntity cloningFrom = findById(cloningFromItemId);

        if (cloneProperties) {
            clonedItem = cloneProperties(clonedItem, cloningFrom);
        }
        if (cloneSources) {
            clonedItem = cloneSources(clonedItem, cloningFrom);
        }
        if (cloneCreateItemElementPlaceholders) {
            clonedItem = cloneCreateItemElements(clonedItem, cloningFrom);
        }

        cloneProperties = false;
        cloneSources = false;
        cloneCreateItemElementPlaceholders = false;

        return clonedItem;
    }

    public boolean isShowCloneCreateItemElementsPlaceholdersOption() {
        if (itemToClone != null) {
            return !itemToClone.getItemElementDisplayList().isEmpty();
        }
        return false;
    }

    public boolean isShowCloneSourcesOption() {
        if (itemToClone != null) {
            return itemToClone.getItemSourceList() != null && !itemToClone.getItemSourceList().isEmpty();
        }
        return false;
    }

    public boolean isShowClonePropertiesOption() {
        if (itemToClone != null) {
            return itemToClone.getPropertyValueList() != null && !itemToClone.getPropertyValueList().isEmpty();
        }
        return false;
    }

    public boolean isNoCloneOptionsAvailable() {
        return !isShowCloneCreateItemElementsPlaceholdersOption()
                && !isShowClonePropertiesOption()
                && !isShowCloneSourcesOption();
    }

    public String prepareCloneForItemToClone() {
        ItemDomainEntity item = (ItemDomainEntity) itemToClone;
        itemToClone = null;
        return prepareClone(item);
    }

    public String checkNoOptionsPrepareCloneForItemToClone() {
        if (isNoCloneOptionsAvailable()) {
            return prepareCloneForItemToClone();
        }

        return "";
    }

    @Override
    protected String getCloneCreatePageName() {
        return "simpleCreate";
    }

    public Boolean getCloneProperties() {
        return cloneProperties;
    }

    public void setCloneProperties(Boolean cloneProperties) {
        this.cloneProperties = cloneProperties;
    }

    public Boolean getCloneCreateItemElementPlaceholders() {
        return cloneCreateItemElementPlaceholders;
    }

    public void setCloneCreateItemElementPlaceholders(Boolean cloneCreateItemElementPlaceholders) {
        this.cloneCreateItemElementPlaceholders = cloneCreateItemElementPlaceholders;
    }

    public Boolean getCloneSources() {
        return cloneSources;
    }

    public void setCloneSources(Boolean cloneSources) {
        this.cloneSources = cloneSources;
    }

    public Item getItemToClone() {
        return itemToClone;
    }

    public void setItemToClone(Item itemToClone) {
        this.itemToClone = itemToClone;
    }

    public ItemElement getNewItemElementForCurrent() {
        return newItemElementForCurrent;
    }

    public Boolean getNewItemElementForCurrentSaveButtonEnabled() {
        return newItemElementForCurrentSaveButtonEnabled;
    }

    public TreeNode getItemElementListTreeTableRootNode() {
        return itemElementListTreeTableRootNode;
    }

    public void setItemElementListTreeTableRootNode(TreeNode itemElementListTreeTableRootNode) {
        this.itemElementListTreeTableRootNode = itemElementListTreeTableRootNode;
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().toString();
        }
        return "";
    }

    public boolean getDisplayItemSourceList() {
        Item item = getCurrent();
        if (item != null) {
            List<ItemSource> itemSourceList = item.getItemSourceList();
            return itemSourceList != null && !itemSourceList.isEmpty();
        }
        return false;
    }
    
    public boolean getDisplayItemConnectorList() {
        Item item = getCurrent();
        if (item != null) {
            List<ItemConnector> itemConnectorList = item.getItemConnectorList(); 
            return itemConnectorList != null && !itemConnectorList.isEmpty();
        }
        return false; 
    }

    public boolean getDisplayItemElementList() {
        Item item = getCurrent();
        if (item != null) {
            List<ItemElement> itemElementList = item.getItemElementDisplayList();
            return itemElementList != null && !itemElementList.isEmpty();
        }
        return false;
    }

    public Boolean getDisplayDerivedFromItemList() {
        Item item = getCurrent();
        if (item != null) {
            List<Item> derivedFromItemList = item.getDerivedFromItemList();
            return derivedFromItemList != null && !derivedFromItemList.isEmpty();
        }
        return false;
    }

    public Boolean getDisplayItemMembership() {
        Item item = getCurrent();
        if (item != null) {
            List<ItemElement> itemElementMemberList = item.getItemElementMemberList();
            return itemElementMemberList != null && !itemElementMemberList.isEmpty();
        }
        return false;
    }

    public Boolean getDisplayDerivedFromPropertyList() {
        Item item = getCurrent();
        if (item != null) {
            Item derivedFromItem = item.getDerivedFromItem();
            if (derivedFromItem != null) {
                List<PropertyValue> derivedFromItemPropertyValueList = derivedFromItem.getPropertyValueList();
                return derivedFromItemPropertyValueList != null && !derivedFromItemPropertyValueList.isEmpty();
            }
        }
        return false;
    }

    public List<Item> getParentItemList() {
        if (currentHasChanged()) {
            Item itemEntity = getCurrent();

            parentItemList = new ArrayList<>();

            List<ItemElement> itemElementList = itemEntity.getItemElementMemberList();
            // Remove currently being viewed item. 
            if (itemElementList != null) {
                for (ItemElement itemElement : itemElementList) {
                    if (parentItemList.contains(itemElement.getParentItem()) == false) {
                        parentItemList.add(itemElement.getParentItem());
                    }
                }
            }
        }

        return parentItemList;
    }   

    public Boolean isItemProjectRequired() {
        return getEntityDisplayItemProject();
    }
    
    public String getDisplayListDataModelScopeDisplayString() {
        if (settingObject.getDisplayListDataModelScope() != null) {
            if (settingObject.getDisplayListDataModelScope().equals(ItemDisplayListDataModelScope.showItemsWithPropertyType.getValue())) {
                return settingObject.getDisplayListDataModelScope() + " '" + getDisplayPropertyTypeName(settingObject.getDisplayListDataModelScopePropertyTypeId()) + "'";
            }
        }
        return settingObject.getDisplayListDataModelScope();
    }         

    public boolean isDisplayListDataModelScopePropertyFilterable() {
        return fetchFilterablePropertyValue(settingObject.getDisplayListDataModelScopePropertyTypeId());
    }
    
    @Override
    protected void setPreProcessPropertyValueInformation(CdbDomainEntity entity) {
        super.setPreProcessPropertyValueInformation(entity);

        if (settingObject.isDisplayListDataModelScopePropertyTypeSelection()) {
            loadPropertyValueInformation(settingObject.getDisplayListDataModelScopePropertyTypeId(), entity);
        }
    }

    @Override
    protected void updatePreProcessCurrentPropertyValueSettingsLoaded() {
        super.updatePreProcessCurrentPropertyValueSettingsLoaded();

        if (settingObject.isDisplayListDataModelScopePropertyTypeSelection()) {
            addPropertyTypeToLoadedDisplayPropertyTypes(settingObject.getDisplayListDataModelScopePropertyTypeId());
        }
    }

    @Override
    protected boolean isPreProcessPropertyValueInformationSettingsPresent() {
        boolean result = super.isPreProcessPropertyValueInformationSettingsPresent();

        if (!result) {
            if (settingObject.isDisplayListDataModelScopePropertyTypeSelection()) {
                return settingObject.getDisplayListDataModelScopePropertyTypeId() != null;
            }
        }

        return result;
    }

    @Override
    public List<PropertyValue> getImageList() {
        // Place primary image at front. 
        List<PropertyValue> imageList = super.getImageList();

        if (imageList != null) {
            PropertyValue propertyValue = getPrimaryImagePropertyValueForItem(getCurrent());

            if (propertyValue != null) {
                imageList.remove(propertyValue);
                imageList.add(0, propertyValue);
            }
        }

        return imageList;
    }

    public boolean isShowMakeImagePrimaryButton() {
        if (LoginController.getInstance().isEntityWriteable(getCurrent().getEntityInfo())) {
            // entity is writeable. 
            return getPropertyValueListWithHandlerForImages(getCurrent()).size() > 1;
        }

        return false;
    }

    public void markImagePrimary(PropertyValue imagePropertyValue) {
        String imageHandlerName = ImagePropertyTypeHandler.HANDLER_NAME;
        List<PropertyValue> imagePropertyValueList = getPropertyValueListWithHandler(getCurrent().getPropertyValueList(), imageHandlerName);

        String currentValue = imagePropertyValue.getPropertyMetadataValueForKey(PRIMARY_IMAGE_PROPERTY_METADATA_KEY);
        if (currentValue != null) {
            if (currentValue.equals("true")) {
                SessionUtility.addInfoMessage("Already Primary", "Selected Image is already primary.");
                return;
            }
        }

        boolean requestedPropertyFound = false;
        for (PropertyValue propertyValue : imagePropertyValueList) {
            if (propertyValue.equals(imagePropertyValue)) {
                // will be updated. 
                requestedPropertyFound = true;
                continue;
            }
            propertyValue.setPropertyMetadataValue(PRIMARY_IMAGE_PROPERTY_METADATA_KEY, "false");
        }

        if (requestedPropertyFound) {
            imagePropertyValue.setPropertyMetadataValue(PRIMARY_IMAGE_PROPERTY_METADATA_KEY, "true");
            update();
            // Should be regenerated. 
            getCurrent().setPrimaryImageValue(null);
            getCurrent().setImagePropertyList(null);
        } else {
            // Views should not allow this case to execute. 
            SessionUtility.addErrorMessage("Error", "Requested property value was not found in current item. Please go to its detail view page.");
        }

    }

    public String getPrimaryImageButtonIcon(Item item, PropertyValue propertyValue) {
        String primaryImageValue = getPrimaryImageValueForItem(item);
        if (propertyValue.getValue() != null) {
            if (propertyValue.getValue().equals(primaryImageValue)) {
                return "ui-icon-check";
            }
        }
        return "ui-icon-close";
    }

    public boolean itemHasPrimaryImage(Item item) {
        if (item != null) {
            String value = getPrimaryImageValueForItem(item);
            return !value.isEmpty();
        }
        return false;
    }

    public String getPrimaryImageThumbnailForItem(Item item) {
        String value = getPrimaryImageValueForItem(item);
        if (!value.isEmpty()) {
            return PropertyValueController.getThumbnailImagePathByValue(value);
        }
        return value;
    }

    /**
     * Get primary image value for an item. Checks if the value needs to be
     * loaded.
     *
     * @param item - Item to get primary image value for.
     * @return
     */
    public String getPrimaryImageValueForItem(Item item) {
        if (item.getPrimaryImageValue() == null) {
            loadItemPrimaryImageValueForItem(item);
            if (item.getPrimaryImageValue() == null) {
                item.setPrimaryImageValue("");
            }
        }
        return item.getPrimaryImageValue();
    }

    /**
     * Determines what is the primary image for a particular item and sets the
     * value.
     *
     * @param item
     */
    private void loadItemPrimaryImageValueForItem(Item item) {
        PropertyValue propertyValue = getPrimaryImagePropertyValueForItem(item);

        if (propertyValue != null) {
            item.setPrimaryImageValue(propertyValue.getValue());
        } else {
            item.setPrimaryImageValue(null);
        }

    }

    private PropertyValue getPrimaryImagePropertyValueForItem(Item item) {
        List<PropertyValue> imagePropertyValueList = getPropertyValueListWithHandlerForImages(item);
        if (!imagePropertyValueList.isEmpty()) {
            for (PropertyValue propertyValue : imagePropertyValueList) {
                String value = propertyValue.getPropertyMetadataValueForKey(PRIMARY_IMAGE_PROPERTY_METADATA_KEY);

                if (value != null) {
                    if (value.equals("true")) {
                        return propertyValue;
                    }
                }

            }
            return imagePropertyValueList.get(0);

        }
        return null;
    }

    private List<PropertyValue> getPropertyValueListWithHandlerForImages(Item item) {
        String imageHandlerName = ImagePropertyTypeHandler.HANDLER_NAME;
        return getPropertyValueListWithHandler(item.getPropertyValueDisplayList(), imageHandlerName);
    }

    private List<PropertyValue> getPropertyValueListWithHandler(List<PropertyValue> propertyValueList, String handlerName) {
        List<PropertyValue> resultingList = new ArrayList<>();
        for (PropertyValue propertyValue : propertyValueList) {
            PropertyType propertyType = propertyValue.getPropertyType();
            if (propertyType != null) {
                PropertyTypeHandler propertyTypeHandler = propertyType.getPropertyTypeHandler();
                if (propertyTypeHandler != null && propertyTypeHandler.getName().equals(handlerName)) {
                    resultingList.add(propertyValue);
                }
            }
        }

        return resultingList;
    }

    @Override
    public Boolean getDisplayLoadPropertyValuesButton() {
        if (isFilterByPropertiesAutoLoad()) {
            return false;
        }

        return super.getDisplayLoadPropertyValuesButton()
                || checkDisplayLoadPropertyValueButtonByProperty(settingObject.getDisplayListDataModelScopePropertyTypeId());
    }   

    @Override
    protected ItemDomainEntity createEntityInstance() {
        ItemDomainEntity item = instenciateNewItemDomainEntity();

        Domain domain = getDefaultDomain();
        if (domain != null) {
            item.init(domain);
        } else {
            item.init();
        }

        if (qrIdViewParam != null) {
            item.setQrId(qrIdViewParam);
            qrIdViewParam = null;
        }

        return item;
    }

    @Override
    public ItemDomainEntity selectByViewRequestParams() throws CdbException {
        setBreadcrumbRequestParams();
        Integer idParam = null;
        String paramValue = SessionUtility.getRequestParameterValue("id");

        try {
            if (paramValue != null) {
                idParam = Integer.parseInt(paramValue);
            }
        } catch (NumberFormatException ex) {
            throw new InvalidRequest("Invalid value supplied for " + getDisplayEntityTypeName() + " id: " + paramValue);
        }
        if (idParam != null) {
            ItemDomainEntity item = findById(idParam);
            if (item == null) {
                throw new InvalidRequest("Item id " + idParam + " does not exist.");
            }

            return performItemRedirection(item, "id=" + idParam, false);

        } else {
            // Due to bug in primefaces, we cannot have more than one
            // f:viewParam on the web page, so process qrId here
            paramValue = SessionUtility.getRequestParameterValue("qrId");
            if (paramValue != null) {
                try {
                    Integer qrParam = Integer.parseInt(paramValue);
                    ItemDomainEntity item = findByQrId(qrParam);
                    if (item == null) {
                        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();

                        ItemDomainInventoryController inventoryController;
                        inventoryController = ItemDomainInventoryController.getInstance();
                        inventoryController.qrIdViewParam = qrParam;
                        inventoryController.setCurrent(null);

                        if (sessionUser != null) {
                            SessionUtility.navigateTo("/views/itemDomainInventory/create.xhtml?faces-redirect=true");
                        } else {
                            SessionUtility.pushViewOnStack("/views/item/view.xhtml?qrId=" + qrParam);
                            SessionUtility.navigateTo("/views/login.xhtml?faces-redirect=true");
                        }
                        return null;
                    }

                    return performItemRedirection(item, "qrId=" + qrParam, false);
                } catch (NumberFormatException ex) {
                    throw new InvalidRequest("Invalid value supplied for QR id: " + paramValue);
                }
            } else if (current == null) {
                throw new InvalidRequest(getDisplayEntityTypeName() + " has not been selected.");
            }
            return current;
        }
    }

    @Override
    public String prepareView(Item item) {
        prepareItemElementListTreeTable(item);
        return "/views/item/view.xhtml?faces-redirect=true&id=" + item.getId();
    }

    private ItemDomainEntity performItemRedirection(ItemDomainEntity item, String paramString, boolean forceRedirection) {
        String currentViewId = SessionUtility.getCurrentViewId();

        Domain itemDomain = item.getDomain();
        String desiredViewId;
        if (itemDomain != null) {
            desiredViewId = getDomainPath(itemDomain) + "/view.xhtml";
        } else {
            desiredViewId = "/views/item/view.xhtml";
        }

        if (currentViewId.equals(desiredViewId)) {
            if (forceRedirection == false) {
                setCurrent(item);
                prepareView(item);
                return item;
            }
        }

        SessionUtility.navigateTo(desiredViewId + "?" + paramString + "faces-redirect=true");
        return null;
    }

    public String getCurrentItemDisplayTitle() {
        if (current != null) {
            if (current.getQrId() != null) {
                return "Qr: " + current.getQrIdDisplay();
            } else if (current.getName() != null) {
                return ": " + current.getName();
            }
        }
        return "";
    }

    public ItemDomainEntity findByQrId(Integer qrId) {
        return getEntityDbFacade().findByQrId(qrId);
    }

    public String getItemElementContainedItemText(ItemElement itemElement) {
        if (itemElement.getContainedItem() == null) {
            return "No item is contained.";
        }

        return itemElement.getContainedItem().getName();
    }

    @Override
    public EntityInfo getEntityInfo(Item item) {
        return item.getEntityInfo();
    }

    @Override
    public void prepareEntityInsert(ItemDomainEntity item) throws CdbException {
        checkItem(item);
    }

    @Override
    public void prepareEntityUpdate(ItemDomainEntity item) throws CdbException {
        checkItem(item);
        item.resetAttributesToNullIfEmpty();
        EntityInfo entityInfo = item.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
        Log logEntry = prepareLogEntry();
        if (logEntry != null) {
            List<Log> logList = item.getLogList();
            logList.add(logEntry);
            item.setLogList(logList);
        }

        // Compare properties with what is in the db
        List<PropertyValue> originalPropertyValueList = getEntityDbFacade().findById(item.getId()).getPropertyValueList();
        List<PropertyValue> newPropertyValueList = item.getPropertyValueList();
        logger.debug("Verifying properties for item " + item);
        PropertyValueUtility.preparePropertyValueHistory(originalPropertyValueList, newPropertyValueList, entityInfo);
        item.clearPropertyValueCache();
        prepareImageList(item);

        List<Item> derivedFromItemList = item.getDerivedFromItemList();
        if (derivedFromItemList != null) {
            for (Item derivedItem : derivedFromItemList) {
                derivedItem.resetAttributesToNullIfEmpty();
                ItemController derivedItemController = getItemItemController(derivedItem);
                derivedItemController.checkItem(derivedItem);
            }
        }

        logger.debug("Updating item " + item.getId()
                + " (user: " + entityInfo.getLastModifiedByUser().getUsername() + ")");

    }

    @Override
    protected void prepareEntityDestroy(ItemDomainEntity item) throws CdbException {
        super.prepareEntityDestroy(item);
        if (item.getItemElementMemberList() != null && item.getItemElementMemberList().isEmpty() == false) {
            throw new CdbException("Item is part of an assembly.");
        }
    }

    protected ItemController getItemItemController(Item item) {
        return findDomainController(getItemDomainName(item));
    }

    protected String getItemDomainName(Item item) {
        Domain domain = item.getDomain();
        return domain.getName();
    }

    public void checkCurrentItem() throws CdbException {
        checkCurrentItem(false);
    }

    public void checkCurrentItem(boolean skipProjects) throws CdbException {
        if (getCurrent() != null) {
            checkItem(getCurrent(), skipProjects);
        } else {
            throw new CdbException("Current item does not exist.");
        }
    }

    protected void checkItem(ItemDomainEntity item) throws CdbException {
        checkItem(item, false);
    }

    protected void checkItem(ItemDomainEntity item, boolean skipProjects) throws CdbException {
        Domain itemDomain = item.getDomain();

        if (itemDomain == null) {
            throw new CdbException("No domain has been specified for " + itemDomainToString(item));
        }

        if (skipProjects == false) {
            if (isItemProjectRequired()) {
                checkItemProject(item);
            }
        }

        checkItemUniqueness(item);
        checkItemElementsForItem(item);
    }

    protected void checkItemElementsForItem(ItemDomainEntity item) throws CdbException {
        item.resetItemElementDisplayList();
        List<String> elementNames = new ArrayList<>();
        for (ItemElement itemElement : item.getItemElementDisplayList()) {
            if (itemElement.getName() == null || itemElement.getName().isEmpty()) {
                throw new CdbException("Item element name cannot be empty.");
            }
            String itemElementName = itemElement.getName();
            if (elementNames.contains(itemElementName)) {
                throw new CdbException("Element names must be unique within their assembly. '" + itemElementName + "' is repeated.");
            }

            elementNames.add(itemElement.getName());
        }
        // Throws exception if a tree cannot be generated due to circular reference. 
        ItemElementUtility.createItemElementRoot(item);
    }

    public void checkItemElement(ItemElement itemElement) throws CdbException {
        ItemDomainEntity parentItem = (ItemDomainEntity) itemElement.getParentItem();
        checkItemElementsForItem(parentItem);
    }

    protected String itemDomainToString(Item item) {
        return item.toString();
    }

    public boolean isAllowedSetDerivedFromItemForCurrentItem() {
        return getEntityDisplayDerivedFromItem();
    }

    public void checkItemUniqueness(Item item) throws CdbException {
        String name = item.getName();
        String itemIdentifier1 = item.getItemIdentifier1();
        String itemIdentifier2 = item.getItemIdentifier2();
        Item derivedFromItem = item.getDerivedFromItem();
        Domain itemDomain = item.getDomain();
        Integer qrId = item.getQrId();

        if (getEntityDisplayItemName()) {
            if (name != null && name.isEmpty()) {
                throw new CdbException("No " + getNameTitle() + " has been specified for " + itemDomainToString(item));
            }
        }

        if (getEntityDisplayQrId()) {
            if (qrId != null) {
                Item existingItem = getEntityDbFacade().findByQrId(qrId);
                if (existingItem != null) {
                    if (!Objects.equals(existingItem.getId(), item.getId())) {
                        throw new ObjectAlreadyExists("Item " + existingItem.toString() + " already exists with qrId " + existingItem.getQrIdDisplay() + ".");
                    }
                }
            }
        }

        Item existingItem = getEntityDbFacade().findByUniqueAttributes(derivedFromItem, itemDomain, name, itemIdentifier1, itemIdentifier2);

        // The same item will have all the same attributes if it wasn't changed.  
        if (existingItem != null) {
            if (Objects.equals(item.getId(), existingItem.getId()) == false) {
                String additionalInfo = "Please update some of the following:  ";

                if (getEntityDisplayItemName()) {
                    additionalInfo += "Name, ";
                }
                if (getEntityDisplayItemIdentifier1()) {
                    additionalInfo += getItemIdentifier1Title() + ", ";
                }
                if (getEntityDisplayItemIdentifier2()) {
                    additionalInfo += getItemIdentifier2Title() + ", ";
                }

                //Remove last comma. 
                additionalInfo = additionalInfo.substring(0, additionalInfo.length() - 2);

                throw new ObjectAlreadyExists("Item " + itemDomainToString(item) + " has nonunique attributes. " + additionalInfo);

            }
        }
    }

    protected SettingController getSettingController() {
        if (settingController == null) {
            settingController = SettingController.getInstance();
        }
        return settingController; 
    }

    @FacesConverter(value = "itemConverter", forClass = Item.class)
    public static class ItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent item, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemController controller = (ItemGenericViewController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemGenericViewController");
            return controller.getItem(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Item) {
                Item o = (Item) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Item.class.getName());
            }
        }

    }

}
