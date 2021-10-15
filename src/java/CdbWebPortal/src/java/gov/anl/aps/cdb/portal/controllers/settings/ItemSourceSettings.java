/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemSourceController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class ItemSourceSettings extends CdbEntitySettingsBase<ItemSourceController> {
    
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ItemSource.List.Display.NumberOfItemsPerPage";
    private static final String DisplayContactInfoSettingTypeKey = "ItemSource.List.Display.ContactInfo";
    private static final String DisplayCostSettingTypeKey = "ItemSource.List.Display.Cost";
    private static final String DisplayDescriptionSettingTypeKey = "ItemSource.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "ItemSource.List.Display.Id";
    private static final String DisplayIsManufacturerSettingTypeKey = "ItemSource.List.Display.IsManufacturer";
    private static final String DisplayIsVendorSettingTypeKey = "ItemSource.List.Display.IsVendor";
    private static final String DisplayPartNumberSettingTypeKey = "ItemSource.List.Display.PartNumber";
    private static final String DisplayUrlSettingTypeKey = "ItemSource.List.Display.Url";
    private static final String FilterByContactInfoSettingTypeKey = "ItemSource.List.FilterBy.ContactInfo";
    private static final String FilterByCostSettingTypeKey = "ItemSource.List.FilterBy.Cost";
    private static final String FilterByDescriptionSettingTypeKey = "ItemSource.List.FilterBy.Description";
    private static final String FilterByIsManufacturerSettingTypeKey = "ItemSource.List.FilterBy.IsManufacturer";
    private static final String FilterByIsVendorSettingTypeKey = "ItemSource.List.FilterBy.IsVendor";
    private static final String FilterByPartNumberSettingTypeKey = "ItemSource.List.FilterBy.PartNumber";
    private static final String FilterBySourceNameSettingTypeKey = "ItemSource.List.FilterBy.SourceName";
    private static final String FilterByUrlSettingTypeKey = "ItemSource.List.FilterBy.Url";
    
    private Boolean displayContactInfo = null;
    private Boolean displayCost = null;
    private Boolean displayIsManufacturer = null;
    private Boolean displayIsVendor = null;
    private Boolean displayPartNumber = null;
    private Boolean displayUrl = null;

    private String filterByContactInfo = null;
    private String filterByCost = null;
    private String filterByIsManufacturer = null;
    private String filterByIsVendor = null;
    private String filterByPartNumber = null;
    private String filterBySourceName = null;
    private String filterByUrl = null;

    public ItemSourceSettings(ItemSourceController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayContactInfo = Boolean.parseBoolean(settingTypeMap.get(DisplayContactInfoSettingTypeKey).getDefaultValue());
        displayCost = Boolean.parseBoolean(settingTypeMap.get(DisplayCostSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayIsManufacturer = Boolean.parseBoolean(settingTypeMap.get(DisplayIsManufacturerSettingTypeKey).getDefaultValue());
        displayIsVendor = Boolean.parseBoolean(settingTypeMap.get(DisplayIsVendorSettingTypeKey).getDefaultValue());
        displayPartNumber = Boolean.parseBoolean(settingTypeMap.get(DisplayPartNumberSettingTypeKey).getDefaultValue());
        displayUrl = Boolean.parseBoolean(settingTypeMap.get(DisplayUrlSettingTypeKey).getDefaultValue());

        filterByContactInfo = settingTypeMap.get(FilterByContactInfoSettingTypeKey).getDefaultValue();
        filterByCost = settingTypeMap.get(FilterByCostSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByIsManufacturer = settingTypeMap.get(FilterByIsManufacturerSettingTypeKey).getDefaultValue();
        filterByIsVendor = settingTypeMap.get(FilterByIsVendorSettingTypeKey).getDefaultValue();
        filterByPartNumber = settingTypeMap.get(FilterByPartNumberSettingTypeKey).getDefaultValue();
        filterBySourceName = settingTypeMap.get(FilterBySourceNameSettingTypeKey).getDefaultValue();
        filterByUrl = settingTypeMap.get(FilterByUrlSettingTypeKey).getDefaultValue();
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayContactInfo = settingEntity.getSettingValueAsBoolean(DisplayContactInfoSettingTypeKey, displayContactInfo);
        displayCost = settingEntity.getSettingValueAsBoolean(DisplayCostSettingTypeKey, displayCost);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayIsManufacturer = settingEntity.getSettingValueAsBoolean(DisplayIsManufacturerSettingTypeKey, displayIsManufacturer);
        displayIsVendor = settingEntity.getSettingValueAsBoolean(DisplayIsVendorSettingTypeKey, displayIsVendor);
        displayPartNumber = settingEntity.getSettingValueAsBoolean(DisplayPartNumberSettingTypeKey, displayPartNumber);
        displayUrl = settingEntity.getSettingValueAsBoolean(DisplayUrlSettingTypeKey, displayUrl);

        filterByContactInfo = settingEntity.getSettingValueAsString(FilterByContactInfoSettingTypeKey, filterByContactInfo);
        filterByCost = settingEntity.getSettingValueAsString(FilterByCostSettingTypeKey, filterByCost);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByIsManufacturer = settingEntity.getSettingValueAsString(FilterByIsManufacturerSettingTypeKey, filterByIsManufacturer);
        filterByIsVendor = settingEntity.getSettingValueAsString(FilterByIsVendorSettingTypeKey, filterByIsVendor);

        filterByPartNumber = settingEntity.getSettingValueAsString(FilterByPartNumberSettingTypeKey, filterByPartNumber);
        filterBySourceName = settingEntity.getSettingValueAsString(FilterBySourceNameSettingTypeKey, filterBySourceName);
        filterByUrl = settingEntity.getSettingValueAsString(FilterByUrlSettingTypeKey, filterByUrl);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayContactInfoSettingTypeKey, displayContactInfo);
        settingEntity.setSettingValue(DisplayCostSettingTypeKey, displayCost);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayIsManufacturerSettingTypeKey, displayIsManufacturer);
        settingEntity.setSettingValue(DisplayIsVendorSettingTypeKey, displayIsVendor);
        settingEntity.setSettingValue(DisplayPartNumberSettingTypeKey, displayPartNumber);
        settingEntity.setSettingValue(DisplayUrlSettingTypeKey, displayUrl);

        settingEntity.setSettingValue(FilterByContactInfoSettingTypeKey, filterByContactInfo);
        settingEntity.setSettingValue(FilterByCostSettingTypeKey, filterByCost);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByIsManufacturerSettingTypeKey, filterByIsManufacturer);
        settingEntity.setSettingValue(FilterByIsVendorSettingTypeKey, filterByIsVendor);
        settingEntity.setSettingValue(FilterByPartNumberSettingTypeKey, filterByPartNumber);
        settingEntity.setSettingValue(FilterBySourceNameSettingTypeKey, filterBySourceName);
        settingEntity.setSettingValue(FilterByUrlSettingTypeKey, filterByUrl);
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByCost = null;
        filterByContactInfo = null;
        filterByIsManufacturer = null;
        filterByIsVendor = null;
        filterByPartNumber = null;
        filterBySourceName = null;
        filterByUrl = null;
    }
    
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterByCost = (String) filters.get("cost").getField();
        filterByContactInfo = (String) filters.get("contactInfo").getField();
        filterByIsManufacturer = (String) filters.get("isManufacturer").getField();
        filterByIsVendor = (String) filters.get("isVendor").getField();
        filterByPartNumber = (String) filters.get("partNumber").getField();
        filterBySourceName = (String) filters.get("source.name").getField();
        filterByUrl = (String) filters.get("url").getField();
    }
    
    public Boolean getDisplayCost() {
        return displayCost;
    }

    public void setDisplayCost(Boolean displayCost) {
        this.displayCost = displayCost;
    }

    public Boolean getDisplayPartNumber() {
        return displayPartNumber;
    }

    public void setDisplayPartNumber(Boolean displayPartNumber) {
        this.displayPartNumber = displayPartNumber;
    }

    public String getFilterByCost() {
        return filterByCost;
    }

    public void setFilterByCost(String filterByCost) {
        this.filterByCost = filterByCost;
    }

    public String getFilterByPartNumber() {
        return filterByPartNumber;
    }

    public void setFilterByPartNumber(String filterByPartNumber) {
        this.filterByPartNumber = filterByPartNumber;
    }

    public String getFilterBySourceName() {
        return filterBySourceName;
    }

    public void setFilterBySourceName(String filterBySourceName) {
        this.filterBySourceName = filterBySourceName;
    }

    public Boolean getDisplayContactInfo() {
        return displayContactInfo;
    }

    public void setDisplayContactInfo(Boolean displayContactInfo) {
        this.displayContactInfo = displayContactInfo;
    }

    public Boolean getDisplayIsManufacturer() {
        return displayIsManufacturer;
    }

    public void setDisplayIsManufacturer(Boolean displayIsManufacturer) {
        this.displayIsManufacturer = displayIsManufacturer;
    }

    public Boolean getDisplayIsVendor() {
        return displayIsVendor;
    }

    public void setDisplayIsVendor(Boolean displayIsVendor) {
        this.displayIsVendor = displayIsVendor;
    }

    public Boolean getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(Boolean displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getFilterByContactInfo() {
        return filterByContactInfo;
    }

    public void setFilterByContactInfo(String filterByContactInfo) {
        this.filterByContactInfo = filterByContactInfo;
    }

    public String getFilterByIsManufacturer() {
        return filterByIsManufacturer;
    }

    public void setFilterByIsManufacturer(String filterByIsManufacturer) {
        this.filterByIsManufacturer = filterByIsManufacturer;
    }

    public String getFilterByIsVendor() {
        return filterByIsVendor;
    }

    public void setFilterByIsVendor(String filterByIsVendor) {
        this.filterByIsVendor = filterByIsVendor;
    }

    public String getFilterByUrl() {
        return filterByUrl;
    }

    public void setFilterByUrl(String filterByUrl) {
        this.filterByUrl = filterByUrl;
    }
    
}
