/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import javax.faces.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author djarosz
 */
public abstract class EmptySettingsBase implements ICdbSettings {
    
    private static final Logger logger = LogManager.getLogger(CdbEntitySettingsBase.class.getName());

    @Override
    public boolean updateSettings() {
        logNoSettingsMessage("updateSettings");
        return false; 
    }

    @Override
    public void saveDisplayListPageHelpFragmentActionListener() {
        logNoSettingsMessage("saveDisplayListPageHelpFragmentActionListener");
    }

    @Override
    public void saveListSettingsForSessionSettingEntityActionListener(ActionEvent actionEvent) {
        logNoSettingsMessage("saveListSettingsForSessionSettingEntityActionListener");
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        logNoSettingsMessage("updateListSettingsFromListDataTable");
    }

    @Override
    public void clearListFilters() {
        logNoSettingsMessage("clearListFilters");
    }

    @Override
    public void clearSelectFilters() {
        logNoSettingsMessage("clearSelectFilters");
    }
    
    private void logNoSettingsMessage(String loadingMessage) {
        logger.warn("Attempting to load: " + loadingMessage + ". No settings for " + this.getClass().getSimpleName());        
    }    
    
}
