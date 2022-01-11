/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import static gov.anl.aps.cdb.portal.controllers.settings.SettingBase.parseSettingValueAsInteger;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;

/**
 *
 * @author djarosz
 */
public class ItemDomainCableCatalogSettings extends ItemSettings<ItemDomainCableCatalogController> {
    
    private static final String DisplayTechnicalSystemSettingTypeKey = "ItemDomainCableCatalog.List.Display.ItemCategory";
    private static final String DisplayCreatedByUserSettingTypeKey = "ItemDomainCableCatalog.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "ItemDomainCableCatalog.List.Display.CreatedOnDateTime";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ItemDomainCableCatalog.List.Display.NumberOfItemsPerPage";
    private static final String DisplayDescriptionSettingTypeKey = "ItemDomainCableCatalog.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "ItemDomainCableCatalog.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "ItemDomainCableCatalog.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "ItemDomainCableCatalog.List.Display.LastModifiedOnDateTime";
    private static final String DisplayPartNumberSettingTypeKey = "ItemDomainCableCatalog.List.Display.ItemIdentifier1";
    private static final String DisplayOwnerUserSettingTypeKey = "ItemDomainCableCatalog.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "ItemDomainCableCatalog.List.Display.OwnerGroup";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "ItemDomainCableCatalog.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "ItemDomainCableCatalog.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "ItemDomainCableCatalog.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "ItemDomainCableCatalog.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "ItemDomainCableCatalog.List.Display.PropertyTypeId5";
    private static final String DisplayRowExpansionSettingTypeKey = "ItemDomainCableCatalog.List.Display.RowExpansion";
    private static final String DisplayAlternateNameSettingTypeKey = "ItemDomainCableCatalog.List.Display.AlternateName";
    private static final String DisplayItemProjectSettingTypeKey = "ItemDomainCableCatalog.List.Display.Project";
    private static final String DisplayItemSourcesSettingTypeKey = "ItemDomainCableCatalog.List.Display.Sources";
    private static final String DisplayComponentInstanceRowExpansionSettingTypeKey = "ItemDomainCableCatalog.List.Display.ComponentInstance.RowExpansion";
    private static final String LoadRowExpansionPropertyValueSettingTypeKey = "ItemDomainCableCatalog.List.Load.RowExpansionPropertyValue";
    private static final String LoadComponentInstanceRowExpansionPropertyValueSettingTypeKey = "ItemDomainCableCatalog.List.Load.ComponentInstance.RowExpansionPropertyValue";
    private static final String AutoLoadListFilterValuesSettingTypeKey = "ItemDomainCableCatalog.List.Load.FilterDataTable"; 
    private static final String FilterByTechnicalSystemSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.ItemCategory";
    private static final String FilterByCreatedByUserSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByNameSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.Name";
    private static final String FilterByPartNumberSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.ItemIdentifier1";
    private static final String FilterByItemSourcesSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.Sources";
    private static final String FilterByOwnerUserSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.OwnerGroup";
    private static final String FilterByPropertyValue1SettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.PropertyValue1";
    private static final String FilterByPropertyValue2SettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.PropertyValue2";
    private static final String FilterByPropertyValue3SettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.PropertyValue3";
    private static final String FilterByPropertyValue4SettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.PropertyValue4";
    private static final String FilterByPropertyValue5SettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.PropertyValue5";
    private static final String FilterByPropertiesAutoLoadTypeKey = "ItemDomainCableCatalog.List.AutoLoad.FilterBy.Properties";    

    private static final String DisplayHeatLimitSettingTypeKey = "ItemDomainCableCatalog.List.Display.HeatLimit";
    private static final String DisplayInsulationSettingTypeKey = "ItemDomainCableCatalog.List.Display.Insulation";
    private static final String DisplayJacketColorSettingTypeKey = "ItemDomainCableCatalog.List.Display.JacketColor";
    private static final String DisplayWeightSettingTypeKey = "ItemDomainCableCatalog.List.Display.Weight";
    private static final String DisplayDiameterSettingTypeKey = "ItemDomainCableCatalog.List.Display.Diameter";
    private static final String DisplayUrlSettingTypeKey = "ItemDomainCableCatalog.List.Display.Url";
    private static final String DisplayBendRadiusSettingTypeKey = "ItemDomainCableCatalog.List.Display.BendRadius";
    private static final String DisplayRadiationToleranceSettingTypeKey = "ItemDomainCableCatalog.List.Display.RadiationTolerance";
    private static final String DisplayConductorsSettingTypeKey = "ItemDomainCableCatalog.List.Display.Conductors";
    private static final String DisplayImageUrlSettingTypeKey = "ItemDomainCableCatalog.List.Display.ImageUrl";
    private static final String DisplayFireLoadSettingTypeKey = "ItemDomainCableCatalog.List.Display.FireLoad";
    private static final String DisplayVoltageRatingSettingTypeKey = "ItemDomainCableCatalog.List.Display.VoltageRating";
    private static final String DisplayAltPartNumberSettingTypeKey = "ItemDomainCableCatalog.List.Display.AltPartNumber";

    private static final String FilterByHeatLimitSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.HeatLimit";
    private static final String FilterByInsulationSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.Insulation";
    private static final String FilterByJacketColorSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.JacketColor";
    private static final String FilterByWeightSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.Weight";
    private static final String FilterByDiameterSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.Diameter";
    private static final String FilterByUrlSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.Url";
    private static final String FilterByBendRadiusSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.BendRadius";
    private static final String FilterByRadiationToleranceSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.RadiationTolerance";
    private static final String FilterByConductorsSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.Conductors";
    private static final String FilterByImageUrlSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.ImageUrl";
    private static final String FilterByFireLoadSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.FireLoad";
    private static final String FilterByVoltageRatingSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.VoltageRating";
    private static final String FilterByAltPartNumberSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.AltPartNumber";
    
    private static final String DisplayTotalLengthSettingTypeKey = "ItemDomainCableCatalog.List.Display.TotalLength";
    private static final String FilterByTotalLengthSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.TotalLength";
    private static final String DisplayReelLengthSettingTypeKey = "ItemDomainCableCatalog.List.Display.ReelLength";
    private static final String FilterByReelLengthSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.ReelLength";
    private static final String DisplayReelQuantitySettingTypeKey = "ItemDomainCableCatalog.List.Display.ReelQuantity";
    private static final String FilterByReelQuantitySettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.ReelQuantity";
    private static final String DisplayLeadTimeSettingTypeKey = "ItemDomainCableCatalog.List.Display.LeadTime";
    private static final String FilterByLeadTimeSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.LeadTime";
    private static final String DisplayProcurementStatusSettingTypeKey = "ItemDomainCableCatalog.List.Display.ProcurementStatus";
    private static final String FilterByProcurementStatusSettingTypeKey = "ItemDomainCableCatalog.List.FilterBy.ProcurementStatus";


    protected boolean heatLimitDisplay;
    protected boolean insulationDisplay;
    protected boolean jacketColorDisplay;
    protected boolean weightDisplay;
    protected boolean diameterDisplay;
    protected boolean urlDisplay;
    protected boolean bendRadiusDisplay;
    protected boolean radToleranceDisplay;
    protected boolean conductorsDisplay;
    protected boolean imageUrlDisplay;
    protected boolean fireLoadDisplay;
    protected boolean voltageRatingDisplay;
    protected boolean altPartNumberDisplay;
    
    protected String heatLimitFilter;
    protected String insulationFilter;
    protected String jacketColorFilter;
    protected String weightFilter;
    protected String diameterFilter;
    protected String urlFilter;
    protected String bendRadiusFilter;
    protected String radToleranceFilter;
    protected String conductorsFilter;
    protected String imageUrlFilter;
    protected String fireLoadFilter;
    protected String voltageRatingFilter;
    protected String altPartNumberFilter;
    
    protected boolean totalLengthDisplay;
    protected String totalLengthFilter;
    protected boolean reelLengthDisplay;
    protected String reelLengthFilter;
    protected boolean reelQuantityDisplay;
    protected String reelQuantityFilter;
    protected boolean leadTimeDisplay;
    protected String leadTimeFilter;
    protected boolean procurementStatusDisplay;
    protected String procurementStatusFilter;
    
    protected String filterByTechnicalSystem = null;
    
    public ItemDomainCableCatalogSettings(ItemDomainCableCatalogController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 25;
    }
    
    public boolean isHeatLimitDisplay() {
        return heatLimitDisplay;
    }

    public void setHeatLimitDisplay(boolean heatLimitDisplay) {
        this.heatLimitDisplay = heatLimitDisplay;
    }

    public boolean isInsulationDisplay() {
        return insulationDisplay;
    }

    public void setInsulationDisplay(boolean insulationDisplay) {
        this.insulationDisplay = insulationDisplay;
    }

    public boolean isJacketColorDisplay() {
        return jacketColorDisplay;
    }

    public void setJacketColorDisplay(boolean jacketColorDisplay) {
        this.jacketColorDisplay = jacketColorDisplay;
    }

    public boolean isWeightDisplay() {
        return weightDisplay;
    }

    public void setWeightDisplay(boolean weightDisplay) {
        this.weightDisplay = weightDisplay;
    }

    public boolean isDiameterDisplay() {
        return diameterDisplay;
    }

    public void setDiameterDisplay(boolean diameterDisplay) {
        this.diameterDisplay = diameterDisplay;
    }

    public boolean isUrlDisplay() {
        return urlDisplay;
    }

    public void setUrlDisplay(boolean urlDisplay) {
        this.urlDisplay = urlDisplay;
    }

    public boolean isBendRadiusDisplay() {
        return bendRadiusDisplay;
    }

    public void setBendRadiusDisplay(boolean bendRadiusDisplay) {
        this.bendRadiusDisplay = bendRadiusDisplay;
    }

    public boolean isRadToleranceDisplay() {
        return radToleranceDisplay;
    }

    public void setRadToleranceDisplay(boolean radToleranceDisplay) {
        this.radToleranceDisplay = radToleranceDisplay;
    }

    public boolean isConductorsDisplay() {
        return conductorsDisplay;
    }

    public void setConductorsDisplay(boolean conductorsDisplay) {
        this.conductorsDisplay = conductorsDisplay;
    }

    public boolean isImageUrlDisplay() {
        return imageUrlDisplay;
    }

    public void setImageUrlDisplay(boolean imageUrlDisplay) {
        this.imageUrlDisplay = imageUrlDisplay;
    }

    public boolean isFireLoadDisplay() {
        return fireLoadDisplay;
    }

    public void setFireLoadDisplay(boolean fireLoadDisplay) {
        this.fireLoadDisplay = fireLoadDisplay;
    }

    public boolean isVoltageRatingDisplay() {
        return voltageRatingDisplay;
    }

    public void setVoltageRatingDisplay(boolean voltageRatingDisplay) {
        this.voltageRatingDisplay = voltageRatingDisplay;
    }

    public boolean isAltPartNumberDisplay() {
        return altPartNumberDisplay;
    }

    public void setAltPartNumberDisplay(boolean altPartNumberDisplay) {
        this.altPartNumberDisplay = altPartNumberDisplay;
    }

    public String getHeatLimitFilter() {
        return heatLimitFilter;
    }

    public void setHeatLimitFilter(String heatLimitFilter) {
        this.heatLimitFilter = heatLimitFilter;
    }

    public String getInsulationFilter() {
        return insulationFilter;
    }

    public void setInsulationFilter(String insulationFilter) {
        this.insulationFilter = insulationFilter;
    }

    public String getJacketColorFilter() {
        return jacketColorFilter;
    }

    public void setJacketColorFilter(String jacketColorFilter) {
        this.jacketColorFilter = jacketColorFilter;
    }

    public String getWeightFilter() {
        return weightFilter;
    }

    public void setWeightFilter(String weightFilter) {
        this.weightFilter = weightFilter;
    }

    public String getDiameterFilter() {
        return diameterFilter;
    }

    public void setDiameterFilter(String diameterFilter) {
        this.diameterFilter = diameterFilter;
    }

    public String getUrlFilter() {
        return urlFilter;
    }

    public void setUrlFilter(String urlFilter) {
        this.urlFilter = urlFilter;
    }

    public String getBendRadiusFilter() {
        return bendRadiusFilter;
    }

    public void setBendRadiusFilter(String bendRadiusFilter) {
        this.bendRadiusFilter = bendRadiusFilter;
    }

    public String getRadToleranceFilter() {
        return radToleranceFilter;
    }

    public void setRadToleranceFilter(String radToleranceFilter) {
        this.radToleranceFilter = radToleranceFilter;
    }

    public String getConductorsFilter() {
        return conductorsFilter;
    }

    public void setConductorsFilter(String conductorsFilter) {
        this.conductorsFilter = conductorsFilter;
    }

    public String getImageUrlFilter() {
        return imageUrlFilter;
    }

    public void setImageUrlFilter(String imageUrlFilter) {
        this.imageUrlFilter = imageUrlFilter;
    }

    public String getFireLoadFilter() {
        return fireLoadFilter;
    }

    public void setFireLoadFilter(String fireLoadFilter) {
        this.fireLoadFilter = fireLoadFilter;
    }

    public String getVoltageRatingFilter() {
        return voltageRatingFilter;
    }

    public void setVoltageRatingFilter(String voltageRatingFilter) {
        this.voltageRatingFilter = voltageRatingFilter;
    }
     
    public String getAltPartNumberFilter() {
        return altPartNumberFilter;
    }

    public void setAltPartNumberFilter(String altPartNumberFilter) {
        this.altPartNumberFilter = altPartNumberFilter;
    }
     
    public boolean isTotalLengthDisplay() {
        return totalLengthDisplay;
    }

    public void setTotalLengthDisplay(boolean totalLengthDisplay) {
        this.totalLengthDisplay = totalLengthDisplay;
    }

    public String getTotalLengthFilter() {
        return totalLengthFilter;
    }

    public void setTotalLengthFilter(String totalLengthFilter) {
        this.totalLengthFilter = totalLengthFilter;
    }

    public boolean isReelLengthDisplay() {
        return reelLengthDisplay;
    }

    public void setReelLengthDisplay(boolean reelLengthDisplay) {
        this.reelLengthDisplay = reelLengthDisplay;
    }

    public String getReelLengthFilter() {
        return reelLengthFilter;
    }

    public void setReelLengthFilter(String reelLengthFilter) {
        this.reelLengthFilter = reelLengthFilter;
    }

    public boolean isReelQuantityDisplay() {
        return reelQuantityDisplay;
    }

    public void setReelQuantityDisplay(boolean reelQuantityDisplay) {
        this.reelQuantityDisplay = reelQuantityDisplay;
    }

    public String getReelQuantityFilter() {
        return reelQuantityFilter;
    }

    public void setReelQuantityFilter(String reelQuantityFilter) {
        this.reelQuantityFilter = reelQuantityFilter;
    }

    public boolean isLeadTimeDisplay() {
        return leadTimeDisplay;
    }

    public void setLeadTimeDisplay(boolean leadTimeDisplay) {
        this.leadTimeDisplay = leadTimeDisplay;
    }

    public String getLeadTimeFilter() {
        return leadTimeFilter;
    }

    public void setLeadTimeFilter(String leadTimeFilter) {
        this.leadTimeFilter = leadTimeFilter;
    }

    public boolean isProcurementStatusDisplay() {
        return procurementStatusDisplay;
    }

    public void setProcurementStatusDisplay(boolean procurementStatusDisplay) {
        this.procurementStatusDisplay = procurementStatusDisplay;
    }

    public String getProcurementStatusFilter() {
        return procurementStatusFilter;
    }

    public void setProcurementStatusFilter(String procurementStatusFilter) {
        this.procurementStatusFilter = procurementStatusFilter;
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);        

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayItemCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayTechnicalSystemSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        displayItemIdentifier1 = Boolean.parseBoolean(settingTypeMap.get(DisplayPartNumberSettingTypeKey).getDefaultValue());
        displayItemProject = Boolean.parseBoolean(settingTypeMap.get(DisplayItemProjectSettingTypeKey).getDefaultValue());
        displayItemSources = Boolean.parseBoolean(settingTypeMap.get(DisplayItemSourcesSettingTypeKey).getDefaultValue());

        displayRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayRowExpansionSettingTypeKey).getDefaultValue());

        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());

        heatLimitDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayHeatLimitSettingTypeKey).getDefaultValue());
        insulationDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayInsulationSettingTypeKey).getDefaultValue());
        jacketColorDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayJacketColorSettingTypeKey).getDefaultValue());
        weightDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayWeightSettingTypeKey).getDefaultValue());
        diameterDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayDiameterSettingTypeKey).getDefaultValue());
        urlDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayUrlSettingTypeKey).getDefaultValue());
        bendRadiusDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayBendRadiusSettingTypeKey).getDefaultValue());
        radToleranceDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayRadiationToleranceSettingTypeKey).getDefaultValue());
        conductorsDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayConductorsSettingTypeKey).getDefaultValue());
        imageUrlDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayImageUrlSettingTypeKey).getDefaultValue());
        fireLoadDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayFireLoadSettingTypeKey).getDefaultValue());
        voltageRatingDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayVoltageRatingSettingTypeKey).getDefaultValue());
        altPartNumberDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayAltPartNumberSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByTechnicalSystem = settingTypeMap.get(FilterByTechnicalSystemSettingTypeKey).getDefaultValue();
        filterByItemIdentifier1 = settingTypeMap.get(FilterByPartNumberSettingTypeKey).getDefaultValue();
        filterByItemSources = settingTypeMap.get(FilterByItemSourcesSettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();

        filterByPropertyValue1 = settingTypeMap.get(FilterByPropertyValue1SettingTypeKey).getDefaultValue();
        filterByPropertyValue2 = settingTypeMap.get(FilterByPropertyValue2SettingTypeKey).getDefaultValue();
        filterByPropertyValue3 = settingTypeMap.get(FilterByPropertyValue3SettingTypeKey).getDefaultValue();
        filterByPropertyValue4 = settingTypeMap.get(FilterByPropertyValue4SettingTypeKey).getDefaultValue();
        filterByPropertyValue5 = settingTypeMap.get(FilterByPropertyValue5SettingTypeKey).getDefaultValue();
        filterByPropertiesAutoLoad = Boolean.parseBoolean(settingTypeMap.get(FilterByPropertiesAutoLoadTypeKey).getDefaultValue());

        heatLimitFilter = settingTypeMap.get(FilterByHeatLimitSettingTypeKey).getDefaultValue();
        insulationFilter = settingTypeMap.get(FilterByInsulationSettingTypeKey).getDefaultValue();
        jacketColorFilter = settingTypeMap.get(FilterByJacketColorSettingTypeKey).getDefaultValue();
        weightFilter = settingTypeMap.get(FilterByWeightSettingTypeKey).getDefaultValue();
        diameterFilter = settingTypeMap.get(FilterByDiameterSettingTypeKey).getDefaultValue();
        urlFilter = settingTypeMap.get(FilterByUrlSettingTypeKey).getDefaultValue();
        bendRadiusFilter = settingTypeMap.get(FilterByBendRadiusSettingTypeKey).getDefaultValue();
        radToleranceFilter = settingTypeMap.get(FilterByRadiationToleranceSettingTypeKey).getDefaultValue();
        conductorsFilter = settingTypeMap.get(FilterByConductorsSettingTypeKey).getDefaultValue();
        imageUrlFilter = settingTypeMap.get(FilterByImageUrlSettingTypeKey).getDefaultValue();
        fireLoadFilter = settingTypeMap.get(FilterByFireLoadSettingTypeKey).getDefaultValue();
        voltageRatingFilter = settingTypeMap.get(FilterByVoltageRatingSettingTypeKey).getDefaultValue();
        altPartNumberFilter = settingTypeMap.get(FilterByAltPartNumberSettingTypeKey).getDefaultValue();
        
        totalLengthDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayTotalLengthSettingTypeKey).getDefaultValue());
        totalLengthFilter = settingTypeMap.get(FilterByTotalLengthSettingTypeKey).getDefaultValue();
        reelLengthDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayReelLengthSettingTypeKey).getDefaultValue());
        reelLengthFilter = settingTypeMap.get(FilterByReelLengthSettingTypeKey).getDefaultValue();
        reelQuantityDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayReelQuantitySettingTypeKey).getDefaultValue());
        reelQuantityFilter = settingTypeMap.get(FilterByReelQuantitySettingTypeKey).getDefaultValue();
        leadTimeDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayLeadTimeSettingTypeKey).getDefaultValue());
        leadTimeFilter = settingTypeMap.get(FilterByLeadTimeSettingTypeKey).getDefaultValue();
        procurementStatusDisplay = Boolean.parseBoolean(settingTypeMap.get(DisplayProcurementStatusSettingTypeKey).getDefaultValue());
        procurementStatusFilter = settingTypeMap.get(FilterByProcurementStatusSettingTypeKey).getDefaultValue();

        autoLoadListFilterValues = Boolean.parseBoolean(settingTypeMap.get(AutoLoadListFilterValuesSettingTypeKey).getDefaultValue()); 

        parentController.resetDomainEntityPropertyTypeIdIndexMappings();
    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);
        
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayItemCategory = settingEntity.getSettingValueAsBoolean(DisplayTechnicalSystemSettingTypeKey, displayItemCategory);
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        displayItemIdentifier1 = settingEntity.getSettingValueAsBoolean(DisplayPartNumberSettingTypeKey, displayItemIdentifier1);
        displayItemProject = settingEntity.getSettingValueAsBoolean(DisplayItemProjectSettingTypeKey, displayItemProject);
        displayItemSources = settingEntity.getSettingValueAsBoolean(DisplayItemSourcesSettingTypeKey, displayItemSources);

        displayRowExpansion = settingEntity.getSettingValueAsBoolean(DisplayRowExpansionSettingTypeKey, displayRowExpansion);

        displayPropertyTypeId1 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        heatLimitDisplay = settingEntity.getSettingValueAsBoolean(DisplayHeatLimitSettingTypeKey, heatLimitDisplay);
        insulationDisplay = settingEntity.getSettingValueAsBoolean(DisplayInsulationSettingTypeKey, insulationDisplay);
        jacketColorDisplay = settingEntity.getSettingValueAsBoolean(DisplayJacketColorSettingTypeKey, jacketColorDisplay);
        weightDisplay = settingEntity.getSettingValueAsBoolean(DisplayWeightSettingTypeKey, weightDisplay);
        diameterDisplay = settingEntity.getSettingValueAsBoolean(DisplayDiameterSettingTypeKey, diameterDisplay);
        urlDisplay = settingEntity.getSettingValueAsBoolean(DisplayUrlSettingTypeKey, urlDisplay);
        bendRadiusDisplay = settingEntity.getSettingValueAsBoolean(DisplayBendRadiusSettingTypeKey, bendRadiusDisplay);
        radToleranceDisplay = settingEntity.getSettingValueAsBoolean(DisplayRadiationToleranceSettingTypeKey, radToleranceDisplay);
        conductorsDisplay = settingEntity.getSettingValueAsBoolean(DisplayConductorsSettingTypeKey, conductorsDisplay);
        imageUrlDisplay = settingEntity.getSettingValueAsBoolean(DisplayImageUrlSettingTypeKey, imageUrlDisplay);
        fireLoadDisplay = settingEntity.getSettingValueAsBoolean(DisplayFireLoadSettingTypeKey, fireLoadDisplay);
        voltageRatingDisplay = settingEntity.getSettingValueAsBoolean(DisplayVoltageRatingSettingTypeKey, voltageRatingDisplay);
        altPartNumberDisplay = settingEntity.getSettingValueAsBoolean(DisplayAltPartNumberSettingTypeKey, altPartNumberDisplay);

        filterByName = settingEntity.getSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByTechnicalSystem = settingEntity.getSettingValueAsString(FilterByTechnicalSystemSettingTypeKey, filterByTechnicalSystem);
        filterByItemIdentifier1 = settingEntity.getSettingValueAsString(FilterByPartNumberSettingTypeKey, filterByItemIdentifier1);
        filterByItemSources = settingEntity.getSettingValueAsString(FilterByItemSourcesSettingTypeKey, filterByItemSources);
        filterByOwnerUser = settingEntity.getSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = settingEntity.getSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = settingEntity.getSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = settingEntity.getSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = settingEntity.getSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = settingEntity.getSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByPropertyValue1 = settingEntity.getSettingValueAsString(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        filterByPropertyValue2 = settingEntity.getSettingValueAsString(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        filterByPropertyValue3 = settingEntity.getSettingValueAsString(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        filterByPropertyValue4 = settingEntity.getSettingValueAsString(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        filterByPropertyValue5 = settingEntity.getSettingValueAsString(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        filterByPropertiesAutoLoad = settingEntity.getSettingValueAsBoolean(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        heatLimitFilter = settingEntity.getSettingValueAsString(FilterByHeatLimitSettingTypeKey, heatLimitFilter);
        insulationFilter = settingEntity.getSettingValueAsString(FilterByInsulationSettingTypeKey, insulationFilter);
        jacketColorFilter = settingEntity.getSettingValueAsString(FilterByJacketColorSettingTypeKey, jacketColorFilter);
        weightFilter = settingEntity.getSettingValueAsString(FilterByWeightSettingTypeKey, weightFilter);
        diameterFilter = settingEntity.getSettingValueAsString(FilterByDiameterSettingTypeKey, diameterFilter);
        urlFilter = settingEntity.getSettingValueAsString(FilterByUrlSettingTypeKey, urlFilter);
        bendRadiusFilter = settingEntity.getSettingValueAsString(FilterByBendRadiusSettingTypeKey, bendRadiusFilter);
        radToleranceFilter = settingEntity.getSettingValueAsString(FilterByRadiationToleranceSettingTypeKey, radToleranceFilter);
        conductorsFilter = settingEntity.getSettingValueAsString(FilterByConductorsSettingTypeKey, conductorsFilter);
        imageUrlFilter = settingEntity.getSettingValueAsString(FilterByImageUrlSettingTypeKey, imageUrlFilter);
        fireLoadFilter = settingEntity.getSettingValueAsString(FilterByFireLoadSettingTypeKey, fireLoadFilter);
        voltageRatingFilter = settingEntity.getSettingValueAsString(FilterByVoltageRatingSettingTypeKey, voltageRatingFilter);
        altPartNumberFilter = settingEntity.getSettingValueAsString(FilterByAltPartNumberSettingTypeKey, altPartNumberFilter);
        
        totalLengthDisplay = settingEntity.getSettingValueAsBoolean(DisplayTotalLengthSettingTypeKey, totalLengthDisplay);
        totalLengthFilter = settingEntity.getSettingValueAsString(FilterByTotalLengthSettingTypeKey, totalLengthFilter);
        reelLengthDisplay = settingEntity.getSettingValueAsBoolean(DisplayReelLengthSettingTypeKey, reelLengthDisplay);
        reelLengthFilter = settingEntity.getSettingValueAsString(FilterByReelLengthSettingTypeKey, reelLengthFilter);
        reelQuantityDisplay = settingEntity.getSettingValueAsBoolean(DisplayReelQuantitySettingTypeKey, reelQuantityDisplay);
        reelQuantityFilter = settingEntity.getSettingValueAsString(FilterByReelQuantitySettingTypeKey, reelQuantityFilter);
        leadTimeDisplay = settingEntity.getSettingValueAsBoolean(DisplayLeadTimeSettingTypeKey, leadTimeDisplay);
        leadTimeFilter = settingEntity.getSettingValueAsString(FilterByLeadTimeSettingTypeKey, leadTimeFilter);
        procurementStatusDisplay = settingEntity.getSettingValueAsBoolean(DisplayProcurementStatusSettingTypeKey, procurementStatusDisplay);
        procurementStatusFilter = settingEntity.getSettingValueAsString(FilterByProcurementStatusSettingTypeKey, procurementStatusFilter);

        autoLoadListFilterValues = settingEntity.getSettingValueAsBoolean(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues); 

        parentController.resetDomainEntityPropertyTypeIdIndexMappings();
    }
    
    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        super.saveSettingsForSessionSettingEntity(settingEntity);       

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        settingEntity.setSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        settingEntity.setSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        settingEntity.setSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        settingEntity.setSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        settingEntity.setSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        settingEntity.setSettingValue(DisplayPartNumberSettingTypeKey, displayItemIdentifier1);
        settingEntity.setSettingValue(DisplayItemProjectSettingTypeKey, displayItemProject);
        settingEntity.setSettingValue(DisplayItemSourcesSettingTypeKey, displayItemSources);
        settingEntity.setSettingValue(DisplayTechnicalSystemSettingTypeKey, displayItemCategory);

        settingEntity.setSettingValue(DisplayRowExpansionSettingTypeKey, displayRowExpansion);

        settingEntity.setSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        settingEntity.setSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        settingEntity.setSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        settingEntity.setSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        settingEntity.setSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        settingEntity.setSettingValue(DisplayHeatLimitSettingTypeKey, heatLimitDisplay);
        settingEntity.setSettingValue(DisplayInsulationSettingTypeKey, insulationDisplay);
        settingEntity.setSettingValue(DisplayJacketColorSettingTypeKey, jacketColorDisplay);
        settingEntity.setSettingValue(DisplayWeightSettingTypeKey, weightDisplay);
        settingEntity.setSettingValue(DisplayDiameterSettingTypeKey, diameterDisplay);
        settingEntity.setSettingValue(DisplayUrlSettingTypeKey, urlDisplay);
        settingEntity.setSettingValue(DisplayBendRadiusSettingTypeKey, bendRadiusDisplay);
        settingEntity.setSettingValue(DisplayRadiationToleranceSettingTypeKey, radToleranceDisplay);
        settingEntity.setSettingValue(DisplayConductorsSettingTypeKey, conductorsDisplay);
        settingEntity.setSettingValue(DisplayImageUrlSettingTypeKey, imageUrlDisplay);
        settingEntity.setSettingValue(DisplayFireLoadSettingTypeKey, fireLoadDisplay);
        settingEntity.setSettingValue(DisplayVoltageRatingSettingTypeKey, voltageRatingDisplay);
        settingEntity.setSettingValue(DisplayAltPartNumberSettingTypeKey, altPartNumberDisplay);

        settingEntity.setSettingValue(FilterByNameSettingTypeKey, filterByName);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        settingEntity.setSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        settingEntity.setSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        settingEntity.setSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        settingEntity.setSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        settingEntity.setSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        settingEntity.setSettingValue(FilterByTechnicalSystemSettingTypeKey, filterByTechnicalSystem);
        settingEntity.setSettingValue(FilterByPartNumberSettingTypeKey, filterByItemIdentifier1);
        settingEntity.setSettingValue(FilterByItemSourcesSettingTypeKey, filterByItemSources);

        settingEntity.setSettingValue(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        settingEntity.setSettingValue(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        settingEntity.setSettingValue(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        settingEntity.setSettingValue(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        settingEntity.setSettingValue(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        settingEntity.setSettingValue(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        settingEntity.setSettingValue(FilterByHeatLimitSettingTypeKey, heatLimitFilter);
        settingEntity.setSettingValue(FilterByInsulationSettingTypeKey, insulationFilter);
        settingEntity.setSettingValue(FilterByJacketColorSettingTypeKey, jacketColorFilter);
        settingEntity.setSettingValue(FilterByWeightSettingTypeKey, weightFilter);
        settingEntity.setSettingValue(FilterByDiameterSettingTypeKey, diameterFilter);
        settingEntity.setSettingValue(FilterByUrlSettingTypeKey, urlFilter);
        settingEntity.setSettingValue(FilterByBendRadiusSettingTypeKey, bendRadiusFilter);
        settingEntity.setSettingValue(FilterByRadiationToleranceSettingTypeKey, radToleranceFilter);
        settingEntity.setSettingValue(FilterByConductorsSettingTypeKey, conductorsFilter);
        settingEntity.setSettingValue(FilterByImageUrlSettingTypeKey, imageUrlFilter);
        settingEntity.setSettingValue(FilterByFireLoadSettingTypeKey, fireLoadFilter);
        settingEntity.setSettingValue(FilterByVoltageRatingSettingTypeKey, voltageRatingFilter);
        settingEntity.setSettingValue(FilterByAltPartNumberSettingTypeKey, altPartNumberFilter);

        settingEntity.setSettingValue(DisplayTotalLengthSettingTypeKey, totalLengthDisplay);
        settingEntity.setSettingValue(FilterByTotalLengthSettingTypeKey, totalLengthFilter);
        settingEntity.setSettingValue(DisplayReelLengthSettingTypeKey, reelLengthDisplay);
        settingEntity.setSettingValue(FilterByReelLengthSettingTypeKey, reelLengthFilter);
        settingEntity.setSettingValue(DisplayReelQuantitySettingTypeKey, reelQuantityDisplay);
        settingEntity.setSettingValue(FilterByReelQuantitySettingTypeKey, reelQuantityFilter);
        settingEntity.setSettingValue(DisplayLeadTimeSettingTypeKey, leadTimeDisplay);
        settingEntity.setSettingValue(FilterByLeadTimeSettingTypeKey, leadTimeFilter);
        settingEntity.setSettingValue(DisplayProcurementStatusSettingTypeKey, radToleranceDisplay);
        settingEntity.setSettingValue(FilterByProcurementStatusSettingTypeKey, radToleranceFilter);

        settingEntity.setSettingValue(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues);

    }
}
