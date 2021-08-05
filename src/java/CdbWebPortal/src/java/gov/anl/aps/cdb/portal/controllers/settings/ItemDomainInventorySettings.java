/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class ItemDomainInventorySettings extends ItemSettings<ItemDomainInventoryController> {
    
    private static final String DisplayItemElementListItemIdentifier1SettingTypeKey = "ItemDomainInventory.ItemElementList.Display.ItemIdentifier1"; 
    private static final String DisplayItemElementListProjectSettingTypeKey = "ItemDomainInventory.ItemElementList.Display.Project";
    private static final String DisplayItemElementListDescriptionSettingTypeKey = "ItemDomainInventory.ItemElementList.Display.Description";
    private static final String DisplayItemElementListQrIdSettingTypeKey = "ItemDomainInventory.ItemElementList.Display.QrId";
    
    private static final String DisplayCreatedByUserSettingTypeKey = "ItemDomainInventory.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "ItemDomainInventory.List.Display.CreatedOnDateTime";
    private static final String DisplayDescriptionSettingTypeKey = "ItemDomainInventory.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "ItemDomainInventory.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "ItemDomainInventory.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "ItemDomainInventory.List.Display.LastModifiedOnDateTime";
    private static final String DisplayLocationDetailsSettingTypeKey = "ItemDomainInventory.List.Display.LocationDetails";
    private static final String DisplayLocationSettingTypeKey = "ItemDomainInventory.List.Display.Location";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ItemDomainInventory.List.Display.NumberOfItemsPerPage";
    private static final String DisplayOwnerUserSettingTypeKey = "ItemDomainInventory.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "ItemDomainInventory.List.Display.OwnerGroup";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "ItemDomainInventory.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "ItemDomainInventory.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "ItemDomainInventory.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "ItemDomainInventory.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "ItemDomainInventory.List.Display.PropertyTypeId5";
    private static final String DisplayQrIdSettingTypeKey = "ItemDomainInventory.List.Display.QrId";
    private static final String DisplaySerialNumberSettingTypeKey = "ItemDomainInventory.List.Display.SerialNumber";
    private static final String DisplayItemProjectSettingTypeKey = "ItemDomainInventory.List.Display.Project";   
    private static final String DisplayRowExpansionSettingTypeKey = "ItemDomainInventory.List.Display.RowExpansion";
    private static final String LoadRowExpansionPropertyValueSettingTypeKey = "ItemDomainInventory.List.Load.RowExpansionPropertyValue";
    private static final String AutoLoadListFilterValuesSettingTypeKey = "ItemDomainInventory.List.Load.FilterDataTable"; 
    private static final String FilterByComponentSettingTypeKey = "ItemDomainInventory.List.FilterBy.Component";
    private static final String FilterByCreatedByUserSettingTypeKey = "ItemDomainInventory.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "ItemDomainInventory.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "ItemDomainInventory.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "ItemDomainInventory.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "ItemDomainInventory.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByLocationSettingTypeKey = "ItemDomainInventory.List.FilterBy.Location";
    private static final String FilterByLocationDetailsSettingTypeKey = "ItemDomainInventory.List.FilterBy.LocationDetails";
    private static final String FilterByOwnerUserSettingTypeKey = "ItemDomainInventory.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "ItemDomainInventory.List.FilterBy.OwnerGroup";
    private static final String FilterByPropertyValue1SettingTypeKey = "ItemDomainInventory.List.FilterBy.PropertyValue1";
    private static final String FilterByPropertyValue2SettingTypeKey = "ItemDomainInventory.List.FilterBy.PropertyValue2";
    private static final String FilterByPropertyValue3SettingTypeKey = "ItemDomainInventory.List.FilterBy.PropertyValue3";
    private static final String FilterByPropertyValue4SettingTypeKey = "ItemDomainInventory.List.FilterBy.PropertyValue4";
    private static final String FilterByPropertyValue5SettingTypeKey = "ItemDomainInventory.List.FilterBy.PropertyValue5";
    private static final String FilterByTagSettingTypeKey = "ItemDomainInventory.List.FilterBy.Tag";
    private static final String FilterByQrIdSettingTypeKey = "ItemDomainInventory.List.FilterBy.QrId";
    private static final String FilterBySerialNumberSettingTypeKey = "ItemDomainInventory.List.FilterBy.SerialNumber";
    private static final String DisplayListPageHelpFragmentSettingTypeKey = "ItemDomainInventory.Help.ListPage.Display.Fragment";
    private static final String FilterByPropertiesAutoLoadTypeKey = "ItemDomainInventory.List.AutoLoad.FilterBy.Properties";
    private static final String DisplayListDataModelScopeSettingTypeKey = "ItemDomainInventory.List.Scope.Display";
    private static final String DisplayListDataModelScopePropertyTypeIdSettingTypeKey = "ItemDomainInventory.List.Scope.Display.PropertyTypeId";
    private static final String DisplayStatusSettingTypeKey = "ItemDomainInventory.List.Display.Status";
    private static final String FilterByStatusSettingTypeKey = "ItemDomainInventory.List.FilterBy.Status";
    
    private Boolean displayLocationDetails = null;
    private Boolean displayLocation = null;
    private Boolean displaySerialNumber = null;
    private Boolean displayStatus = null;

    private String filterByComponent = null;
    private String filterByLocation = null;
    private String filterByLocationDetails = null;
    private String filterBySerialNumber = null;
    private String filterByTag = null;
    private String filterByStatus = null;
    
    public ItemDomainInventorySettings(ItemDomainInventoryController parentController) {
        super(parentController);
    }
    
    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLocationDetails = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationDetailsSettingTypeKey).getDefaultValue());
        displayLocation = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationSettingTypeKey).getDefaultValue());

        displayQrId = Boolean.parseBoolean(settingTypeMap.get(DisplayQrIdSettingTypeKey).getDefaultValue());
        displaySerialNumber = Boolean.parseBoolean(settingTypeMap.get(DisplaySerialNumberSettingTypeKey).getDefaultValue());
        displayItemProject = Boolean.parseBoolean(settingTypeMap.get(DisplayItemProjectSettingTypeKey).getDefaultValue());
        //displayItemEntityTypes = Boolean.parseBoolean(settingTypeMap.get(DisplayItemEntityTypeSettingTypeKey).getDefaultValue());

        displayRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayRowExpansionSettingTypeKey).getDefaultValue());
        loadRowExpansionPropertyValues = Boolean.parseBoolean(settingTypeMap.get(LoadRowExpansionPropertyValueSettingTypeKey).getDefaultValue());

        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());

        filterByComponent = settingTypeMap.get(FilterByComponentSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();

        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();

        filterByLocation = settingTypeMap.get(FilterByLocationSettingTypeKey).getDefaultValue();
        filterByLocationDetails = settingTypeMap.get(FilterByLocationDetailsSettingTypeKey).getDefaultValue();
        filterByQrId = settingTypeMap.get(FilterByQrIdSettingTypeKey).getDefaultValue();
        filterBySerialNumber = settingTypeMap.get(FilterBySerialNumberSettingTypeKey).getDefaultValue();
        filterByTag = settingTypeMap.get(FilterByTagSettingTypeKey).getDefaultValue();

        filterByPropertyValue1 = settingTypeMap.get(FilterByPropertyValue1SettingTypeKey).getDefaultValue();
        filterByPropertyValue2 = settingTypeMap.get(FilterByPropertyValue2SettingTypeKey).getDefaultValue();
        filterByPropertyValue3 = settingTypeMap.get(FilterByPropertyValue3SettingTypeKey).getDefaultValue();
        filterByPropertyValue4 = settingTypeMap.get(FilterByPropertyValue4SettingTypeKey).getDefaultValue();
        filterByPropertyValue5 = settingTypeMap.get(FilterByPropertyValue5SettingTypeKey).getDefaultValue();
        filterByPropertiesAutoLoad = Boolean.parseBoolean(settingTypeMap.get(FilterByPropertiesAutoLoadTypeKey).getDefaultValue());

        displayListPageHelpFragment = Boolean.parseBoolean(settingTypeMap.get(DisplayListPageHelpFragmentSettingTypeKey).getDefaultValue());

        displayListDataModelScope = settingTypeMap.get(DisplayListDataModelScopeSettingTypeKey).getDefaultValue();
        displayListDataModelScopePropertyTypeId = parseSettingValueAsInteger(settingTypeMap.get(DisplayListDataModelScopePropertyTypeIdSettingTypeKey).getDefaultValue());
        
        displayStatus = Boolean.parseBoolean(settingTypeMap.get(DisplayStatusSettingTypeKey).getDefaultValue());
        filterByStatus = settingTypeMap.get(FilterByStatusSettingTypeKey).getDefaultValue();

        autoLoadListFilterValues = Boolean.parseBoolean(settingTypeMap.get(AutoLoadListFilterValuesSettingTypeKey).getDefaultValue()); 
    }
    
    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);
        
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        displayLocationDetails = settingEntity.getSettingValueAsBoolean(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);
        displayLocation = settingEntity.getSettingValueAsBoolean(DisplayLocationSettingTypeKey, displayLocation);
        displayQrId = settingEntity.getSettingValueAsBoolean(DisplayQrIdSettingTypeKey, displayQrId);
        displaySerialNumber = settingEntity.getSettingValueAsBoolean(DisplaySerialNumberSettingTypeKey, displaySerialNumber);
        displayItemProject = settingEntity.getSettingValueAsBoolean(DisplayItemProjectSettingTypeKey, displayItemProject);
        //displayItemEntityTypes = settingEntity.getSettingValueAsBoolean(DisplayItemEntityTypeSettingTypeKey, displayItemEntityTypes);

        displayRowExpansion = settingEntity.getSettingValueAsBoolean(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        loadRowExpansionPropertyValues = settingEntity.getSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);

        displayPropertyTypeId1 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        filterByComponent = settingEntity.getSettingValueAsString(FilterByComponentSettingTypeKey, filterByComponent);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByOwnerUser = settingEntity.getSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = settingEntity.getSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = settingEntity.getSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = settingEntity.getSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = settingEntity.getSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = settingEntity.getSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByLocation = settingEntity.getSettingValueAsString(FilterByLocationSettingTypeKey, filterByLocation);
        filterByLocationDetails = settingEntity.getSettingValueAsString(FilterByLocationDetailsSettingTypeKey, filterByLocationDetails);
        filterByQrId = settingEntity.getSettingValueAsString(FilterByQrIdSettingTypeKey, filterByQrId);
        filterBySerialNumber = settingEntity.getSettingValueAsString(FilterBySerialNumberSettingTypeKey, filterBySerialNumber);
        filterByTag = settingEntity.getSettingValueAsString(FilterByTagSettingTypeKey, filterByTag);

        filterByPropertyValue1 = settingEntity.getSettingValueAsString(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        filterByPropertyValue2 = settingEntity.getSettingValueAsString(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        filterByPropertyValue3 = settingEntity.getSettingValueAsString(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        filterByPropertyValue4 = settingEntity.getSettingValueAsString(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        filterByPropertyValue5 = settingEntity.getSettingValueAsString(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        filterByPropertiesAutoLoad = settingEntity.getSettingValueAsBoolean(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        displayListPageHelpFragment = settingEntity.getSettingValueAsBoolean(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);

        displayListDataModelScope = settingEntity.getSettingValueAsString(DisplayListDataModelScopeSettingTypeKey, displayListDataModelScope);
        displayListDataModelScopePropertyTypeId = settingEntity.getSettingValueAsInteger(DisplayListDataModelScopePropertyTypeIdSettingTypeKey, displayListDataModelScopePropertyTypeId);
        
        displayStatus = settingEntity.getSettingValueAsBoolean(DisplayStatusSettingTypeKey, displayStatus);
        filterByStatus = settingEntity.getSettingValueAsString(FilterByStatusSettingTypeKey, filterByStatus);

        autoLoadListFilterValues = settingEntity.getSettingValueAsBoolean(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues);        
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
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

        settingEntity.setSettingValue(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);
        settingEntity.setSettingValue(DisplayLocationSettingTypeKey, displayLocation);
        settingEntity.setSettingValue(DisplayQrIdSettingTypeKey, displayQrId);
        settingEntity.setSettingValue(DisplaySerialNumberSettingTypeKey, displaySerialNumber);
        settingEntity.setSettingValue(DisplayItemProjectSettingTypeKey, displayItemProject);
        //settingEntity.setSettingValue(DisplayItemEntityTypeSettingTypeKey, displayItemEntityTypes);

        settingEntity.setSettingValue(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        settingEntity.setSettingValue(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);

        settingEntity.setSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        settingEntity.setSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        settingEntity.setSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        settingEntity.setSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        settingEntity.setSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        settingEntity.setSettingValue(FilterByComponentSettingTypeKey, filterByComponent);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        settingEntity.setSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        settingEntity.setSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        settingEntity.setSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        settingEntity.setSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        settingEntity.setSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        settingEntity.setSettingValue(FilterByLocationSettingTypeKey, filterByLocation);
        settingEntity.setSettingValue(FilterByLocationDetailsSettingTypeKey, filterByLocationDetails);
        settingEntity.setSettingValue(FilterByQrIdSettingTypeKey, filterByQrId);
        settingEntity.setSettingValue(FilterBySerialNumberSettingTypeKey, filterBySerialNumber);
        settingEntity.setSettingValue(FilterByTagSettingTypeKey, filterByTag);

        settingEntity.setSettingValue(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        settingEntity.setSettingValue(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        settingEntity.setSettingValue(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        settingEntity.setSettingValue(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        settingEntity.setSettingValue(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        settingEntity.setSettingValue(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        settingEntity.setSettingValue(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);

        settingEntity.setSettingValue(DisplayListDataModelScopeSettingTypeKey, displayListDataModelScope);
        settingEntity.setSettingValue(DisplayListDataModelScopePropertyTypeIdSettingTypeKey, displayListDataModelScopePropertyTypeId);
        
        settingEntity.setSettingValue(DisplayStatusSettingTypeKey, displayStatus);
        settingEntity.setSettingValue(FilterByStatusSettingTypeKey, filterByStatus);

        settingEntity.setSettingValue(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues);
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByComponent = null;
        filterByLocation = null;
        filterByLocationDetails = null;
        filterByQrId = null;
        filterBySerialNumber = null;
        filterByTag = null;
        filterByPropertyValue1 = null;
        filterByPropertyValue2 = null;
        filterByPropertyValue3 = null;
        filterByPropertyValue4 = null;
        filterByPropertyValue5 = null;
        filterByStatus = null;
    }
    
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterByComponent = (String) filters.get("component.name").getField();
        filterByLocation = (String) filters.get("location.name").getField();
        filterByLocationDetails = (String) filters.get("locationDetails").getField();
        filterByQrId = (String) filters.get("qrId").getField();
        filterBySerialNumber = (String) filters.get("serialNumber").getField();
        filterByTag = (String) filters.get("tag").getField();
        filterByStatus = (String) filters.get("status").getField();

        filterByPropertyValue1 = (String) filters.get("propertyValue1").getField();
        filterByPropertyValue2 = (String) filters.get("propertyValue2").getField();
        filterByPropertyValue3 = (String) filters.get("propertyValue3").getField();
        filterByPropertyValue4 = (String) filters.get("propertyValue4").getField();
        filterByPropertyValue5 = (String) filters.get("propertyValue5").getField();
    }
    
    @Override
    public String getDisplayItemElementListItemIdentifier1Key() {
        return DisplayItemElementListItemIdentifier1SettingTypeKey; 
    }
    
    @Override
    public String getDisplayItemElementListProjectKey() {
        return DisplayItemElementListProjectSettingTypeKey; 
    } 

    @Override
    public String getDisplayItemElementListDescriptionKey() {
        return DisplayItemElementListDescriptionSettingTypeKey; 
    }

    @Override
    public String getDisplayItemElementListQrIdKey() {
        return DisplayItemElementListQrIdSettingTypeKey; 
    }
    
    public void setDisplaySerialNumber(Boolean displaySerialNumber) {
        this.displaySerialNumber = displaySerialNumber;
    }

    @Override
    public String getFilterByItemIdentifier1() {
        return filterBySerialNumber;
    }

    @Override
    public void setFilterByItemIdentifier1(String filterByItemIdentifier1) {
        this.filterBySerialNumber = filterByItemIdentifier1;
    }

    @Override
    public String getFilterByItemIdentifier2() {
        return filterByTag;
    }

    @Override
    public void setFilterByItemIdentifier2(String filterByItemIdentifier2) {
        this.filterByTag = filterByItemIdentifier2;
    }
    
    @Override
    public Boolean getDisplayItemIdentifier1() {
        return displaySerialNumber;
    }

    @Override
    public void setDisplayItemIdentifier1(Boolean displayItemIdentifier1) {
        this.displaySerialNumber = displayItemIdentifier1;
    }

    public Boolean getDisplaySerialNumber() {
        return displaySerialNumber;
    } 

    public Boolean getDisplayLocationDetails() {
        return displayLocationDetails;
    }

    public void setDisplayLocationDetails(Boolean displayLocationDetails) {
        this.displayLocationDetails = displayLocationDetails;
    }

    public Boolean getDisplayLocation() {
        return displayLocation;
    }

    public void setDisplayLocation(Boolean displayLocation) {
        this.displayLocation = displayLocation;
    }

    public String getFilterByComponent() {
        return filterByComponent;
    }

    public void setFilterByComponent(String filterByComponent) {
        this.filterByComponent = filterByComponent;
    }

    public String getFilterByLocation() {
        return filterByLocation;
    }

    public void setFilterByLocation(String filterByLocation) {
        this.filterByLocation = filterByLocation;
    }

    public String getFilterByLocationDetails() {
        return filterByLocationDetails;
    }

    public void setFilterByLocationDetails(String filterByLocationDetails) {
        this.filterByLocationDetails = filterByLocationDetails;
    }

    public String getFilterBySerialNumber() {
        return filterBySerialNumber;
    }

    public void setFilterBySerialNumber(String filterBySerialNumber) {
        this.filterBySerialNumber = filterBySerialNumber;
    }

    public String getFilterByTag() {
        return filterByTag;
    }

    public void setFilterByTag(String filterByTag) {
        this.filterByTag = filterByTag;
   }
    
    public Boolean getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(Boolean displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getFilterByStatus() {
        return filterByStatus;
    }

    public void setFilterByStatus(String filterByStatus) {
        this.filterByStatus = filterByStatus;
    }

    @Override
    public String getDisplayListPageHelpFragmentSettingTypeKey() {
        // No help fragment yet exists for cdb 3.0 Inventory. 
        return null;
        //return DisplayListPageHelpFragmentSettingTypeKey;
    }
}
