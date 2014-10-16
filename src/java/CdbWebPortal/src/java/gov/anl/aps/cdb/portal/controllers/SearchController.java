/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.beans.SettingTypeFacade;
import gov.anl.aps.cdb.portal.model.entities.SettingType;
import gov.anl.aps.cdb.portal.model.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.entities.UserSetting;
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
public class SearchController implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Search.List.Display.NumberOfItemsPerPage";
    
    @EJB
    private SettingTypeFacade settingTypeFacade;
    
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    private String searchString = null;
    private Boolean caseInsensitive = false;
    protected Integer displayNumberOfItemsPerPage = null;
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

}
