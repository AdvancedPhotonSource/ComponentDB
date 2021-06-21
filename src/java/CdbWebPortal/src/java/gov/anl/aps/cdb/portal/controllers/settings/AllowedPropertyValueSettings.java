/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.AllowedPropertyValueController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class AllowedPropertyValueSettings extends CdbEntitySettingsBase<AllowedPropertyValueController> {
    
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "AllowedPropertyValue.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "AllowedPropertyValue.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "AllowedPropertyValue.List.Display.Description";
    private static final String DisplaySortOrderSettingTypeKey = "AllowedPropertyValue.List.Display.SortOrder";
    private static final String DisplayUnitsSettingTypeKey = "AllowedPropertyValue.List.Display.Units";
    private static final String FilterByDescriptionSettingTypeKey = "AllowedPropertyValue.List.FilterBy.Description";
    private static final String FilterBySortOrderSettingTypeKey = "AllowedPropertyValue.List.FilterBy.SortOrder";
    private static final String FilterByUnitsSettingTypeKey = "AllowedPropertyValue.List.FilterBy.Units";
    private static final String FilterByValueSettingTypeKey = "AllowedPropertyValue.List.FilterBy.Value";
    
    private Boolean displayUnits = null;
    private Boolean displaySortOrder = null;

    private String filterBySortOrder = null;
    private String filterByUnits = null;
    private String filterByValue = null;

    public AllowedPropertyValueSettings(AllowedPropertyValueController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displaySortOrder = Boolean.parseBoolean(settingTypeMap.get(DisplaySortOrderSettingTypeKey).getDefaultValue());
        displayUnits = Boolean.parseBoolean(settingTypeMap.get(DisplayUnitsSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterBySortOrder = settingTypeMap.get(FilterBySortOrderSettingTypeKey).getDefaultValue();
        filterByUnits = settingTypeMap.get(FilterByUnitsSettingTypeKey).getDefaultValue();
        filterByValue = settingTypeMap.get(FilterByValueSettingTypeKey).getDefaultValue();
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        displaySortOrder = settingEntity.getSettingValueAsBoolean(DisplaySortOrderSettingTypeKey, displaySortOrder);
        displayUnits = settingEntity.getSettingValueAsBoolean(DisplayUnitsSettingTypeKey, displayUnits);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterBySortOrder = settingEntity.getSettingValueAsString(FilterBySortOrderSettingTypeKey, filterBySortOrder);
        filterByUnits = settingEntity.getSettingValueAsString(FilterByUnitsSettingTypeKey, filterByUnits);
        filterByValue = settingEntity.getSettingValueAsString(FilterByValueSettingTypeKey, filterByValue);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplaySortOrderSettingTypeKey, displaySortOrder);
        settingEntity.setSettingValue(DisplayUnitsSettingTypeKey, displayUnits);

        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterBySortOrderSettingTypeKey, filterBySortOrder);
        settingEntity.setSettingValue(FilterByUnitsSettingTypeKey, filterByUnits);
        settingEntity.setSettingValue(FilterByValueSettingTypeKey, filterByValue);
    }
    
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterBySortOrder = (String) filters.get("sortOrder").getField();
        filterByUnits = (String) filters.get("units").getField();
        filterByValue = (String) filters.get("value").getField();
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterBySortOrder = null;
        filterByUnits = null;
        filterByValue = null;
    }

    public Boolean getDisplayUnits() {
        return displayUnits;
    }

    public void setDisplayUnits(Boolean displayUnits) {
        this.displayUnits = displayUnits;
    }

    public Boolean getDisplaySortOrder() {
        return displaySortOrder;
    }

    public void setDisplaySortOrder(Boolean displaySortOrder) {
        this.displaySortOrder = displaySortOrder;
    }

    public String getFilterBySortOrder() {
        return filterBySortOrder;
    }

    public void setFilterBySortOrder(String filterBySortOrder) {
        this.filterBySortOrder = filterBySortOrder;
    }

    public String getFilterByUnits() {
        return filterByUnits;
    }

    public void setFilterByUnits(String filterByUnits) {
        this.filterByUnits = filterByUnits;
    }

    public String getFilterByValue() {
        return filterByValue;
    }

    public void setFilterByValue(String filterByValue) {
        this.filterByValue = filterByValue;
    }
    
}
