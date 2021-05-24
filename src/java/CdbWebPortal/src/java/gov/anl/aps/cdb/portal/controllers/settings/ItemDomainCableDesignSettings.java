/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
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
    
    private static final String DisplayDescriptionEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.Display.DescriptionEndpoint1";
    private static final String FilterDescriptionEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.DescriptionEndpoint1";
    private static final String DisplayRouteEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.Display.RouteEndpoint1";
    private static final String FilterRouteEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.RouteEndpoint1";
    private static final String DisplayEndLengthEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.Display.EndLengthEndpoint1";
    private static final String FilterEndLengthEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.EndLengthEndpoint1";
    private static final String DisplayTerminationEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.Display.TerminationEndpoint1";
    private static final String FilterTerminationEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.TerminationEndpoint1";
    private static final String DisplayPinlistEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.Display.PinlistEndpoint1";
    private static final String FilterPinlistEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.PinlistEndpoint1";
    private static final String DisplayNotesEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.Display.NotesEndpoint1";
    private static final String FilterNotesEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.NotesEndpoint1";
    private static final String DisplayDrawingEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.Display.DrawingEndpoint1";
    private static final String FilterDrawingEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.DrawingEndpoint1";

    private static final String DisplayDescriptionEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.Display.DescriptionEndpoint2";
    private static final String FilterDescriptionEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.DescriptionEndpoint2";
    private static final String DisplayRouteEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.Display.RouteEndpoint2";
    private static final String FilterRouteEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.RouteEndpoint2";
    private static final String DisplayEndLengthEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.Display.EndLengthEndpoint2";
    private static final String FilterEndLengthEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.EndLengthEndpoint2";
    private static final String DisplayTerminationEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.Display.TerminationEndpoint2";
    private static final String FilterTerminationEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.TerminationEndpoint2";
    private static final String DisplayPinlistEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.Display.PinlistEndpoint2";
    private static final String FilterPinlistEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.PinlistEndpoint2";
    private static final String DisplayNotesEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.Display.NotesEndpoint2";
    private static final String FilterNotesEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.NotesEndpoint2";
    private static final String DisplayDrawingEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.Display.DrawingEndpoint2";
    private static final String FilterDrawingEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.DrawingEndpoint2";

    private static final String DisplayDeviceEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.Display.DeviceEndpoint1";
    private static final String FilterDeviceEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.DeviceEndpoint1";
    private static final String DisplayPortEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.Display.PortEndpoint1";
    private static final String FilterPortEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.PortEndpoint1";
    private static final String DisplayConnectorEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.Display.ConnectorEndpoint1";
    private static final String FilterConnectorEndpoint1SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.ConnectorEndpoint1";
    private static final String DisplayDeviceEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.Display.DeviceEndpoint2";
    private static final String FilterDeviceEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.DeviceEndpoint2";
    private static final String DisplayPortEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.Display.PortEndpoint2";
    private static final String FilterPortEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.PortEndpoint2";
    private static final String DisplayConnectorEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.Display.ConnectorEndpoint2";
    private static final String FilterConnectorEndpoint2SettingTypeKey = "ItemDomainCableDesign.List.FilterBy.ConnectorEndpoint2";

    protected Boolean displayEndpoints = null;
    protected Boolean displayCatalogItem = null;
    protected Boolean displayLocation = null;
    protected Boolean displayLocationDetails = null;
    
    // cable metadata fields
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
    
    // connection metadata fields
    protected Boolean descriptionEndpoint1Display = null;
    protected String descriptionEndpoint1Filter = null;
    protected Boolean routeEndpoint1Display = null;
    protected String routeEndpoint1Filter = null;
    protected Boolean endLengthEndpoint1Display = null;
    protected String endLengthEndpoint1Filter = null;
    protected Boolean terminationEndpoint1Display = null;
    protected String terminationEndpoint1Filter = null;
    protected Boolean pinlistEndpoint1Display = null;
    protected String pinlistEndpoint1Filter = null;
    protected Boolean notesEndpoint1Display = null;
    protected String notesEndpoint1Filter = null;
    protected Boolean drawingEndpoint1Display = null;
    protected String drawingEndpoint1Filter = null;
    protected Boolean descriptionEndpoint2Display = null;
    protected String descriptionEndpoint2Filter = null;
    protected Boolean routeEndpoint2Display = null;
    protected String routeEndpoint2Filter = null;
    protected Boolean endLengthEndpoint2Display = null;
    protected String endLengthEndpoint2Filter = null;
    protected Boolean terminationEndpoint2Display = null;
    protected String terminationEndpoint2Filter = null;
    protected Boolean pinlistEndpoint2Display = null;
    protected String pinlistEndpoint2Filter = null;
    protected Boolean notesEndpoint2Display = null;
    protected String notesEndpoint2Filter = null;
    protected Boolean drawingEndpoint2Display = null;
    protected String drawingEndpoint2Filter = null;
    
    // endpoint connection details
    protected Boolean deviceEndpoint1Display = null;
    protected String deviceEndpoint1Filter = null;
    protected Boolean portEndpoint1Display = null;
    protected String portEndpoint1Filter = null;
    protected Boolean connectorEndpoint1Display = null;
    protected String connectorEndpoint1Filter = null;
    protected Boolean deviceEndpoint2Display = null;
    protected String deviceEndpoint2Filter = null;
    protected Boolean portEndpoint2Display = null;
    protected String portEndpoint2Filter = null;
    protected Boolean connectorEndpoint2Display = null;
    protected String connectorEndpoint2Filter = null;

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

    public boolean isEndpoint1DescriptionDisplay() {
        return descriptionEndpoint1Display;
    }

    public void setEndpoint1DescriptionDisplay(boolean descriptionEndpoint1Display) {
        this.descriptionEndpoint1Display = descriptionEndpoint1Display;
    }

    public String getEndpoint1DescriptionFilter() {
        return descriptionEndpoint1Filter;
    }

    public void setEndpoint1DescriptionFilter(String descriptionEndpoint1Filter) {
        this.descriptionEndpoint1Filter = descriptionEndpoint1Filter;
    }

    public boolean isEndpoint1RouteDisplay() {
        return routeEndpoint1Display;
    }

    public void setEndpoint1RouteDisplay(boolean routeEndpoint1Display) {
        this.routeEndpoint1Display = routeEndpoint1Display;
    }

    public String getEndpoint1RouteFilter() {
        return routeEndpoint1Filter;
    }

    public void setEndpoint1RouteFilter(String routeEndpoint1Filter) {
        this.routeEndpoint1Filter = routeEndpoint1Filter;
    }

    public boolean isEndpoint1EndLengthDisplay() {
        return endLengthEndpoint1Display;
    }

    public void setEndpoint1EndLengthDisplay(boolean endLengthEndpoint1Display) {
        this.endLengthEndpoint1Display = endLengthEndpoint1Display;
    }

    public String getEndpoint1EndLengthFilter() {
        return endLengthEndpoint1Filter;
    }

    public void setEndpoint1EndLengthFilter(String endLengthEndpoint1Filter) {
        this.endLengthEndpoint1Filter = endLengthEndpoint1Filter;
    }

    public boolean isEndpoint1TerminationDisplay() {
        return terminationEndpoint1Display;
    }

    public void setEndpoint1TerminationDisplay(boolean terminationEndpoint1Display) {
        this.terminationEndpoint1Display = terminationEndpoint1Display;
    }

    public String getEndpoint1TerminationFilter() {
        return terminationEndpoint1Filter;
    }

    public void setEndpoint1TerminationFilter(String terminationEndpoint1Filter) {
        this.terminationEndpoint1Filter = terminationEndpoint1Filter;
    }

    public boolean isEndpoint1PinlistDisplay() {
        return pinlistEndpoint1Display;
    }

    public void setEndpoint1PinlistDisplay(boolean pinlistEndpoint1Display) {
        this.pinlistEndpoint1Display = pinlistEndpoint1Display;
    }

    public String getEndpoint1PinlistFilter() {
        return pinlistEndpoint1Filter;
    }

    public void setEndpoint1PinlistFilter(String pinlistEndpoint1Filter) {
        this.pinlistEndpoint1Filter = pinlistEndpoint1Filter;
    }

    public boolean isEndpoint1NotesDisplay() {
        return notesEndpoint1Display;
    }

    public void setEndpoint1NotesDisplay(boolean notesEndpoint1Display) {
        this.notesEndpoint1Display = notesEndpoint1Display;
    }

    public String getEndpoint1NotesFilter() {
        return notesEndpoint1Filter;
    }

    public void setEndpoint1NotesFilter(String notesEndpoint1Filter) {
        this.notesEndpoint1Filter = notesEndpoint1Filter;
    }

    public boolean isEndpoint1DrawingDisplay() {
        return drawingEndpoint1Display;
    }

    public void setEndpoint1DrawingDisplay(boolean drawingEndpoint1Display) {
        this.drawingEndpoint1Display = drawingEndpoint1Display;
    }

    public String getEndpoint1DrawingFilter() {
        return drawingEndpoint1Filter;
    }

    public void setEndpoint1DrawingFilter(String drawingEndpoint1Filter) {
        this.drawingEndpoint1Filter = drawingEndpoint1Filter;
    }

    public boolean isEndpoint2DescriptionDisplay() {
        return descriptionEndpoint2Display;
    }

    public void setEndpoint2DescriptionDisplay(boolean descriptionEndpoint2Display) {
        this.descriptionEndpoint2Display = descriptionEndpoint2Display;
    }

    public String getEndpoint2DescriptionFilter() {
        return descriptionEndpoint2Filter;
    }

    public void setEndpoint2DescriptionFilter(String descriptionEndpoint2Filter) {
        this.descriptionEndpoint2Filter = descriptionEndpoint2Filter;
    }

    public boolean isEndpoint2RouteDisplay() {
        return routeEndpoint2Display;
    }

    public void setEndpoint2RouteDisplay(boolean routeEndpoint2Display) {
        this.routeEndpoint2Display = routeEndpoint2Display;
    }

    public String getEndpoint2RouteFilter() {
        return routeEndpoint2Filter;
    }

    public void setEndpoint2RouteFilter(String routeEndpoint2Filter) {
        this.routeEndpoint2Filter = routeEndpoint2Filter;
    }

    public boolean isEndpoint2EndLengthDisplay() {
        return endLengthEndpoint2Display;
    }

    public void setEndpoint2EndLengthDisplay(boolean endLengthEndpoint2Display) {
        this.endLengthEndpoint2Display = endLengthEndpoint2Display;
    }

    public String getEndpoint2EndLengthFilter() {
        return endLengthEndpoint2Filter;
    }

    public void setEndpoint2EndLengthFilter(String endLengthEndpoint2Filter) {
        this.endLengthEndpoint2Filter = endLengthEndpoint2Filter;
    }

    public boolean isEndpoint2TerminationDisplay() {
        return terminationEndpoint2Display;
    }

    public void setEndpoint2TerminationDisplay(boolean terminationEndpoint2Display) {
        this.terminationEndpoint2Display = terminationEndpoint2Display;
    }

    public String getEndpoint2TerminationFilter() {
        return terminationEndpoint2Filter;
    }

    public void setEndpoint2TerminationFilter(String terminationEndpoint2Filter) {
        this.terminationEndpoint2Filter = terminationEndpoint2Filter;
    }

    public boolean isEndpoint2PinlistDisplay() {
        return pinlistEndpoint2Display;
    }

    public void setEndpoint2PinlistDisplay(boolean pinlistEndpoint2Display) {
        this.pinlistEndpoint2Display = pinlistEndpoint2Display;
    }

    public String getEndpoint2PinlistFilter() {
        return pinlistEndpoint2Filter;
    }

    public void setEndpoint2PinlistFilter(String pinlistEndpoint2Filter) {
        this.pinlistEndpoint2Filter = pinlistEndpoint2Filter;
    }

    public boolean isEndpoint2NotesDisplay() {
        return notesEndpoint2Display;
    }

    public void setEndpoint2NotesDisplay(boolean notesEndpoint2Display) {
        this.notesEndpoint2Display = notesEndpoint2Display;
    }

    public String getEndpoint2NotesFilter() {
        return notesEndpoint2Filter;
    }

    public void setEndpoint2NotesFilter(String notesEndpoint2Filter) {
        this.notesEndpoint2Filter = notesEndpoint2Filter;
    }

    public boolean isEndpoint2DrawingDisplay() {
        return drawingEndpoint2Display;
    }

    public void setEndpoint2DrawingDisplay(boolean drawingEndpoint2Display) {
        this.drawingEndpoint2Display = drawingEndpoint2Display;
    }

    public String getEndpoint2DrawingFilter() {
        return drawingEndpoint2Filter;
    }

    public void setEndpoint2DrawingFilter(String drawingEndpoint2Filter) {
        this.drawingEndpoint2Filter = drawingEndpoint2Filter;
    }

    public boolean isDeviceEndpoint1Display() {
        return deviceEndpoint1Display;
    }

    public void setDeviceEndpoint1Display(boolean deviceEndpoint1Display) {
        this.deviceEndpoint1Display = deviceEndpoint1Display;
    }

    public String getDeviceEndpoint1Filter() {
        return deviceEndpoint1Filter;
    }

    public void setDeviceEndpoint1Filter(String deviceEndpoint1Filter) {
        this.deviceEndpoint1Filter = deviceEndpoint1Filter;
    }

    public boolean isPortEndpoint1Display() {
        return portEndpoint1Display;
    }

    public void setPortEndpoint1Display(boolean portEndpoint1Display) {
        this.portEndpoint1Display = portEndpoint1Display;
    }

    public String getPortEndpoint1Filter() {
        return portEndpoint1Filter;
    }

    public void setPortEndpoint1Filter(String portEndpoint1Filter) {
        this.portEndpoint1Filter = portEndpoint1Filter;
    }

    public boolean isConnectorEndpoint1Display() {
        return connectorEndpoint1Display;
    }

    public void setConnectorEndpoint1Display(boolean connectorEndpoint1Display) {
        this.connectorEndpoint1Display = connectorEndpoint1Display;
    }

    public String getConnectorEndpoint1Filter() {
        return connectorEndpoint1Filter;
    }

    public void setConnectorEndpoint1Filter(String connectorEndpoint1Filter) {
        this.connectorEndpoint1Filter = connectorEndpoint1Filter;
    }

    public boolean isDeviceEndpoint2Display() {
        return deviceEndpoint2Display;
    }

    public void setDeviceEndpoint2Display(boolean deviceEndpoint2Display) {
        this.deviceEndpoint2Display = deviceEndpoint2Display;
    }

    public String getDeviceEndpoint2Filter() {
        return deviceEndpoint2Filter;
    }

    public void setDeviceEndpoint2Filter(String deviceEndpoint2Filter) {
        this.deviceEndpoint2Filter = deviceEndpoint2Filter;
    }

    public boolean isPortEndpoint2Display() {
        return portEndpoint2Display;
    }

    public void setPortEndpoint2Display(boolean portEndpoint2Display) {
        this.portEndpoint2Display = portEndpoint2Display;
    }

    public String getPortEndpoint2Filter() {
        return portEndpoint2Filter;
    }

    public void setPortEndpoint2Filter(String portEndpoint2Filter) {
        this.portEndpoint2Filter = portEndpoint2Filter;
    }

    public boolean isConnectorEndpoint2Display() {
        return connectorEndpoint2Display;
    }

    public void setConnectorEndpoint2Display(boolean connectorEndpoint2Display) {
        this.connectorEndpoint2Display = connectorEndpoint2Display;
    }

    public String getConnectorEndpoint2Filter() {
        return connectorEndpoint2Filter;
    }

    public void setConnectorEndpoint2Filter(String connectorEndpoint2Filter) {
        this.connectorEndpoint2Filter = connectorEndpoint2Filter;
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

        descriptionEndpoint1Display = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionEndpoint1SettingTypeKey).getDefaultValue());
        descriptionEndpoint1Filter = settingTypeMap.get(FilterDescriptionEndpoint1SettingTypeKey).getDefaultValue();
        routeEndpoint1Display = Boolean.parseBoolean(settingTypeMap.get(DisplayRouteEndpoint1SettingTypeKey).getDefaultValue());
        routeEndpoint1Filter = settingTypeMap.get(FilterRouteEndpoint1SettingTypeKey).getDefaultValue();
        endLengthEndpoint1Display = Boolean.parseBoolean(settingTypeMap.get(DisplayEndLengthEndpoint1SettingTypeKey).getDefaultValue());
        endLengthEndpoint1Filter = settingTypeMap.get(FilterEndLengthEndpoint1SettingTypeKey).getDefaultValue();
        terminationEndpoint1Display = Boolean.parseBoolean(settingTypeMap.get(DisplayTerminationEndpoint1SettingTypeKey).getDefaultValue());
        terminationEndpoint1Filter = settingTypeMap.get(FilterTerminationEndpoint1SettingTypeKey).getDefaultValue();
        pinlistEndpoint1Display = Boolean.parseBoolean(settingTypeMap.get(DisplayPinlistEndpoint1SettingTypeKey).getDefaultValue());
        pinlistEndpoint1Filter = settingTypeMap.get(FilterPinlistEndpoint1SettingTypeKey).getDefaultValue();
        notesEndpoint1Display = Boolean.parseBoolean(settingTypeMap.get(DisplayNotesEndpoint1SettingTypeKey).getDefaultValue());
        notesEndpoint1Filter = settingTypeMap.get(FilterNotesEndpoint1SettingTypeKey).getDefaultValue();
        drawingEndpoint1Display = Boolean.parseBoolean(settingTypeMap.get(DisplayDrawingEndpoint1SettingTypeKey).getDefaultValue());
        drawingEndpoint1Filter = settingTypeMap.get(FilterDrawingEndpoint1SettingTypeKey).getDefaultValue();
        
        descriptionEndpoint2Display = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionEndpoint2SettingTypeKey).getDefaultValue());
        descriptionEndpoint2Filter = settingTypeMap.get(FilterDescriptionEndpoint2SettingTypeKey).getDefaultValue();
        routeEndpoint2Display = Boolean.parseBoolean(settingTypeMap.get(DisplayRouteEndpoint2SettingTypeKey).getDefaultValue());
        routeEndpoint2Filter = settingTypeMap.get(FilterRouteEndpoint2SettingTypeKey).getDefaultValue();
        endLengthEndpoint2Display = Boolean.parseBoolean(settingTypeMap.get(DisplayEndLengthEndpoint2SettingTypeKey).getDefaultValue());
        endLengthEndpoint2Filter = settingTypeMap.get(FilterEndLengthEndpoint2SettingTypeKey).getDefaultValue();
        terminationEndpoint2Display = Boolean.parseBoolean(settingTypeMap.get(DisplayTerminationEndpoint2SettingTypeKey).getDefaultValue());
        terminationEndpoint2Filter = settingTypeMap.get(FilterTerminationEndpoint2SettingTypeKey).getDefaultValue();
        pinlistEndpoint2Display = Boolean.parseBoolean(settingTypeMap.get(DisplayPinlistEndpoint2SettingTypeKey).getDefaultValue());
        pinlistEndpoint2Filter = settingTypeMap.get(FilterPinlistEndpoint2SettingTypeKey).getDefaultValue();
        notesEndpoint2Display = Boolean.parseBoolean(settingTypeMap.get(DisplayNotesEndpoint2SettingTypeKey).getDefaultValue());
        notesEndpoint2Filter = settingTypeMap.get(FilterNotesEndpoint2SettingTypeKey).getDefaultValue();
        drawingEndpoint2Display = Boolean.parseBoolean(settingTypeMap.get(DisplayDrawingEndpoint2SettingTypeKey).getDefaultValue());
        drawingEndpoint2Filter = settingTypeMap.get(FilterDrawingEndpoint2SettingTypeKey).getDefaultValue();
        
        deviceEndpoint1Display = Boolean.parseBoolean(settingTypeMap.get(DisplayDeviceEndpoint1SettingTypeKey).getDefaultValue());
        deviceEndpoint1Filter = settingTypeMap.get(FilterDeviceEndpoint1SettingTypeKey).getDefaultValue();
        portEndpoint1Display = Boolean.parseBoolean(settingTypeMap.get(DisplayPortEndpoint1SettingTypeKey).getDefaultValue());
        portEndpoint1Filter = settingTypeMap.get(FilterPortEndpoint1SettingTypeKey).getDefaultValue();
        connectorEndpoint1Display = Boolean.parseBoolean(settingTypeMap.get(DisplayConnectorEndpoint1SettingTypeKey).getDefaultValue());
        connectorEndpoint1Filter = settingTypeMap.get(FilterConnectorEndpoint1SettingTypeKey).getDefaultValue();

        deviceEndpoint2Display = Boolean.parseBoolean(settingTypeMap.get(DisplayDeviceEndpoint2SettingTypeKey).getDefaultValue());
        deviceEndpoint2Filter = settingTypeMap.get(FilterDeviceEndpoint2SettingTypeKey).getDefaultValue();
        portEndpoint2Display = Boolean.parseBoolean(settingTypeMap.get(DisplayPortEndpoint2SettingTypeKey).getDefaultValue());
        portEndpoint2Filter = settingTypeMap.get(FilterPortEndpoint2SettingTypeKey).getDefaultValue();
        connectorEndpoint2Display = Boolean.parseBoolean(settingTypeMap.get(DisplayConnectorEndpoint2SettingTypeKey).getDefaultValue());
        connectorEndpoint2Filter = settingTypeMap.get(FilterConnectorEndpoint2SettingTypeKey).getDefaultValue();

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

        descriptionEndpoint1Display = settingEntity.getSettingValueAsBoolean(DisplayDescriptionEndpoint1SettingTypeKey, descriptionEndpoint1Display);
        descriptionEndpoint1Filter = settingEntity.getSettingValueAsString(FilterDescriptionEndpoint1SettingTypeKey, descriptionEndpoint1Filter);
        routeEndpoint1Display = settingEntity.getSettingValueAsBoolean(DisplayRouteEndpoint1SettingTypeKey, routeEndpoint1Display);
        routeEndpoint1Filter = settingEntity.getSettingValueAsString(FilterRouteEndpoint1SettingTypeKey, routeEndpoint1Filter);
        endLengthEndpoint1Display = settingEntity.getSettingValueAsBoolean(DisplayEndLengthEndpoint1SettingTypeKey, endLengthEndpoint1Display);
        endLengthEndpoint1Filter = settingEntity.getSettingValueAsString(FilterEndLengthEndpoint1SettingTypeKey, endLengthEndpoint1Filter);
        terminationEndpoint1Display = settingEntity.getSettingValueAsBoolean(DisplayTerminationEndpoint1SettingTypeKey, terminationEndpoint1Display);
        terminationEndpoint1Filter = settingEntity.getSettingValueAsString(FilterTerminationEndpoint1SettingTypeKey, terminationEndpoint1Filter);
        pinlistEndpoint1Display = settingEntity.getSettingValueAsBoolean(DisplayPinlistEndpoint1SettingTypeKey, pinlistEndpoint1Display);
        pinlistEndpoint1Filter = settingEntity.getSettingValueAsString(FilterPinlistEndpoint1SettingTypeKey, pinlistEndpoint1Filter);
        notesEndpoint1Display = settingEntity.getSettingValueAsBoolean(DisplayNotesEndpoint1SettingTypeKey, notesEndpoint1Display);
        notesEndpoint1Filter = settingEntity.getSettingValueAsString(FilterNotesEndpoint1SettingTypeKey, notesEndpoint1Filter);
        drawingEndpoint1Display = settingEntity.getSettingValueAsBoolean(DisplayDrawingEndpoint1SettingTypeKey, drawingEndpoint1Display);
        drawingEndpoint1Filter = settingEntity.getSettingValueAsString(FilterDrawingEndpoint1SettingTypeKey, drawingEndpoint1Filter);

        descriptionEndpoint2Display = settingEntity.getSettingValueAsBoolean(DisplayDescriptionEndpoint2SettingTypeKey, descriptionEndpoint2Display);
        descriptionEndpoint2Filter = settingEntity.getSettingValueAsString(FilterDescriptionEndpoint2SettingTypeKey, descriptionEndpoint2Filter);
        routeEndpoint2Display = settingEntity.getSettingValueAsBoolean(DisplayRouteEndpoint2SettingTypeKey, routeEndpoint2Display);
        routeEndpoint2Filter = settingEntity.getSettingValueAsString(FilterRouteEndpoint2SettingTypeKey, routeEndpoint2Filter);
        endLengthEndpoint2Display = settingEntity.getSettingValueAsBoolean(DisplayEndLengthEndpoint2SettingTypeKey, endLengthEndpoint2Display);
        endLengthEndpoint2Filter = settingEntity.getSettingValueAsString(FilterEndLengthEndpoint2SettingTypeKey, endLengthEndpoint2Filter);
        terminationEndpoint2Display = settingEntity.getSettingValueAsBoolean(DisplayTerminationEndpoint2SettingTypeKey, terminationEndpoint2Display);
        terminationEndpoint2Filter = settingEntity.getSettingValueAsString(FilterTerminationEndpoint2SettingTypeKey, terminationEndpoint2Filter);
        pinlistEndpoint2Display = settingEntity.getSettingValueAsBoolean(DisplayPinlistEndpoint2SettingTypeKey, pinlistEndpoint2Display);
        pinlistEndpoint2Filter = settingEntity.getSettingValueAsString(FilterPinlistEndpoint2SettingTypeKey, pinlistEndpoint2Filter);
        notesEndpoint2Display = settingEntity.getSettingValueAsBoolean(DisplayNotesEndpoint2SettingTypeKey, notesEndpoint2Display);
        notesEndpoint2Filter = settingEntity.getSettingValueAsString(FilterNotesEndpoint2SettingTypeKey, notesEndpoint2Filter);
        drawingEndpoint2Display = settingEntity.getSettingValueAsBoolean(DisplayDrawingEndpoint2SettingTypeKey, drawingEndpoint2Display);
        drawingEndpoint2Filter = settingEntity.getSettingValueAsString(FilterDrawingEndpoint2SettingTypeKey, drawingEndpoint2Filter);

        deviceEndpoint1Display = settingEntity.getSettingValueAsBoolean(DisplayDeviceEndpoint1SettingTypeKey, deviceEndpoint1Display);
        deviceEndpoint1Filter = settingEntity.getSettingValueAsString(FilterDescriptionEndpoint1SettingTypeKey, deviceEndpoint1Filter);
        portEndpoint1Display = settingEntity.getSettingValueAsBoolean(DisplayPortEndpoint1SettingTypeKey, portEndpoint1Display);
        portEndpoint1Filter = settingEntity.getSettingValueAsString(FilterPortEndpoint1SettingTypeKey, portEndpoint1Filter);
        connectorEndpoint1Display = settingEntity.getSettingValueAsBoolean(DisplayConnectorEndpoint1SettingTypeKey, connectorEndpoint1Display);
        connectorEndpoint1Filter = settingEntity.getSettingValueAsString(FilterConnectorEndpoint1SettingTypeKey, connectorEndpoint1Filter);

        deviceEndpoint2Display = settingEntity.getSettingValueAsBoolean(DisplayDeviceEndpoint2SettingTypeKey, deviceEndpoint2Display);
        deviceEndpoint2Filter = settingEntity.getSettingValueAsString(FilterDescriptionEndpoint2SettingTypeKey, deviceEndpoint2Filter);
        portEndpoint2Display = settingEntity.getSettingValueAsBoolean(DisplayPortEndpoint2SettingTypeKey, portEndpoint2Display);
        portEndpoint2Filter = settingEntity.getSettingValueAsString(FilterPortEndpoint2SettingTypeKey, portEndpoint2Filter);
        connectorEndpoint2Display = settingEntity.getSettingValueAsBoolean(DisplayConnectorEndpoint2SettingTypeKey, connectorEndpoint2Display);
        connectorEndpoint2Filter = settingEntity.getSettingValueAsString(FilterConnectorEndpoint2SettingTypeKey, connectorEndpoint2Filter);

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

        settingEntity.setSettingValue(DisplayDescriptionEndpoint1SettingTypeKey, descriptionEndpoint1Display);
        settingEntity.setSettingValue(FilterDescriptionEndpoint1SettingTypeKey, descriptionEndpoint1Filter);
        settingEntity.setSettingValue(DisplayRouteEndpoint1SettingTypeKey, routeEndpoint1Display);
        settingEntity.setSettingValue(FilterRouteEndpoint1SettingTypeKey, routeEndpoint1Filter);
        settingEntity.setSettingValue(DisplayEndLengthEndpoint1SettingTypeKey, endLengthEndpoint1Display);
        settingEntity.setSettingValue(FilterEndLengthEndpoint1SettingTypeKey, endLengthEndpoint1Filter);
        settingEntity.setSettingValue(DisplayTerminationEndpoint1SettingTypeKey, terminationEndpoint1Display);
        settingEntity.setSettingValue(FilterTerminationEndpoint1SettingTypeKey, terminationEndpoint1Filter);
        settingEntity.setSettingValue(DisplayPinlistEndpoint1SettingTypeKey, pinlistEndpoint1Display);
        settingEntity.setSettingValue(FilterPinlistEndpoint1SettingTypeKey, pinlistEndpoint1Filter);
        settingEntity.setSettingValue(DisplayNotesEndpoint1SettingTypeKey, notesEndpoint1Display);
        settingEntity.setSettingValue(FilterNotesEndpoint1SettingTypeKey, notesEndpoint1Filter);
        settingEntity.setSettingValue(DisplayDrawingEndpoint1SettingTypeKey, drawingEndpoint1Display);
        settingEntity.setSettingValue(FilterDrawingEndpoint1SettingTypeKey, drawingEndpoint1Filter);

        settingEntity.setSettingValue(DisplayDescriptionEndpoint2SettingTypeKey, descriptionEndpoint2Display);
        settingEntity.setSettingValue(FilterDescriptionEndpoint2SettingTypeKey, descriptionEndpoint2Filter);
        settingEntity.setSettingValue(DisplayRouteEndpoint2SettingTypeKey, routeEndpoint2Display);
        settingEntity.setSettingValue(FilterRouteEndpoint2SettingTypeKey, routeEndpoint2Filter);
        settingEntity.setSettingValue(DisplayEndLengthEndpoint2SettingTypeKey, endLengthEndpoint2Display);
        settingEntity.setSettingValue(FilterEndLengthEndpoint2SettingTypeKey, endLengthEndpoint2Filter);
        settingEntity.setSettingValue(DisplayTerminationEndpoint2SettingTypeKey, terminationEndpoint2Display);
        settingEntity.setSettingValue(FilterTerminationEndpoint2SettingTypeKey, terminationEndpoint2Filter);
        settingEntity.setSettingValue(DisplayPinlistEndpoint2SettingTypeKey, pinlistEndpoint2Display);
        settingEntity.setSettingValue(FilterPinlistEndpoint2SettingTypeKey, pinlistEndpoint2Filter);
        settingEntity.setSettingValue(DisplayNotesEndpoint2SettingTypeKey, notesEndpoint2Display);
        settingEntity.setSettingValue(FilterNotesEndpoint2SettingTypeKey, notesEndpoint2Filter);
        settingEntity.setSettingValue(DisplayDrawingEndpoint2SettingTypeKey, drawingEndpoint2Display);
        settingEntity.setSettingValue(FilterDrawingEndpoint2SettingTypeKey, drawingEndpoint2Filter);

        settingEntity.setSettingValue(DisplayDeviceEndpoint1SettingTypeKey, deviceEndpoint1Display);
        settingEntity.setSettingValue(FilterDeviceEndpoint1SettingTypeKey, deviceEndpoint1Filter);
        settingEntity.setSettingValue(DisplayPortEndpoint1SettingTypeKey, portEndpoint1Display);
        settingEntity.setSettingValue(FilterPortEndpoint1SettingTypeKey, portEndpoint1Filter);
        settingEntity.setSettingValue(DisplayConnectorEndpoint1SettingTypeKey, connectorEndpoint1Display);
        settingEntity.setSettingValue(FilterConnectorEndpoint1SettingTypeKey, connectorEndpoint1Filter);

        settingEntity.setSettingValue(DisplayDeviceEndpoint2SettingTypeKey, deviceEndpoint2Display);
        settingEntity.setSettingValue(FilterDeviceEndpoint2SettingTypeKey, deviceEndpoint2Filter);
        settingEntity.setSettingValue(DisplayPortEndpoint2SettingTypeKey, portEndpoint2Display);
        settingEntity.setSettingValue(FilterPortEndpoint2SettingTypeKey, portEndpoint2Filter);
        settingEntity.setSettingValue(DisplayConnectorEndpoint2SettingTypeKey, connectorEndpoint2Display);
        settingEntity.setSettingValue(FilterConnectorEndpoint2SettingTypeKey, connectorEndpoint2Filter);

        settingEntity.setSettingValue(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues);
    }
}
