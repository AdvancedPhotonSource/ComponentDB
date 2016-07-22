package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.common.utilities.CollectionUtility;
import gov.anl.aps.cdb.portal.constants.ItemViews;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.DomainHandlerFacade;
import gov.anl.aps.cdb.portal.model.db.beans.EntityTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.DomainHandler;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.primefaces.event.FlowEvent;
import org.primefaces.model.TreeNode;

public abstract class ItemController extends CdbDomainEntityController<Item, ItemFacade> implements Serializable {

    @EJB
    private ItemFacade itemFacade;

    @EJB
    private ItemElementFacade itemElementFacade;

    @EJB
    private DomainFacade domainFacade;

    @EJB
    private DomainHandlerFacade domainHandlerFacade;

    @EJB
    private EntityTypeFacade entityTypeFacade;

    @EJB
    private ItemCategoryFacade itemCategoryFacade;

    protected Boolean displayItemIdentifier1 = null;
    protected Boolean displayItemIdentifier2 = null;
    protected Boolean displayItemSources = null;
    protected Boolean displayItemType = null;
    protected Boolean displayItemCategory = null;
    protected Boolean displayName = null;
    protected Boolean displayDerivedFromItem = null;
    protected Boolean displayQrId = null;

    private static final Logger logger = Logger.getLogger(Item.class.getName());

    protected String filterByItemSources = null;
    protected String filterByQrId = null;

    private List<Item> parentItemList;
    private int currentItemEntityHashCode;

    private Integer qrIdViewParam = null;

    private TreeNode itemElementListTreeTableRootNode = null;

    private List<Item> selectItemCandidateList;
    private List<Item> selectedItems;

    private List<Item> selectItemElementItemCandidateList;

    private Domain selectionDomain;

    private DomainHandler itemDomainHandler;

    private EntityType listEntityType;

    protected DataModel allowedChildItemSelectDataModel = null;

    protected List<ItemCategory> domainItemCategoryList = null;
    
    protected String listViewSelected;

    protected List<ItemCategory> filterViewItemCategorySelectionList = null;

    // Geenerated based on the currently selected item category. 
    protected List<ItemType> filterViewItemTypeList = null;

    protected ItemType filterViewSelectedItemType = null;

    protected boolean filterViewListDataModelLoaded = false;

    protected ListDataModel filterViewListDataModel = null;

    protected ItemProject filterViewSelectedItemProject = null; 

    private final int FILTER_VIEW_MIN_ROWS = 8;

    private final int FILTER_VIEW_MAX_ROWS = 15;

    private int filterViewDataTableRowCount = -1;

    protected enum ItemCreateWizardSteps {
        derivedFromItemSelection("derivedFromItemSelectionTab"),
        basicInformation("basicItemInformationTab"),
        classification("itemClassificationTab"),
        permissions("itemPermissionTab"),
        reviewSave("reviewItemTab");

        private String value;

        private ItemCreateWizardSteps(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    };

    private String currentWizardStep;

    public ItemController() {
    }

    /**
     *
     * @return
     */
    public abstract Domain getDefaultDomain();

    public abstract String getListDomainName();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemIdentifier1();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemIdentifier2();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemName();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemType();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemCategory();

    //TODO chagne to parent derived from item. 
    public abstract boolean getEntityDisplayDerivedFromItem();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayQrId();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemGallery();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemLogs();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemSources();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemProperties();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemElements();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemsDerivedFromItem();

    /**
     *
     * @return
     */
    public abstract boolean getEntityDisplayItemMemberships();

    /**
     *
     * @return
     */
    public abstract String getItemIdentifier1Title();

    public abstract String getItemIdentifier2Title();

    public abstract String getItemsDerivedFromItemTitle();

    public abstract String getDerivedFromItemTitle();

    public abstract String getStyleName();

    public abstract String getDomainHandlerName();

    public abstract String getItemDerivedFromDomainHandlerName();

    public String getNameTitle() {
        return "Name";
    }

    public String getItemItemTypeTitle() {
        return "Type";
    }

    public String getItemItemCategoryTitle() {
        return "Category";
    }

    public List<Item> getItemList() {
        return itemFacade.findByDomain(getListDomainName());
    }
    
    public boolean isItemHasSimpleListView() {
        return false;
    }

    public String getListViewSelected() {
        if (listViewSelected == null) {
            if (isItemHasSimpleListView()) {
                listViewSelected = ItemViews.simpleListView.getValue();
            } else {
                listViewSelected = ItemViews.advancedListView.getValue();
            }
        }
        return listViewSelected;
    }

    public void setListViewSelected(String listViewSelected) {
        this.listViewSelected = listViewSelected;
    }

    public boolean getRenderSimpleView() {
        return getListViewSelected().equals(ItemViews.simpleListView.getValue());
    }

    public boolean getRenderAdvanceView() {
        return getListViewSelected().equals(ItemViews.advancedListView.getValue());
    }

    public List<ItemCategory> getDomainItemCategoryList() {
        if (domainItemCategoryList == null) {
            domainItemCategoryList = itemCategoryFacade.findByDomainHandlerName(this.getDomainHandlerName());
        }
        return domainItemCategoryList;
    }

    public List<ItemCategory> getFilterViewItemCategorySelection() {
        return filterViewItemCategorySelectionList;
    }

    public void setFilterViewItemCategorySelection(List<ItemCategory> filterViewItemCategorySelectionList) {
        Boolean listIsDifferent = true; 
        if (this.filterViewItemCategorySelectionList == null ||
                filterViewItemCategorySelectionList.size() == this.filterViewItemCategorySelectionList.size()) {
            List<ItemCategory> test = new ArrayList<>(filterViewItemCategorySelectionList);
            if (this.filterViewItemCategorySelectionList != null) {
                test.removeAll(this.filterViewItemCategorySelectionList);
            }
            
            listIsDifferent = !test.isEmpty(); 
        }

        // List is diferent.
        if (listIsDifferent) {
            this.filterViewItemCategorySelectionList = filterViewItemCategorySelectionList;
            filterViewItemTypeList = null;
            filterViewListDataModelLoaded = false;
            // Verify validity of current selection. 
            setFilterViewSelectedItemType(filterViewSelectedItemType);            
        }
    }

    public ItemType getFilterViewSelectedItemType() {
        return filterViewSelectedItemType;
    }

    public void setFilterViewSelectedItemType(ItemType filterViewSelectedItemType) {
        if (getFilterViewItemTypeList().contains(filterViewSelectedItemType)) {
            filterViewListDataModelLoaded = false;
            this.filterViewSelectedItemType = filterViewSelectedItemType;
        } else if (!getFilterViewItemTypeList().contains(this.filterViewSelectedItemType)) {
            // Current item is not valid
            filterViewListDataModelLoaded = false;
            this.filterViewSelectedItemType = null;            
        } else if (filterViewSelectedItemType == null) {
            filterViewListDataModelLoaded = false;
            this.filterViewSelectedItemType = null;            
        }
    }
    
    public boolean isFilterViewItemTypeSelectOneDisabled() {
        return getFilterViewItemTypeList().isEmpty(); 
    }

    public ItemProject getFilterViewSelectedItemProject() {
        return filterViewSelectedItemProject;
    }

    public void setFilterViewSelectedItemProject(ItemProject filterViewSelectedItemProject) {
        if (this.filterViewSelectedItemProject != filterViewSelectedItemProject) {
            filterViewListDataModelLoaded = false;
        }
        this.filterViewSelectedItemProject = filterViewSelectedItemProject;
    }

    public List<ItemType> getFilterViewItemTypeList() {
        if (filterViewItemTypeList == null) {
            filterViewItemTypeList = new ArrayList<>();
            if (filterViewItemCategorySelectionList != null) {
                for (ItemCategory itemCategory : filterViewItemCategorySelectionList) {
                    for (ItemType itemType : itemCategory.getItemTypeList()) {
                        if (!filterViewItemTypeList.contains(itemType)) {
                            filterViewItemTypeList.add(itemType);
                        }
                    }
                }
            }
        }
        return filterViewItemTypeList;
    }

    public ListDataModel getFilterViewListDataModel() {
        if (filterViewListDataModelLoaded == false) {
            List<Item> filterViewItemList = itemFacade.findByFilterViewAttributes(filterViewSelectedItemProject, 
                    filterViewItemCategorySelectionList, filterViewSelectedItemType);
            
            filterViewListDataModel = new ListDataModel(filterViewItemList);
            filterViewListDataModelLoaded = true;
            filterViewDataTableRowCount = -1;
        }

        return filterViewListDataModel;
    }

    /**
     * Function generates number of optimal rows for a data table.
     * A known issue is that data table will stop attempting to fetch number of rows after interaction with paginator. 
     * @return number of rows to display in data table.
     */
    public int getFilterViewDataTableRowCount() {
        if (filterViewDataTableRowCount == -1) {
            ListDataModel filterDataModel = getFilterViewListDataModel();

            int filterDataModelRowCount = filterDataModel.getRowCount();
            
            int lastBestNumRows = 0;
            if (filterDataModelRowCount != -1) {
                
                Double lastBestResult = null;
                for (int i = FILTER_VIEW_MAX_ROWS; i > FILTER_VIEW_MIN_ROWS; i--) {
                    if (lastBestResult == null) {
                        lastBestResult = (i * 1.0) / (filterDataModelRowCount * 1.0);
                        lastBestNumRows = i;
                    } else {
                        double currentResult = (i * 1.0) / (filterDataModelRowCount * 1.0);
                        double resultDecimal = currentResult - Math.floor(currentResult);
                        if (resultDecimal != 0) {
                            double lastDecimal = lastBestResult - Math.floor(lastBestResult); 
                            if (currentResult > lastDecimal) {
                                lastBestResult = currentResult; 
                                lastBestNumRows = i; 
                            }
                        } else {
                            // Whole number reached. 
                            lastBestResult = currentResult;                         
                        }

                    }

                    // Optimal result reached... whole number returned. 
                    if (lastBestResult % 1 == 0) {
                        lastBestNumRows = i;
                        break;
                    }
                }
            } 

            filterViewDataTableRowCount = lastBestNumRows;
        }

        return filterViewDataTableRowCount;

    }

    public ListDataModel getDomainListDataModel() {
        return new ListDataModel(getItemList());
    }

    public ListDataModel getDomainListDataModel(EntityType entityType) {
        List<Item> itemList = itemFacade.findByDomainAndEntityType(getListDomainName(), entityType.getName());
        return new ListDataModel(itemList);
    }

    public String prepareListForEntityType(String entityTypeName) {
        EntityType entityType = entityTypeFacade.findByName(entityTypeName);
        if (Objects.equals(entityType, getListEntityType()) == false) {

            setListEntityType(entityType);

            // This will now use the new list entity type; 
            resetList();
        }

        return prepareListFromViewPath(getDomainHandlerPath(getItemDomainHandler()));
    }

    public String getDomainHandlerPath(DomainHandler domainHandler) {
        return "/views/itemDomain" + domainHandler.getName();
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

    public Domain getDerivedDomain() {
        return getDefaultDomain();
    }

    public List<Domain> getItemElementItemSelectionDomainList() {
        return domainFacade.findAll();
    }

    public final ItemController getSelectionController() {
        if (selectionDomain == null) {
            List<Domain> domainList = getItemElementItemSelectionDomainList();
            if (domainList != null && domainList.size() > 0) {
                selectionDomain = domainList.get(0);
            }
        }
        if (selectionDomain != null) {
            DomainHandler domainHandler = selectionDomain.getDomainHandler();
            if (domainHandler != null) {
                return findDomainController(selectionDomain.getDomainHandler().getName());
            }
        }
        return (ItemController) SessionUtility.findBean("itemGenericViewController");
    }

    public ItemController findDomainController(String domainHandlerName) {
        return (ItemController) SessionUtility.findBean("itemDomain" + domainHandlerName + "Controller");
    }

    public List<Domain> getAvailableDomains() {
        String domainHandlerName = getDomainHandlerName();
        if (domainHandlerName != null) {
            return domainFacade.findByDomainHandlerName(domainHandlerName);
        } else {
            return domainFacade.findAll();
        }
    }

    public DomainHandler getItemDomainHandler() {
        if (itemDomainHandler == null) {
            String domainHandlerName = getDomainHandlerName();
            if (domainHandlerName != null) {
                itemDomainHandler = domainHandlerFacade.findByName(domainHandlerName);
            }
        }
        return itemDomainHandler;
    }

    public List<EntityType> getDomainHandlerAllowedEnityTypes() {
        DomainHandler domainHandler = getItemDomainHandler();
        if (domainHandler != null) {
            return domainHandler.getAllowedEntityTypeList();
        }
        return null;
    }

    public List<EntityType> getFilterableEntityTypes() {
        return getDomainHandlerAllowedEnityTypes();
    }

    public SelectItem[] getEntityTypesForDataTableFilterSelectOne() {
        return CollectionUtility.getSelectItems(getFilterableEntityTypes(), true);
    }

    public boolean isEntityTypeEditable() {
        List<EntityType> allowedEntityType = getDomainHandlerAllowedEnityTypes();
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
        selectDataModel = getDomainListDataModel();
        return selectDataModel;
    }

    @Override
    public void createListDataModel() {
        if (listEntityType != null) {
            setListDataModel(getDomainListDataModel(listEntityType));
        } else {
            setListDataModel(getDomainListDataModel());
        }
    }

    public String getEntityListPageTitle() {
        if (getListEntityType() != null) {
            return getListEntityType().getName() + " " + getDomainHandlerName();
        }
        return getDisplayEntityTypeName() + " List";
    }

    private String getEntityTypeStyleName(String genEntityTypeName) {
        return genEntityTypeName.toLowerCase() + getDomainHandlerName();
    }

    public String getListStyleName() {
        /* Style is changed based on the list shown 
        if (getListEntityType() != null) {
            return getEntityTypeStyleName(getListEntityType().getName());
        }
         */

        return getStyleName();

    }

    public String getCurrentItemStyleName() {
        /* Style is change based on entity type of item shown. 
        Item item = getCurrent();
        if (item != null) {
            List<EntityType> itemEntityTypeList = item.getEntityTypeDisplayList();
            if (itemEntityTypeList != null && itemEntityTypeList.size() == 1) {
                return getEntityTypeStyleName(itemEntityTypeList.get(0).getName());
            }
        }
         */

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
        return itemFacade.find(id);
    }

    @Override
    protected ItemFacade getEntityDbFacade() {
        return itemFacade;
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

    public void prepareAddItemElement(Item item) {
        List<ItemElement> itemElementList = item.getFullItemElementList();
        List<ItemElement> itemElementsDisplayList = item.getItemElementDisplayList();
        ItemElement itemElement = new ItemElement();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        itemElement.setEntityInfo(entityInfo);
        itemElement.setParentItem(item);
        itemElementList.add(itemElement);
        itemElementsDisplayList.add(0, itemElement);
    }

    public void deleteItemElement(ItemElement itemElement) {
        Item item = getCurrent();
        List<ItemElement> itemElementList = item.getFullItemElementList();
        List<ItemElement> itemElementsDisplayList = item.getItemElementDisplayList();
        itemElementList.remove(itemElement);
        itemElementsDisplayList.remove(itemElement);
        updateOnRemoval();
    }

    public void saveItemElementList() {
        update();
    }

    public List<Item> getSelectItemCandidateList() {
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

    public List<Item> completeItem(String query) {
        return ItemUtility.filterItem(query, getSelectItemCandidateList());
    }

    public Boolean isRenderClassificationCreateWizardTab() {
        return this.isEntityTypeEditable()
                && this.getEntityDisplayItemType()
                && this.getEntityDisplayItemCategory();
    }

    public ItemController getItemDerivedFromDomainController() {
        return findDomainController(getItemDerivedFromDomainHandlerName());
    }

    public Boolean isItemExistInDb(Item item) {
        Item dbItem = null;
        if (item.getId() != null) {
            dbItem = itemFacade.find(item.getId());
        }

        return dbItem != null;
    }

    public String createItemWizardFlowListener(FlowEvent event) {
        String prevStep = event.getOldStep();
        String nextStep = getNextStepForCreateItemWizard(event);

        currentWizardStep = nextStep;
        if (prevStep.equals(currentWizardStep) == false) {
            RequestContext.getCurrentInstance().execute("updateWizardButtonsOutputPanel()");
        }
        return nextStep;
    }

    public void resetCurrentWizardStep() {
        currentWizardStep = null;
    }

    public boolean isCreateWizardOnLastStep() {
        if (currentWizardStep != null) {
            return getLastCreateWizardStep().equals(currentWizardStep);
        }
        return false;
    }

    public boolean isCreateWizardOnFirstStep() {
        if (currentWizardStep != null) {
            return getFirstCreateWizardStep().equals(currentWizardStep);
        }
        // On first step; flow listener never got a chance to set current step.
        return true;
    }

    public String getLastCreateWizardStep() {
        return ItemCreateWizardSteps.reviewSave.getValue();

    }

    public String getFirstCreateWizardStep() {
        return ItemCreateWizardSteps.derivedFromItemSelection.getValue();
    }

    public void resetCreateItemWizardVariables() {
        // Reset any variables for the wizard.
        currentWizardStep = null;
    }

    public void setCurrentDerivedFromItem(Item derivedFromItem) {
        getCurrent().setDerivedFromItem(derivedFromItem);
    }

    public String getNextStepForCreateItemWizard(FlowEvent event) {
        logger.debug("User entering step " + event.getNewStep() + " in " + getDisplayEntityTypeName() + "Create Wizard.");
        String finishedStep = event.getOldStep();

        // Verify that the new item is unique. Prompt user to update information if this is not the case. 
        if ((finishedStep.equals(ItemCreateWizardSteps.basicInformation.getValue()))
                && (!event.getNewStep().equals(ItemCreateWizardSteps.derivedFromItemSelection.getValue()))) {
            try {
                checkItemUniqueness(getCurrent());
            } catch (CdbException ex) {
                SessionUtility.addWarningMessage("Requirement Not Met", ex.getErrorMessage());
                return ItemCreateWizardSteps.basicInformation.getValue();
            }
        }

        return event.getNewStep();
    }

    @Override
    public String prepareCreate() {
        resetCreateItemWizardVariables();
        return super.prepareCreate();
    }

    public String getItemDisplayString(Item item) {
        return item.toString();
    }

    public String getItemMembmershipPartIdentifier(Item item) {
        return getItemDisplayString(item);
    }

    public List<Item> getSelectItemElementItemCandidateList() {
        if (selectItemElementItemCandidateList == null) {
            logger.debug("Preparing Item element candiate list for user.");
            selectItemElementItemCandidateList = new ArrayList<>();

            Item item = getCurrent();
            List<EntityType> entityTypeList = item.getEntityTypeList();

            List<EntityType> allowedChildEntityTypeList = new ArrayList<>();

            for (EntityType entityType : entityTypeList) {
                List<EntityType> currentEntityTypeAllowedList = entityType.getAllowedEntityTypeList();

                for (EntityType allowedEntityType : currentEntityTypeAllowedList) {
                    if (allowedChildEntityTypeList.contains(allowedEntityType) == false) {
                        allowedChildEntityTypeList.add(allowedEntityType);
                    }
                }
            }

            Domain itemDomain = item.getDomain();
            for (EntityType allowedEntityType : allowedChildEntityTypeList) {
                List<Item> itemList = itemFacade.findByDomainAndEntityType(itemDomain.getName(), allowedEntityType.getName());
                if (itemList != null) {
                    for (Item newItem : itemList) {
                        if (selectItemElementItemCandidateList.contains(newItem) == false) {
                            selectItemElementItemCandidateList.add(newItem);
                        }
                    }
                }
            }
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
        return ItemUtility.filterItem(queryString, getSelectItemElementItemCandidateList());
    }

    public void prepareAddItemDerivedFromItem(Item derivedFromItem) {
        List<Item> itemDerivedFromItemList = derivedFromItem.getDerivedFromItemList();

        Item newItemDerivedFromItem = new Item();

        newItemDerivedFromItem.init(getDerivedDomain());

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

    public void deleteItemDerivedFromItem(Item itemDerivedFromItem) {
        Item item = itemDerivedFromItem.getDerivedFromItem();
        List<Item> itemDerivedFromItemList = item.getDerivedFromItemList();
        itemDerivedFromItemList.remove(itemDerivedFromItem);
        setCurrent(item);
        updateOnRemoval();
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
            for (ItemElement itemElement : itemElementList) {
                if (parentItemList.contains(itemElement.getParentItem()) == false) {
                    parentItemList.add(itemElement.getParentItem());
                }
            }
        }

        return parentItemList;
    }

    public String getFilterByQrId() {
        return filterByQrId;
    }

    public void setFilterByQrId(String filterByQrId) {
        this.filterByQrId = filterByQrId;
    }

    public EntityType getListEntityType() {
        return listEntityType;
    }

    public void setListEntityType(EntityType listEntityType) {
        this.listEntityType = listEntityType;
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByItemIdentifier1 = null;
        filterByItemIdentifier2 = null;

        filterByPropertyValue1 = null;
        filterByPropertyValue2 = null;
        filterByPropertyValue3 = null;
        filterByPropertyValue4 = null;
        filterByPropertyValue5 = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        filterByItemIdentifier1 = null;
        filterByItemIdentifier2 = null;
    }

    public String getFilterByItemIdentifier1() {
        return filterByItemIdentifier1;
    }

    public void setFilterByItemIdentifier1(String filterByItemIdentifier1) {
        this.filterByItemIdentifier1 = filterByItemIdentifier1;
    }

    public String getFilterByItemIdentifier2() {
        return filterByItemIdentifier2;
    }

    public void setFilterByItemIdentifier2(String filterByItemIdentifier2) {
        this.filterByItemIdentifier2 = filterByItemIdentifier2;
    }

    public Boolean getDisplayQrId() {
        if (displayQrId == null) {
            displayQrId = getEntityDisplayQrId();
        }

        return displayQrId;
    }

    public void setDisplayQrId(Boolean displayQrId) {
        this.displayQrId = displayQrId;
    }

    public Boolean getDisplayItemIdentifier1() {
        if (displayItemIdentifier1 == null) {
            displayItemIdentifier1 = getEntityDisplayItemIdentifier1();
        }
        return displayItemIdentifier1;
    }

    public void setDisplayItemIdentifier1(Boolean displayItemIdentifier1) {
        this.displayItemIdentifier1 = displayItemIdentifier1;
    }

    public Boolean getDisplayItemIdentifier2() {
        if (displayItemIdentifier2 == null) {
            displayItemIdentifier2 = getEntityDisplayItemIdentifier2();
        }
        return displayItemIdentifier2;
    }

    public void setDisplayItemIdentifier2(Boolean displayItemIdentifier2) {
        this.displayItemIdentifier2 = displayItemIdentifier2;
    }

    public Boolean getDisplayItemSources() {
        if (displayItemSources == null) {
            displayItemSources = getEntityDisplayItemSources();
        }
        return displayItemSources;
    }

    public void setDisplayItemSources(Boolean displayItemSources) {
        this.displayItemSources = displayItemSources;
    }

    public Boolean getDisplayName() {
        if (displayName == null) {
            displayName = getEntityDisplayItemName();
        }
        return displayName;
    }

    public void setDisplayName(Boolean displayItemName) {
        this.displayName = displayItemName;
    }

    public Boolean getDisplayItemType() {
        if (displayItemType == null) {
            displayItemType = getEntityDisplayItemType();
        }
        return displayItemType;
    }

    public void setDisplayItemType(Boolean displayItemType) {
        this.displayItemType = displayItemType;
    }

    public Boolean getDisplayItemCategory() {
        if (displayItemCategory == null) {
            displayItemCategory = getEntityDisplayItemCategory();
        }
        return displayItemCategory;
    }

    public void setDisplayItemCategory(Boolean displayItemCategory) {
        this.displayItemCategory = displayItemCategory;
    }

    public String getFilterByItemSources() {
        return filterByItemSources;
    }

    public void setFilterByItemSources(String filterByItemSources) {
        this.filterByItemSources = filterByItemSources;
    }

    public Boolean getDisplayDerivedFromItem() {
        if (displayDerivedFromItem == null) {
            displayDerivedFromItem = getEntityDisplayDerivedFromItem();
        }
        return displayDerivedFromItem;
    }

    public void setDisplayDerivedFromItem(Boolean displayDerivedFromItem) {
        this.displayDerivedFromItem = displayDerivedFromItem;
    }

    @Override
    protected Item createEntityInstance() {
        Item item = new Item();

        DomainHandler domainHandler = getItemDomainHandler();
        if (domainHandler != null && domainHandler.getDomainList().size() > 0) {
            item.init(domainHandler.getDomainList().get(0));
        } else {
            item.init();
        }

        return item;
    }

    @Override
    public Item findById(Integer id) {
        return itemFacade.findById(id);
    }

    @Override
    public Item selectByViewRequestParams() throws CdbException {
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
            Item item = findById(idParam);
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
                    qrIdViewParam = Integer.parseInt(paramValue);
                    Item item = findByQrId(qrIdViewParam);
                    if (item == null) {
                        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
                        if (sessionUser != null) {
                            SessionUtility.navigateTo("/views/componentInstance/create.xhtml?faces-redirect=true");
                        } else {
                            SessionUtility.pushViewOnStack("/views/componentInstance/create.xhtml");
                            SessionUtility.navigateTo("/views/login.xhtml?faces-redirect=true");
                        }
                        return null;
                    }

                    return performItemRedirection(item, "qrId=" + qrIdViewParam, false);
                } catch (NumberFormatException ex) {
                    throw new InvalidRequest("Invalid value supplied for QR id: " + paramValue);
                }
            } else if (current == null) {
                throw new InvalidRequest("Component instance has not been selected.");
            }
            return current;
        }
    }

    @Override
    public String prepareView(Item item) {
        prepareItemElementListTreeTable(item);
        return "/views/item/view.xhtml?faces-redirect=true&id=" + item.getId();
    }

    private Item performItemRedirection(Item item, String paramString, boolean forceRedirection) {
        String currentViewId = SessionUtility.getCurrentViewId();

        DomainHandler itemDomainHandler = item.getDomain().getDomainHandler();
        String desiredViewId;
        if (itemDomainHandler != null) {
            desiredViewId = getDomainHandlerPath(itemDomainHandler) + "/view.xhtml";
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

    public Item findByQrId(Integer qrId) {
        return itemFacade.findByQrId(qrId);
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
    public void prepareEntityInsert(Item item) throws CdbException {
        checkItem(item);
    }

    @Override
    public void prepareEntityUpdate(Item item) throws CdbException {
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
        List<PropertyValue> originalPropertyValueList = itemFacade.findById(item.getId()).getPropertyValueList();
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
    protected void prepareEntityDestroy(Item item) throws CdbException {
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
        if (domain.getDomainHandler() != null) {
            return domain.getDomainHandler().getName();
        }
        return domain.getName();
    }

    protected void checkItem(Item item) throws CdbException {
        Domain itemDomain = item.getDomain();

        if (itemDomain == null) {
            throw new CdbException("No domain has been specified for " + itemDomainToString(item));
        }

        checkItemUniqueness(item);

        item.resetItemElementDisplayList();
        for (ItemElement itemElement : item.getItemElementDisplayList()) {
            if (itemElement.getName() == null || itemElement.getName().isEmpty()) {
                throw new CdbException("Item element name cannot be empty.");
            }
        }
    }

    protected String itemDomainToString(Item item) {
        return item.toString();
    }

    protected void checkItemUniqueness(Item item) throws CdbException {
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
                Item existingItem = itemFacade.findByQrId(qrId);
                if (existingItem != null) {
                    if (!Objects.equals(existingItem.getId(), item.getId())) {
                        throw new ObjectAlreadyExists("Item " + existingItem.toString() + " already exists with qrId " + existingItem.getQrIdDisplay() + ".");
                    }
                }
            }
        }

        Item existingItem = itemFacade.findByUniqueAttributes(derivedFromItem, itemDomain, name, itemIdentifier1, itemIdentifier2);

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
