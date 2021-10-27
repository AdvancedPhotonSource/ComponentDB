/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.UserInfoController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class UserInfoSettings extends CdbEntitySettingsBase<UserInfoController> {
    
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "UserInfo.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "UserInfo.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "UserInfo.List.Display.Description";
    private static final String DisplayEmailSettingTypeKey = "UserInfo.List.Display.Email";
    private static final String DisplayFirstNameSettingTypeKey = "UserInfo.List.Display.FirstName";
    private static final String DisplayGroupsSettingTypeKey = "UserInfo.List.Display.Groups";
    private static final String DisplayLastNameSettingTypeKey = "UserInfo.List.Display.LastName";
    private static final String DisplayMiddleNameSettingTypeKey = "UserInfo.List.Display.MiddleName";
    private static final String FilterByDescriptionSettingTypeKey = "UserInfo.List.FilterBy.Description";
    private static final String FilterByEmailSettingTypeKey = "UserInfo.List.FilterBy.Email";
    private static final String FilterByFirstNameSettingTypeKey = "UserInfo.List.FilterBy.FirstName";
    private static final String FilterByGroupsSettingTypeKey = "UserInfo.List.FilterBy.Groups";
    private static final String FilterByLastNameSettingTypeKey = "UserInfo.List.FilterBy.LastName";
    private static final String FilterByMiddleNameSettingTypeKey = "UserInfo.List.FilterBy.MiddleName";
    private static final String FilterByUsernameSettingTypeKey = "UserInfo.List.FilterBy.Username";
    
    private Boolean displayEmail = null;
    private Boolean displayFirstName = null;
    private Boolean displayGroups = null;
    private Boolean displayLastName = null;
    private Boolean displayMiddleName = null;

    private String filterByEmail = null;
    private String filterByFirstName = null;
    private String filterByGroups = null;
    private String filterByLastName = null;
    private String filterByMiddleName = null;
    private String filterByUsername = null;

    public UserInfoSettings(UserInfoController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayEmail = Boolean.parseBoolean(settingTypeMap.get(DisplayEmailSettingTypeKey).getDefaultValue());
        displayFirstName = Boolean.parseBoolean(settingTypeMap.get(DisplayFirstNameSettingTypeKey).getDefaultValue());
        displayGroups = Boolean.parseBoolean(settingTypeMap.get(DisplayGroupsSettingTypeKey).getDefaultValue());
        displayLastName = Boolean.parseBoolean(settingTypeMap.get(DisplayLastNameSettingTypeKey).getDefaultValue());
        displayMiddleName = Boolean.parseBoolean(settingTypeMap.get(DisplayMiddleNameSettingTypeKey).getDefaultValue());

        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByEmail = settingTypeMap.get(FilterByEmailSettingTypeKey).getDefaultValue();
        filterByFirstName = settingTypeMap.get(FilterByFirstNameSettingTypeKey).getDefaultValue();
        filterByGroups = settingTypeMap.get(FilterByGroupsSettingTypeKey).getDefaultValue();
        filterByLastName = settingTypeMap.get(FilterByLastNameSettingTypeKey).getDefaultValue();
        filterByMiddleName = settingTypeMap.get(FilterByMiddleNameSettingTypeKey).getDefaultValue();
        filterByUsername = settingTypeMap.get(FilterByUsernameSettingTypeKey).getDefaultValue();
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayEmail = settingEntity.getSettingValueAsBoolean(DisplayEmailSettingTypeKey, displayEmail);
        displayFirstName = settingEntity.getSettingValueAsBoolean(DisplayFirstNameSettingTypeKey, displayFirstName);
        displayGroups = settingEntity.getSettingValueAsBoolean(DisplayGroupsSettingTypeKey, displayGroups);
        displayLastName = settingEntity.getSettingValueAsBoolean(DisplayLastNameSettingTypeKey, displayLastName);
        displayMiddleName = settingEntity.getSettingValueAsBoolean(DisplayMiddleNameSettingTypeKey, displayMiddleName);

        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByEmail = settingEntity.getSettingValueAsString(FilterByEmailSettingTypeKey, filterByEmail);
        filterByFirstName = settingEntity.getSettingValueAsString(FilterByFirstNameSettingTypeKey, filterByFirstName);
        filterByGroups = settingEntity.getSettingValueAsString(FilterByGroupsSettingTypeKey, filterByGroups);
        filterByLastName = settingEntity.getSettingValueAsString(FilterByLastNameSettingTypeKey, filterByLastName);
        filterByMiddleName = settingEntity.getSettingValueAsString(FilterByMiddleNameSettingTypeKey, filterByMiddleName);
        filterByUsername = settingEntity.getSettingValueAsString(FilterByUsernameSettingTypeKey, filterByUsername);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayEmailSettingTypeKey, displayEmail);
        settingEntity.setSettingValue(DisplayFirstNameSettingTypeKey, displayFirstName);
        settingEntity.setSettingValue(DisplayGroupsSettingTypeKey, displayGroups);
        settingEntity.setSettingValue(DisplayLastNameSettingTypeKey, displayLastName);
        settingEntity.setSettingValue(DisplayMiddleNameSettingTypeKey, displayMiddleName);

        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByEmailSettingTypeKey, filterByEmail);
        settingEntity.setSettingValue(FilterByFirstNameSettingTypeKey, filterByFirstName);
        settingEntity.setSettingValue(FilterByGroupsSettingTypeKey, filterByGroups);
        settingEntity.setSettingValue(FilterByLastNameSettingTypeKey, filterByLastName);
        settingEntity.setSettingValue(FilterByMiddleNameSettingTypeKey, filterByMiddleName);
        settingEntity.setSettingValue(FilterByUsernameSettingTypeKey, filterByUsername);
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEmail = null;
        filterByFirstName = null;
        filterByGroups = null;
        filterByLastName = null;
        filterByMiddleName = null;
        filterByUsername = null;
    }
    
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterByEmail = (String) filters.get("email").getField();
        filterByFirstName = (String) filters.get("firstName").getField();
        filterByGroups = (String) filters.get("groups").getField();
        filterByLastName = (String) filters.get("lastName").getField();
        filterByMiddleName = (String) filters.get("middleName").getField();
        filterByUsername = (String) filters.get("username").getField();
    }

    public Boolean getDisplayEmail() {
        return displayEmail;
    }

    public void setDisplayEmail(Boolean displayEmail) {
        this.displayEmail = displayEmail;
    }

    public Boolean getDisplayFirstName() {
        return displayFirstName;
    }

    public void setDisplayFirstName(Boolean displayFirstName) {
        this.displayFirstName = displayFirstName;
    }

    public Boolean getDisplayGroups() {
        return displayGroups;
    }

    public void setDisplayGroups(Boolean displayGroups) {
        this.displayGroups = displayGroups;
    }

    public Boolean getDisplayLastName() {
        return displayLastName;
    }

    public void setDisplayLastName(Boolean displayLastName) {
        this.displayLastName = displayLastName;
    }

    public Boolean getDisplayMiddleName() {
        return displayMiddleName;
    }

    public void setDisplayMiddleName(Boolean displayMiddleName) {
        this.displayMiddleName = displayMiddleName;
    }

    public String getFilterByEmail() {
        return filterByEmail;
    }

    public void setFilterByEmail(String filterByEmail) {
        this.filterByEmail = filterByEmail;
    }

    public String getFilterByFirstName() {
        return filterByFirstName;
    }

    public void setFilterByFirstName(String filterByFirstName) {
        this.filterByFirstName = filterByFirstName;
    }

    public String getFilterByGroups() {
        return filterByGroups;
    }

    public void setFilterByGroups(String filterByGroups) {
        this.filterByGroups = filterByGroups;
    }

    public String getFilterByLastName() {
        return filterByLastName;
    }

    public void setFilterByLastName(String filterByLastName) {
        this.filterByLastName = filterByLastName;
    }

    public String getFilterByMiddleName() {
        return filterByMiddleName;
    }

    public void setFilterByMiddleName(String filterByMiddleName) {
        this.filterByMiddleName = filterByMiddleName;
    }

    public String getFilterByUsername() {
        return filterByUsername;
    }

    public void setFilterByUsername(String filterByUsername) {
        this.filterByUsername = filterByUsername;
    }

    
}
