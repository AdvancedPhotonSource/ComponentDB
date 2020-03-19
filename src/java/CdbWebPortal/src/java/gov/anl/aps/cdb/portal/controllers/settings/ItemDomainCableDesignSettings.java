/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;

/**
 *
 * @author djarosz
 */
public class ItemDomainCableDesignSettings extends ItemSettings<ItemDomainCableDesignController> {
    
    private static final String DisplayAlternateNameSettingTypeKey = "ItemDomainCableDesign.List.Display.ItemIdentifier1";
    private static final String DisplayDesignDescriptionSettingTypeKey = "ItemDomainCableDesign.List.Display.Description";
    private static final String DisplayLocationSettingTypeKey = "ItemDomainCableDesign.List.Display.Location";
    private static final String DisplayLocationDetailsSettingTypeKey = "ItemDomainCableDesign.List.Display.LocationDetails";
    private static final String DisplayProjectSettingTypeKey = "ItemDomainCableDesign.List.Display.ItemProject";
    private static final String DisplayIdSettingTypeKey = "ItemDomainCableDesign.List.Display.Id";
    private static final String DisplayOwnerUserSettingTypeKey = "ItemDomainCableDesign.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "ItemDomainCableDesign.List.Display.OwnerGroup";
    private static final String DisplayCreatedByUserSettingTypeKey = "ItemDomainCableDesign.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "ItemDomainCableDesign.List.Display.CreatedOnDateTime";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "ItemDomainCableDesign.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "ItemDomainCableDesign.List.Display.LastModifiedOnDateTime";
    private static final String DisplayEndpointsSettingTypeKey = "ItemDomainCableDesign.List.Display.Endpoints";
    private static final String DisplayCatalogItemSettingTypeKey = "ItemDomainCableDesign.List.Display.CatalogItem";
    private static final String DisplayVoltageSettingTypeKey = "ItemDomainCableDesign.List.Display.Voltage";
    private static final String DisplayLayingSettingTypeKey = "ItemDomainCableDesign.List.Display.Laying";
    private static final String DisplayTechnicalSystemSettingTypeKey = "ItemDomainCableDesign.List.Display.ItemCategory";

    private static final String FilterEndpointsSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.Endpoints";
    private static final String FilterCatalogItemSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.CatalogItem";
    private static final String FilterVoltageSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.Voltage";
    private static final String FilterLayingSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.Laying";

    private static final String AutoLoadListFilterValuesSettingTypeKey = "ItemDomainCableDesign.List.Load.FilterDataTable"; 

    protected Boolean displayEndpoints = null;
    protected Boolean displayCatalogItem = null;
    protected Boolean displayLocation = null;
    protected Boolean displayLocationDetails = null;
    
    // metadata fields
    protected Boolean voltageDisplay = null;
    protected Boolean layingDisplay = null;

    
    protected String filterEndpoints = null;
    protected String filterCatalogItem = null;
    
    // metadata fields
    protected String voltageFilter = null;
    protected String layingFilter = null;

    public ItemDomainCableDesignSettings(ItemDomainCableDesignController parentController) {
        super(parentController);
    }

    public Boolean getDisplayEndpoints() {
        return displayEndpoints;
    }

    public void setDisplayEndpoints(Boolean displayEndpoints) {
        this.displayEndpoints = displayEndpoints;
    }

    public Boolean getDisplayCatalogItem() {
        return displayCatalogItem;
    }

    public void setDisplayCatalogItem(Boolean displayCatalogItem) {
        this.displayCatalogItem = displayCatalogItem;
    }

    public Boolean getDisplayLocation() {
        return displayLocation;
    }

    public void setDisplayLocation(Boolean displayLocation) {
        this.displayLocation = displayLocation;
    }

    public Boolean getDisplayLocationDetails() {
        return displayLocationDetails;
    }

    public void setDisplayLocationDetails(Boolean displayLocationDetails) {
        this.displayLocationDetails = displayLocationDetails;
    }
    
    public boolean isVoltageDisplay() {
        return voltageDisplay;
    }

    public void setVoltageDisplay(boolean voltageDisplay) {
        this.voltageDisplay = voltageDisplay;
    }

    public boolean isLayingDisplay() {
        return layingDisplay;
    }

    public void setLayingDisplay(boolean layingDisplay) {
        this.layingDisplay = layingDisplay;
    }

    public String getFilterEndpoints() {
        return filterEndpoints;
    }

    public void setFilterEndpoints(String filterEndpoints) {
        this.filterEndpoints = filterEndpoints;
    }

    public String getFilterCatalogItem() {
        return filterCatalogItem;
    }

    public void setFilterCatalogItem(String filterCatalogItem) {
        this.filterCatalogItem = filterCatalogItem;
    }

    public String getVoltageFilter() {
        return voltageFilter;
    }

    public void setVoltageFilter(String voltageFilter) {
        this.voltageFilter = voltageFilter;
    }

    public String getLayingFilter() {
        return layingFilter;
    }

    public void setLayingFilter(String layingFilter) {
        this.layingFilter = layingFilter;
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);
        displayItemIdentifier1 = Boolean.parseBoolean(settingTypeMap.get(DisplayAlternateNameSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDesignDescriptionSettingTypeKey).getDefaultValue());
        displayItemProject = Boolean.parseBoolean(settingTypeMap.get(DisplayProjectSettingTypeKey).getDefaultValue());
        setDisplayLocation((Boolean) Boolean.parseBoolean(settingTypeMap.get(DisplayLocationSettingTypeKey).getDefaultValue()));
        setDisplayLocationDetails((Boolean) Boolean.parseBoolean(settingTypeMap.get(DisplayLocationDetailsSettingTypeKey).getDefaultValue()));
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        setDisplayItemCategory((Boolean) Boolean.parseBoolean(settingTypeMap.get(DisplayTechnicalSystemSettingTypeKey).getDefaultValue()));
        
        displayEndpoints = Boolean.parseBoolean(settingTypeMap.get(DisplayEndpointsSettingTypeKey).getDefaultValue());
        displayCatalogItem = Boolean.parseBoolean(settingTypeMap.get(DisplayCatalogItemSettingTypeKey).getDefaultValue());
        layingDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayLayingSettingTypeKey).getDefaultValue());
        voltageDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayVoltageSettingTypeKey).getDefaultValue());

        filterEndpoints = settingTypeMap.get(FilterEndpointsSettingTypeKey).getDefaultValue();
        filterCatalogItem = settingTypeMap.get(FilterCatalogItemSettingTypeKey).getDefaultValue();
        layingFilter = settingTypeMap.get(FilterLayingSettingTypeKey).getDefaultValue();
        voltageFilter = settingTypeMap.get(FilterVoltageSettingTypeKey).getDefaultValue();

        autoLoadListFilterValues = Boolean.parseBoolean(settingTypeMap.get(AutoLoadListFilterValuesSettingTypeKey).getDefaultValue()); 
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);
        displayItemIdentifier1 = settingEntity.getSettingValueAsBoolean(DisplayAlternateNameSettingTypeKey, displayItemIdentifier1); 
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDesignDescriptionSettingTypeKey, displayDescription);
        displayItemProject = settingEntity.getSettingValueAsBoolean(DisplayProjectSettingTypeKey, displayItemProject);
        displayLocation = settingEntity.getSettingValueAsBoolean(DisplayLocationSettingTypeKey, displayLocation);
        displayLocationDetails = settingEntity.getSettingValueAsBoolean(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        displayItemCategory = settingEntity.getSettingValueAsBoolean(DisplayTechnicalSystemSettingTypeKey, displayItemCategory);

        displayEndpoints = settingEntity.getSettingValueAsBoolean(DisplayEndpointsSettingTypeKey, displayEndpoints);
        displayCatalogItem = settingEntity.getSettingValueAsBoolean(DisplayCatalogItemSettingTypeKey, displayCatalogItem);
        layingDisplay = settingEntity.getSettingValueAsBoolean(DisplayLayingSettingTypeKey, layingDisplay);
        voltageDisplay = settingEntity.getSettingValueAsBoolean(DisplayVoltageSettingTypeKey, voltageDisplay);

        filterEndpoints = settingEntity.getSettingValueAsString(FilterEndpointsSettingTypeKey, filterEndpoints);
        filterCatalogItem = settingEntity.getSettingValueAsString(FilterCatalogItemSettingTypeKey, filterCatalogItem);
        layingFilter = settingEntity.getSettingValueAsString(FilterLayingSettingTypeKey, layingFilter);
        voltageFilter = settingEntity.getSettingValueAsString(FilterVoltageSettingTypeKey, voltageFilter);

        autoLoadListFilterValues = settingEntity.getSettingValueAsBoolean(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues); 
    }
    
    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        super.saveSettingsForSessionSettingEntity(settingEntity);
        settingEntity.setSettingValue(DisplayAlternateNameSettingTypeKey, displayItemIdentifier1);
        settingEntity.setSettingValue(DisplayDesignDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayProjectSettingTypeKey, displayItemProject);
        settingEntity.setSettingValue(DisplayLocationSettingTypeKey, displayLocation);
        settingEntity.setSettingValue(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        settingEntity.setSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        settingEntity.setSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        settingEntity.setSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        settingEntity.setSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        settingEntity.setSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        settingEntity.setSettingValue(DisplayTechnicalSystemSettingTypeKey, displayItemCategory);

        settingEntity.setSettingValue(DisplayEndpointsSettingTypeKey, displayEndpoints);
        settingEntity.setSettingValue(DisplayCatalogItemSettingTypeKey, displayCatalogItem);
        settingEntity.setSettingValue(DisplayLayingSettingTypeKey, layingDisplay);
        settingEntity.setSettingValue(DisplayVoltageSettingTypeKey, voltageDisplay);

        settingEntity.setSettingValue(FilterEndpointsSettingTypeKey, filterEndpoints);
        settingEntity.setSettingValue(FilterCatalogItemSettingTypeKey, filterCatalogItem);
        settingEntity.setSettingValue(FilterLayingSettingTypeKey, layingFilter);
        settingEntity.setSettingValue(FilterVoltageSettingTypeKey, voltageFilter);

        settingEntity.setSettingValue(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues);
    }
}
