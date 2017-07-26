/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.SettingController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author djarosz
 */
public abstract class SettingsBase<EntityController extends CdbEntityController> implements ICdbSettings {
    
    private static final Logger logger = Logger.getLogger(SettingsBase.class.getName());
    
    protected Integer displayNumberOfItemsPerPage = null;
    protected Boolean displayId = null;
    protected Boolean displayDescription = null;
    protected Boolean displayOwnerUser = null;
    protected Boolean displayOwnerGroup = null;
    protected Boolean displayCreatedByUser = null;
    protected Boolean displayCreatedOnDateTime = null;
    protected Boolean displayLastModifiedByUser = null;
    protected Boolean displayLastModifiedOnDateTime = null;

    protected String filterById = null;
    protected String filterByName = null;
    protected String filterByDescription = null;
    protected String filterByOwnerUser = null;
    protected String filterByOwnerGroup = null;
    protected String filterByCreatedByUser = null;
    protected String filterByCreatedOnDateTime = null;
    protected String filterByLastModifiedByUser = null;
    protected String filterByLastModifiedOnDateTime = null;

    protected Integer selectDisplayNumberOfItemsPerPage = null;
    protected Boolean selectDisplayId = false;
    protected Boolean selectDisplayDescription = false;
    protected Boolean selectDisplayOwnerUser = true;
    protected Boolean selectDisplayOwnerGroup = true;
    protected Boolean selectDisplayCreatedByUser = false;
    protected Boolean selectDisplayCreatedOnDateTime = false;
    protected Boolean selectDisplayLastModifiedByUser = false;
    protected Boolean selectDisplayLastModifiedOnDateTime = false;

    protected String selectFilterById = null;
    protected String selectFilterByName = null;
    protected String selectFilterByDescription = null;
    protected String selectFilterByOwnerUser = null;
    protected String selectFilterByOwnerGroup = null;
    protected String selectFilterByCreatedByUser = null;
    protected String selectFilterByCreatedOnDateTime = null;
    protected String selectFilterByLastModifiedByUser = null;
    protected String selectFilterByLastModifiedOnDateTime = null;

    protected Boolean displayListPageHelpFragment = true;
    
    private SettingController settingController = null;
    protected EntityController parentController; 
    
    protected Date settingsTimestamp = null;
        
    protected Map<String, SettingType> settingTypeMap;
    
    public SettingsBase(EntityController parentController) {
        this.parentController = parentController;        
    }        
    
    /**
     * Update controller session settings with setting type default values.
     *
     * This method should be overridden in any derived controller class that has
     * its own settings.
     *
     * @param settingTypeMap map of setting types
     */
    protected abstract void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap);

    /**
     * Update controller session settings with user-specific values from the
     * database.
     *
     * This method should be overridden in any derived controller class that has
     * its own settings.
     *
     * @param settingEntity current session setting entity
     */
    protected abstract void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity);

    /**
     * Save controller session settings for the current setting entity.
     *
     * This method should be overridden in any derived controller class that has
     * its own settings.
     *
     * @param settingEntity current session setting entity
     */
    protected abstract void saveSettingsForSessionSettingEntity(SettingEntity settingEntity);      
        
    protected void loadSettings(SettingEntity settingEntity) {
        logger.debug("Updating " + getClassName() + " from session (settings timestamp: " + settingEntity.getSettingsModificationDate() + ")");
        updateSettingsFromSessionSettingEntity(settingEntity);
        settingsTimestamp = new Date();
    }   
    
    /**
     * Update controller session settings based on session user and settings
     * modification date.
     *
     * @return true if some settings have been loaded.
     */
    public boolean updateSettings() {
        try {
            settingController = getSettingController();

            SettingEntity settingEntity = settingController.getCurrentSettingEntity();
            if (settingEntity != null) {
                if (settingController.SettingsRequireLoading(settingsTimestamp)) {
                    parentController.settingsAreReloaded();
                    loadSettings(settingEntity);
                    return true;
                }
            } else if (settingEntity == null) {
                if (settingController.SettingsRequireLoading(settingsTimestamp)) {
                    parentController.settingsAreReloaded();
                    Map<String, SettingType> defaultSettingMap = getSettingTypeMap();
                    if(defaultSettingMap != null) {
                        updateSettingsFromSettingTypeDefaults(defaultSettingMap);
                        settingsTimestamp = new Date();
                        return true;
                    }                    
                }
            }
        } catch (Exception ex) {
            logger.error(getClassName() + ": " + ex);
        }

        return false;
    }
    
    /**
     * Get setting type map.
     *
     * If not set, this map is constructed from list of setting types retrieved
     * from the database.
     *
     * @return setting type map
     */
    protected Map<String, SettingType> getSettingTypeMap() {
        if (settingTypeMap == null) {
            settingTypeMap = new HashMap<>();
            List<SettingType> settingTypeList = parentController.getSettingTypeList();
            for (SettingType settingType : settingTypeList) {
                settingTypeMap.put(settingType.getName(), settingType);
            }
        }
        return settingTypeMap;
    }        
    
    protected SettingController getSettingController() {
        if (settingController == null) {
            settingController = SettingController.getInstance();
        }

        return settingController;
    }
    
    /**
     * Entities may include a help fragment with useful information. The fragment could be toggled on and off using the key. 
     * 
     * @return 
     */
    public String getDisplayListPageHelpFragmentSettingTypeKey() {
        return null;
    }
    
    /**
     * Allows for saving the toggle state of help fragment whenever a session user is available.
     */
    public void saveDisplayListPageHelpFragmentActionListener() {
        logger.debug("Saving settings");
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {            
            logger.debug("Updating display list settings for " + getClassName());
            sessionUser.setSettingValue(getDisplayListPageHelpFragmentSettingTypeKey(), displayListPageHelpFragment);
            settingsTimestamp = new Date();
            SessionUtility.addInfoMessage("Saved", "Saved setting for displaying help info for " + getClassName());
        } else {
            String error = "The setting for showing help infomation could not be saved.";
            SessionUtility.addErrorMessage("Error", error);
            logger.error(error);
        }
    }
    
    /**
     * Listener for saving session user settings.
     *
     * @param actionEvent event
     */
    public void saveListSettingsForSessionSettingEntityActionListener(ActionEvent actionEvent) {
        logger.debug("Saving settings");
        settingController = getSettingController();

        SettingEntity settingEntity = settingController.getCurrentSettingEntity();

        if (settingEntity != null) {
            logger.debug("Updating list settings for " + settingEntity);
            saveSettingsForSessionSettingEntity(settingEntity);
            parentController.resetListDataModel();
            settingsTimestamp = new Date();
        }
    }
    
    /**
     * Parse setting value as integer.
     *
     * @param settingValue setting string value
     * @return integer value, or null in case string value cannot be parsed
     */
    protected static Integer parseSettingValueAsInteger(String settingValue) {
        if (settingValue == null || settingValue.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(settingValue);
        } catch (NumberFormatException ex) {
            logger.warn("Could not parse setting value: " + settingValue);
            return null;
        }
    }
        
    /**
     * Update entity list display settings using current data table values.
     *
     * This method should be overridden in any derived controller class that has
     * its own settings.
     *
     * @param dataTable entity list data table
     */
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterById = (String) filters.get("id");
        filterByName = (String) filters.get("name");
        filterByDescription = (String) filters.get("description");
        filterByOwnerUser = (String) filters.get("entityInfo.ownerUser.username");
        filterByOwnerGroup = (String) filters.get("entityInfo.ownerUserGroup.name");
        filterByCreatedByUser = (String) filters.get("entityInfo.createdByUser.username");
        filterByCreatedOnDateTime = (String) filters.get("entityInfo.createdOnDateTime");
        filterByLastModifiedByUser = (String) filters.get("entityInfo.lastModifiedByUser.username");
        filterByLastModifiedOnDateTime = (String) filters.get("entityInfo.lastModifiedOnDateTime");
    }

    /**
     * Clear entity list filters.
     *
     * This method should be overridden in any derived controller class that has
     * its own filters.
     */
    public void clearListFilters() {
        filterById = null;
        filterByName = null;
        filterByDescription = null;
        filterByOwnerUser = null;
        filterByOwnerGroup = null;
        filterByCreatedByUser = null;
        filterByCreatedOnDateTime = null;
        filterByLastModifiedByUser = null;
        filterByLastModifiedOnDateTime = null;
    }

    /**
     * Clear entity selection list filters.
     *
     * This method should be overridden in any derived controller class that has
     * its own select filters.
     */
    public void clearSelectFilters() {
        selectFilterById = null;
        selectFilterByName = null;
        selectFilterByDescription = null;
        selectFilterByOwnerUser = null;
        selectFilterByOwnerGroup = null;
        selectFilterByCreatedByUser = null;
        selectFilterByCreatedOnDateTime = null;
        selectFilterByLastModifiedByUser = null;
        selectFilterByLastModifiedOnDateTime = null;
    }        
    
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }        

    public Integer getDisplayNumberOfItemsPerPage() {
        return displayNumberOfItemsPerPage;
    }

    public void setDisplayNumberOfItemsPerPage(Integer displayNumberOfItemsPerPage) {
        this.displayNumberOfItemsPerPage = displayNumberOfItemsPerPage;
    }

    public Boolean getDisplayId() {
        return displayId;
    }

    public void setDisplayId(Boolean displayId) {
        this.displayId = displayId;
    }

    public Boolean getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(Boolean displayDescription) {
        this.displayDescription = displayDescription;
    }

    public Boolean getDisplayOwnerUser() {
        return displayOwnerUser;
    }

    public void setDisplayOwnerUser(Boolean displayOwnerUser) {
        this.displayOwnerUser = displayOwnerUser;
    }

    public Boolean getDisplayOwnerGroup() {
        return displayOwnerGroup;
    }

    public void setDisplayOwnerGroup(Boolean displayOwnerGroup) {
        this.displayOwnerGroup = displayOwnerGroup;
    }

    public Boolean getDisplayCreatedByUser() {
        return displayCreatedByUser;
    }

    public void setDisplayCreatedByUser(Boolean displayCreatedByUser) {
        this.displayCreatedByUser = displayCreatedByUser;
    }

    public Boolean getDisplayCreatedOnDateTime() {
        return displayCreatedOnDateTime;
    }

    public void setDisplayCreatedOnDateTime(Boolean displayCreatedOnDateTime) {
        this.displayCreatedOnDateTime = displayCreatedOnDateTime;
    }

    public Boolean getDisplayLastModifiedByUser() {
        return displayLastModifiedByUser;
    }

    public void setDisplayLastModifiedByUser(Boolean displayLastModifiedByUser) {
        this.displayLastModifiedByUser = displayLastModifiedByUser;
    }

    public Boolean getDisplayLastModifiedOnDateTime() {
        return displayLastModifiedOnDateTime;
    }

    public void setDisplayLastModifiedOnDateTime(Boolean displayLastModifiedOnDateTime) {
        this.displayLastModifiedOnDateTime = displayLastModifiedOnDateTime;
    }

    public void setFilterById(String filterById) {
        this.filterById = filterById;
    }

    public String getFilterById() {
        return filterById;
    }

    public void setFilterByName(String filterByName) {
        this.filterByName = filterByName;
    }

    public String getFilterByName() {
        return filterByName;
    }

    public String getFilterByDescription() {
        return filterByDescription;
    }

    public void setFilterByDescription(String filterByDescription) {
        this.filterByDescription = filterByDescription;
    }

    public String getFilterByOwnerUser() {
        return filterByOwnerUser;
    }

    public void setFilterByOwnerUser(String filterByOwnerUser) {
        this.filterByOwnerUser = filterByOwnerUser;
    }

    public String getFilterByOwnerGroup() {
        return filterByOwnerGroup;
    }

    public void setFilterByOwnerGroup(String filterByOwnerGroup) {
        this.filterByOwnerGroup = filterByOwnerGroup;
    }

    public String getFilterByCreatedByUser() {
        return filterByCreatedByUser;
    }

    public void setFilterByCreatedByUser(String filterByCreatedByUser) {
        this.filterByCreatedByUser = filterByCreatedByUser;
    }

    public String getFilterByCreatedOnDateTime() {
        return filterByCreatedOnDateTime;
    }

    public void setFilterByCreatedOnDateTime(String filterByCreatedOnDateTime) {
        this.filterByCreatedOnDateTime = filterByCreatedOnDateTime;
    }

    public String getFilterByLastModifiedByUser() {
        return filterByLastModifiedByUser;
    }

    public void setFilterByLastModifiedByUser(String filterByLastModifiedByUser) {
        this.filterByLastModifiedByUser = filterByLastModifiedByUser;
    }

    public String getFilterByLastModifiedOnDateTime() {
        return filterByLastModifiedOnDateTime;
    }

    public void setFilterByLastModifiedOnDateTime(String filterByLastModifiedOnDateTime) {
        this.filterByLastModifiedOnDateTime = filterByLastModifiedOnDateTime;
    }

    public Boolean getDisplayListPageHelpFragment() {
        return displayListPageHelpFragment;
    }

    public void setDisplayListPageHelpFragment(Boolean displayListPageHelpFragment) {
        this.displayListPageHelpFragment = displayListPageHelpFragment;
    }
    
    public Integer getSelectDisplayNumberOfItemsPerPage() {
        return selectDisplayNumberOfItemsPerPage;
    }

    public void setSelectDisplayNumberOfItemsPerPage(Integer selectDisplayNumberOfItemsPerPage) {
        this.selectDisplayNumberOfItemsPerPage = selectDisplayNumberOfItemsPerPage;
    }

    public Boolean getSelectDisplayId() {
        return selectDisplayId;
    }

    public void setSelectDisplayId(Boolean selectDisplayId) {
        this.selectDisplayId = selectDisplayId;
    }

    public Boolean getSelectDisplayDescription() {
        return selectDisplayDescription;
    }

    public void setSelectDisplayDescription(Boolean selectDisplayDescription) {
        this.selectDisplayDescription = selectDisplayDescription;
    }

    public Boolean getSelectDisplayOwnerUser() {
        return selectDisplayOwnerUser;
    }

    public void setSelectDisplayOwnerUser(Boolean selectDisplayOwnerUser) {
        this.selectDisplayOwnerUser = selectDisplayOwnerUser;
    }

    public Boolean getSelectDisplayOwnerGroup() {
        return selectDisplayOwnerGroup;
    }

    public void setSelectDisplayOwnerGroup(Boolean selectDisplayOwnerGroup) {
        this.selectDisplayOwnerGroup = selectDisplayOwnerGroup;
    }

    public Boolean getSelectDisplayCreatedByUser() {
        return selectDisplayCreatedByUser;
    }

    public void setSelectDisplayCreatedByUser(Boolean selectDisplayCreatedByUser) {
        this.selectDisplayCreatedByUser = selectDisplayCreatedByUser;
    }

    public Boolean getSelectDisplayCreatedOnDateTime() {
        return selectDisplayCreatedOnDateTime;
    }

    public void setSelectDisplayCreatedOnDateTime(Boolean selectDisplayCreatedOnDateTime) {
        this.selectDisplayCreatedOnDateTime = selectDisplayCreatedOnDateTime;
    }

    public Boolean getSelectDisplayLastModifiedByUser() {
        return selectDisplayLastModifiedByUser;
    }

    public void setSelectDisplayLastModifiedByUser(Boolean selectDisplayLastModifiedByUser) {
        this.selectDisplayLastModifiedByUser = selectDisplayLastModifiedByUser;
    }

    public Boolean getSelectDisplayLastModifiedOnDateTime() {
        return selectDisplayLastModifiedOnDateTime;
    }

    public void setSelectDisplayLastModifiedOnDateTime(Boolean selectDisplayLastModifiedOnDateTime) {
        this.selectDisplayLastModifiedOnDateTime = selectDisplayLastModifiedOnDateTime;
    }

    public String getSelectFilterById() {
        return selectFilterById;
    }

    public void setSelectFilterById(String selectFilterById) {
        this.selectFilterById = selectFilterById;
    }

    public String getSelectFilterByName() {
        return selectFilterByName;
    }

    public void setSelectFilterByName(String selectFilterByName) {
        this.selectFilterByName = selectFilterByName;
    }

    public String getSelectFilterByDescription() {
        return selectFilterByDescription;
    }

    public void setSelectFilterByDescription(String selectFilterByDescription) {
        this.selectFilterByDescription = selectFilterByDescription;
    }

    public String getSelectFilterByOwnerUser() {
        return selectFilterByOwnerUser;
    }

    public void setSelectFilterByOwnerUser(String selectFilterByOwnerUser) {
        this.selectFilterByOwnerUser = selectFilterByOwnerUser;
    }

    public String getSelectFilterByOwnerGroup() {
        return selectFilterByOwnerGroup;
    }

    public void setSelectFilterByOwnerGroup(String selectFilterByOwnerGroup) {
        this.selectFilterByOwnerGroup = selectFilterByOwnerGroup;
    }

    public String getSelectFilterByCreatedByUser() {
        return selectFilterByCreatedByUser;
    }

    public void setSelectFilterByCreatedByUser(String selectFilterByCreatedByUser) {
        this.selectFilterByCreatedByUser = selectFilterByCreatedByUser;
    }

    public String getSelectFilterByCreatedOnDateTime() {
        return selectFilterByCreatedOnDateTime;
    }

    public void setSelectFilterByCreatedOnDateTime(String selectFilterByCreatedOnDateTime) {
        this.selectFilterByCreatedOnDateTime = selectFilterByCreatedOnDateTime;
    }

    public String getSelectFilterByLastModifiedByUser() {
        return selectFilterByLastModifiedByUser;
    }

    public void setSelectFilterByLastModifiedByUser(String selectFilterByLastModifiedByUser) {
        this.selectFilterByLastModifiedByUser = selectFilterByLastModifiedByUser;
    }

    public String getSelectFilterByLastModifiedOnDateTime() {
        return selectFilterByLastModifiedOnDateTime;
    }

    public void setSelectFilterByLastModifiedOnDateTime(String selectFilterByLastModifiedOnDateTime) {
        this.selectFilterByLastModifiedOnDateTime = selectFilterByLastModifiedOnDateTime;
    }    
    
    public Date getSettingsTimestamp() {
        return settingsTimestamp;
    }

    public void setSettingsTimestamp(Date settingsTimestamp) {
        this.settingsTimestamp = settingsTimestamp;
    }
}
