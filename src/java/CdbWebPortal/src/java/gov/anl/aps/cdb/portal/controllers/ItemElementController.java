package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;

import static gov.anl.aps.cdb.portal.controllers.CdbEntityController.parseSettingValueAsInteger;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DragDropEvent;
import org.primefaces.model.TreeNode;

@Named("itemElementController")
@SessionScoped
public class ItemElementController extends CdbDomainEntityController<ItemElement, ItemElementFacade>implements Serializable {

    @EJB
    private ItemElementFacade itemElementFacade; 
    
    /**
     * Controller specific settings
     */
    private static final String DisplayChildItemSettingTypeKey = "DesignElement.List.Display.ChildDesign";
    private static final String DisplayComponentSettingTypeKey = "DesignElement.List.Display.Component";
    private static final String DisplayComponentTypeSettingTypeKey = "DesignElement.List.Display.ComponentType";
    private static final String DisplayCreatedByUserSettingTypeKey = "DesignElement.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "DesignElement.List.Display.CreatedOnDateTime";
    private static final String DisplayDescriptionSettingTypeKey = "DesignElement.List.Display.Description";
    private static final String DisplayFlatTableViewSettingTypeKey = "DesignElement.List.Display.FlatTableView";
    private static final String DisplayIdSettingTypeKey = "DesignElement.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "DesignElement.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "DesignElement.List.Display.LastModifiedOnDateTime";
    private static final String DisplayLocationSettingTypeKey = "DesignElement.List.Display.Location";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "DesignElement.List.Display.NumberOfItemsPerPage";
    private static final String DisplayOwnerUserSettingTypeKey = "DesignElement.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "DesignElement.List.Display.OwnerGroup";
    private static final String DisplaySortOrderSettingTypeKey = "DesignElement.List.Display.SortOrder";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "DesignElement.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "DesignElement.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "DesignElement.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "DesignElement.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "DesignElement.List.Display.PropertyTypeId5";
    private static final String DisplayItemElementRowColorTypeKey="DesignElement.List.Display.RowColor"; 
    private static final String DisplayBillOfMaterialsActionColumnTypeKey = "DesignElement.List.Display.BillOfMaterialsActionColumn";
    private static final String DisplayRowExpansionSettingTypeKey = "DesignElement.List.Display.RowExpansion";
    private static final String LoadRowExpansionPropertyValueSettingTypeKey = "DesignElement.List.Load.RowExpansionPropertyValue";
    private static final String SortByPropertyTypeIdSettingTypeKey = "DesignElement.List.SortBy.PropertyTypeId"; 
    private static final String FilterByChildItemSettingTypeKey = "DesignElement.List.FilterBy.ChildDesign";
    private static final String FilterByComponentSettingTypeKey = "DesignElement.List.FilterBy.Component";
    private static final String FilterByComponentTypeSettingTypeKey = "DesignElement.List.FilterBy.ComponentType";
    private static final String FilterByCreatedByUserSettingTypeKey = "DesignElement.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "DesignElement.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "DesignElement.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "DesignElement.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "DesignElement.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByLocationSettingTypeKey = "DesignElement.List.FilterBy.Location";
    private static final String FilterByNameSettingTypeKey = "DesignElement.List.FilterBy.Name";
    private static final String FilterByOwnerUserSettingTypeKey = "DesignElement.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "DesignElement.List.FilterBy.OwnerGroup";
    private static final String FilterBySortOrderSettingTypeKey = "DesignElement.List.FilterBy.SortOrder";

    private static final Logger logger = Logger.getLogger(ItemElementController.class.getName());
    
    private static final String DESIGN_ELEMENT_ROW_COLOR_PROPERTY_NAME="Item Element Row Color";

    private Boolean displayChildItem = null;
    private Boolean displayComponent = null;
    private Boolean displayComponentType = null;
    private Boolean displayFlatTableView = null;
    private Boolean displayLocation = null;
    private Boolean displaySortOrder = null;
    private Boolean displayItemElementRowColor = null; 
    private Boolean displayBillOfMaterialsActionColumn = null; 
    
    private Integer sortByPropertyTypeId = null;

    private String filterByChildItem = null;
    private String filterByComponent = null;
    private String filterByComponentType = null;
    private String filterByLocation = null;
    private String filterBySortOrder = null;

    private Item selectedParentItem = null;

    private List<PropertyValue> filteredPropertyValueList = null;

    private List<ItemElement> pendingChangesItemElementList = null;
    private List<ItemElement> sortableItemElementList = null;
    
    private List<Item> selectChildItemCandidateList = null;

    public ItemElementController() {
    }

    @Override
    protected ItemElementFacade getEntityDbFacade() {
        return itemElementFacade;
    }

    private void resetSelectObjectLists() {
        selectChildItemCandidateList = null;
    }

    @Override
    protected ItemElement createEntityInstance() {
        ItemElement designElement = new ItemElement();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        designElement.setEntityInfo(entityInfo);

        // clear selection lists
        resetSelectObjectLists();
        return designElement;
    }

    @Override
    public ItemElement findById(Integer id) {
        return itemElementFacade.findById(id);
    }

    @Override
    public EntityInfo getEntityInfo(ItemElement entity) {
        if (entity != null) {
            return entity.getEntityInfo();
        }
        return null;
    }

    public TreeNode getItemElementListTreeTableRootNode(ItemElement parent) {

        if (parent.getChildItemElementListTreeTableRootNode() == null) {
            try{
                if(parent.getContainedItem() != null){
                    parent.setChildItemElementListTreeTableRootNode(ItemElementUtility.createItemElementRoot(parent.getContainedItem()));
                }
            } catch (CdbException ex) {
                logger.debug(ex);
            }
        }
        
        return parent.getChildItemElementListTreeTableRootNode(); 
    }
    
    @Override
    public String getEntityTypeName() {
        return "designElement";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Item Element";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<ItemElement> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(ItemElement designElement) throws ObjectAlreadyExists {
    }
/*
    @Override
    public void prepareEntityUpdate(ItemElement designElement) throws ObjectAlreadyExists {
        EntityInfo entityInfo = designElement.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);

        // Compare properties with what is in the db
        List<PropertyValue> originalPropertyValueList = designElementFacade.findById(designElement.getId()).getPropertyValueList();
        List<PropertyValue> newPropertyValueList = designElement.getPropertyValueList();
        logger.debug("Verifying properties for design element " + designElement);
        PropertyValueUtility.preparePropertyValueHistory(originalPropertyValueList, newPropertyValueList, entityInfo);
        prepareImageList(designElement);
        resetSelectObjectLists();
    }
*/
    @Override
    public void prepareEntityUpdateOnRemoval(ItemElement designElement) {
        EntityInfo entityInfo = designElement.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
        prepareImageList(designElement);
        resetSelectObjectLists();
    }

    @Override
    public String prepareView(ItemElement itemElement) {
        logger.debug("Preparing item element view");
        super.prepareView(itemElement);
        return "/views/itemElement/view.xhtml?faces-redirect=true";
    }

    public void prepareAddProperty() {
        ItemElement designElement = getCurrent();
        List<PropertyValue> propertyList = designElement.getPropertyValueList();
        PropertyValue property = new PropertyValue();
        propertyList.add(property);
    }

    public String destroyAndReturnItemView(ItemElement designElement) {
        Item parentItem = designElement.getParentItem();
        setCurrent(designElement);
        try {
            logger.debug("Destroying " + designElement.getName());
            getEntityDbFacade().remove(designElement);
            SessionUtility.addInfoMessage("Success", "Deleted design element id " + designElement.getId() + ".");
            return "/views/design/view.xhtml?faces-redirect=true&id=" + parentItem.getId();
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
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
        displayFlatTableView = Boolean.parseBoolean(settingTypeMap.get(DisplayFlatTableViewSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        
        displayRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayRowExpansionSettingTypeKey).getDefaultValue()); 
        loadRowExpansionPropertyValues = Boolean.parseBoolean(settingTypeMap.get(LoadRowExpansionPropertyValueSettingTypeKey).getDefaultValue());
        
        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());

        sortByPropertyTypeId = parseSettingValueAsInteger(settingTypeMap.get(SortByPropertyTypeIdSettingTypeKey).getDefaultValue()); 
        
        displayChildItem = Boolean.parseBoolean(settingTypeMap.get(DisplayChildItemSettingTypeKey).getDefaultValue());
        displayComponent = Boolean.parseBoolean(settingTypeMap.get(DisplayComponentSettingTypeKey).getDefaultValue());
        displayComponentType = Boolean.parseBoolean(settingTypeMap.get(DisplayComponentTypeSettingTypeKey).getDefaultValue());
        displayLocation = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationSettingTypeKey).getDefaultValue());
        displaySortOrder = Boolean.parseBoolean(settingTypeMap.get(DisplaySortOrderSettingTypeKey).getDefaultValue());
        displayItemElementRowColor = Boolean.parseBoolean(settingTypeMap.get(DisplayItemElementRowColorTypeKey).getDefaultValue()); 
        displayBillOfMaterialsActionColumn = Boolean.parseBoolean(settingTypeMap.get(DisplayBillOfMaterialsActionColumnTypeKey).getDefaultValue());
        
        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();

        filterByChildItem = settingTypeMap.get(FilterByChildItemSettingTypeKey).getDefaultValue();
        filterByComponent = settingTypeMap.get(FilterByComponentSettingTypeKey).getDefaultValue();
        filterByComponentType = settingTypeMap.get(FilterByComponentTypeSettingTypeKey).getDefaultValue();
        filterByLocation = settingTypeMap.get(FilterByLocationSettingTypeKey).getDefaultValue();
        filterBySortOrder = settingTypeMap.get(FilterBySortOrderSettingTypeKey).getDefaultValue();
        
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
        displayFlatTableView = sessionUser.getUserSettingValueAsBoolean(DisplayFlatTableViewSettingTypeKey, displayFlatTableView);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        
        displayRowExpansion = sessionUser.getUserSettingValueAsBoolean(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        loadRowExpansionPropertyValues = sessionUser.getUserSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);
        
        displayPropertyTypeId1 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);
        
        sortByPropertyTypeId = sessionUser.getUserSettingValueAsInteger(SortByPropertyTypeIdSettingTypeKey, sortByPropertyTypeId); 

        displayChildItem = sessionUser.getUserSettingValueAsBoolean(DisplayChildItemSettingTypeKey, displayChildItem);
        displayComponent = sessionUser.getUserSettingValueAsBoolean(DisplayComponentSettingTypeKey, displayComponent);
        displayComponentType = sessionUser.getUserSettingValueAsBoolean(DisplayComponentTypeSettingTypeKey, displayComponentType);
        displayLocation = sessionUser.getUserSettingValueAsBoolean(DisplayLocationSettingTypeKey, displayLocation);
        displaySortOrder = sessionUser.getUserSettingValueAsBoolean(DisplaySortOrderSettingTypeKey, displaySortOrder);
        displayItemElementRowColor = sessionUser.getUserSettingValueAsBoolean(DisplayItemElementRowColorTypeKey, displayItemElementRowColor);
        displayBillOfMaterialsActionColumn = sessionUser.getUserSettingValueAsBoolean(DisplayBillOfMaterialsActionColumnTypeKey, displayBillOfMaterialsActionColumn);
        
        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByChildItem = sessionUser.getUserSettingValueAsString(FilterByChildItemSettingTypeKey, filterByChildItem);
        filterByComponent = sessionUser.getUserSettingValueAsString(FilterByComponentSettingTypeKey, filterByComponent);
        filterByComponentType = sessionUser.getUserSettingValueAsString(FilterByComponentTypeSettingTypeKey, filterByComponentType);
        filterByLocation = sessionUser.getUserSettingValueAsString(FilterByLocationSettingTypeKey, filterByLocation);
        filterBySortOrder = sessionUser.getUserSettingValueAsString(FilterBySortOrderSettingTypeKey, filterBySortOrder);
        
        resetDomainEntityPropertyTypeIdIndexMappings();
    }
    
    @Override
    public void resetDomainEntityPropertyTypeIdIndexMappings() {
        super.resetDomainEntityPropertyTypeIdIndexMappings();
        ItemElement.setSortByPropertyTypeId(sortByPropertyTypeId);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterByChildItem = (String) filters.get("childItem");
        filterByComponent = (String) filters.get("component");
        filterByComponentType = (String) filters.get("componentType");
        filterByLocation = (String) filters.get("location");
        filterBySortOrder = (String) filters.get("sortOrder");
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
        sessionUser.setUserSettingValue(DisplayFlatTableViewSettingTypeKey, displayFlatTableView);
        sessionUser.setUserSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        sessionUser.setUserSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        sessionUser.setUserSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        sessionUser.setUserSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        sessionUser.setUserSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        sessionUser.setUserSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        
        sessionUser.setUserSettingValue(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        sessionUser.setUserSettingValue(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);
        
        sessionUser.setUserSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);
        
        sessionUser.setUserSettingValue(SortByPropertyTypeIdSettingTypeKey, sortByPropertyTypeId);

        sessionUser.setUserSettingValue(DisplayChildItemSettingTypeKey, displayChildItem);
        sessionUser.setUserSettingValue(DisplayComponentSettingTypeKey, displayComponent);
        sessionUser.setUserSettingValue(DisplayComponentTypeSettingTypeKey, displayComponentType);
        sessionUser.setUserSettingValue(DisplayLocationSettingTypeKey, displayLocation);
        sessionUser.setUserSettingValue(DisplaySortOrderSettingTypeKey, displaySortOrder);
        sessionUser.setUserSettingValue(DisplayItemElementRowColorTypeKey, displayItemElementRowColor);
        sessionUser.setUserSettingValue(DisplayBillOfMaterialsActionColumnTypeKey, displayBillOfMaterialsActionColumn);
        
        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        sessionUser.setUserSettingValue(FilterByChildItemSettingTypeKey, filterByChildItem);
        sessionUser.setUserSettingValue(FilterByComponentSettingTypeKey, filterByComponent);
        sessionUser.setUserSettingValue(FilterByComponentTypeSettingTypeKey, filterByComponentType);
        sessionUser.setUserSettingValue(FilterByLocationSettingTypeKey, filterByLocation);
        sessionUser.setUserSettingValue(FilterBySortOrderSettingTypeKey, filterBySortOrder);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByChildItem = null;
        filterByComponent = null;
        filterByComponentType = null;
        filterByLocation = null;
        filterBySortOrder = null;
    }
    
    public String getDisplayRowColor(ItemElement itemElement) {
        if(displayItemElementRowColor != null && displayItemElementRowColor) {
            List<PropertyValue> propertyValueList = itemElement.getPropertyValueList(); 
            if(propertyValueList != null && propertyValueList.size() > 0) {
                for (PropertyValue propertyValue : propertyValueList) {
                    if (propertyValue.getPropertyType().getName().equals(DESIGN_ELEMENT_ROW_COLOR_PROPERTY_NAME)) {
                        String value = propertyValue.getValue(); 
                        return value+"Row";
                    }
                }
            }
        }
        return null; 
    }
    
    public Boolean getDisplayItemElementRowColor() {
        return displayItemElementRowColor;
    }

    public void setDisplayItemElementRowColor(Boolean displayItemElementRowColor) {
        this.displayItemElementRowColor = displayItemElementRowColor;
    }
    
    public Integer getSortByPropertyTypeId() {
        return sortByPropertyTypeId;
    }

    public void setSortByPropertyTypeId(Integer sortByPropertyTypeId) {
        this.sortByPropertyTypeId = sortByPropertyTypeId;
    }

    public Boolean getDisplayBillOfMaterialsActionColumn() {
        return displayBillOfMaterialsActionColumn;
    }

    public void setDisplayBillOfMaterialsActionColumn(Boolean displayBillOfMaterialsActionColumn) {
        this.displayBillOfMaterialsActionColumn = displayBillOfMaterialsActionColumn;
    }

    public Boolean getDisplayChildItem() {
        return displayChildItem;
    }

    public void setDisplayChildItem(Boolean displayChildItem) {
        this.displayChildItem = displayChildItem;
    }

    public Boolean getDisplayComponent() {
        return displayComponent;
    }

    public void setDisplayComponent(Boolean displayComponent) {
        this.displayComponent = displayComponent;
    }

    public Boolean getDisplayComponentType() {
        return displayComponentType;
    }

    public void setDisplayComponentType(Boolean displayComponentType) {
        this.displayComponentType = displayComponentType;
    }

    public Boolean getDisplayFlatTableView() {
        return displayFlatTableView;
    }

    public void setDisplayFlatTableView(Boolean displayFlatTableView) {
        this.displayFlatTableView = displayFlatTableView;
    }

    public Boolean getDisplayLocation() {
        return displayLocation;
    }

    public void setDisplayLocation(Boolean displayLocation) {
        this.displayLocation = displayLocation;
    }

    public Boolean getDisplaySortOrder() {
        return displaySortOrder;
    }

    public void setDisplaySortOrder(Boolean displaySortOrder) {
        this.displaySortOrder = displaySortOrder;
    }

    public String getFilterByChildItem() {
        return filterByChildItem;
    }

    public void setFilterByChildItem(String filterByChildItem) {
        this.filterByChildItem = filterByChildItem;
    }

    public String getFilterByComponent() {
        return filterByComponent;
    }

    public void setFilterByComponent(String filterByComponent) {
        this.filterByComponent = filterByComponent;
    }

    public String getFilterByComponentType() {
        return filterByComponentType;
    }

    public void setFilterByComponentType(String filterByComponentType) {
        this.filterByComponentType = filterByComponentType;
    }

    public String getFilterByLocation() {
        return filterByLocation;
    }

    public void setFilterByLocation(String filterByLocation) {
        this.filterByLocation = filterByLocation;
    }

    public String getFilterBySortOrder() {
        return filterBySortOrder;
    }

    public void setFilterBySortOrder(String filterBySortOrder) {
        this.filterBySortOrder = filterBySortOrder;
    }

    public Item getSelectedParentItem() {
        return selectedParentItem;
    }

    /**
     * Function handles the sorting of dropped designElement based on where it was dropped. 
     * 
     * @param ddEvent 
     */
    public void onItemElementDrop(DragDropEvent ddEvent) {
        String[] draggedId = ddEvent.getDragId().split(":");
        String[] droppedId = ddEvent.getDropId().split(":");
        int dragIndex = Integer.parseInt(draggedId[draggedId.length - 2]);
        int dropIndex = Integer.parseInt(droppedId[droppedId.length - 2]);

        String dropDescription = droppedId[droppedId.length - 1];

        if (dragIndex > dropIndex) { //Dragged from bottom to top
            if (dropDescription.equals("designElementRearrangeDataGridBottomPanel")) {
                dropIndex++;
            }
        } else if (dragIndex < dropIndex) { //Dragged from top to bottom
            if (dropDescription.equals("designElementRearrangeDataGridTopPanel")) {
                dropIndex--;
            }
        }

        if (dragIndex == dropIndex) {
            return;
        }
        ItemElement designElementDroppped = (ItemElement) ddEvent.getData();

        List<ItemElement> designElementList = sortableItemElementList;

        List<ItemElement> newItemElementList = new ArrayList<>();

        float sortOrder = 1;
        for (int i = 0; i < designElementList.size(); i++) {
            if (dragIndex > dropIndex) {
                if (dragIndex == i) {
                    continue;
                }
                if (dropIndex == i) {
                    newItemElementList.add(designElementDroppped);
                    updateItemElementSortOrder(designElementDroppped, sortOrder++);
                }
                newItemElementList.add(designElementList.get(i));
                updateItemElementSortOrder(designElementList.get(i), sortOrder++);
            } else if (dragIndex < dropIndex) {
                if (dragIndex == i) {
                    continue;
                }
                newItemElementList.add(designElementList.get(i));
                updateItemElementSortOrder(designElementList.get(i), sortOrder++);
                if (dropIndex == i) {
                    newItemElementList.add(designElementDroppped);
                    updateItemElementSortOrder(designElementDroppped, sortOrder++);
                }
            }
        }

        sortableItemElementList = newItemElementList;
    }

    public List<ItemElement> getSortableItemElementList() {
        return sortableItemElementList;
    }

    /**
     * Creates a sorted list in preparation for sorting. 
     * 
     * @param parentItem 
     * @param onSuccessCommand
     */
    public void configureSortableItemElementList(Item parentItem, String onSuccessCommand) {
        if (pendingChangesItemElementList != null) {
            pendingChangesItemElementList.clear();
        }

        List<ItemElement> parentItemElementList = parentItem.getItemElementDisplayList();

        if (parentItemElementList != null && parentItemElementList.size() > 0) {
            sortableItemElementList = new ArrayList<>();

            for (ItemElement designElement : parentItemElementList) {
                if(designElement.getId() == null) {
                    SessionUtility.addWarningMessage("Unsaved Item Element", "Please save design element list and try again.");
                    return; 
                }
                sortableItemElementList.add(designElement);
            }
            // Apply a sort in case the user applied filters to the data table
            Collections.sort(sortableItemElementList, new Comparator<ItemElement>() {
                @Override
                public int compare(ItemElement e1, ItemElement e2) {
                    if (e1.getSortOrder() != null && e2.getSortOrder() != null) {
                        return e1.getSortOrder().compareTo(e2.getSortOrder());
                    }
                    return 1; 
                }
            });
            
            RequestContext.getCurrentInstance().execute(onSuccessCommand);
        } else {
            SessionUtility.addInfoMessage("Info", "No design elements to sort.");
        }

    }

    /**
     * Updates the design element with new sort order number if needed
     * adds to list that will be saved once sorting is complete. 
     * 
     * @param designElement object to have the sort order updated
     * @param sortOrder sort order to set on the designElement object
     */
    private void updateItemElementSortOrder(ItemElement designElement, float sortOrder) {
        if (designElement.getSortOrder() != null && designElement.getSortOrder() == sortOrder) {
            return;
        }
        designElement.setSortOrder(sortOrder);

        if (pendingChangesItemElementList == null) {
            pendingChangesItemElementList = new ArrayList<>();
        }
        if (pendingChangesItemElementList.contains(designElement) == false) {
            pendingChangesItemElementList.add(designElement);
        }
    }

    @Override
    public Boolean getDisplayUpdateSortOrderButton() {
        return true;
    }

    /**
     * Determines if pending changes need to be saved by checking the list.
     * 
     * @return boolean
     */
    public Boolean getItemElementPendingChanges() {
        return pendingChangesItemElementList != null && pendingChangesItemElementList.size() > 0;
    }

    /**
     * Updates each design element object that had the sort order modified. 
     */
    public void saveItemElementPendingChanges() {
        if (getItemElementPendingChanges()) {
            for (int i = 0; i < pendingChangesItemElementList.size(); i++) {
                this.setCurrent(pendingChangesItemElementList.get(i));
                this.update();
            }

            // Need the server to refresh database information for the particular entity.
            int parentItemId = pendingChangesItemElementList.get(0).getParentItem().getId();
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("?id=" + parentItemId);
            } catch (IOException ex) {
                logger.debug(ex);
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }

            // Clear the pending changes list since everythign was saved
            pendingChangesItemElementList.clear();
            // Clear the sortable list pointer since sorting is complete. 
            sortableItemElementList = null;

        }
    }

    public void prepareComponentInstancePropertyValueDisplay(ItemElement designElement) {
        List<PropertyValue> propertyValueList = designElement.getPropertyValueList();
        for (PropertyValue propertyValue : propertyValueList) {
            PropertyValueUtility.configurePropertyValueDisplay(propertyValue);
        }
    }
    
    
    

   

}
