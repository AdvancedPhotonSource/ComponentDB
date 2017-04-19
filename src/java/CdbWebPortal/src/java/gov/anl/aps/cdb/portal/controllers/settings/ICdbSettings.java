/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;

public interface ICdbSettings {
    
    /**
     * Update controller session settings based on session user and settings
     * modification date.
     *
     * @return true if some settings have been loaded.
     */
    public boolean updateSettings();
    
    /**
     * Allows for saving the toggle state of help fragment whenever a session user is available.
     */
    public void saveDisplayListPageHelpFragmentActionListener();
    
    /**
     * Listener for saving session user settings.
     *
     * @param actionEvent event
     */
    public void saveListSettingsForSessionSettingEntityActionListener(ActionEvent actionEvent);
    
    /**
     * Update entity list display settings using current data table values.
     *
     * This method should be overridden in any derived controller class that has
     * its own settings.
     *
     * @param dataTable entity list data table
     */
    public void updateListSettingsFromListDataTable(DataTable dataTable);
    
    /**
     * Clear entity list filters.
     *
     * This method should be overridden in any derived controller class that has
     * its own filters.
     */
    public void clearListFilters();
    
    /**
     * Clear entity selection list filters.
     *
     * This method should be overridden in any derived controller class that has
     * its own select filters.
     */
    public void clearSelectFilters();
}
