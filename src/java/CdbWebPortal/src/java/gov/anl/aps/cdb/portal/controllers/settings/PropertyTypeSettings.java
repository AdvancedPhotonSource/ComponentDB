/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.PropertyTypeController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class PropertyTypeSettings extends CdbEntitySettingsBase<PropertyTypeController> {
    
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyType.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyType.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyType.List.Display.Description";
    private static final String DisplayCategorySettingTypeKey = "PropertyType.List.Display.Category";
    private static final String DisplayDefaultUnitsSettingTypeKey = "PropertyType.List.Display.DefaultUnits";
    private static final String DisplayDefaultValueSettingTypeKey = "PropertyType.List.Display.DefaultValue";
    private static final String DisplayHandlerSettingTypeKey = "PropertyType.List.Display.Handler";
    private static final String FilterByNameSettingTypeKey = "PropertyType.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "PropertyType.List.FilterBy.Description";
    private static final String FilterByCategorySettingTypeKey = "PropertyType.List.FilterBy.Category";
    private static final String FilterByDefaultUnitsSettingTypeKey = "PropertyType.List.FilterBy.DefaultUnits";
    private static final String FilterByDefaultValueSettingTypeKey = "PropertyType.List.FilterBy.DefaultValue";
    private static final String FilterByHandlerSettingTypeKey = "PropertyType.List.FilterBy.Handler";
    
    private Boolean displayCategory = null;
    private Boolean displayDefaultUnits = null;
    private Boolean displayDefaultValue = null;
    private Boolean displayHandler = null;

    private String filterByCategory = null;
    private String filterByDefaultUnits = null;
    private String filterByDefaultValue = null;
    private String filterByHandler = null;
    
    private Boolean selectDisplayDefaultUnits = true;
    private Boolean selectDisplayDefaultValue = true;

    private String selectFilterByCategory = null;
    private String selectFilterByDefaultUnits = null;
    private String selectFilterByDefaultValue = null;
    private String selectFilterByHandler = null;

    public PropertyTypeSettings(PropertyTypeController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        displayCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayCategorySettingTypeKey).getDefaultValue());
        displayDefaultUnits = Boolean.parseBoolean(settingTypeMap.get(DisplayDefaultUnitsSettingTypeKey).getDefaultValue());
        displayDefaultValue = Boolean.parseBoolean(settingTypeMap.get(DisplayDefaultValueSettingTypeKey).getDefaultValue());
        displayHandler = Boolean.parseBoolean(settingTypeMap.get(DisplayHandlerSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();

        filterByCategory = settingTypeMap.get(FilterByCategorySettingTypeKey).getDefaultValue();
        filterByDefaultUnits = settingTypeMap.get(FilterByDefaultUnitsSettingTypeKey).getDefaultValue();
        filterByDefaultValue = settingTypeMap.get(FilterByDefaultValueSettingTypeKey).getDefaultValue();
        filterByHandler = settingTypeMap.get(FilterByHandlerSettingTypeKey).getDefaultValue();
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        displayCategory = settingEntity.getSettingValueAsBoolean(DisplayCategorySettingTypeKey, displayCategory);
        displayDefaultUnits = settingEntity.getSettingValueAsBoolean(DisplayDefaultUnitsSettingTypeKey, displayDefaultUnits);
        displayDefaultValue = settingEntity.getSettingValueAsBoolean(DisplayDefaultValueSettingTypeKey, displayDefaultValue);
        displayHandler = settingEntity.getSettingValueAsBoolean(DisplayHandlerSettingTypeKey, displayHandler);

        filterByName = settingEntity.getSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByCategory = settingEntity.getSettingValueAsString(FilterByCategorySettingTypeKey, filterByCategory);
        filterByDefaultUnits = settingEntity.getSettingValueAsString(FilterByDefaultUnitsSettingTypeKey, filterByDefaultUnits);
        filterByDefaultValue = settingEntity.getSettingValueAsString(FilterByDefaultValueSettingTypeKey, filterByDefaultValue);
        filterByHandler = settingEntity.getSettingValueAsString(FilterByHandlerSettingTypeKey, filterByHandler);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);

        settingEntity.setSettingValue(DisplayCategorySettingTypeKey, displayCategory);
        settingEntity.setSettingValue(DisplayDefaultUnitsSettingTypeKey, displayDefaultUnits);
        settingEntity.setSettingValue(DisplayDefaultValueSettingTypeKey, displayDefaultValue);
        settingEntity.setSettingValue(DisplayHandlerSettingTypeKey, displayHandler);

        settingEntity.setSettingValue(FilterByNameSettingTypeKey, filterByName);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);

        settingEntity.setSettingValue(FilterByCategorySettingTypeKey, filterByCategory);
        settingEntity.setSettingValue(FilterByDefaultUnitsSettingTypeKey, filterByDefaultUnits);
        settingEntity.setSettingValue(FilterByDefaultValueSettingTypeKey, filterByDefaultValue);
        settingEntity.setSettingValue(FilterByHandlerSettingTypeKey, filterByHandler);
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByCategory = null;
        filterByDefaultUnits = null;
        filterByDefaultValue = null;
        filterByHandler = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByCategory = null;
        selectFilterByDefaultUnits = null;
        selectFilterByDefaultValue = null;
        selectFilterByHandler = null;
    }

    
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterByCategory = (String) filters.get("propertyTypeCategory.name").getField();
        filterByDefaultUnits = (String) filters.get("defaultUnits").getField();
        filterByDefaultValue = (String) filters.get("defaultValue").getField();
        filterByHandler = (String) filters.get("handlerName").getField();
    }
    
    public Boolean getDisplayCategory() {
        return displayCategory;
    }

    public void setDisplayCategory(Boolean displayCategory) {
        this.displayCategory = displayCategory;
    }

    public Boolean getDisplayDefaultUnits() {
        return displayDefaultUnits;
    }

    public void setDisplayDefaultUnits(Boolean displayDefaultUnits) {
        this.displayDefaultUnits = displayDefaultUnits;
    }

    public Boolean getDisplayDefaultValue() {
        return displayDefaultValue;
    }

    public void setDisplayDefaultValue(Boolean displayDefaultValue) {
        this.displayDefaultValue = displayDefaultValue;
    }

    public Boolean getDisplayHandler() {
        return displayHandler;
    }

    public void setDisplayHandler(Boolean displayHandler) {
        this.displayHandler = displayHandler;
    }

    public String getFilterByCategory() {
        return filterByCategory;
    }

    public void setFilterByCategory(String filterByCategory) {
        this.filterByCategory = filterByCategory;
    }

    public String getFilterByDefaultUnits() {
        return filterByDefaultUnits;
    }

    public void setFilterByDefaultUnits(String filterByDefaultUnits) {
        this.filterByDefaultUnits = filterByDefaultUnits;
    }

    public String getFilterByDefaultValue() {
        return filterByDefaultValue;
    }

    public void setFilterByDefaultValue(String filterByDefaultValue) {
        this.filterByDefaultValue = filterByDefaultValue;
    }

    public String getFilterByHandler() {
        return filterByHandler;
    }

    public void setFilterByHandler(String filterByHandler) {
        this.filterByHandler = filterByHandler;
    }

    public Boolean getSelectDisplayDefaultUnits() {
        return selectDisplayDefaultUnits;
    }

    public void setSelectDisplayDefaultUnits(Boolean selectDisplayDefaultUnits) {
        this.selectDisplayDefaultUnits = selectDisplayDefaultUnits;
    }

    public Boolean getSelectDisplayDefaultValue() {
        return selectDisplayDefaultValue;
    }

    public void setSelectDisplayDefaultValue(Boolean selectDisplayDefaultValue) {
        this.selectDisplayDefaultValue = selectDisplayDefaultValue;
    }

    public String getSelectFilterByCategory() {
        return selectFilterByCategory;
    }

    public void setSelectFilterByCategory(String selectFilterByCategory) {
        this.selectFilterByCategory = selectFilterByCategory;
    }

    public String getSelectFilterByDefaultUnits() {
        return selectFilterByDefaultUnits;
    }

    public void setSelectFilterByDefaultUnits(String selectFilterByDefaultUnits) {
        this.selectFilterByDefaultUnits = selectFilterByDefaultUnits;
    }

    public String getSelectFilterByDefaultValue() {
        return selectFilterByDefaultValue;
    }

    public void setSelectFilterByDefaultValue(String selectFilterByDefaultValue) {
        this.selectFilterByDefaultValue = selectFilterByDefaultValue;
    }

    public String getSelectFilterByHandler() {
        return selectFilterByHandler;
    }

    public void setSelectFilterByHandler(String selectFilterByHandler) {
        this.selectFilterByHandler = selectFilterByHandler;
    }
    
}
