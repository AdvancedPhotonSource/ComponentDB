/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.SettingController;
import gov.anl.aps.cdb.portal.model.db.beans.SettingTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
public abstract class SettingBase<ObjectController> implements ICdbSettings {
    
    private static final Logger logger = LogManager.getLogger(SettingBase.class.getName());

    protected Integer displayNumberOfItemsPerPage = null;
    protected Boolean displayListPageHelpFragment = true;
    private SettingController settingController = null;
    protected ObjectController parentController;

    protected Date settingsTimestamp = null;

    protected Map<String, SettingType> settingTypeMap;
        
    private SettingTypeFacade settingTypeFacade;
    
    protected List<SettingType> settingTypeList;

    public SettingBase(ObjectController parentController) {
        this.parentController = parentController;
        settingTypeFacade = settingTypeFacade.getInstance(); 
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
    
    /**
     * Functionality used to notify controller that settings were reloaded 
     */
    protected abstract void settingsAreReloaded();        
    
    /**
     * Get list of setting types.
     *
     * If not set, this list is retrieved from the database.
     *
     * @return setting type list
     */
    public List<SettingType> getSettingTypeList() {
        if (settingTypeList == null) {
            settingTypeList = settingTypeFacade.findAll();
        }
        return settingTypeList;
    }
        
    protected SettingController getSettingController() {
        if (settingController == null) {
            settingController = SettingController.getInstance();
        }

        return settingController;
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
                    settingsAreReloaded();
                    loadSettings(settingEntity);
                    return true;
                }
            } else if (settingEntity == null) {
                if (settingController.SettingsRequireLoading(settingsTimestamp)) {
                    settingsAreReloaded();
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
    
    protected String getClassName() {
        return this.getClass().getSimpleName();
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
            List<SettingType> settingTypeList = getSettingTypeList();
            for (SettingType settingType : settingTypeList) {
                settingTypeMap.put(settingType.getName(), settingType);
            }
        }
        return settingTypeMap;
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
            settingsAreReloaded();
            settingsTimestamp = new Date();
        }
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
     * Entities may include a help fragment with useful information. The fragment could be toggled on and off using the key. 
     * 
     * @return 
     */
    public String getDisplayListPageHelpFragmentSettingTypeKey() {
        return null;
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
    
    protected void loadSettings(SettingEntity settingEntity) {
        logger.debug("Updating " + getClassName() + " from session (settings timestamp: " + settingEntity.getSettingsModificationDate() + ")");
        updateSettingsFromSessionSettingEntity(settingEntity);
        settingsTimestamp = new Date();
    }   
    
    public Integer getDisplayNumberOfItemsPerPage() {
        return displayNumberOfItemsPerPage;
    }

    public void setDisplayNumberOfItemsPerPage(Integer displayNumberOfItemsPerPage) {
        this.displayNumberOfItemsPerPage = displayNumberOfItemsPerPage;
    }

}
