/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.PropertyValueHistoryController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class PropertyValueHistorySettings extends CdbEntitySettingsBase<PropertyValueHistoryController> {
    
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyValueHistory.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyValueHistory.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyValueHistory.List.Display.Description";
    private static final String DisplayEnteredByUserSettingTypeKey = "PropertyValueHistory.List.Display.EnteredByUser";
    private static final String DisplayEnteredOnDateTimeSettingTypeKey = "PropertyValueHistory.List.Display.EnteredOnDateTime";
    private static final String DisplayTagSettingTypeKey = "PropertyValueHistory.List.Display.Tag";
    private static final String DisplayUnitsSettingTypeKey = "PropertyValueHistory.List.Display.Units";
    private static final String DisplayEffectiveFromDateTimeSettingTypeKey = "PropertyValueHistory.List.Display.EffectiveFromDateTime"; 
    private static final String DisplayEffectiveToDateTimeSettingTypeKey = "PropertyValueHistory.List.Display.EffectiveToDateTime"; 
    private static final String FilterByDescriptionSettingTypeKey = "PropertyValueHistory.List.FilterBy.Description";
    private static final String FilterByEnteredByUserSettingTypeKey = "PropertyValueHistory.List.FilterBy.EnteredByUser";
    private static final String FilterByEnteredOnDateTimeSettingTypeKey = "PropertyValueHistory.List.FilterBy.EnteredOnDateTime";
    private static final String FilterByTagSettingTypeKey = "PropertyValueHistory.List.FilterBy.Tag";
    private static final String FilterByValueSettingTypeKey = "PropertyValueHistory.List.FilterBy.Value";
    private static final String FilterByUnitsSettingTypeKey = "PropertyValueHistory.List.FilterBy.Units";

    private Boolean displayEnteredByUser = null;
    private Boolean displayEnteredOnDateTime = null;
    private Boolean displayTag = null;
    private Boolean displayUnits = null;
    private Boolean displayEffectiveFromDateTime = null;
    private Boolean displayEffectiveToDateTime = null;

    private String filterByEnteredByUser = null;
    private String filterByEnteredOnDateTime = null;
    private String filterByTag = null;
    private String filterByValue = null;
    private String filterByUnits = null;

    public PropertyValueHistorySettings(PropertyValueHistoryController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayEnteredByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredByUserSettingTypeKey).getDefaultValue());
        displayEnteredOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredOnDateTimeSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayTag = Boolean.parseBoolean(settingTypeMap.get(DisplayTagSettingTypeKey).getDefaultValue());
        displayUnits = Boolean.parseBoolean(settingTypeMap.get(DisplayUnitsSettingTypeKey).getDefaultValue());
        displayEffectiveFromDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEffectiveFromDateTimeSettingTypeKey).getDefaultValue());
        displayEffectiveToDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEffectiveToDateTimeSettingTypeKey).getDefaultValue());

        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByEnteredByUser = settingTypeMap.get(FilterByEnteredByUserSettingTypeKey).getDefaultValue();
        filterByEnteredOnDateTime = settingTypeMap.get(FilterByEnteredOnDateTimeSettingTypeKey).getDefaultValue();
        filterByTag = settingTypeMap.get(FilterByTagSettingTypeKey).getDefaultValue();
        filterByUnits = settingTypeMap.get(FilterByUnitsSettingTypeKey).getDefaultValue();
        filterByValue = settingTypeMap.get(FilterByValueSettingTypeKey).getDefaultValue();
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayEnteredByUser = settingEntity.getSettingValueAsBoolean(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        displayEnteredOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayTag = settingEntity.getSettingValueAsBoolean(DisplayTagSettingTypeKey, displayTag);
        displayUnits = settingEntity.getSettingValueAsBoolean(DisplayUnitsSettingTypeKey, displayUnits);
        displayEffectiveFromDateTime = settingEntity.getSettingValueAsBoolean(DisplayEffectiveFromDateTimeSettingTypeKey, displayEffectiveFromDateTime);
        displayEffectiveToDateTime = settingEntity.getSettingValueAsBoolean(DisplayEffectiveToDateTimeSettingTypeKey, displayEffectiveToDateTime);

        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByEnteredByUser = settingEntity.getSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = settingEntity.getSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        filterByTag = settingEntity.getSettingValueAsString(FilterByTagSettingTypeKey, filterByTag);
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
        settingEntity.setSettingValue(DisplayTagSettingTypeKey, displayTag);
        settingEntity.setSettingValue(DisplayUnitsSettingTypeKey, displayUnits);
        settingEntity.setSettingValue(DisplayEffectiveFromDateTimeSettingTypeKey, displayEffectiveFromDateTime);
        settingEntity.setSettingValue(DisplayEffectiveToDateTimeSettingTypeKey, displayEffectiveToDateTime);

        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        settingEntity.setSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        settingEntity.setSettingValue(FilterByTagSettingTypeKey, filterByTag);
        settingEntity.setSettingValue(FilterByUnitsSettingTypeKey, filterByUnits);
        settingEntity.setSettingValue(FilterByValueSettingTypeKey, filterByValue);
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEnteredByUser = null;
        filterByEnteredOnDateTime = null;
        filterByTag = null;
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
        filterByTag = (String) filters.get("tag").getField();
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

    public Boolean getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(Boolean displayTag) {
        this.displayTag = displayTag;
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

    public String getFilterByTag() {
        return filterByTag;
    }

    public void setFilterByTag(String filterByTag) {
        this.filterByTag = filterByTag;
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
