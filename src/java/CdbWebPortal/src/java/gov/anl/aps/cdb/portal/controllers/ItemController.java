/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.common.utilities.CollectionUtility;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDisplayListDataModelScope;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.constants.ListName;
import gov.anl.aps.cdb.portal.constants.PortalStyles;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ConnectorControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.EntityTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemConnectorFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.beans.ItemTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ListFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyMetadataValue;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.ListTbl;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import gov.anl.aps.cdb.portal.model.jsf.handlers.ImagePropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataFieldInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.Visibility;

public abstract class ItemController<
            ControllerUtility extends ItemControllerUtility<ItemDomainEntity, ItemDomainEntityFacade>, ItemDomainEntity extends Item, ItemDomainEntityFacade extends ItemFacadeBase<ItemDomainEntity>, ItemSettingsObject extends ItemSettings>
        extends CdbDomainEntityController<ControllerUtility, ItemDomainEntity, ItemDomainEntityFacade, ItemSettingsObject> implements IItemController<ItemDomainEntity, ItemSettingsObject> {

    private static final Logger LOGGER = LogManager.getLogger(ItemController.class.getName());
    protected static final String PRIMARY_IMAGE_PROPERTY_METADATA_KEY = "Primary";

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

    @EJB
    protected RelationshipTypeFacade relationshipTypeFacade;

    @EJB
    private ItemElementRelationshipFacade itemElementRelationshipFacade;

    @EJB
    protected PropertyTypeFacade propertyTypeFacade;
    
    @EJB
    protected ItemConnectorFacade itemConnectorFacade;

    private List<ItemElementRelationship> locationRelationshipCache;

    private List<Item> parentItemList;
    private int currentItemEntityHashCode;

    protected DataModel scopedListDataModel = null;
    protected List<String> displayListDataModelScopeSelectionList = null;

    protected Integer qrIdViewParam = null;

    private List<ItemDomainEntity> selectItemCandidateList;
    private List<Item> selectedItems;

    private List<ItemDomainEntity> selectItemElementItemCandidateList;

    protected DataModel templateItemsListDataModel = null;
    protected ItemDomainEntity templateToCreateNewItem = null;

    protected DataModel itemsWithNoParentsListDataModel = null;
    protected TreeNode itemsWithNoParentsRootNode = null;

    private Domain selectionDomain;

    protected DataModel allowedChildItemSelectDataModel = null;

    protected List<ItemCategory> domainItemCategoryList = null;

    protected Boolean cloneProperties = false;
    protected Boolean cloneCreateItemElementPlaceholders = false;
    protected Boolean cloneSources = false;
    protected Item itemToClone;

    // Globalized item project functionality. 
    protected ItemProjectController itemProjectController = null;
    protected SettingController settingController;

    private LocatableItemController locatableItemController = null;

    protected ItemElementController itemElementController;

    protected Integer domainId = null;

    protected Domain defaultControllerDomain = null;

    protected List<String> expandedRowUUIDs = null;

    protected ItemMetadataPropertyInfo coreMetadataPropertyInfo = null;
    protected PropertyType coreMetadataPropertyType = null;

    public ItemController() {
    }

    /**
     * Get item create wizard controller for current controller.
     *
     * @return
     */
    protected ItemCreateWizardController getItemCreateWizardController() {
        return null;
    }

    /**
     * Get item multi edit controller for the current controller.
     *
     * @return
     */
    public ItemMultiEditController getItemMultiEditController() {
        return null;
    }

    @Override
    public ItemEnforcedPropertiesController getItemEnforcedPropertiesController() {
        return null;
    }

    public Domain getDefaultDomain() {
        return getControllerUtility().getDefaultDomain();
    }

    @Override
    public String getNameTitle() {
        return getControllerUtility().getNameTitle();
    }

    public String getItemElementsListTitle() {
        if (settingObject.getDisplayItemElementSimpleView()) {
            return "Assembly Listing";
        }
        return "Elements";
    }

    public String getItemEntityTypeTitle() {
        return "Entity Type";
    }

    @Override
    public String getItemItemTypeTitle() {
        if (getDefaultDomain() != null) {
            return getDefaultDomain().getItemTypeLabel();
        }
        return null;
    }

    @Override
    public String getItemItemCategoryTitle() {
        if (getDefaultDomain() != null) {
            return getDefaultDomain().getItemCategoryLabel();
        }
        return null;
    }

    @Override
    public String getItemIdentifier1Title() {
        return getControllerUtility().getItemIdentifier1Title();
    }

    @Override
    public String getItemIdentifier2Title() {
        return getControllerUtility().getItemIdentifier2Title();
    }

    @Override
    public final String getDerivedFromItemTitle() {
        return getControllerUtility().getDerivedFromItemTitle();
    }

    /**
     * Return true if item elements can be sorted when user has permissions to
     * update item.
     *
     * @return
     */
    public boolean getEntityHasSortableElements() {
        return false;
    }

    public boolean getEntityDisplayItemElementsForCurrent() {
        return getEntityDisplayItemElements();
    }

    @Override
    public final boolean getEntityDisplayItemIdentifier1() {
        return getControllerUtility().isEntityHasItemIdentifier1();
    }

    @Override
    public final boolean getEntityDisplayItemIdentifier2() {
        return getControllerUtility().isEntityHasItemIdentifier2();
    }

    @Override
    public boolean getEntityDisplayItemType() {
        return getItemItemTypeTitle() != null;
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        return getItemItemCategoryTitle() != null;
    }

    public boolean getEntityDisplayTemplates() {
        return false;
    }

    @Override
    public boolean getEntityDisplayDeletedItems() {
        return false;
    }

    @Override
    public boolean getEntityDisplayConnectorCableEndDesignation() {
        return false;
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

    public List<ItemDomainEntity> getItemListWithProjectExcludeTemplates(ItemProject itemProject) {
        String projectName = itemProject.getName();
        String templateEntityTypeName = EntityTypeName.template.getValue();
        return getEntityDbFacade().findByDomainAndProjectExcludeEntityType(getDefaultDomainName(), projectName, templateEntityTypeName);
    }

    /**
     * Called from item project controller whenever item project changes.
     *
     */
    @Override
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

        LOGGER.warn("Item project controller was not set properly.");
        return null;
    }

    @Override
    public void processPreRenderList() {
        super.processPreRenderList();
        filteredObjectList = null;

        expandedRowUUIDs = new ArrayList<>();

        // Check if itemProject registered. 
        if (itemProjectController == null) {
            itemProjectController = ItemProjectController.getInstance();
            itemProjectController.addItemControllerProjectChangeListener(this);
        }
    }

    public void processPreRenderTemplateList() {
        processPreRenderList();
    }

    @Override
    public final List<ItemDomainEntity> getItemList() {
        return getControllerUtility().getItemList();
    }

    public List<ItemDomainEntity> getItemListExcludeTemplates() {
        String templateEntityTypeName = EntityTypeName.template.getValue();
        return getEntityDbFacade().findByDomainNameAndExcludeEntityType(getDefaultDomainName(), templateEntityTypeName);
    }

    public List<ItemDomainEntity> getItemListExcludeDeletedItems() {
        String entityTypeName = EntityTypeName.deleted.getValue();
        return getEntityDbFacade().findByDomainNameAndExcludeEntityType(getDefaultDomainName(), entityTypeName);
    }

    @Override
    public List<ItemCategory> getDomainItemCategoryList() {
        if (domainItemCategoryList == null) {
            domainItemCategoryList = itemCategoryFacade.findByDomainName(this.getDefaultDomainName());
        }
        return domainItemCategoryList;
    }

    @Override
    public SelectItem[] getDomainItemCategoryListForSelectOne() {
        return CollectionUtility.getSelectItems(getDomainItemCategoryList(), true);
    }

    @Override
    public List<ItemType> getAvailableItemTypesForCurrentItem() {
        return getAvailableItemTypes(getCurrent());
    }

    @Override
    public List<ItemType> getAvailableItemTypes(Item item) {
        List<ItemType> availableItemType = null;
        if (item != null) {
            if (getEntityDisplayItemCategory()) {
                List<ItemCategory> itemCategoryList = item.getItemCategoryList();

                if (item.getLastKnownItemCategoryList() != null) {
                    if (item.getLastKnownItemCategoryList().size() != itemCategoryList.size()) {
                        item.setAvailableItemTypes(null);
                    } else {
                        for (ItemCategory itemCategory : item.getLastKnownItemCategoryList()) {
                            if (itemCategoryList.contains(itemCategory) == false) {
                                item.setAvailableItemTypes(null);
                                break;
                            }
                        }
                    }
                } else {
                    item.setAvailableItemTypes(null);
                }
                item.setLastKnownItemCategoryList(itemCategoryList);

                if (item.getAvailableItemTypes() == null) {
                    List<ItemType> avaiableTypesForItemCategoryList = getAvaiableTypesForItemCategoryList(itemCategoryList);
                    item.setAvailableItemTypes(avaiableTypesForItemCategoryList);
                    updateItemTypeListBasedOnNewAvailableTypes(item);
                }
            } else {
                // Item does not have item category to pick from                
                item.setAvailableItemTypes(itemTypeFacade.findByDomainName(getDefaultDomainName()));
            }
            availableItemType = item.getAvailableItemTypes();
        }

        return availableItemType;
    }

    private void updateItemTypeListBasedOnNewAvailableTypes(Item item) {
        if (item.getItemTypeList() != null) {
            List<ItemType> itemItemTypeList = new ArrayList<>(item.getItemTypeList());
            List<ItemType> availableItemTypes = item.getAvailableItemTypes();

            for (ItemType itemType : itemItemTypeList) {
                if (availableItemTypes.contains(itemType) == false) {
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

    @Override
    public String getItemItemTypeEditString(Item item) {
        if (item != null) {
            if (isDisabledItemItemType(item)) {
                return "First Select " + getItemItemCategoryTitle();
            } else {
                return item.getEditItemTypeString(getItemItemTypeTitle());
            }
        }
        return "";
    }

    @Override
    public boolean isDisabledItemItemType(Item item) {
        List<ItemType> avaiableItemTypesForCurrentItem = getAvailableItemTypes(item);
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
        if (getEntityDisplayTemplates()) {
            if (currentItemProject != null) {
                itemList = getItemListWithProjectExcludeTemplates(currentItemProject);
            } else {
                itemList = getItemListExcludeTemplates();
            }
        } else {
            if (currentItemProject != null) {
                itemList = getItemListWithProject(currentItemProject);
            } else {
                itemList = getAllObjectList();
            }
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
        return "itemDomain" + domainName.replaceAll("\\s", "");
    }

    @Override
    public String getEntityApplicationViewPath() {
        return getDomainPath(getDefaultDomain());
    }

    @Override
    public String getCurrentEntityRelativePermalink() {
        ItemDomainEntity current = getCurrent();
        return "/views/item/view?id=" + current.getId();
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

    @Override
    public final ItemController getSelectionController() {
        return this;
    }

    public static ItemController findDomainControllerForItem(Item item) {
        if (item.getDomain() != null) {
            String domainName = item.getDomain().getName();
            domainName = domainName.replace(" ", "");
            return findDomainController(domainName);
        }
        return null;
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

    @Override
    public String getDomainControllerName() {
        return getDomainControllerName(getDefaultDomainName());
    }

    protected static String getDomainControllerName(String domainName) {
        domainName = domainName.replace(" ", "");
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

    @Override
    public boolean isEntityTypeEditable() {
        List<EntityType> allowedEntityType = getDomainAllowedEnityTypes();
        return allowedEntityType != null && !allowedEntityType.isEmpty();
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
        templateItemsListDataModel = null;
        itemsWithNoParentsRootNode = null;
        displayListDataModelScopeSelectionList = null;
        locationRelationshipCache = null;
    }

    public boolean isDataTableNotScoped() {
        return settingObject.getDisplayListDataModelScope().equals(ItemDisplayListDataModelScope.showAll.getValue());
    }

    public final DataModel getScopedListDataModel() {
        // Show all
        if (isDataTableNotScoped()) {
            return getListDataModel();
        } else if (scopedListDataModel == null) {
            String templateEntityTypeName = EntityTypeName.template.getValue();
            ItemDomainEntityFacade itemFacade = getEntityDbFacade();
            String domainName = getDefaultDomainName();

            if (settingObject.getDisplayListDataModelScope().equals(ItemDisplayListDataModelScope.showItemsWithPropertyType.getValue())) {
                if (settingObject.getDisplayListDataModelScopePropertyTypeId() == null) {
                    return null;
                } else {
                    /**
                     * GET ITEMS WITH PROEPRTY TYPE *
                     */
                    List<ItemDomainEntity> itemList = null;
                    ItemProject currentProject = getCurrentItemProject();
                    Integer propertyTypeId = settingObject.getDisplayListDataModelScopePropertyTypeId();
                    if (getEntityDisplayTemplates()) {
                        if (currentProject != null) {
                            itemList = itemFacade.getItemsWithPropertyTypeAndProjectExcludeEntityType(
                                    domainName,
                                    propertyTypeId,
                                    currentProject.getName(),
                                    templateEntityTypeName);
                        } else {
                            itemList = itemFacade.getItemListWithPropertyTypeExcludeEntityType(
                                    domainName,
                                    propertyTypeId,
                                    templateEntityTypeName);
                        }
                    } else {
                        if (currentProject != null) {
                            itemList = itemFacade.getItemsWithPropertyTypeAndProject(
                                    domainName,
                                    propertyTypeId,
                                    currentProject.getName());
                        } else {
                            itemList = itemFacade.getItemListWithPropertyType(
                                    domainName,
                                    propertyTypeId);
                        }

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
                    scopedListDataModel = createFavoritesListDataModel();
                } else {
                    // Show owned or owned & favorites. 
                    boolean showOwnedAndFavorites = settingObject.getDisplayListDataModelScope().equals(ItemDisplayListDataModelScope.showOwnedPlusFavorites.getValue());
                    boolean showOwned = !showOwnedAndFavorites;

                    List<ItemDomainEntity> itemList = null;

                    if (showOwnedAndFavorites) {
                        if (getFavoritesList() != null) {
                            if (settingEntity instanceof UserInfo) {
                                if (getEntityDisplayTemplates()) {
                                    itemList = itemFacade
                                            .getItemListContainedInListOrOwnedByUserExcludeEntityType(domainName, getFavoritesList(), (UserInfo) settingEntity, templateEntityTypeName);
                                } else {
                                    itemList = itemFacade
                                            .getItemListContainedInListOrOwnedByUser(domainName, getFavoritesList(), (UserInfo) settingEntity);
                                }
                            } else if (settingEntity instanceof UserGroup) {
                                if (getEntityDisplayTemplates()) {
                                    itemList = itemFacade
                                            .getItemListContainedInListOrOwnedByGroupExcludeEntityType(domainName, getFavoritesList(), (UserGroup) settingEntity, templateEntityTypeName);
                                } else {
                                    itemList = itemFacade
                                            .getItemListContainedInListOrOwnedByGroup(domainName, getFavoritesList(), (UserGroup) settingEntity);
                                }
                            }
                        } else {
                            // No favorites list will not show any results from query. 
                            showOwned = true;
                        }
                    }

                    if (showOwned) {
                        if (settingEntity instanceof UserInfo) {
                            if (getEntityDisplayTemplates()) {
                                itemList = itemFacade
                                        .getItemListOwnedByUserExcludeEntityType(domainName, (UserInfo) settingEntity, templateEntityTypeName);
                            } else {
                                itemList = itemFacade
                                        .getItemListOwnedByUser(domainName, (UserInfo) settingEntity);
                            }
                        } else if (settingEntity instanceof UserGroup) {
                            if (getEntityDisplayTemplates()) {
                                itemList = itemFacade
                                        .getItemListOwnedByUserGroupExcludeEntityType(domainName, (UserGroup) settingEntity, templateEntityTypeName);
                            } else {
                                itemList = itemFacade
                                        .getItemListOwnedByUserGroup(domainName, (UserGroup) settingEntity);
                            }
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

    public ListDataModel createFavoritesListDataModel() {
        List<ItemDomainEntity> itemList = getFavoriteItems();
        return new ListDataModel(itemList);
    }

    public List<ItemDomainEntity> getFavoriteItems() {
        SettingEntity currentSettingEntity = getCurrentSettingEntity();
        return getControllerUtility().getFavoriteItems(currentSettingEntity);
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

    @Override
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

        String favoritesListName = ListName.favorite.getValue();

        if (favoriteItemElementList.contains(favoriteItemElement)) {
            favoriteItemElementList.remove(favoriteItemElement);
            LOGGER.debug(String.format("Removing %s to %s List", favoriteItemElement, favoritesListName));
        } else {
            favoriteItemElementList.add(item.getSelfElement());
            LOGGER.debug(String.format("Adding %s to %s List", favoriteItemElement, favoritesListName));
        }

        // Update
        try {
            listFacade.edit(favoriteList);
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            LOGGER.error(ex);
        }

    }

    @Override
    public String getItemFavoritesIconStyle(Item item) {
        if (item != null) {
            ListTbl favoritesList = getFavoritesList();
            if (favoritesList != null) {
                if (favoritesList.getItemElementList().contains(item.getSelfElement())) {
                    return PortalStyles.favoritesOn.getValue();
                }
            }
        }

        return PortalStyles.favoritesOff.getValue();
    }

    private SettingEntity getCurrentSettingEntity() {
        return getSettingController().getCurrentSettingEntity();
    }

    protected ListTbl getFavoritesList() {
        SettingEntity currentSettingEntity = getCurrentSettingEntity();
        return getControllerUtility().getFavoritesList(currentSettingEntity);
    }

    private ListTbl createFavoritesList() {
        ListTbl favoriteList = new ListTbl();

        String favoritesListName = ListName.favorite.getValue();

        // Link the setting entity and the new favorites list
        SettingEntity settingEntity = getSettingController().getCurrentSettingEntity();
        settingEntity.getItemElementLists().add(favoriteList);
        favoriteList.init(favoritesListName, settingEntity);

        LOGGER.debug(String.format("Creating %s List for %s", favoritesListName, settingEntity));
        try {
            listFacade.create(favoriteList);
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            LOGGER.error(ex);
        }

        return favoriteList;
    }

    public String getItemListPageTitle() {
        return getDisplayEntityTypeName() + " List";
    }

    public String getItemTemplateListPageTitle() {
        return getDisplayEntityTypeName() + " Template List";
    }

    @Override
    public String getListStyleName() {
        return getStyleName();

    }

    @Override
    protected boolean isPreProcessListDataModelIterateNeeded() {
        boolean result = super.isPreProcessListDataModelIterateNeeded();

        if (!result) {
            return settingObject.getDisplayLocation();
        }

        return result;
    }

    @Override
    protected void processPreProcessIteratedDomainEntity(CdbDomainEntity entity) {
        super.processPreProcessIteratedDomainEntity(entity);

        if (entity instanceof LocatableItem) {
            if (settingObject.getDisplayLocation()) {
                LocatableItem item = (LocatableItem) entity;
                getLocatableItemController().loadCachedLocationStringForItem(getLocationRelationshipCache(), item);
            }
        }
    }

    private List<ItemElementRelationship> getLocationRelationshipCache() {
        if (locationRelationshipCache == null) {
            String locationRelationshipName = ItemElementRelationshipTypeNames.itemLocation.getValue();
            locationRelationshipCache = itemElementRelationshipFacade.findItemElementRelationshipsByTypeAndItemDomain(getDefaultDomainName(), locationRelationshipName);
        }
        return locationRelationshipCache;
    }

    @Override
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

    public ItemSource getCurrentEditItemSource() {
        ItemDomainEntity current = getCurrent();
        return current.getCurrentEditItemSource();
    }

    public void setCurrentEditItemSource(ItemSource currentEditItemSource) {
        ItemDomainEntity current = getCurrent();
        current.setCurrentEditItemSource(currentEditItemSource);
    }

    public void itemSourceObjectEditRowEvent(RowEditEvent event) {
        this.saveSourceList();
    }

    public void verifySaveCurrentEditItemSource(String onSuccessCommand) {
        ItemSource currentEditItemSource = getCurrentEditItemSource();
        if (currentEditItemSource != null) {
            if (currentEditItemSource.getSource() == null) {
                SessionUtility.addErrorMessage("Error", "Source must be specified before proceeding.");
                return;
            }
            // Verify that source hasn't been listed yet. 
            int currentItemId = getCurrent().getId();
            ItemDomainEntity dbItem = findById(currentItemId);
            Source source = currentEditItemSource.getSource();
            for (ItemSource is : dbItem.getItemSourceList()) {
                if (is.getSource().equals(source)) {
                    SessionUtility.addErrorMessage("Error", "The source for " + source.getName() + " is already defined.");
                    return;
                }
            }

            SessionUtility.executeRemoteCommand(onSuccessCommand);
        } else {
            SessionUtility.addErrorMessage("Error", "No item source to edit exists.");
        }
    }

    public void prepareAddSource(Item item) {
        List<ItemSource> sourceList = item.getItemSourceList();
        ItemSource source = new ItemSource();
        source.setItem(item);
        sourceList.add(0, source);

        setCurrentEditItemSource(source);
    }

    public void removeCurrentEditItemSource() {
        ItemSource currentEditItemSource = getCurrentEditItemSource();
        if (currentEditItemSource != null) {
            ItemDomainEntity current = getCurrent();
            List<ItemSource> itemSourceList = current.getItemSourceList();

            itemSourceList.remove(currentEditItemSource);
            setCurrentEditItemSource(null);
        }
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
            UserInfo user = SessionUtility.getUser();
            item.setCurrentEditItemElement(getControllerUtility().createItemElement(getCurrent(), user));
        }
    }

    public void cancelCreateSingleItemElementSimpleDialog() {
        ItemDomainEntity current = getCurrent();
        current.setCurrentEditItemElement(null);
        current.setCurrentEditItemElementSaveButtonEnabled(false);
    }

    public void saveCreateSingleItemElementSimpleDialog() {
        ItemElement currentEditItemElement = getCurrentEditItemElement();

        UserInfo user = SessionUtility.getUser();
        try {
            getControllerUtility().saveNewItemElement(currentEditItemElement, user);
            resetListDataModel();
            resetSelectDataModel();
        } catch (CdbException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }

        reloadCurrent();

        setCurrentEditItemElement(null);
    }

    public void changeItemCreateSingleItemElementSimpleDialog() {
        ItemDomainEntity current = getCurrent();
        ItemElement currentEditItemElement = current.getCurrentEditItemElement();
        currentEditItemElement.setContainedItem(null);
        current.setCurrentEditItemElementSaveButtonEnabled(false);
    }

    public void validateCreateSingleItemElementSimpleDialog(String onSuccessCommand, String errorSummary) {
        ItemDomainEntity item = getCurrent();
        ItemElement currentEditItemElement = item.getCurrentEditItemElement();
        try {
            beforeValidateItemElement();
            prepareAddItemElement(item, currentEditItemElement);
            getControllerUtility().checkItemElementsForItem(item);

            item.setCurrentEditItemElementSaveButtonEnabled(true);
            SessionUtility.executeRemoteCommand(onSuccessCommand);
        } catch (CdbException ex) {
            failedValidateItemElement();
            SessionUtility.addErrorMessage(errorSummary, ex.getErrorMessage());
        } catch (CloneNotSupportedException ex) {
            failedValidateItemElement();
            SessionUtility.addErrorMessage(errorSummary, ex.getMessage());
        } finally {
            item.getFullItemElementList().remove(currentEditItemElement);
            item.resetItemElementDisplayList();
        }
    }

    public void beforeValidateItemElement() throws CloneNotSupportedException, CdbException {

    }

    public void failedValidateItemElement() {

    }

    public void deleteItemConnector(ItemConnector itemConnector) {
        Item item = getCurrent();

        ConnectorControllerUtility connectorControllerUtility = new ConnectorControllerUtility();
        itemConnector = itemConnectorFacade.find(itemConnector.getId());
        Connector connector = itemConnector.getConnector();
        if (connectorControllerUtility.verifySafeRemovalOfConnector(connector)) {
            completeDeleteItemConnector(itemConnector);
        } else {
            // Generate a userfull message
            String message = "";
            List<ItemConnector> itemConnectorList = connector.getItemConnectorList();
            List<ItemConnector> connectorDeleteList = new ArrayList<>();
            for (ItemConnector ittrConnector : itemConnectorList) {
                Item ittrItem = ittrConnector.getItem();
                if (ittrItem.equals(item) == false) {
                    if (ittrItem.getDomain().getName().equals(ItemDomainName.machineDesign.getValue())) {
                        if (ittrConnector.getItemElementRelationshipList().size() == 0) {
                            connectorDeleteList.add(ittrConnector);
                        } else {
                            message = "Please check connections on machine design item: " + ittrItem.toString();
                            SessionUtility.addErrorMessage("Error", "Cannot remove connector, check if it is used for connections in machine design. " + message);
                        }
                    }
                } else {
                    connectorDeleteList.add(ittrConnector);
                }
            }

            if (itemConnectorList.size() == connectorDeleteList.size()) {
                // All save. 
                for (ItemConnector relatedConnector : connectorDeleteList) {
                    completeDeleteItemConnector(relatedConnector);
                }
            }
        }
        reloadCurrent();
    }

    private void completeDeleteItemConnector(ItemConnector itemConnector) {
        Item item = getCurrent();
        List<ItemConnector> itemConnectorList = item.getItemConnectorList();
        itemConnectorList.remove(itemConnector);
        ItemConnectorController.getInstance().destroy(itemConnector);
    }
    
    /**
     * Initializes new instance of ItemConnector. Subclasses override to customize.
     */
    protected void initializeItemConnector(ItemConnector itemConnector) {
    }

    public final void prepareAddItemConnector(Item item) {
        if (item != null) {
            UserInfo user = SessionUtility.getUser();
            ItemConnector itemConnector = getControllerUtility().prepareAddItemConnector(item, user);
            initializeItemConnector(itemConnector);

            ItemConnectorController itemConnectorController = ItemConnectorController.getInstance();
            itemConnectorController.setCurrent(itemConnector);
        }
    }

    protected final ItemConnector prepareItemConnectorForDomain(ItemConnector itemConnector) {
        return itemConnector;
    }

    public void revertItemConnectorListForCurrent() {
        Item item = getCurrent();
        if (item != null) {
            Item dbItem = findById(item.getId());
            item.setItemConnectorList(dbItem.getItemConnectorList());
        }
    }

    protected final void prepareAddItemElement(ItemDomainEntity item, ItemElement itemElement) {
        getControllerUtility().prepareAddItemElement(item, itemElement);
    }

    @Override
    public final void checkItemUniqueness(ItemDomainEntity item) throws CdbException {
        getControllerUtility().checkItemUniqueness(item);
    }

    public void prepareAddItemElement(ItemDomainEntity item) {
        UserInfo user = SessionUtility.getUser();
        ItemElement itemElement = getControllerUtility().createItemElement(item, user);
        prepareAddItemElement(item, itemElement);
    }

    public void completeSuccessfulItemElementRemoval(ItemElement itemElement) {
        ItemDomainEntity current = getCurrent();
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
        ItemDomainEntity current = getCurrent();
        if (current != null) {
            Item parentItem = itemElement.getParentItem();
            if (current.equals(parentItem)) {
                // Resaving the item could cause revivial of the element prior update due to various connections. 
                setCurrent(findById(current.getId()));
            }
        }
    }

    public ItemElementConstraintInformation loadItemElementConstraintInformation(ItemElement itemElement) {
        return new ItemElementConstraintInformation(itemElement);
    }

    public void completeSucessfulDerivedFromItemCreation() {
        ItemDomainEntity current = getCurrent();
        if (current != null) {
            // Item elements have outdated reference back to parent with new inventory item. 
            // Will allow user to see the latest constraints on item elements.          
            setCurrent(findById(current.getId()));
        }
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

    @Override
    public ItemController getDefaultDomainDerivedFromDomainController() {
        if (getEntityDisplayDerivedFromItem()) {
            return findDomainController(getDefaultDomainDerivedFromDomainName());
        }
        return null;
    }

    @Override
    public ItemController getDefaultDomainDerivedToDomainController() {
        if (getEntityDisplayItemsDerivedFromItem()) {
            return findDomainController(getDefaultDomainDerivedToDomainName());
        }
        return null;
    }

    @Override
    public final Boolean isItemExistInDb(Item item) {
        return getControllerUtility().isItemExistInDb(item);
    }

    public void setCurrentDerivedFromItem(Item derivedFromItem) {
        getCurrent().setDerivedFromItem(derivedFromItem);
    }

    @Override
    public String prepareCreate() {
        // Reset create wizard variables. 
        ItemCreateWizardController itemCreateWizardController = getItemCreateWizardController();
        if (itemCreateWizardController != null) {
            itemCreateWizardController.resetCreateItemWizardVariables();
        }
        templateToCreateNewItem = null;
        return super.prepareCreate();
    }

    @Override
    public String getItemDisplayString(Item item) {
        return item.toString();
    }

    public String getItemMembmershipPartIdentifier(Item item) {
        return getItemDisplayString(item);
    }

    @Override
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

    @Override
    public boolean isDisplayRowExpansionAssembly(Item item) {
        if (item != null) {
            if (getEntityDisplayItemElements()) {
                List<ItemElement> itemElementsList = item.getItemElementDisplayList();
                return itemElementsList != null && !itemElementsList.isEmpty();
            }
        }
        return false;
    }

    @Override
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
    public boolean isDisplayRowExpansionProperties(Item item) {
        if (getEntityDisplayItemProperties()) {
            return super.isDisplayRowExpansionProperties(item);
        }
        return false;
    }

    @Override
    public boolean isDisplayRowExpansionLogs(Item item) {
        if (getEntityDisplayItemLogs()) {
            return super.isDisplayRowExpansionLogs(item);
        }
        return false;
    }

    public List<ItemDomainEntity> getSelectItemElementItemCandidateList() {
        if (selectItemElementItemCandidateList == null) {
            LOGGER.debug("Preparing Item element candiate list for user.");
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
        selectItemElementItemCandidateList = null;
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

        ItemDomainEntity newItemDerivedFromItem = createEntityInstance();

        itemDerivedFromItemList.add(0, newItemDerivedFromItem);

        setCurrent(newItemDerivedFromItem);

        setCurrentDerivedFromItem(derivedFromItem);
    }

    public void addDynamicPropertiesToItem(Item item) {
        if (item.getId() == null) {
            Item itemDerivedFromItem = item.getDerivedFromItem();
            if (itemDerivedFromItem != null) {
                UserInfo createdByUser = (UserInfo) SessionUtility.getUser();
                Date createdOnDateTime = new Date();
                item.updateDynamicProperties(createdByUser, createdOnDateTime);
            }
        }
    }

    public void saveItemDerivedFromItemList() {
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
                PropertyTypeHandlerInterface handler;
                handler = PropertyTypeHandlerFactory.getHandler(propertyValue);
                if (handler != null) {
                    if (handler.isPropertyCloneable(cloningFrom.getDomain()) == false) {
                        continue;
                    }
                }

                PropertyValue newPropertyValue = new PropertyValue();
                newPropertyValue.setPropertyType(propertyValue.getPropertyType());
                newPropertyValue.setValue(propertyValue.getValue());
                newPropertyValue.setTag(propertyValue.getTag());
                newPropertyValue.setUnits(propertyValue.getUnits());
                newPropertyValue.setDescription(propertyValue.getDescription());
                newPropertyValue.setIsDynamic(propertyValue.getIsDynamic());
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
        return cloneCreateItemElements(clonedItem, cloningFrom, addContained, false);
    }

    protected ItemDomainEntity cloneCreateItemElements(ItemDomainEntity clonedItem, ItemDomainEntity cloningFrom, boolean addContained, boolean assignDerivedFromItemElement) {
        return cloneCreateItemElements(clonedItem, cloningFrom, true, true, false);
    }

    protected ItemDomainEntity cloneCreateItemElements(ItemDomainEntity clonedItem, ItemDomainEntity cloningFrom, boolean addContained, boolean assignDerivedFromItemElement, boolean generateUniqueElementName) {
        List<ItemElement> cloningFromItemElementList = cloningFrom.getItemElementDisplayList();

        if (cloningFromItemElementList != null) {
            for (ItemElement itemElement : cloningFromItemElementList) {
                String newName = null;
                if (generateUniqueElementName) {
                    clonedItem.resetItemElementVars();
                    newName = getControllerUtility().generateUniqueElementNameForItem(clonedItem);
                }
                ItemElement newElement = cloneCreateItemElement(itemElement, clonedItem, addContained, assignDerivedFromItemElement);

                if (newName != null) {
                    newElement.setName(newName);
                }
            }
        }

        return clonedItem;
    }

    protected ItemElement cloneCreateItemElement(ItemElement itemElement, Item clonedItem, boolean addContained, boolean assignDerivedFromItemElement) {
        ItemElement newItemElement = new ItemElement();

        UserInfo user = SessionUtility.getUser();
        if (itemElement.getDerivedFromItemElement() != null) {            
            newItemElement.init(clonedItem, itemElement.getDerivedFromItemElement(), user);
        } else {
            newItemElement.init(clonedItem, user);
        }

        if (addContained) {
            newItemElement.setContainedItem(itemElement.getContainedItem());
            newItemElement.setContainedItem2(itemElement.getContainedItem2());
        }

        if (assignDerivedFromItemElement) {
            newItemElement.setDerivedFromItemElement(itemElement);
        }

        newItemElement.setName(itemElement.getName());
        newItemElement.setIsRequired(itemElement.getIsRequired());

        clonedItem.getFullItemElementList().add(newItemElement);

        newItemElement.setSortOrder(itemElement.getSortOrder());

        return newItemElement;
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

    public ItemElement getCurrentEditItemElement() {
        ItemDomainEntity current = getCurrent();
        return current.getCurrentEditItemElement();
    }

    public void setCurrentEditItemElement(ItemElement newItemElementForCurrent) {
        ItemDomainEntity current = getCurrent();
        current.setCurrentEditItemElement(newItemElementForCurrent);
    }

    public Boolean getCurrentEditItemElementSaveButtonEnabled() {
        ItemDomainEntity current = getCurrent();
        return current.getCurrentEditItemElementSaveButtonEnabled();
    }

    public Boolean getHasElementReorderChangesForCurrent() {
        ItemDomainEntity current = getCurrent();
        Boolean hasElementReorderChangesForCurrent = current.getHasElementReorderChangesForCurrent();
        return hasElementReorderChangesForCurrent;
    }

    public void setHasElementReorderChangesForCurrent(Boolean hasElementReorderChangesForCurrent) {
        ItemDomainEntity current = getCurrent();
        current.setHasElementReorderChangesForCurrent(hasElementReorderChangesForCurrent);
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

    public boolean getRenderDerivedFromItemList() {
        if (getEntityDisplayItemsDerivedFromItem()) {
            return !isCurrentItemTemplate();
        }
        return false;
    }

    public boolean getRenderItemElementList() {
        if (getEntityDisplayItemElements()) {
            return !isCurrentItemTemplate();
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
            if (itemElementMemberList != null && !itemElementMemberList.isEmpty()) {
                return true;
            } else {
                itemElementMemberList = item.getItemElementMemberList2();
                return itemElementMemberList != null && !itemElementMemberList.isEmpty();
            }
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
            parentItemList = getParentItemList(itemEntity);
        }

        return parentItemList;
    }

    public static List<Item> getParentItemList(Item itemEntity) {

        List<Item> itemList = new ArrayList<>();

        List<ItemElement> itemElementList = new ArrayList<>();
        itemElementList.addAll(itemEntity.getItemElementMemberList());
        itemElementList.addAll(itemEntity.getItemElementMemberList2());
        // Remove currently being viewed item. 
        if (itemElementList != null) {
            for (ItemElement itemElement : itemElementList) {
                Item memberItem = itemElement.getParentItem();

                if (itemList.contains(memberItem) == false) {
                    itemList.add(memberItem);
                }
            }
        }

        return itemList;
    }

    @Override
    public final Boolean isItemProjectRequired() {
        return getControllerUtility().isItemProjectRequired();
    }

    @Override
    public final boolean getEntityDisplayItemProject() {
        return getControllerUtility().isEntityHasProject();
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
                if (imageList.size() > 0) {
                    imageList.remove(propertyValue);
                }
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

    @Override
    public boolean itemHasPrimaryImage(Item item) {
        if (item != null) {
            String value = getPrimaryImageValueForItem(item);
            return !value.isEmpty();
        }
        return false;
    }

    @Override
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
    @Override
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

    public static PropertyValue getPrimaryImagePropertyValueForItem(Item item) {
        List<PropertyValue> imagePropertyValueList = getPropertyValueListWithHandlerForImages(item);
        if (imagePropertyValueList != null && !imagePropertyValueList.isEmpty()) {
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

    private static List<PropertyValue> getPropertyValueListWithHandlerForImages(Item item) {
        String imageHandlerName = ImagePropertyTypeHandler.HANDLER_NAME;
        return getPropertyValueListWithHandler(item.getPropertyValueList(), imageHandlerName);
    }

    private static List<PropertyValue> getPropertyValueListWithHandler(List<PropertyValue> propertyValueList, String handlerName) {
        if (propertyValueList != null) {
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
        return null;
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
    public ItemDomainEntity createEntityInstance() {
        ItemDomainEntity item = super.createEntityInstance();

        if (qrIdViewParam != null) {
            item.setQrId(qrIdViewParam);
            qrIdViewParam = null;
        }

        setCurrent(item);

        return item;
    }

    @Override
    public final ItemDomainEntity createItemEntity() {
        return createEntityInstance();
    }

    @Override
    public Integer getDomainId() {
        if (domainId == null) {
            domainId = domainFacade.findByName(getDefaultDomainName()).getId();
        }

        return domainId;
    }

    @Override
    public ItemDomainEntity selectByViewRequestParams() throws CdbException {
        setBreadcrumbRequestParams();
        Integer idParam = null;
        String paramValue = SessionUtility.getRequestParameterValue("id");

        String urlParams = "";
        String mode = SessionUtility.getRequestParameterValue("mode");
        if (mode != null) {
            urlParams += "mode=" + mode + "&";
        }

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

            urlParams += "id=" + idParam;
            return performItemRedirection(item, urlParams, false);

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

                    urlParams += "qrId=" + qrParam;
                    return performItemRedirection(item, urlParams, false);
                } catch (NumberFormatException ex) {
                    throw new InvalidRequest("Invalid value supplied for QR id: " + paramValue);
                }
            }

            if (getCurrent() == null) {
                loadCurrentFromFlash();
            }

            if (getCurrent() == null) {
                throw new InvalidRequest(getDisplayEntityTypeName() + " has not been selected.");
            }

            ItemDomainEntity current = getCurrent();
            return current;
        }
    }

    @Override
    public String prepareView(Item item) {
        return "/views/item/view.xhtml?faces-redirect=true&id=" + item.getId();
    }

    public String prepareDeletedItemsList() {
        return deletedItemsList();
    }

    public String deletedItemsList() {
        return "deletedItemsList.xhtml?faces-redirect=true";
    }

    /**
     * Reset list variables and associated filter values and data model.
     *
     * @return URL to entity template list view
     */
    public String resetTemplateList() {
        super.resetList();
        return prepareTemplateList();
    }

    public String prepareTemplateList() {
        return templateList();
    }

    public String templateList() {
        return "templateList.xhtml?faces-redirect=true";
    }

    public DataModel getTemplateItemsListDataModel() {
        if (templateItemsListDataModel == null) {
            List<ItemDomainEntity> templates = getEntityDbFacade().findByDomainAndEntityType(getDefaultDomainName(), EntityTypeName.template.getValue());
            templateItemsListDataModel = new ListDataModel(templates);
        }
        return templateItemsListDataModel;
    }

    public String prepareCreateTemplate() {
        String prepareCreate = prepareCreate();

        EntityType templateType = entityTypeFacade.findByName(EntityTypeName.template.getValue());
        if (getCurrent().getEntityTypeList() == null) {
            try {
                getCurrent().setEntityTypeList(new ArrayList<>());
            } catch (CdbException ex) {
                LOGGER.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                return null;
            }
        }

        getCurrent().getEntityTypeList().add(templateType);

        return prepareCreate;
    }

    public boolean isCurrentItemTemplate() {
        ItemDomainEntity current = getCurrent();
        if (current != null) {
            return current.getIsItemTemplate();
        }
        return false;
    }

    public ItemDomainEntity getTemplateToCreateNewItem() {
        return templateToCreateNewItem;
    }

    public void setTemplateToCreateNewItem(ItemDomainEntity templateToCreateNewItem) {
        this.templateToCreateNewItem = templateToCreateNewItem;
    }

    public void completeSelectionOfTemplate() {
        ItemDomainEntity current = getCurrent();
        if (this.templateToCreateNewItem != null) {
            current.setItemCategoryList(templateToCreateNewItem.getItemCategoryList());
            current.setItemTypeList(templateToCreateNewItem.getItemTypeList());
            current = cloneProperties(current, templateToCreateNewItem);
            current = cloneSources(current, templateToCreateNewItem);
            addCreatedFromTemplateRelationshipToItem(current);
        }
    }

    protected String getItemCreatedFromTemplateRelationshipName() {
        return ItemElementRelationshipTypeNames.template.getValue();
    }

    public void addCreatedFromTemplateRelationshipToItem(ItemDomainEntity item) {
        addCreatedFromTemplateRelationshipToItem(item, templateToCreateNewItem);
    }

    public void addCreatedFromTemplateRelationshipToItem(ItemDomainEntity item, ItemDomainEntity templateToCreateNewItem) {
        RelationshipType templateRelationship
                = relationshipTypeFacade.findByName(getItemCreatedFromTemplateRelationshipName());

        // Create item element relationship between the template and the clone 
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setRelationshipType(templateRelationship);
        itemElementRelationship.setFirstItemElement(item.getSelfElement());
        itemElementRelationship.setSecondItemElement(templateToCreateNewItem.getSelfElement());

        item.setItemElementRelationshipList(new ArrayList<>());
        item.getItemElementRelationshipList().add(itemElementRelationship);
    }

    public Item getCreatedFromTemplateForCurrentItem() {
        ItemDomainEntity current = getCurrent();
        return current.getCreatedFromTemplate();
    }

    public List<Item> getItemsCreatedFromCurrentTemplateItem() {
        ItemDomainEntity current = getCurrent();
        return getItemsCreatedFromTemplateItem(current);
    }

    public List<Item> getItemsCreatedFromTemplateItem(Item templateItem) {
        return templateItem.getItemsCreatedFromThisTemplateItem();
    }

    public boolean getDisplayContentsOfCreatedFromTemplateItem() {
        return !(getItemsCreatedFromCurrentTemplateItem().size() > 0);
    }

    public boolean getDisplayCreatedFromTemplateForCurrent() {
        ItemDomainEntity current = getCurrent();
        if (getEntityDisplayTemplates() && current != null) {
            return !current.getIsItemTemplate();
        }
        return false;
    }

    public boolean getDisplayCreatedFromCurrentItemList() {
        if (getEntityDisplayTemplates()) {
            return getCurrent().getIsItemTemplate();
        }
        return false;
    }

    protected String getViewPath() {
        Domain defaultDomain = getDefaultDomain();
        return getDomainPath(defaultDomain) + "/view.xhtml";
    }

    protected ItemDomainEntity performItemRedirection(ItemDomainEntity item, String paramString, boolean forceRedirection) {
        String currentViewId = SessionUtility.getCurrentViewId();

        Domain itemDomain = item.getDomain();
        String desiredViewId;
        if (itemDomain != null) {
            ItemController itemDomainController = item.getItemDomainController();
            desiredViewId = itemDomainController.getViewPath();
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

        String newUrl = desiredViewId + "?" + paramString + "faces-redirect=true";        

        SessionUtility.navigateTo(newUrl);
        return null;
    }

    public String getCurrentItemDisplayTitle() {
        ItemDomainEntity current = getCurrent();
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
    public void processEditRequestParams() {
        super.processEditRequestParams();

        if (getCurrent() instanceof LocatableItem) {
            // Reset location vars to generate proper model when requested
            LocatableItem locatableItem = (LocatableItem) getCurrent();
            locatableItem.resetLocationVariables();
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
            getControllerUtility().checkItem(getCurrent(), skipProjects);
        } else {
            throw new CdbException("Current item does not exist.");
        }
    }

    public boolean isAllowedSetDerivedFromItemForCurrentItem() {
        return getEntityDisplayDerivedFromItem();
    }

    protected SettingController getSettingController() {
        if (settingController == null) {
            settingController = SettingController.getInstance();
        }
        return settingController;
    }

    public LocatableItemController getLocatableItemController() {
        if (locatableItemController == null) {
            locatableItemController = LocatableItemController.getInstance();
        }
        return locatableItemController;
    }

    public ItemElementController getItemElementController() {
        if (itemElementController == null) {
            itemElementController = ItemElementController.getInstance();
        }
        return itemElementController;
    }

    public void dataTableRowToggleListener(ToggleEvent event) {
        Item data = (Item) event.getData();

        if (expandedRowUUIDs == null) {
            expandedRowUUIDs = new ArrayList<>();
        }

        if (event.getVisibility() == Visibility.VISIBLE) {
            expandedRowUUIDs.add(data.getViewUUID());
        } else {
            expandedRowUUIDs.remove(data.getViewUUID());
        }
    }

    public boolean renderRowExpansionContents(Item item) {
        if (expandedRowUUIDs != null) {
            String viewUUID = item.getViewUUID();
            return expandedRowUUIDs.contains(viewUUID);
        }
        return false;
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

    public ItemMetadataPropertyInfo getCoreMetadataPropertyInfo() {
        if (coreMetadataPropertyInfo == null) {
            coreMetadataPropertyInfo = getControllerUtility().createCoreMetadataPropertyInfo();
        }
        return coreMetadataPropertyInfo;
    }

    public PropertyType getCoreMetadataPropertyType() {
        if (coreMetadataPropertyType == null) {
            initializeCoreMetadataPropertyType();
        }
        return coreMetadataPropertyType;
    }

    protected void initializeCoreMetadataPropertyType() {
        ItemMetadataPropertyInfo info = getCoreMetadataPropertyInfo();
        if (info != null) {
            coreMetadataPropertyType = PropertyTypeFacade.getInstance().findByName(info.getPropertyName());
        }
    }

    public final AllowedPropertyMetadataValue newAllowedPropertyMetadataValue(
            String value,
            PropertyTypeMetadata ptm) {
        return getControllerUtility().newAllowedPropertyMetadataValue(value, ptm);
    }

    public final PropertyTypeMetadata newPropertyTypeMetadataForField(
            ItemMetadataFieldInfo field,
            PropertyType propertyType) {
        return getControllerUtility().newPropertyTypeMetadataForField(field, propertyType);
    }

    public void createOrMigrateCoreMetadataPropertyType() {
        getControllerUtility().createOrMigrateCoreMetadataPropertyType(getCoreMetadataPropertyType());
    }

    public String getCoreMetadataPropertyTitle() {
        ItemMetadataPropertyInfo info = getCoreMetadataPropertyInfo();
        if (info == null) {
            return "";
        } else {
            return info.getDisplayName();
        }
    }

    public boolean getRenderCoreMetadataProperty() {
        return (getCoreMetadataPropertyInfo() != null);
    }

    public boolean getDisplayCoreMetadataProperty() {
        return (getCoreMetadataPropertyInfo() != null);
    }

    @Override
    public final boolean getEntityDisplayItemName() {
        return getControllerUtility().isEntityHasName();
    }

    @Override
    public final boolean getEntityDisplayQrId() {
        return getControllerUtility().isEntityHasQrId();
    }

    public boolean getEntityDisplayQrId(ItemDomainEntity item) {
        return getEntityDisplayQrId();
    }

    @Override
    public final void checkItemProject(Item item) throws CdbException {
        getControllerUtility().checkItemProject(item);
    }

}
