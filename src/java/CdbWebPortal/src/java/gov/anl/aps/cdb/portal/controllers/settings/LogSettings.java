/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.LogController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class LogSettings extends CdbEntitySettingsBase<LogController>{
    
    private static final String DisplayAttachmentsSettingTypeKey = "Log.List.Display.Attachments";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Log.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "Log.List.Display.Id";
    private static final String DisplayEnteredByUserSettingTypeKey = "Log.List.Display.EnteredByUser";
    private static final String DisplayEnteredOnDateTimeSettingTypeKey = "Log.List.Display.EnteredOnDateTime";
    private static final String DisplayEffectiveFromDateTimeSettingTypeKey = "Log.List.Display.EffectiveFromDateTime";
    private static final String DisplayEffectiveToDateTimeSettingTypeKey = "Log.List.Display.EffectiveToDateTime";
    private static final String DisplayTopicSettingTypeKey = "Log.List.Display.Topic";
    private static final String FilterByEnteredByUserSettingTypeKey = "Log.List.FilterBy.EnteredByUser";
    private static final String FilterByEnteredOnDateTimeSettingTypeKey = "Log.List.FilterBy.EnteredOnDateTime";
    private static final String FilterByTextSettingTypeKey = "Log.List.FilterBy.Text";
    private static final String FilterByTopicSettingTypeKey = "Log.List.FilterBy.Topic";

    private Boolean displayAttachments = null;
    private Boolean displayEnteredByUser = null;
    private Boolean displayEnteredOnDateTime = null;
    private Boolean displayTopic = null;
    private Boolean displayEffectiveFromDateTime = null; 
    private Boolean displayEffectiveToDateTime = null; 

    private String filterByEnteredByUser = null;
    private String filterByEnteredOnDateTime = null;
    private String filterByText = null;
    private String filterByTopic = null;

    public LogSettings(LogController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayAttachments = Boolean.parseBoolean(settingTypeMap.get(DisplayAttachmentsSettingTypeKey).getDefaultValue());
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayEnteredByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredByUserSettingTypeKey).getDefaultValue());
        displayEnteredOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredOnDateTimeSettingTypeKey).getDefaultValue());
        displayEffectiveFromDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEffectiveFromDateTimeSettingTypeKey).getDefaultValue());
        displayEffectiveToDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEffectiveToDateTimeSettingTypeKey).getDefaultValue());        
        displayTopic = Boolean.parseBoolean(settingTypeMap.get(DisplayTopicSettingTypeKey).getDefaultValue());

        filterByEnteredByUser = settingTypeMap.get(FilterByEnteredByUserSettingTypeKey).getDefaultValue();
        filterByEnteredOnDateTime = settingTypeMap.get(FilterByEnteredOnDateTimeSettingTypeKey).getDefaultValue();
        filterByText = settingTypeMap.get(FilterByTextSettingTypeKey).getDefaultValue();
        filterByTopic = settingTypeMap.get(FilterByTopicSettingTypeKey).getDefaultValue();
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayAttachments = settingEntity.getSettingValueAsBoolean(DisplayAttachmentsSettingTypeKey, displayAttachments);
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayEnteredByUser = settingEntity.getSettingValueAsBoolean(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        displayEnteredOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        displayEffectiveFromDateTime = settingEntity.getSettingValueAsBoolean(DisplayEffectiveFromDateTimeSettingTypeKey, displayEffectiveFromDateTime);
        displayEffectiveToDateTime = settingEntity.getSettingValueAsBoolean(DisplayEffectiveToDateTimeSettingTypeKey, displayEffectiveToDateTime);
        displayTopic = settingEntity.getSettingValueAsBoolean(DisplayTopicSettingTypeKey, displayTopic);    

        filterByEnteredByUser = settingEntity.getSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = settingEntity.getSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        filterByText = settingEntity.getSettingValueAsString(FilterByTextSettingTypeKey, filterByText);
        filterByTopic = settingEntity.getSettingValueAsString(FilterByTopicSettingTypeKey, filterByTopic);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(DisplayAttachmentsSettingTypeKey, displayAttachments);
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        settingEntity.setSettingValue(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        settingEntity.setSettingValue(DisplayEffectiveFromDateTimeSettingTypeKey, displayEffectiveFromDateTime);
        settingEntity.setSettingValue(DisplayEffectiveToDateTimeSettingTypeKey, displayEffectiveToDateTime);
        settingEntity.setSettingValue(DisplayTopicSettingTypeKey, displayTopic);

        settingEntity.setSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        settingEntity.setSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        settingEntity.setSettingValue(FilterByTextSettingTypeKey, filterByText);
        settingEntity.setSettingValue(FilterByTopicSettingTypeKey, filterByTopic);
    }
    
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterByEnteredByUser = (String) filters.get("enteredByUser").getField();
        filterByEnteredOnDateTime = (String) filters.get("enteredOnDateTime").getField();
        filterByText = (String) filters.get("text").getField();
        filterByTopic = (String) filters.get("topic").getField();
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEnteredByUser = null;
        filterByEnteredOnDateTime = null;
        filterByText = null;
        filterByTopic = null;
    }
    
    public Boolean getDisplayEnteredByUser() {
        return displayEnteredByUser;
    }

    public void setDisplayEnteredByUser(Boolean displayEnteredByUser) {
        this.displayEnteredByUser = displayEnteredByUser;
    }

    public Boolean getDisplayEnteredOnDateTime() {
        return displayEnteredOnDateTime;
    }

    public void setDisplayEnteredOnDateTime(Boolean displayEnteredOnDateTime) {
        this.displayEnteredOnDateTime = displayEnteredOnDateTime;
    }

    public Boolean getDisplayEffectiveFromDateTime() {
        return displayEffectiveFromDateTime;
    }

    public void setDisplayEffectiveFromDateTime(Boolean displayEffectiveFromDateTime) {
        this.displayEffectiveFromDateTime = displayEffectiveFromDateTime;
    }

    public Boolean getDisplayEffectiveToDateTime() {
        return displayEffectiveToDateTime;
    }

    public void setDisplayEffectiveToDateTime(Boolean displayEffectiveToDateTime) {
        this.displayEffectiveToDateTime = displayEffectiveToDateTime;
    }

    public Boolean getDisplayAttachments() {
        return displayAttachments;
    }

    public void setDisplayAttachments(Boolean displayAttachments) {
        this.displayAttachments = displayAttachments;
    }

    public Boolean getDisplayTopic() {
        return displayTopic;
    }

    public void setDisplayTopic(Boolean displayTopic) {
        this.displayTopic = displayTopic;
    }

    public String getFilterByEnteredByUser() {
        return filterByEnteredByUser;
    }

    public void setFilterByEnteredByUser(String filterByEnteredByUser) {
        this.filterByEnteredByUser = filterByEnteredByUser;
    }

    public String getFilterByEnteredOnDateTime() {
        return filterByEnteredOnDateTime;
    }

    public void setFilterByEnteredOnDateTime(String filterByEnteredOnDateTime) {
        this.filterByEnteredOnDateTime = filterByEnteredOnDateTime;
    }

    public String getFilterByText() {
        return filterByText;
    }

    public void setFilterByText(String filterByText) {
        this.filterByText = filterByText;
    }

    public String getFilterByTopic() {
        return filterByTopic;
    }

    public void setFilterByTopic(String filterByTopic) {
        this.filterByTopic = filterByTopic;
    }
    
}
