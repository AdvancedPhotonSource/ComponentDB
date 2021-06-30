/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class SourceSettings extends CdbEntitySettingsBase<SourceController> {
    
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Source.List.Display.NumberOfItemsPerPage";
    private static final String DisplayContactInfoSettingTypeKey = "Source.List.Display.ContactInfo";
    private static final String DisplayDescriptionSettingTypeKey = "Source.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "Source.List.Display.Id";
    private static final String DisplayUrlSettingTypeKey = "Source.List.Display.Url";
    private static final String FilterByNameSettingTypeKey = "Source.List.FilterBy.Name";
    private static final String FilterByContactInfoSettingTypeKey = "Source.List.FilterBy.ContactInfo";
    private static final String FilterByDescriptionSettingTypeKey = "Source.List.FilterBy.Description";
    private static final String FilterByUrlSettingTypeKey = "Source.List.FilterBy.Url";
    
    private Boolean displayContactInfo = null;
    private Boolean displayUrl = null;

    private String filterByContactInfo = null;
    private String filterByUrl = null;

    public SourceSettings(SourceController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayContactInfo = Boolean.parseBoolean(settingTypeMap.get(DisplayContactInfoSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayUrl = Boolean.parseBoolean(settingTypeMap.get(DisplayUrlSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByContactInfo = settingTypeMap.get(FilterByContactInfoSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByUrl = settingTypeMap.get(FilterByUrlSettingTypeKey).getDefaultValue();
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayContactInfo = settingEntity.getSettingValueAsBoolean(DisplayContactInfoSettingTypeKey, displayContactInfo);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayUrl = settingEntity.getSettingValueAsBoolean(DisplayUrlSettingTypeKey, displayUrl);

        filterByName = settingEntity.getSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByContactInfo = settingEntity.getSettingValueAsString(FilterByContactInfoSettingTypeKey, filterByContactInfo);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByUrl = settingEntity.getSettingValueAsString(FilterByUrlSettingTypeKey, filterByUrl);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayContactInfoSettingTypeKey, displayContactInfo);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayUrlSettingTypeKey, displayUrl);

        settingEntity.setSettingValue(FilterByNameSettingTypeKey, filterByName);
        settingEntity.setSettingValue(FilterByContactInfoSettingTypeKey, filterByContactInfo);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByUrlSettingTypeKey, filterByUrl);
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByContactInfo = null;
        filterByUrl = null;
    }
    
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterByContactInfo = (String) filters.get("contactInfo").getField();
        filterByUrl = (String) filters.get("url").getField();
    }
    
    public Boolean getDisplayContactInfo() {
        return displayContactInfo;
    }

    public void setDisplayContactInfo(Boolean displayContactInfo) {
        this.displayContactInfo = displayContactInfo;
    }

    public Boolean getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(Boolean displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getFilterByContactInfo() {
        return filterByContactInfo;
    }

    public void setFilterByContactInfo(String filterByContactInfo) {
        this.filterByContactInfo = filterByContactInfo;
    }

    public String getFilterByUrl() {
        return filterByUrl;
    }

    public void setFilterByUrl(String filterByUrl) {
        this.filterByUrl = filterByUrl;
    }
    
}
