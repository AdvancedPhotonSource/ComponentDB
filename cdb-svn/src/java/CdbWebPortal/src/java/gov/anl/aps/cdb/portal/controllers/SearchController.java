/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.beans.SettingTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 * Search controller.
 */
@Named("searchController")
@SessionScoped
public class SearchController implements Serializable {

    private final String SETTING_CONTROLLER_NAME = "settingController";

    /* 
     * Controller specific settings
     */
    private static final String CaseInsensitiveSettingTypeKey = "Search.CaseInsensitive";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Search.Display.NumberOfItemsPerPage";
    private static final String DisplayItemTypesSettingTypeKey = "Search.Display.ItemTypes";
    private static final String DisplayItemCategoriesSettingTypeKey = "Search.Display.ItemCategories";
    private static final String DisplayItemElementsSettingTypeKey = "Search.Display.ItemElements";
    private static final String DisplayLocationsSettingTypeKey = "Search.Display.ItemDomainLocation";
    private static final String DisplayCatalogItemsSettingTypeKey = "Search.Display.ItemDomainCatalog";
    private static final String DisplayInventoryItemsSettingTypeKey = "Search.Display.ItemDomainInventory";
    private static final String DisplayPropertyTypesSettingTypeKey = "Search.Display.PropertyTypes";
    private static final String DisplayPropertyTypeCategoriesSettingTypeKey = "Search.Display.PropertyTypeCategories";
    private static final String DisplaySourcesSettingTypeKey = "Search.Display.Sources";
    private static final String DisplayUsersSettingTypeKey = "Search.Display.Users";
    private static final String DisplayUserGroupsSettingTypeKey = "Search.Display.UserGroups";

    @EJB
    private SettingTypeFacade settingTypeFacade;

    private static final Logger logger = Logger.getLogger(SearchController.class.getName());
    private String searchString = null;
    private Boolean caseInsensitive = true;

    protected Integer displayNumberOfItemsPerPage = null;

    protected Boolean displayCatalogItems = null;
    protected Boolean displayInventoryItems = null;
    protected Boolean displayLocationItems = null;
    protected Boolean displayItemTypes = null;
    protected Boolean displayItemCategories = null;
    protected Boolean displayItemElements = null;
    protected Boolean displayPropertyTypes = null;
    protected Boolean displayPropertyTypeCategories = null;
    protected Boolean displaySources = null;
    protected Boolean displayUsers = null;
    protected Boolean displayUserGroups = null;

    private Boolean performSearch = false;
    private Boolean performExternallyInitializedSearch = false; 

    private List<SettingType> settingTypeList;
    private Map<String, SettingType> settingTypeMap;

    private Boolean settingsInitializedFromDefaults = false;

    private SettingController settingController = null;
    protected Date settingsTimestamp = null;

    /**
     * Constructor.
     */
    public SearchController() {
    }

    @PostConstruct
    public void initialize() {
        updateSettings();
    }
    
    public String performInputBoxSearch() {
        if (searchString == null || searchString.isEmpty()) {
            SessionUtility.addWarningMessage("Warning", "Please specify a search entry.");
            return null; 
        }
        performExternallyInitializedSearch = true; 
        return "/views/search/search.xhtml?faces-redirect=true";
    }
    
    public String getInputBoxSearchString() {
        return ""; 
    }
    
    public void setInputBoxSearchString(String searchString) {
        this.searchString = searchString; 
    }

    public void search() {
        if (searchString != null && !searchString.isEmpty()) {
            performSearch = true;
            performExternallyInitializedSearch = false; 
        }
    }

    public void completeSearch() {
        if (searchString == null || searchString.isEmpty()) {
            SessionUtility.addWarningMessage("Warning", "Search string is empty.");
        } else {
            performSearch = false;
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("search");
            } catch (IOException ex) {
                logger.debug(ex);
            }
        }
    }

    public boolean isDisplayResults() {
        return (searchString != null && !searchString.isEmpty()) && !performSearch;
    }

    public boolean isDisplayLoadingScreen() {
        return performSearch;
    }

    public boolean isPerformSearch() {
        return performSearch;
    }

    public boolean isPerformExternallyInitializedSearch() {
        return performExternallyInitializedSearch;
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

    /**
     * Update controller session settings based on session user and settings
     * modification date.
     *
     * @return true if some settings have been loaded.
     */
    public boolean updateSettings() {
        try {
            if (settingController == null) {
                settingController = (SettingController) SessionUtility.findBean(SETTING_CONTROLLER_NAME);
            }

            SettingEntity settingEntity = settingController.getCurrentSettingEntity();
            if (settingEntity != null) {
                if (settingController.SettingsRequireLoading(settingsTimestamp)) {
                    logger.debug("Updating settings for search from session (settings timestamp: " + settingEntity.getSettingsModificationDate() + ")");
                    updateSettingsFromSessionSettingEntity(settingEntity);
                    settingsTimestamp = new Date();
                    return true;
                }
            } else if (settingEntity == null) {
                if (settingController.SettingsRequireLoading(settingsTimestamp)) {
                    updateSettingsFromSettingTypeDefaults(getSettingTypeMap());
                    settingsTimestamp = new Date();
                    return true;
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }

        return false;
    }

    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        caseInsensitive = Boolean.parseBoolean(settingTypeMap.get(CaseInsensitiveSettingTypeKey).getDefaultValue());
        displayCatalogItems = Boolean.parseBoolean(settingTypeMap.get(DisplayCatalogItemsSettingTypeKey).getDefaultValue());
        displayInventoryItems = Boolean.parseBoolean(settingTypeMap.get(DisplayInventoryItemsSettingTypeKey).getDefaultValue());
        displayItemTypes = Boolean.parseBoolean(settingTypeMap.get(DisplayItemTypesSettingTypeKey).getDefaultValue());
        displayItemCategories = Boolean.parseBoolean(settingTypeMap.get(DisplayItemCategoriesSettingTypeKey).getDefaultValue());
        displayItemElements = Boolean.parseBoolean(settingTypeMap.get(DisplayItemElementsSettingTypeKey).getDefaultValue());
        displayLocationItems = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationsSettingTypeKey).getDefaultValue());
        displayPropertyTypes = Boolean.parseBoolean(settingTypeMap.get(DisplayPropertyTypesSettingTypeKey).getDefaultValue());
        displayPropertyTypeCategories = Boolean.parseBoolean(settingTypeMap.get(DisplayPropertyTypeCategoriesSettingTypeKey).getDefaultValue());
        displaySources = Boolean.parseBoolean(settingTypeMap.get(DisplaySourcesSettingTypeKey).getDefaultValue());
        displayUsers = Boolean.parseBoolean(settingTypeMap.get(DisplayUsersSettingTypeKey).getDefaultValue());
        displayUserGroups = Boolean.parseBoolean(settingTypeMap.get(DisplayUserGroupsSettingTypeKey).getDefaultValue());
    }

    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        caseInsensitive = settingEntity.getSettingValueAsBoolean(CaseInsensitiveSettingTypeKey, caseInsensitive);
        displayCatalogItems = settingEntity.getSettingValueAsBoolean(DisplayCatalogItemsSettingTypeKey, displayCatalogItems);
        displayInventoryItems = settingEntity.getSettingValueAsBoolean(DisplayInventoryItemsSettingTypeKey, displayInventoryItems);
        displayItemTypes = settingEntity.getSettingValueAsBoolean(DisplayItemTypesSettingTypeKey, displayItemTypes);
        displayItemCategories = settingEntity.getSettingValueAsBoolean(DisplayItemCategoriesSettingTypeKey, displayItemCategories);
        displayItemElements = settingEntity.getSettingValueAsBoolean(DisplayItemElementsSettingTypeKey, displayItemElements);
        displayLocationItems = settingEntity.getSettingValueAsBoolean(DisplayLocationsSettingTypeKey, displayLocationItems);
        displayPropertyTypes = settingEntity.getSettingValueAsBoolean(DisplayPropertyTypesSettingTypeKey, displayPropertyTypes);
        displayPropertyTypeCategories = settingEntity.getSettingValueAsBoolean(DisplayPropertyTypeCategoriesSettingTypeKey, displayPropertyTypeCategories);
        displaySources = settingEntity.getSettingValueAsBoolean(DisplaySourcesSettingTypeKey, displaySources);
        displayUsers = settingEntity.getSettingValueAsBoolean(DisplayUsersSettingTypeKey, displayUsers);
        displayUserGroups = settingEntity.getSettingValueAsBoolean(DisplayUserGroupsSettingTypeKey, displayUserGroups);

    }

    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(CaseInsensitiveSettingTypeKey, caseInsensitive);
        settingEntity.setSettingValue(DisplayInventoryItemsSettingTypeKey, displayCatalogItems);
        settingEntity.setSettingValue(DisplayCatalogItemsSettingTypeKey, displayInventoryItems);
        settingEntity.setSettingValue(DisplayItemTypesSettingTypeKey, displayItemTypes);
        settingEntity.setSettingValue(DisplayItemCategoriesSettingTypeKey, displayItemCategories);
        settingEntity.setSettingValue(DisplayItemElementsSettingTypeKey, displayItemElements);
        settingEntity.setSettingValue(DisplayLocationsSettingTypeKey, displayLocationItems);
        settingEntity.setSettingValue(DisplayPropertyTypesSettingTypeKey, displayPropertyTypes);
        settingEntity.setSettingValue(DisplayPropertyTypeCategoriesSettingTypeKey, displayPropertyTypeCategories);
        settingEntity.setSettingValue(DisplaySourcesSettingTypeKey, displaySources);
        settingEntity.setSettingValue(DisplayUsersSettingTypeKey, displayUsers);
        settingEntity.setSettingValue(DisplayUserGroupsSettingTypeKey, displayUserGroups);
    }

    public void saveListSettingsForSessionSettingEntityActionListener(ActionEvent actionEvent) {
        logger.debug("Saving settings");
        if (settingController == null) {
            settingController = (SettingController) SessionUtility.findBean(SETTING_CONTROLLER_NAME);
        }

        SettingEntity settingEntity = settingController.getCurrentSettingEntity();

        if (settingEntity != null) {
            logger.debug("Updating search settings for setting entity");
            saveSettingsForSessionSettingEntity(settingEntity);
        }
    }

    public String customizeSearch() {
        String returnPage = SessionUtility.getCurrentViewId() + "?faces-redirect=true";
        logger.debug("Returning to page: " + returnPage);
        return returnPage;
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

    public Boolean getDisplayCatalogItems() {
        return displayCatalogItems;
    }

    public void setDisplayCatalogItems(Boolean displayCatalogItems) {
        this.displayCatalogItems = displayCatalogItems;
    }

    public Boolean getDisplayInventoryItems() {
        return displayInventoryItems;
    }

    public void setDisplayInventoryItems(Boolean displayInventoryItems) {
        this.displayInventoryItems = displayInventoryItems;
    }

    public Boolean getDisplayLocationItems() {
        return displayLocationItems;
    }

    public void setDisplayLocationItems(Boolean displayLocationItems) {
        this.displayLocationItems = displayLocationItems;
    }

    public Boolean getDisplayItemTypes() {
        return displayItemTypes;
    }

    public void setDisplayItemTypes(Boolean displayItemTypes) {
        this.displayItemTypes = displayItemTypes;
    }

    public Boolean getDisplayItemCategories() {
        return displayItemCategories;
    }

    public void setDisplayItemCategories(Boolean displayItemCategories) {
        this.displayItemCategories = displayItemCategories;
    }

    public Boolean getDisplayItemElements() {
        return displayItemElements;
    }

    public void setDisplayItemElements(Boolean displayItemElements) {
        this.displayItemElements = displayItemElements;
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

    public Boolean getDisplayUserGroups() {
        return displayUserGroups;
    }

    public void setDisplayUserGroups(Boolean displayUserGroups) {
        this.displayUserGroups = displayUserGroups;
    }

}
