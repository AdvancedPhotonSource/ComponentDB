/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.beans.SettingTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 * Search controller.
 */
@Named("searchController")
@SessionScoped
public class SearchController implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Search.List.Display.NumberOfItemsPerPage";

    private static final String DisplayComponentsSettingTypeKey = "Search.List.Display.Components";
    private static final String DisplayComponentInstancesSettingTypeKey = "Search.List.Display.ComponentInstances";
    private static final String DisplayComponentTypesSettingTypeKey = "Search.List.Display.ComponentTypes";
    private static final String DisplayComponentTypeCategoriesSettingTypeKey = "Search.List.Display.ComponentTypeCategories";
    private static final String DisplayPropertyTypesSettingTypeKey = "Search.List.Display.PropertyTypes";
    private static final String DisplayPropertyTypeCategoriesSettingTypeKey = "Search.List.Display.PropertyTypeCategories";
    private static final String DisplaySourcesSettingTypeKey = "Search.List.Display.Sources";
    private static final String DisplayUsersSettingTypeKey = "Search.List.Display.Users";
    private static final String DisplayUserGroupsSettingTypeKey = "Search.List.Display.UserGroups";

    @EJB
    private SettingTypeFacade settingTypeFacade;

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    private String searchString = null;
    private Boolean caseInsensitive = false;

    protected Integer displayNumberOfItemsPerPage = null;

    protected Boolean displayComponents = null;
    protected Boolean displayComponentInstances = null;
    protected Boolean displayComponentTypes = null;
    protected Boolean displayComponentTypeCategories = null;
    protected Boolean displayPropertyTypes = null;
    protected Boolean displayPropertyTypeCategories = null;
    protected Boolean displaySources = null;
    protected Boolean displayUsers = null;
    protected Boolean displayUserGroups = null;

    private List<SettingType> settingTypeList;
    private Map<String, SettingType> settingTypeMap;

    /**
     * Constructor.
     */
    public SearchController() {
    }

    public String search() {
        updateSettings();
        return null;
    }

    public boolean isDisplayResults() {
        return (searchString != null && !searchString.isEmpty());
    }

    public String getCurrentViewId() {
        return SessionUtility.getCurrentViewId();
    }

    public List<SettingType> getSettingTypeList() {
        if (settingTypeList == null) {
            settingTypeList = settingTypeFacade.findAll();
        }
        return settingTypeList;
    }

    public Map<String, SettingType> getSettingTypeMap() {
        if (settingTypeMap == null) {
            settingTypeMap = new HashMap<>();
            for (SettingType settingType : getSettingTypeList()) {
                settingTypeMap.put(settingType.getName(), settingType);
            }
        }
        return settingTypeMap;
    }

    public void updateSettings() {
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        boolean settingsUpdated = false;
        if (sessionUser != null) {
            List<UserSetting> userSettingList = sessionUser.getUserSettingList();
            if (userSettingList != null && !userSettingList.isEmpty()) {
                updateSettingsFromSessionUser(sessionUser);
                settingsUpdated = true;
            }
        }

        if (!settingsUpdated) {
            updateSettingsFromSettingTypeDefaults(getSettingTypeMap());
        }
    }

    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
    }

    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }

    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public Boolean getCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(Boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public Integer getDisplayNumberOfItemsPerPage() {
        return displayNumberOfItemsPerPage;
    }

    public void setDisplayNumberOfItemsPerPage(Integer displayNumberOfItemsPerPage) {
        this.displayNumberOfItemsPerPage = displayNumberOfItemsPerPage;
    }

    public Boolean getDisplayComponents() {
        return displayComponents;
    }

    public void setDisplayComponents(Boolean displayComponents) {
        this.displayComponents = displayComponents;
    }

    public Boolean getDisplayComponentInstances() {
        return displayComponentInstances;
    }

    public void setDisplayComponentInstances(Boolean displayComponentInstances) {
        this.displayComponentInstances = displayComponentInstances;
    }

    public Boolean getDisplayComponentTypes() {
        return displayComponentTypes;
    }

    public void setDisplayComponentTypes(Boolean displayComponentTypes) {
        this.displayComponentTypes = displayComponentTypes;
    }

    public Boolean getDisplayComponentTypeCategories() {
        return displayComponentTypeCategories;
    }

    public void setDisplayComponentTypeCategories(Boolean displayComponentTypeCategories) {
        this.displayComponentTypeCategories = displayComponentTypeCategories;
    }

    public Boolean getDisplayPropertyTypes() {
        return displayPropertyTypes;
    }

    public void setDisplayPropertyTypes(Boolean displayPropertyTypes) {
        this.displayPropertyTypes = displayPropertyTypes;
    }

    public Boolean getDisplayPropertyTypeCategories() {
        return displayPropertyTypeCategories;
    }

    public void setDisplayPropertyTypeCategories(Boolean displayPropertyTypeCategories) {
        this.displayPropertyTypeCategories = displayPropertyTypeCategories;
    }

    public Boolean getDisplaySources() {
        return displaySources;
    }

    public void setDisplaySources(Boolean displaySources) {
        this.displaySources = displaySources;
    }

    public Boolean getDisplayUsers() {
        return displayUsers;
    }

    public void setDisplayUsers(Boolean displayUsers) {
        this.displayUsers = displayUsers;
    }

}
