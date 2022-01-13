/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;

/**
 *
 * @author djarosz
 */
public class ItemDomainCatalogSettings extends ItemDomainCatalogBaseSettings<ItemDomainCatalogController> {
    
    private static final String DisplayItemElementListItemIdentifier1SettingTypeKey = "ItemDomainCatalog.ItemElementList.Display.ItemIdentifier1";  
    private static final String DisplayItemElementListItemIdentifier2SettingTypeKey = "ItemDomainCatalog.ItemElementList.Display.ItemIdentifier2";
    private static final String DisplayItemElementListItemTypeSettingTypeKey = "ItemDomainCatalog.ItemElementList.Display.ItemType"; 
    private static final String DisplayItemElementListItemCategorySettingTypeKey = "ItemDomainCatalog.ItemElementList.Display.ItemCategory";
    private static final String DisplayItemElementListSourceSettingTypeKey = "ItemDomainCatalog.ItemElementList.Display.Source";
    private static final String DisplayItemElementListProjectSettingTypeKey = "ItemDomainCatalog.ItemElementList.Display.Project";
    private static final String DisplayItemElementListDescriptionSettingTypeKey = "ItemDomainCatalog.ItemElementList.Display.Description";               
    
    private static final String DisplayCategorySettingTypeKey = "ItemDomainCatalog.List.Display.Category";
    private static final String DisplayCreatedByUserSettingTypeKey = "ItemDomainCatalog.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "ItemDomainCatalog.List.Display.CreatedOnDateTime";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ItemDomainCatalog.List.Display.NumberOfItemsPerPage";
    private static final String DisplayDescriptionSettingTypeKey = "ItemDomainCatalog.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "ItemDomainCatalog.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "ItemDomainCatalog.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "ItemDomainCatalog.List.Display.LastModifiedOnDateTime";
    private static final String DisplayModelNumberSettingTypeKey = "ItemDomainCatalog.List.Display.ModelNumber";
    private static final String DisplayOwnerUserSettingTypeKey = "ItemDomainCatalog.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "ItemDomainCatalog.List.Display.OwnerGroup";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "ItemDomainCatalog.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "ItemDomainCatalog.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "ItemDomainCatalog.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "ItemDomainCatalog.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "ItemDomainCatalog.List.Display.PropertyTypeId5";
    private static final String DisplayTypeSettingTypeKey = "ItemDomainCatalog.List.Display.Type";
    private static final String DisplayRowExpansionSettingTypeKey = "ItemDomainCatalog.List.Display.RowExpansion";
    private static final String DisplayAlternateNameSettingTypeKey = "ItemDomainCatalog.List.Display.AlternateName";
    private static final String DisplayItemProjectSettingTypeKey = "ItemDomainCatalog.List.Display.Project";
    private static final String DisplayItemSourcesSettingTypeKey = "ItemDomainCatalog.List.Display.Sources";
    private static final String DisplayComponentInstanceRowExpansionSettingTypeKey = "ItemDomainCatalog.List.Display.ComponentInstance.RowExpansion";
    private static final String LoadRowExpansionPropertyValueSettingTypeKey = "ItemDomainCatalog.List.Load.RowExpansionPropertyValue";
    private static final String LoadComponentInstanceRowExpansionPropertyValueSettingTypeKey = "ItemDomainCatalog.List.Load.ComponentInstance.RowExpansionPropertyValue";
    private static final String AutoLoadListFilterValuesSettingTypeKey = "ItemDomainCatalog.List.Load.FilterDataTable"; 
    private static final String FilterByCategorySettingTypeKey = "ItemDomainCatalog.List.FilterBy.Category";
    private static final String FilterByCreatedByUserSettingTypeKey = "ItemDomainCatalog.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "ItemDomainCatalog.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "ItemDomainCatalog.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "ItemDomainCatalog.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "ItemDomainCatalog.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByNameSettingTypeKey = "ItemDomainCatalog.List.FilterBy.Name";
    private static final String FilterByModelNumberSettingTypeKey = "ItemDomainCatalog.List.FilterBy.ModelNumber";
    private static final String FilterByOwnerUserSettingTypeKey = "ItemDomainCatalog.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "ItemDomainCatalog.List.FilterBy.OwnerGroup";
    private static final String FilterByPropertyValue1SettingTypeKey = "ItemDomainCatalog.List.FilterBy.PropertyValue1";
    private static final String FilterByPropertyValue2SettingTypeKey = "ItemDomainCatalog.List.FilterBy.PropertyValue2";
    private static final String FilterByPropertyValue3SettingTypeKey = "ItemDomainCatalog.List.FilterBy.PropertyValue3";
    private static final String FilterByPropertyValue4SettingTypeKey = "ItemDomainCatalog.List.FilterBy.PropertyValue4";
    private static final String FilterByPropertyValue5SettingTypeKey = "ItemDomainCatalog.List.FilterBy.PropertyValue5";
    private static final String FilterByTypeSettingTypeKey = "ItemDomainCatalog.List.FilterBy.Type";
    private static final String FilterByItemSourcesSettingTypeKey = "ItemDomainCatalog.List.FilterBy.Sources";
    private static final String FilterByPropertiesAutoLoadTypeKey = "ItemDomainCatalog.List.AutoLoad.FilterBy.Properties";    

    private static final String DisplayListPageHelpFragmentSettingTypeKey = "ItemDomainCatalog.Help.ListPage.Display.Fragment";

    private static final String DisplayListDataModelScopeSettingTypeKey = "ItemDomainCatalog.List.Scope.Display";
    private static final String DisplayListDataModelScopePropertyTypeIdSettingTypeKey = "ItemDomainCatalog.List.Scope.Display.PropertyTypeId";

    
    public ItemDomainCatalogSettings(ItemDomainCatalogController parentController) {
        super(parentController);
    }
    
    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);        

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayItemType = Boolean.parseBoolean(settingTypeMap.get(DisplayTypeSettingTypeKey).getDefaultValue());
        displayItemCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayCategorySettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        displayModelNumber = Boolean.parseBoolean(settingTypeMap.get(DisplayModelNumberSettingTypeKey).getDefaultValue());
        displayAlternateName = Boolean.parseBoolean(settingTypeMap.get(DisplayAlternateNameSettingTypeKey).getDefaultValue());
        displayItemProject = Boolean.parseBoolean(settingTypeMap.get(DisplayItemProjectSettingTypeKey).getDefaultValue());
        displayItemSources = Boolean.parseBoolean(settingTypeMap.get(DisplayItemSourcesSettingTypeKey).getDefaultValue());
        //displayItemEntityTypes = Boolean.parseBoolean(settingTypeMap.get(DisplayItemEntityTypesSettingTypeKey).getDefaultValue()); 

        displayRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayRowExpansionSettingTypeKey).getDefaultValue());
        displayComponentInstanceRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayComponentInstanceRowExpansionSettingTypeKey).getDefaultValue());
        loadRowExpansionPropertyValues = Boolean.parseBoolean(settingTypeMap.get(LoadRowExpansionPropertyValueSettingTypeKey).getDefaultValue());
        loadComponentInstanceRowExpansionPropertyValues = Boolean.parseBoolean(settingTypeMap.get(LoadComponentInstanceRowExpansionPropertyValueSettingTypeKey).getDefaultValue());

        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByType = settingTypeMap.get(FilterByTypeSettingTypeKey).getDefaultValue();
        filterByItemSources = settingTypeMap.get(FilterByItemSourcesSettingTypeKey).getDefaultValue();
        filterByCategory = settingTypeMap.get(FilterByCategorySettingTypeKey).getDefaultValue();
        filterByModelNumber = settingTypeMap.get(FilterByModelNumberSettingTypeKey).getDefaultValue();
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

        displayListPageHelpFragment = Boolean.parseBoolean(settingTypeMap.get(DisplayListPageHelpFragmentSettingTypeKey).getDefaultValue());

        displayListDataModelScope = settingTypeMap.get(DisplayListDataModelScopeSettingTypeKey).getDefaultValue();
        displayListDataModelScopePropertyTypeId = parseSettingValueAsInteger(settingTypeMap.get(DisplayListDataModelScopePropertyTypeIdSettingTypeKey).getDefaultValue());
                
        autoLoadListFilterValues = Boolean.parseBoolean(settingTypeMap.get(AutoLoadListFilterValuesSettingTypeKey).getDefaultValue()); 

        parentController.resetDomainEntityPropertyTypeIdIndexMappings();
    }
    
    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);
        
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayItemType = settingEntity.getSettingValueAsBoolean(DisplayTypeSettingTypeKey, displayItemType);
        displayItemCategory = settingEntity.getSettingValueAsBoolean(DisplayCategorySettingTypeKey, displayItemCategory);
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        displayModelNumber = settingEntity.getSettingValueAsBoolean(DisplayModelNumberSettingTypeKey, displayModelNumber);
        displayAlternateName = settingEntity.getSettingValueAsBoolean(DisplayAlternateNameSettingTypeKey, displayAlternateName);
        displayItemProject = settingEntity.getSettingValueAsBoolean(DisplayItemProjectSettingTypeKey, displayItemProject);
        displayItemSources = settingEntity.getSettingValueAsBoolean(DisplayItemSourcesSettingTypeKey, displayItemSources);
        //displayItemEntityTypes = settingEntity.getSettingValueAsBoolean(DisplayItemEntityTypesSettingTypeKey, displayItemEntityTypes);

        displayRowExpansion = settingEntity.getSettingValueAsBoolean(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        displayComponentInstanceRowExpansion = settingEntity.getSettingValueAsBoolean(DisplayComponentInstanceRowExpansionSettingTypeKey, displayComponentInstanceRowExpansion);
        loadRowExpansionPropertyValues = settingEntity.getSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);
        loadComponentInstanceRowExpansionPropertyValues = settingEntity.getSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadComponentInstanceRowExpansionPropertyValues);

        displayPropertyTypeId1 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        filterByName = settingEntity.getSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByType = settingEntity.getSettingValueAsString(FilterByTypeSettingTypeKey, filterByType);
        filterByItemSources = settingEntity.getSettingValueAsString(FilterByItemSourcesSettingTypeKey, filterByItemSources);
        filterByCategory = settingEntity.getSettingValueAsString(FilterByCategorySettingTypeKey, filterByCategory);
        filterByModelNumber = settingEntity.getSettingValueAsString(FilterByModelNumberSettingTypeKey, filterByModelNumber);
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

        displayListPageHelpFragment = settingEntity.getSettingValueAsBoolean(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);

        displayListDataModelScope = settingEntity.getSettingValueAsString(DisplayListDataModelScopeSettingTypeKey, displayListDataModelScope);
        displayListDataModelScopePropertyTypeId = settingEntity.getSettingValueAsInteger(DisplayListDataModelScopePropertyTypeIdSettingTypeKey, displayListDataModelScopePropertyTypeId);
                
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
        settingEntity.setSettingValue(DisplayModelNumberSettingTypeKey, displayModelNumber);
        settingEntity.setSettingValue(DisplayAlternateNameSettingTypeKey, displayAlternateName);
        settingEntity.setSettingValue(DisplayItemProjectSettingTypeKey, displayItemProject);
        settingEntity.setSettingValue(DisplayItemSourcesSettingTypeKey, displayItemSources);
        //settingEntity.setSettingValue(DisplayItemEntityTypesSettingTypeKey, displayItemEntityTypes);

        settingEntity.setSettingValue(DisplayTypeSettingTypeKey, displayItemType);
        settingEntity.setSettingValue(DisplayCategorySettingTypeKey, displayItemCategory);

        settingEntity.setSettingValue(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        settingEntity.setSettingValue(DisplayComponentInstanceRowExpansionSettingTypeKey, displayComponentInstanceRowExpansion);
        settingEntity.setSettingValue(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);
        settingEntity.setSettingValue(LoadComponentInstanceRowExpansionPropertyValueSettingTypeKey, loadComponentInstanceRowExpansionPropertyValues);

        settingEntity.setSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        settingEntity.setSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        settingEntity.setSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        settingEntity.setSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        settingEntity.setSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        settingEntity.setSettingValue(FilterByNameSettingTypeKey, filterByName);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        settingEntity.setSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        settingEntity.setSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        settingEntity.setSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        settingEntity.setSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        settingEntity.setSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        settingEntity.setSettingValue(FilterByTypeSettingTypeKey, filterByType);
        settingEntity.setSettingValue(FilterByItemSourcesSettingTypeKey, filterByItemSources);
        settingEntity.setSettingValue(FilterByCategorySettingTypeKey, filterByCategory);
        settingEntity.setSettingValue(FilterByModelNumberSettingTypeKey, filterByModelNumber);

        settingEntity.setSettingValue(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        settingEntity.setSettingValue(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        settingEntity.setSettingValue(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        settingEntity.setSettingValue(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        settingEntity.setSettingValue(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        settingEntity.setSettingValue(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        settingEntity.setSettingValue(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);

        settingEntity.setSettingValue(DisplayListDataModelScopeSettingTypeKey, displayListDataModelScope);
        settingEntity.setSettingValue(DisplayListDataModelScopePropertyTypeIdSettingTypeKey, displayListDataModelScopePropertyTypeId);
        
        settingEntity.setSettingValue(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues);

    }
    
    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, FilterMeta> filters = dataTable.getFilterByAsMap();
        filterByType = (String) filters.get("componentType").getField();
        filterByCategory = (String) filters.get("componentTypeCategory").getField();
        filterByModelNumber = (String) filters.get("modelNumber").getField();

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
    public String getDisplayItemElementListItemIdentifier2Key() {
        return DisplayItemElementListItemIdentifier2SettingTypeKey;
    }

    @Override
    public String getDisplayItemElementListItemTypeKey() {
        return DisplayItemElementListItemTypeSettingTypeKey;
    }

    @Override
    public String getDisplayItemElementListItemCategoryKey() {
        return DisplayItemElementListItemCategorySettingTypeKey;
    }

    @Override
    public String getDisplayItemElementListSourceKey() {
        return DisplayItemElementListSourceSettingTypeKey;
    }

    @Override
    public String getDisplayItemElementListProjectKey() {
        return DisplayItemElementListProjectSettingTypeKey;
    }

    @Override
    public String getDisplayItemElementListDescriptionKey() {
        return DisplayItemElementListDescriptionSettingTypeKey;
    }

}
