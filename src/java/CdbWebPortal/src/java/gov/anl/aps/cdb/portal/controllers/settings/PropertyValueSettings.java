/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.PropertyValueController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class PropertyValueSettings extends CdbEntitySettingsBase<PropertyValueController> {
    
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyValue.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyValue.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyValue.List.Display.Description";
    private static final String DisplayEnteredByUserSettingTypeKey = "PropertyValue.List.Display.EnteredByUser";
    private static final String DisplayEnteredOnDateTimeSettingTypeKey = "PropertyValue.List.Display.EnteredOnDateTime";
    private static final String DisplayIsDynamicSettingTypeKey = "PropertyValue.List.Display.IsDynamic";
    private static final String DisplayIsUserWriteableSettingTypeKey = "PropertyValue.List.Display.IsUserWriteable";
    private static final String DisplayTagSettingTypeKey = "PropertyValue.List.Display.Tag";
    private static final String DisplayTypeCategorySettingTypeKey = "PropertyValue.List.Display.TypeCategory";
    private static final String DisplayUnitsSettingTypeKey = "PropertyValue.List.Display.Units";
    private static final String DisplayEffectiveFromDateTimeSettingTypeKey = "PropertyValue.List.Display.EffectiveFromDateTime"; 
    private static final String DisplayEffectiveToDateTimeSettingTypeKey = "PropertyValue.List.Display.EffectiveToDateTime"; 
    private static final String FilterByDescriptionSettingTypeKey = "PropertyValue.List.FilterBy.Description";
    private static final String FilterByEnteredByUserSettingTypeKey = "PropertyValue.List.FilterBy.EnteredByUser";
    private static final String FilterByEnteredOnDateTimeSettingTypeKey = "PropertyValue.List.FilterBy.EnteredOnDateTime";
    private static final String FilterByIsDynamicSettingTypeKey = "PropertyValue.List.FilterBy.IsDynamic";
    private static final String FilterByIsUserWriteableSettingTypeKey = "PropertyValue.List.FilterBy.IsUserWriteable";
    private static final String FilterByTagSettingTypeKey = "PropertyValue.List.FilterBy.Tag";
    private static final String FilterByTypeSettingTypeKey = "PropertyValue.List.FilterBy.Type";
    private static final String FilterByTypeCategorySettingTypeKey = "PropertyValue.List.FilterBy.TypeCategory";
    private static final String FilterByValueSettingTypeKey = "PropertyValue.List.FilterBy.Value";
    private static final String FilterByUnitsSettingTypeKey = "PropertyValue.List.FilterBy.Units";

    private Boolean displayEnteredByUser = null;
    private Boolean displayEnteredOnDateTime = null;
    private Boolean displayIsDynamic = null;
    private Boolean displayIsUserWriteable = null;
    private Boolean displayTag = null;
    private Boolean displayTypeCategory = null;
    private Boolean displayUnits = null;
    private Boolean displayEffectiveFromDateTime = null;
    private Boolean displayEffectiveToDateTime = null;

    private String filterByEnteredByUser = null;
    private String filterByEnteredOnDateTime = null;
    private String filterByIsDynamic = null;
    private String filterByIsUserWriteable = null;
    private String filterByTag = null;
    private String filterByType = null;
    private String filterByTypeCategory = null;
    private String filterByValue = null;
    private String filterByUnits = null;

    public PropertyValueSettings(PropertyValueController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayEnteredByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredByUserSettingTypeKey).getDefaultValue());
        displayEnteredOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredOnDateTimeSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayIsDynamic = Boolean.parseBoolean(settingTypeMap.get(DisplayIsDynamicSettingTypeKey).getDefaultValue());
        displayIsUserWriteable = Boolean.parseBoolean(settingTypeMap.get(DisplayIsUserWriteableSettingTypeKey).getDefaultValue());
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayTag = Boolean.parseBoolean(settingTypeMap.get(DisplayTagSettingTypeKey).getDefaultValue());
        displayTypeCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayTypeCategorySettingTypeKey).getDefaultValue());
        displayUnits = Boolean.parseBoolean(settingTypeMap.get(DisplayUnitsSettingTypeKey).getDefaultValue());
        displayEffectiveFromDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEffectiveFromDateTimeSettingTypeKey).getDefaultValue());
        displayEffectiveToDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEffectiveToDateTimeSettingTypeKey).getDefaultValue());

        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByEnteredByUser = settingTypeMap.get(FilterByEnteredByUserSettingTypeKey).getDefaultValue();
        filterByEnteredOnDateTime = settingTypeMap.get(FilterByEnteredOnDateTimeSettingTypeKey).getDefaultValue();
        filterByIsDynamic = settingTypeMap.get(FilterByIsDynamicSettingTypeKey).getDefaultValue();
        filterByIsUserWriteable = settingTypeMap.get(FilterByIsUserWriteableSettingTypeKey).getDefaultValue();
        filterByTag = settingTypeMap.get(FilterByTagSettingTypeKey).getDefaultValue();
        filterByType = settingTypeMap.get(FilterByTypeSettingTypeKey).getDefaultValue();
        filterByTypeCategory = settingTypeMap.get(FilterByTypeCategorySettingTypeKey).getDefaultValue();
        filterByUnits = settingTypeMap.get(FilterByUnitsSettingTypeKey).getDefaultValue();
        filterByValue = settingTypeMap.get(FilterByValueSettingTypeKey).getDefaultValue();
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayEnteredByUser = settingEntity.getSettingValueAsBoolean(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        displayEnteredOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayIsDynamic = settingEntity.getSettingValueAsBoolean(DisplayIsDynamicSettingTypeKey, displayIsDynamic);
        displayIsUserWriteable = settingEntity.getSettingValueAsBoolean(DisplayIsUserWriteableSettingTypeKey, displayIsUserWriteable);
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayTag = settingEntity.getSettingValueAsBoolean(DisplayTagSettingTypeKey, displayTag);
        displayTypeCategory = settingEntity.getSettingValueAsBoolean(DisplayTypeCategorySettingTypeKey, displayTypeCategory);
        displayUnits = settingEntity.getSettingValueAsBoolean(DisplayUnitsSettingTypeKey, displayUnits);
        displayEffectiveFromDateTime = settingEntity.getSettingValueAsBoolean(DisplayEffectiveFromDateTimeSettingTypeKey, displayEffectiveFromDateTime);
        displayEffectiveToDateTime = settingEntity.getSettingValueAsBoolean(DisplayEffectiveToDateTimeSettingTypeKey, displayEffectiveToDateTime);
        
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByEnteredByUser = settingEntity.getSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = settingEntity.getSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        filterByIsDynamic = settingEntity.getSettingValueAsString(FilterByIsDynamicSettingTypeKey, filterByIsDynamic);
        filterByIsUserWriteable = settingEntity.getSettingValueAsString(FilterByIsUserWriteableSettingTypeKey, filterByIsUserWriteable);
        filterByTag = settingEntity.getSettingValueAsString(FilterByTagSettingTypeKey, filterByTag);
        filterByType = settingEntity.getSettingValueAsString(FilterByTypeSettingTypeKey, filterByType);
        filterByTypeCategory = settingEntity.getSettingValueAsString(FilterByTypeCategorySettingTypeKey, filterByTypeCategory);
        filterByUnits = settingEntity.getSettingValueAsString(FilterByUnitsSettingTypeKey, filterByUnits);
        filterByValue = settingEntity.getSettingValueAsString(FilterByValueSettingTypeKey, filterByValue);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        settingEntity.setSettingValue(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        settingEntity.setSettingValue(DisplayIsDynamicSettingTypeKey, displayIsDynamic);
        settingEntity.setSettingValue(DisplayIsUserWriteableSettingTypeKey, displayIsUserWriteable);
        settingEntity.setSettingValue(DisplayTagSettingTypeKey, displayTag);
        settingEntity.setSettingValue(DisplayTypeCategorySettingTypeKey, displayTypeCategory);
        settingEntity.setSettingValue(DisplayUnitsSettingTypeKey, displayUnits);
        settingEntity.setSettingValue(DisplayEffectiveFromDateTimeSettingTypeKey, displayEffectiveFromDateTime);
        settingEntity.setSettingValue(DisplayEffectiveToDateTimeSettingTypeKey, displayEffectiveToDateTime);

        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        settingEntity.setSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        settingEntity.setSettingValue(FilterByIsDynamicSettingTypeKey, filterByIsDynamic);
        settingEntity.setSettingValue(FilterByIsUserWriteableSettingTypeKey, filterByIsUserWriteable);
        settingEntity.setSettingValue(FilterByTagSettingTypeKey, filterByTag);
        settingEntity.setSettingValue(FilterByTypeSettingTypeKey, filterByType);
        settingEntity.setSettingValue(FilterByTypeCategorySettingTypeKey, filterByTypeCategory);
        settingEntity.setSettingValue(FilterByUnitsSettingTypeKey, filterByUnits);
        settingEntity.setSettingValue(FilterByValueSettingTypeKey, filterByValue);
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEnteredByUser = null;
        filterByEnteredOnDateTime = null;
        filterByIsDynamic = null;
        filterByIsUserWriteable = null;
        filterByTag = null;
        filterByType = null;
        filterByTypeCategory = null;
        filterByUnits = null;
        filterByValue = null;
    }
    
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterByEnteredByUser = (String) filters.get("enteredByUser").getField();
        filterByEnteredOnDateTime = (String) filters.get("enteredOnDateTime").getField();
        filterByIsDynamic = (String) filters.get("isDynamic").getField();
        filterByIsUserWriteable = (String) filters.get("isUserWriteable").getField();
        filterByTag = (String) filters.get("tag").getField();
        filterByType = (String) filters.get("type").getField();
        filterByTypeCategory = (String) filters.get("typeCategory").getField();
        filterByUnits = (String) filters.get("units").getField();
        filterByValue = (String) filters.get("value").getField();
    }

    public Boolean getDisplayEffectiveFromDateTime() {
        return displayEffectiveFromDateTime;
    }

    public void setDisplayEffectiveFromDateTime(Boolean displayEffectiveFromDateTime) {
        this.displayEffectiveFromDateTime = displayEffectiveFromDateTime;
    }

    public Boolean getDisplayEffectiveToDateTime() {
        return displayEffectiveToDateTime;
    }

    public void setDisplayEffectiveToDateTime(Boolean displayEffectiveToDateTime) {
        this.displayEffectiveToDateTime = displayEffectiveToDateTime;
    }
    
    public Boolean getDisplayEnteredByUser() {
        return displayEnteredByUser;
    }

    public void setDisplayEnteredByUser(Boolean displayEnteredByUser) {
        this.displayEnteredByUser = displayEnteredByUser;
    }

    public Boolean getDisplayEnteredOnDateTime() {
        return displayEnteredOnDateTime;
    }

    public void setDisplayEnteredOnDateTime(Boolean displayEnteredOnDateTime) {
        this.displayEnteredOnDateTime = displayEnteredOnDateTime;
    }

    public Boolean getDisplayIsDynamic() {
        return displayIsDynamic;
    }

    public void setDisplayIsDynamic(Boolean displayIsDynamic) {
        this.displayIsDynamic = displayIsDynamic;
    }

    public Boolean getDisplayIsUserWriteable() {
        return displayIsUserWriteable;
    }

    public void setDisplayIsUserWriteable(Boolean displayIsUserWriteable) {
        this.displayIsUserWriteable = displayIsUserWriteable;
    }

    public Boolean getDisplayTypeCategory() {
        return displayTypeCategory;
    }

    public void setDisplayTypeCategory(Boolean displayTypeCategory) {
        this.displayTypeCategory = displayTypeCategory;
    }

    public Boolean getDisplayUnits() {
        return displayUnits;
    }

    public void setDisplayUnits(Boolean displayUnits) {
        this.displayUnits = displayUnits;
    }

    public String getFilterByEnteredByUser() {
        return filterByEnteredByUser;
    }

    public void setFilterByEnteredByUser(String filterByEnteredByUser) {
        this.filterByEnteredByUser = filterByEnteredByUser;
    }

    public String getFilterByEnteredOnDateTime() {
        return filterByEnteredOnDateTime;
    }

    public void setFilterByEnteredOnDateTime(String filterByEnteredOnDateTime) {
        this.filterByEnteredOnDateTime = filterByEnteredOnDateTime;
    }

    public String getFilterByIsDynamic() {
        return filterByIsDynamic;
    }

    public void setFilterByIsDynamic(String filterByIsDynamic) {
        this.filterByIsDynamic = filterByIsDynamic;
    }

    public String getFilterByIsUserWriteable() {
        return filterByIsUserWriteable;
    }

    public void setFilterByIsUserWriteable(String filterByIsUserWriteable) {
        this.filterByIsUserWriteable = filterByIsUserWriteable;
    }

    public Boolean getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(Boolean displayTag) {
        this.displayTag = displayTag;
    }

    public String getFilterByTag() {
        return filterByTag;
    }

    public void setFilterByTag(String filterByTag) {
        this.filterByTag = filterByTag;
    }

    public String getFilterByType() {
        return filterByType;
    }

    public void setFilterByType(String filterByType) {
        this.filterByType = filterByType;
    }

    public String getFilterByTypeCategory() {
        return filterByTypeCategory;
    }

    public void setFilterByTypeCategory(String filterByTypeCategory) {
        this.filterByTypeCategory = filterByTypeCategory;
    }

    public String getFilterByValue() {
        return filterByValue;
    }

    public void setFilterByValue(String filterByValue) {
        this.filterByValue = filterByValue;
    }

    public String getFilterByUnits() {
        return filterByUnits;
    }

    public void setFilterByUnits(String filterByUnits) {
        this.filterByUnits = filterByUnits;
    }
    
}
