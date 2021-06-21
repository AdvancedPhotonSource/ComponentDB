/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemElementController;
import gov.anl.aps.cdb.portal.controllers.SettingController;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class ItemElementSettings extends CdbDomainEntitySettings<ItemElementController> {

    private static final String DisplayPartNameSettingTypeKey = "ItemElement.List.Display.SimpleViewPartName";
    private static final String DisplayPartDescriptionSettingTypeKey = "ItemElement.List.Display.SimpleViewPartDescription";
    private static final String DisplayChildItemSettingTypeKey = "ItemElement.List.Display.ChildDesign";
    private static final String DisplayComponentSettingTypeKey = "ItemElement.List.Display.Component";
    private static final String DisplayComponentTypeSettingTypeKey = "ItemElement.List.Display.ComponentType";
    private static final String DisplayCreatedByUserSettingTypeKey = "ItemElement.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "ItemElement.List.Display.CreatedOnDateTime";
    private static final String DisplayDescriptionSettingTypeKey = "ItemElement.List.Display.Description";    
    private static final String DisplayIdSettingTypeKey = "ItemElement.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "ItemElement.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "ItemElement.List.Display.LastModifiedOnDateTime";
    private static final String DisplayLocationSettingTypeKey = "ItemElement.List.Display.Location";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ItemElement.List.Display.NumberOfItemsPerPage";
    private static final String DisplayOwnerUserSettingTypeKey = "ItemElement.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "ItemElement.List.Display.OwnerGroup";
    private static final String DisplaySortOrderSettingTypeKey = "ItemElement.List.Display.SortOrder";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "ItemElement.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "ItemElement.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "ItemElement.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "ItemElement.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "ItemElement.List.Display.PropertyTypeId5";
    private static final String DisplayItemElementRowColorTypeKey = "ItemElement.List.Display.RowColor";
    private static final String DisplayBillOfMaterialsActionColumnTypeKey = "ItemElement.List.Display.BillOfMaterialsActionColumn";
    private static final String DisplayRowExpansionSettingTypeKey = "ItemElement.List.Display.RowExpansion";
    private static final String LoadRowExpansionPropertyValueSettingTypeKey = "ItemElement.List.Load.RowExpansionPropertyValue";
    private static final String SortByPropertyTypeIdSettingTypeKey = "ItemElement.List.SortBy.PropertyTypeId";
    private static final String FilterByChildItemSettingTypeKey = "ItemElement.List.FilterBy.ChildDesign";
    private static final String FilterByComponentSettingTypeKey = "ItemElement.List.FilterBy.Component";
    private static final String FilterByComponentTypeSettingTypeKey = "ItemElement.List.FilterBy.ComponentType";
    private static final String FilterByCreatedByUserSettingTypeKey = "ItemElement.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "ItemElement.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "ItemElement.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "ItemElement.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "ItemElement.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByLocationSettingTypeKey = "ItemElement.List.FilterBy.Location";
    private static final String FilterByNameSettingTypeKey = "ItemElement.List.FilterBy.Name";
    private static final String FilterByOwnerUserSettingTypeKey = "ItemElement.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "ItemElement.List.FilterBy.OwnerGroup";
    private static final String FilterBySortOrderSettingTypeKey = "ItemElement.List.FilterBy.SortOrder";

    private Boolean displayPartName = null;
    private Boolean displayPartDescription = false;
    private Boolean displayChildItem = null;
    private Boolean displayComponent = null;
    private Boolean displayComponentType = null;    
    private Boolean displayLocation = null;
    private Boolean displaySortOrder = null;
    private Boolean displayItemElementRowColor = null;
    private Boolean displayBillOfMaterialsActionColumn = null;

    private Boolean displayGlobalItemElementSimpleView = true;

    private Integer sortByPropertyTypeId = null;

    private String filterByChildItem = null;
    private String filterByComponent = null;
    private String filterByComponentType = null;
    private String filterByLocation = null;
    private String filterBySortOrder = null;

    public ItemElementSettings(ItemElementController parentController) {
        super(parentController);
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());        
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

        displayPartName = Boolean.parseBoolean(settingTypeMap.get(DisplayPartNameSettingTypeKey).getDefaultValue());
        displayPartDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayPartDescriptionSettingTypeKey).getDefaultValue());
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

        parentController.resetDomainEntityPropertyTypeIdIndexMappings();
    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);

        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);        
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        displayRowExpansion = settingEntity.getSettingValueAsBoolean(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        loadRowExpansionPropertyValues = settingEntity.getSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);

        displayPropertyTypeId1 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        sortByPropertyTypeId = settingEntity.getSettingValueAsInteger(SortByPropertyTypeIdSettingTypeKey, sortByPropertyTypeId);

        displayPartName = settingEntity.getSettingValueAsBoolean(DisplayPartNameSettingTypeKey, displayPartName);
        displayPartDescription = settingEntity.getSettingValueAsBoolean(DisplayPartDescriptionSettingTypeKey, displayPartDescription);
        displayChildItem = settingEntity.getSettingValueAsBoolean(DisplayChildItemSettingTypeKey, displayChildItem);
        displayComponent = settingEntity.getSettingValueAsBoolean(DisplayComponentSettingTypeKey, displayComponent);
        displayComponentType = settingEntity.getSettingValueAsBoolean(DisplayComponentTypeSettingTypeKey, displayComponentType);
        displayLocation = settingEntity.getSettingValueAsBoolean(DisplayLocationSettingTypeKey, displayLocation);
        displaySortOrder = settingEntity.getSettingValueAsBoolean(DisplaySortOrderSettingTypeKey, displaySortOrder);
        displayItemElementRowColor = settingEntity.getSettingValueAsBoolean(DisplayItemElementRowColorTypeKey, displayItemElementRowColor);
        displayBillOfMaterialsActionColumn = settingEntity.getSettingValueAsBoolean(DisplayBillOfMaterialsActionColumnTypeKey, displayBillOfMaterialsActionColumn);

        filterByName = settingEntity.getSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByOwnerUser = settingEntity.getSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = settingEntity.getSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = settingEntity.getSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = settingEntity.getSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = settingEntity.getSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = settingEntity.getSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByChildItem = settingEntity.getSettingValueAsString(FilterByChildItemSettingTypeKey, filterByChildItem);
        filterByComponent = settingEntity.getSettingValueAsString(FilterByComponentSettingTypeKey, filterByComponent);
        filterByComponentType = settingEntity.getSettingValueAsString(FilterByComponentTypeSettingTypeKey, filterByComponentType);
        filterByLocation = settingEntity.getSettingValueAsString(FilterByLocationSettingTypeKey, filterByLocation);
        filterBySortOrder = settingEntity.getSettingValueAsString(FilterBySortOrderSettingTypeKey, filterBySortOrder);

        parentController.resetDomainEntityPropertyTypeIdIndexMappings();
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        super.saveSettingsForSessionSettingEntity(settingEntity);

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);        
        settingEntity.setSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        settingEntity.setSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        settingEntity.setSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        settingEntity.setSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        settingEntity.setSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        settingEntity.setSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        settingEntity.setSettingValue(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        settingEntity.setSettingValue(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);

        settingEntity.setSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        settingEntity.setSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        settingEntity.setSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        settingEntity.setSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        settingEntity.setSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        settingEntity.setSettingValue(SortByPropertyTypeIdSettingTypeKey, sortByPropertyTypeId);

        settingEntity.setSettingValue(DisplayPartNameSettingTypeKey, displayPartName);
        settingEntity.setSettingValue(DisplayPartDescriptionSettingTypeKey, displayPartDescription);
        settingEntity.setSettingValue(DisplayChildItemSettingTypeKey, displayChildItem);
        settingEntity.setSettingValue(DisplayComponentSettingTypeKey, displayComponent);
        settingEntity.setSettingValue(DisplayComponentTypeSettingTypeKey, displayComponentType);
        settingEntity.setSettingValue(DisplayLocationSettingTypeKey, displayLocation);
        settingEntity.setSettingValue(DisplaySortOrderSettingTypeKey, displaySortOrder);
        settingEntity.setSettingValue(DisplayItemElementRowColorTypeKey, displayItemElementRowColor);
        settingEntity.setSettingValue(DisplayBillOfMaterialsActionColumnTypeKey, displayBillOfMaterialsActionColumn);

        settingEntity.setSettingValue(FilterByNameSettingTypeKey, filterByName);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        settingEntity.setSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        settingEntity.setSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        settingEntity.setSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        settingEntity.setSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        settingEntity.setSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        settingEntity.setSettingValue(FilterByChildItemSettingTypeKey, filterByChildItem);
        settingEntity.setSettingValue(FilterByComponentSettingTypeKey, filterByComponent);
        settingEntity.setSettingValue(FilterByComponentTypeSettingTypeKey, filterByComponentType);
        settingEntity.setSettingValue(FilterByLocationSettingTypeKey, filterByLocation);
        settingEntity.setSettingValue(FilterBySortOrderSettingTypeKey, filterBySortOrder);

        // Update related external setting values. 
        ItemController itemController = parentController.getCurrentSettingsItemController();
        if (itemController != null) {
            ICdbSettings settings = itemController.getSettingObject();
            if (settings instanceof ItemSettings) {
                ItemSettings itemSettings = (ItemSettings) settings;
                itemSettings.saveItemElementListSettingsForSessionSettingEntity(settingEntity);
            }
        }
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterByChildItem = (String) filters.get("childItem").getField();
        filterByComponent = (String) filters.get("component").getField();
        filterByComponentType = (String) filters.get("componentType").getField();
        filterByLocation = (String) filters.get("location").getField();
        filterBySortOrder = (String) filters.get("sortOrder").getField();
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

    public void toggleGlobalSimpleComplexElementsList() {
        displayGlobalItemElementSimpleView = !displayGlobalItemElementSimpleView;

        SettingController settingController = SettingController.getInstance();
        settingController.saveSettingListForSettingEntity();
    }

    public Boolean getGlobalDisplayItemElementSimpleView() {
        return displayGlobalItemElementSimpleView;
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

    public Boolean getDisplayPartName() {
        return displayPartName;
    }

    public void setDisplayPartName(Boolean displayPartName) {
        this.displayPartName = displayPartName;
    }

    public Boolean getDisplayPartDescription() {
        return displayPartDescription;
    }

    public void setDisplayPartDescription(Boolean displayPartDescription) {
        this.displayPartDescription = displayPartDescription;
    }

    public int getAssemblyColumnCount(int startCount) {
        int count = startCount;
        if (getDisplayPartName()) {
            count++;
        }
        if (getDisplayPartDescription()) {
            count++;
        }
        return count;
    }
    
    public boolean getDisplayAssemblySubHeader(int startCount) {
        return getAssemblyColumnCount(startCount) > 0; 
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

    public PropertyType getCoreMetadataPropertyType() {
        return null;
    }

    public PropertyType getCoreMetadataPropertyInfo() {
        return null;
    }

}
