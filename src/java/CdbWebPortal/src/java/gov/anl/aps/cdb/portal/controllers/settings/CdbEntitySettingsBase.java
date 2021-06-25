/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import java.util.Date;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public abstract class CdbEntitySettingsBase<EntityController extends CdbEntityController> extends SettingBase<EntityController> {
    
    private static final Logger logger = LogManager.getLogger(CdbEntitySettingsBase.class.getName());
        
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
    
    public CdbEntitySettingsBase(EntityController parentController) {
        super(parentController);        
    }        
    
    @Override
    protected void settingsAreReloaded() {
        parentController.settingsAreReloaded();
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

        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterById = (String) filters.get("id").getField();
        filterByName = (String) filters.get("name").getField();
        filterByDescription = (String) filters.get("description").getField();
        filterByOwnerUser = (String) filters.get("entityInfo.ownerUser.username").getField();
        filterByOwnerGroup = (String) filters.get("entityInfo.ownerUserGroup.name").getField();
        filterByCreatedByUser = (String) filters.get("entityInfo.createdByUser.username").getField();
        filterByCreatedOnDateTime = (String) filters.get("entityInfo.createdOnDateTime").getField();
        filterByLastModifiedByUser = (String) filters.get("entityInfo.lastModifiedByUser.username").getField();
        filterByLastModifiedOnDateTime = (String) filters.get("entityInfo.lastModifiedOnDateTime").getField();
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
