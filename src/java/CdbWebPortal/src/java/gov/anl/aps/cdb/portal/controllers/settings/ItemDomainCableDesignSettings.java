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
    private static final String DisplayExternalCableNameSettingTypeKey = "ItemDomainCableDesign.List.Display.ExternalCableName";
    private static final String DisplayImportCableIdSettingTypeKey = "ItemDomainCableDesign.List.Display.ImportCableId";
    private static final String DisplayAlternateCableIdSettingTypeKey = "ItemDomainCableDesign.List.Display.AlternateCableId";
    private static final String DisplayLegacyQrIdSettingTypeKey = "ItemDomainCableDesign.List.Display.LegacyQrId";
    private static final String DisplayVoltageSettingTypeKey = "ItemDomainCableDesign.List.Display.Voltage";
    private static final String DisplayLayingSettingTypeKey = "ItemDomainCableDesign.List.Display.Laying";
    private static final String DisplayTechnicalSystemSettingTypeKey = "ItemDomainCableDesign.List.Display.ItemCategory";

    private static final String FilterEndpointsSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.Endpoints";
    private static final String FilterCatalogItemSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.CatalogItem";
    private static final String FilterExternalCableNameSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.ExternalCableName";
    private static final String FilterImportCableIdSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.ImportCableId";
    private static final String FilterLegacyQrIdSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.LegacyQrId";
    private static final String FilterAlternateCableIdSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.AlternateCableId";
    private static final String FilterVoltageSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.Voltage";
    private static final String FilterLayingSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.Laying";

    private static final String AutoLoadListFilterValuesSettingTypeKey = "ItemDomainCableDesign.List.Load.FilterDataTable"; 

    private static final String DisplayRoutedLengthSettingTypeKey = "ItemDomainCableDesign.List.Display.RoutedLength";
    private static final String FilterRoutedLengthSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.RoutedLength";

    private static final String DisplayRouteSettingTypeKey = "ItemDomainCableDesign.List.Display.Route";
    private static final String FilterRouteSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.Route";

    private static final String DisplayNotesSettingTypeKey = "ItemDomainCableDesign.List.Display.Notes";
    private static final String FilterNotesSettingTypeKey = "ItemDomainCableDesign.List.FilterBy.Notes";

    protected Boolean displayEndpoints = null;
    protected Boolean displayCatalogItem = null;
    protected Boolean displayLocation = null;
    protected Boolean displayLocationDetails = null;
    
    // metadata fields
    protected Boolean voltageDisplay = null;
    protected Boolean layingDisplay = null;
    protected Boolean externalCableNameDisplay = null;
    protected Boolean importCableIdDisplay = null;
    protected Boolean alternateCableIdDisplay = null;
    protected Boolean legacyQrIdDisplay = null;
    protected Boolean endpoint1DescriptionDisplay = null;
    protected Boolean endpoint2DescriptionDisplay = null;
    
    protected String filterEndpoints = null;
    protected String filterCatalogItem = null;
    
    // metadata fields
    protected String voltageFilter = null;
    protected String layingFilter = null;
    protected String externalCableNameFilter = null;
    protected String importCableIdFilter = null;
    protected String alternateCableIdFilter = null;
    protected String legacyQrIdFilter = null;
    protected Boolean routedLengthDisplay = null;
    protected String routedLengthFilter = null;
    protected Boolean routeDisplay = null;
    protected String routeFilter = null;
    protected Boolean notesDisplay = null;
    protected String notesFilter = null;

    public ItemDomainCableDesignSettings(ItemDomainCableDesignController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 25;
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

    public boolean isExternalCableNameDisplay() {
        return externalCableNameDisplay;
    }

    public void setExternalCableNameDisplay(boolean externalCableNameDisplay) {
        this.externalCableNameDisplay = externalCableNameDisplay;
    }

    public boolean isImportCableIdDisplay() {
        return importCableIdDisplay;
    }

    public void setImportCableIdDisplay(boolean importCableIdDisplay) {
        this.importCableIdDisplay = importCableIdDisplay;
    }

    public boolean isAlternateCableIdDisplay() {
        return alternateCableIdDisplay;
    }

    public void setAlternateCableIdDisplay(boolean alternateCableIdDisplay) {
        this.alternateCableIdDisplay = alternateCableIdDisplay;
    }

    public boolean isLegacyQrIdDisplay() {
        return legacyQrIdDisplay;
    }

    public void setLegacyQrIdDisplay(boolean legacyQrIdDisplay) {
        this.legacyQrIdDisplay = legacyQrIdDisplay;
    }

    public boolean isEndpoint1DescriptionDisplay() {
        return endpoint1DescriptionDisplay;
    }

    public void setEndpoint1DescriptionDisplay(boolean endpoint1DescriptionDisplay) {
        this.endpoint1DescriptionDisplay = endpoint1DescriptionDisplay;
    }

    public boolean isEndpoint2DescriptionDisplay() {
        return endpoint2DescriptionDisplay;
    }

    public void setEndpoint2DescriptionDisplay(boolean endpoint2DescriptionDisplay) {
        this.endpoint2DescriptionDisplay = endpoint2DescriptionDisplay;
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

    public String getExternalCableNameFilter() {
        return externalCableNameFilter;
    }

    public void setExternalCableNameFilter(String externalCableNameFilter) {
        this.externalCableNameFilter = externalCableNameFilter;
    }

    public String getImportCableIdFilter() {
        return importCableIdFilter;
    }

    public void setImportCableIdFilter(String importCableIdFilter) {
        this.importCableIdFilter = importCableIdFilter;
    }

    public String getAlternateCableIdFilter() {
        return alternateCableIdFilter;
    }

    public void setAlternateCableIdFilter(String alternateCableIdFilter) {
        this.alternateCableIdFilter = alternateCableIdFilter;
    }

    public String getLegacyQrIdFilter() {
        return legacyQrIdFilter;
    }

    public void setLegacyQrIdFilter(String legacyQrIdFilter) {
        this.legacyQrIdFilter = legacyQrIdFilter;
    }

    public Boolean getDisplayInstalledQrId() {
        return false;
    }

    public boolean isRoutedLengthDisplay() {
        return routedLengthDisplay;
    }

    public void setRoutedLengthDisplay(boolean routedLengthDisplay) {
        this.routedLengthDisplay = routedLengthDisplay;
    }

    public String getRoutedLengthFilter() {
        return routedLengthFilter;
    }

    public void setRoutedLengthFilter(String routedLengthFilter) {
        this.routedLengthFilter = routedLengthFilter;
    }

    public boolean isRouteDisplay() {
        return routeDisplay;
    }

    public void setRouteDisplay(boolean routeDisplay) {
        this.routeDisplay = routeDisplay;
    }

    public String getRouteFilter() {
        return routeFilter;
    }

    public void setRouteFilter(String routeFilter) {
        this.routeFilter = routeFilter;
    }

    public boolean isNotesDisplay() {
        return notesDisplay;
    }

    public void setNotesDisplay(boolean notesDisplay) {
        this.notesDisplay = notesDisplay;
    }

    public String getNotesFilter() {
        return notesFilter;
    }

    public void setNotesFilter(String notesFilter) {
        this.notesFilter = notesFilter;
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
        externalCableNameDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayExternalCableNameSettingTypeKey).getDefaultValue());
        importCableIdDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayImportCableIdSettingTypeKey).getDefaultValue());
        alternateCableIdDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayAlternateCableIdSettingTypeKey).getDefaultValue());
        legacyQrIdDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayLegacyQrIdSettingTypeKey).getDefaultValue());

        filterEndpoints = settingTypeMap.get(FilterEndpointsSettingTypeKey).getDefaultValue();
        filterCatalogItem = settingTypeMap.get(FilterCatalogItemSettingTypeKey).getDefaultValue();
        layingFilter = settingTypeMap.get(FilterLayingSettingTypeKey).getDefaultValue();
        voltageFilter = settingTypeMap.get(FilterVoltageSettingTypeKey).getDefaultValue();
        externalCableNameFilter = settingTypeMap.get(FilterExternalCableNameSettingTypeKey).getDefaultValue();
        importCableIdFilter = settingTypeMap.get(FilterImportCableIdSettingTypeKey).getDefaultValue();
        alternateCableIdFilter = settingTypeMap.get(FilterAlternateCableIdSettingTypeKey).getDefaultValue();
        legacyQrIdFilter = settingTypeMap.get(FilterLegacyQrIdSettingTypeKey).getDefaultValue();

        routedLengthDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayRoutedLengthSettingTypeKey).getDefaultValue());
        routedLengthFilter = settingTypeMap.get(FilterRoutedLengthSettingTypeKey).getDefaultValue();
        routeDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayRouteSettingTypeKey).getDefaultValue());
        routeFilter = settingTypeMap.get(FilterRouteSettingTypeKey).getDefaultValue();
        notesDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayNotesSettingTypeKey).getDefaultValue());
        notesFilter = settingTypeMap.get(FilterNotesSettingTypeKey).getDefaultValue();

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
        externalCableNameDisplay = settingEntity.getSettingValueAsBoolean(DisplayExternalCableNameSettingTypeKey, externalCableNameDisplay);
        importCableIdDisplay = settingEntity.getSettingValueAsBoolean(DisplayImportCableIdSettingTypeKey, importCableIdDisplay);
        alternateCableIdDisplay = settingEntity.getSettingValueAsBoolean(DisplayAlternateCableIdSettingTypeKey, alternateCableIdDisplay);
        legacyQrIdDisplay = settingEntity.getSettingValueAsBoolean(DisplayLegacyQrIdSettingTypeKey, legacyQrIdDisplay);

        filterEndpoints = settingEntity.getSettingValueAsString(FilterEndpointsSettingTypeKey, filterEndpoints);
        filterCatalogItem = settingEntity.getSettingValueAsString(FilterCatalogItemSettingTypeKey, filterCatalogItem);
        layingFilter = settingEntity.getSettingValueAsString(FilterLayingSettingTypeKey, layingFilter);
        voltageFilter = settingEntity.getSettingValueAsString(FilterVoltageSettingTypeKey, voltageFilter);
        externalCableNameFilter = settingEntity.getSettingValueAsString(FilterExternalCableNameSettingTypeKey, externalCableNameFilter);
        importCableIdFilter = settingEntity.getSettingValueAsString(FilterImportCableIdSettingTypeKey, importCableIdFilter);
        alternateCableIdFilter = settingEntity.getSettingValueAsString(FilterAlternateCableIdSettingTypeKey, alternateCableIdFilter);
        legacyQrIdFilter = settingEntity.getSettingValueAsString(FilterLegacyQrIdSettingTypeKey, legacyQrIdFilter);

        routedLengthDisplay = settingEntity.getSettingValueAsBoolean(DisplayRoutedLengthSettingTypeKey, routedLengthDisplay);
        routedLengthFilter = settingEntity.getSettingValueAsString(FilterRoutedLengthSettingTypeKey, routedLengthFilter);
        routeDisplay = settingEntity.getSettingValueAsBoolean(DisplayRouteSettingTypeKey, routeDisplay);
        routeFilter = settingEntity.getSettingValueAsString(FilterRouteSettingTypeKey, routeFilter);
        notesDisplay = settingEntity.getSettingValueAsBoolean(DisplayNotesSettingTypeKey, notesDisplay);
        notesFilter = settingEntity.getSettingValueAsString(FilterNotesSettingTypeKey, notesFilter);

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
        settingEntity.setSettingValue(DisplayExternalCableNameSettingTypeKey, externalCableNameDisplay);
        settingEntity.setSettingValue(DisplayImportCableIdSettingTypeKey, importCableIdDisplay);
        settingEntity.setSettingValue(DisplayAlternateCableIdSettingTypeKey, alternateCableIdDisplay);
        settingEntity.setSettingValue(DisplayLegacyQrIdSettingTypeKey, legacyQrIdDisplay);

        settingEntity.setSettingValue(FilterEndpointsSettingTypeKey, filterEndpoints);
        settingEntity.setSettingValue(FilterCatalogItemSettingTypeKey, filterCatalogItem);
        settingEntity.setSettingValue(FilterLayingSettingTypeKey, layingFilter);
        settingEntity.setSettingValue(FilterVoltageSettingTypeKey, voltageFilter);
        settingEntity.setSettingValue(FilterExternalCableNameSettingTypeKey, externalCableNameFilter);
        settingEntity.setSettingValue(FilterImportCableIdSettingTypeKey, importCableIdFilter);
        settingEntity.setSettingValue(FilterAlternateCableIdSettingTypeKey, alternateCableIdFilter);
        settingEntity.setSettingValue(FilterLegacyQrIdSettingTypeKey, legacyQrIdFilter);

        settingEntity.setSettingValue(DisplayRoutedLengthSettingTypeKey, routedLengthDisplay);
        settingEntity.setSettingValue(FilterRoutedLengthSettingTypeKey, routedLengthFilter);
        settingEntity.setSettingValue(DisplayRouteSettingTypeKey, routeDisplay);
        settingEntity.setSettingValue(FilterRouteSettingTypeKey, routeFilter);
        settingEntity.setSettingValue(DisplayNotesSettingTypeKey, notesDisplay);
        settingEntity.setSettingValue(FilterNotesSettingTypeKey, notesFilter);

        settingEntity.setSettingValue(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues);
    }
}
