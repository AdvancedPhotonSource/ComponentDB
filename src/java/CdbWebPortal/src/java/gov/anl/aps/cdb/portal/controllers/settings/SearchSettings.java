/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.SearchController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author djarosz
 */
public class SearchSettings<EntityController extends SearchController> extends SettingBase<EntityController> {

    /* 
     * Controller specific settings
     */
    private static final String CaseInsensitiveSettingTypeKey = "Search.CaseInsensitive";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Search.Display.NumberOfItemsPerPage";
    private static final String DisplayItemTypesSettingTypeKey = "Search.Display.ItemTypes";
    private static final String DisplayItemCategoriesSettingTypeKey = "Search.Display.ItemCategories";
    private static final String DisplayItemElementsSettingTypeKey = "Search.Display.ItemElements";
    private static final String DisplayLocationsSettingTypeKey = "Search.Display.ItemDomainLocation";
    private static final String DisplayCatalogItemsSettingTypeKey = "Search.Display.ItemDomainCatalog";
    private static final String DisplayInventoryItemsSettingTypeKey = "Search.Display.ItemDomainInventory";
    private static final String DisplayMachineDesignItemsSettingTypeKey = "Search.DisplayItemDomainMachineDesign"; 
    private static final String DisplayCableCatalogItemsSettingTypeKey = "Search.Display.ItemDomainCableCatalog";
    private static final String DisplayCableInventoryItemsSettingTypeKey = "Search.Display.ItemDomainCableInventory";
    private static final String DisplayCableDesignItemsSettingTypeKey = "Search.Display.ItemDomainCableDesign"; 
    private static final String DisplayPropertyTypesSettingTypeKey = "Search.Display.PropertyTypes";
    private static final String DisplayPropertyTypeCategoriesSettingTypeKey = "Search.Display.PropertyTypeCategories";
    private static final String DisplaySourcesSettingTypeKey = "Search.Display.Sources";
    private static final String DisplayUsersSettingTypeKey = "Search.Display.Users";
    private static final String DisplayUserGroupsSettingTypeKey = "Search.Display.UserGroups";
        
    private Boolean caseInsensitive = true;
    
    protected Boolean displayMachineDesignItems = null;
    protected Boolean displayCatalogItems = null;
    protected Boolean displayInventoryItems = null;
    protected Boolean displayCableCatalogItems = null;
    protected Boolean displayCableInventoryItems = null;
    protected Boolean displayCableDesignItems = null;
    protected Boolean displayLocationItems = null;
    protected Boolean displayItemTypes = null;
    protected Boolean displayItemCategories = null;
    protected Boolean displayItemElements = null;
    protected Boolean displayPropertyTypes = null;
    protected Boolean displayPropertyTypeCategories = null;
    protected Boolean displaySources = null;
    protected Boolean displayUsers = null;
    protected Boolean displayUserGroups = null;

    public SearchSettings(EntityController parentController) {
        super(parentController);
    }

    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        caseInsensitive = Boolean.parseBoolean(settingTypeMap.get(CaseInsensitiveSettingTypeKey).getDefaultValue());
        displayCatalogItems = Boolean.parseBoolean(settingTypeMap.get(DisplayCatalogItemsSettingTypeKey).getDefaultValue());
        displayInventoryItems = Boolean.parseBoolean(settingTypeMap.get(DisplayInventoryItemsSettingTypeKey).getDefaultValue());
        displayMachineDesignItems = Boolean.parseBoolean(settingTypeMap.get(DisplayMachineDesignItemsSettingTypeKey).getDefaultValue()); 
        displayCableCatalogItems = Boolean.parseBoolean(settingTypeMap.get(DisplayCableCatalogItemsSettingTypeKey).getDefaultValue());
        displayCableInventoryItems = Boolean.parseBoolean(settingTypeMap.get(DisplayCableInventoryItemsSettingTypeKey).getDefaultValue()); 
        displayCableDesignItems = Boolean.parseBoolean(settingTypeMap.get(DisplayCableDesignItemsSettingTypeKey).getDefaultValue());
        displayItemTypes = Boolean.parseBoolean(settingTypeMap.get(DisplayItemTypesSettingTypeKey).getDefaultValue());
        displayItemCategories = Boolean.parseBoolean(settingTypeMap.get(DisplayItemCategoriesSettingTypeKey).getDefaultValue());
        displayItemElements = Boolean.parseBoolean(settingTypeMap.get(DisplayItemElementsSettingTypeKey).getDefaultValue());
        displayLocationItems = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationsSettingTypeKey).getDefaultValue());
        displayPropertyTypes = Boolean.parseBoolean(settingTypeMap.get(DisplayPropertyTypesSettingTypeKey).getDefaultValue());
        displayPropertyTypeCategories = Boolean.parseBoolean(settingTypeMap.get(DisplayPropertyTypeCategoriesSettingTypeKey).getDefaultValue());
        displaySources = Boolean.parseBoolean(settingTypeMap.get(DisplaySourcesSettingTypeKey).getDefaultValue());
        displayUsers = Boolean.parseBoolean(settingTypeMap.get(DisplayUsersSettingTypeKey).getDefaultValue());
        displayUserGroups = Boolean.parseBoolean(settingTypeMap.get(DisplayUserGroupsSettingTypeKey).getDefaultValue());
    }

    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        caseInsensitive = settingEntity.getSettingValueAsBoolean(CaseInsensitiveSettingTypeKey, caseInsensitive);
        displayCatalogItems = settingEntity.getSettingValueAsBoolean(DisplayCatalogItemsSettingTypeKey, displayCatalogItems);
        displayInventoryItems = settingEntity.getSettingValueAsBoolean(DisplayInventoryItemsSettingTypeKey, displayInventoryItems);
        displayMachineDesignItems = settingEntity.getSettingValueAsBoolean(DisplayMachineDesignItemsSettingTypeKey, displayMachineDesignItems); 
        displayCableCatalogItems = settingEntity.getSettingValueAsBoolean(DisplayCableCatalogItemsSettingTypeKey, displayCableCatalogItems); 
        displayCableInventoryItems = settingEntity.getSettingValueAsBoolean(DisplayCableInventoryItemsSettingTypeKey, displayCableInventoryItems);
        displayCableDesignItems = settingEntity.getSettingValueAsBoolean(DisplayCableDesignItemsSettingTypeKey, displayCableDesignItems);
        displayItemTypes = settingEntity.getSettingValueAsBoolean(DisplayItemTypesSettingTypeKey, displayItemTypes);
        displayItemCategories = settingEntity.getSettingValueAsBoolean(DisplayItemCategoriesSettingTypeKey, displayItemCategories);
        displayItemElements = settingEntity.getSettingValueAsBoolean(DisplayItemElementsSettingTypeKey, displayItemElements);
        displayLocationItems = settingEntity.getSettingValueAsBoolean(DisplayLocationsSettingTypeKey, displayLocationItems);
        displayPropertyTypes = settingEntity.getSettingValueAsBoolean(DisplayPropertyTypesSettingTypeKey, displayPropertyTypes);
        displayPropertyTypeCategories = settingEntity.getSettingValueAsBoolean(DisplayPropertyTypeCategoriesSettingTypeKey, displayPropertyTypeCategories);
        displaySources = settingEntity.getSettingValueAsBoolean(DisplaySourcesSettingTypeKey, displaySources);
        displayUsers = settingEntity.getSettingValueAsBoolean(DisplayUsersSettingTypeKey, displayUsers);
        displayUserGroups = settingEntity.getSettingValueAsBoolean(DisplayUserGroupsSettingTypeKey, displayUserGroups);

    }

    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(CaseInsensitiveSettingTypeKey, caseInsensitive);
        settingEntity.setSettingValue(DisplayInventoryItemsSettingTypeKey, displayCatalogItems);
        settingEntity.setSettingValue(DisplayCatalogItemsSettingTypeKey, displayInventoryItems);
        settingEntity.setSettingValue(DisplayMachineDesignItemsSettingTypeKey, displayMachineDesignItems);
        settingEntity.setSettingValue(DisplayCableCatalogItemsSettingTypeKey, displayCableCatalogItems); 
        settingEntity.setSettingValue(DisplayCableInventoryItemsSettingTypeKey, displayCableInventoryItems);
        settingEntity.setSettingValue(DisplayCableDesignItemsSettingTypeKey, displayCableDesignItems);
        settingEntity.setSettingValue(DisplayItemTypesSettingTypeKey, displayItemTypes);
        settingEntity.setSettingValue(DisplayItemCategoriesSettingTypeKey, displayItemCategories);
        settingEntity.setSettingValue(DisplayItemElementsSettingTypeKey, displayItemElements);
        settingEntity.setSettingValue(DisplayLocationsSettingTypeKey, displayLocationItems);
        settingEntity.setSettingValue(DisplayPropertyTypesSettingTypeKey, displayPropertyTypes);
        settingEntity.setSettingValue(DisplayPropertyTypeCategoriesSettingTypeKey, displayPropertyTypeCategories);
        settingEntity.setSettingValue(DisplaySourcesSettingTypeKey, displaySources);
        settingEntity.setSettingValue(DisplayUsersSettingTypeKey, displayUsers);
        settingEntity.setSettingValue(DisplayUserGroupsSettingTypeKey, displayUserGroups);
    }       

    public Boolean getCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(Boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public Integer getDisplayNumberOfItemsPerPage() {
        return displayNumberOfItemsPerPage;
    }

    public void setDisplayNumberOfItemsPerPage(Integer displayNumberOfItemsPerPage) {
        this.displayNumberOfItemsPerPage = displayNumberOfItemsPerPage;
    }

    public Boolean getDisplayMachineDesignItems() {
        return displayMachineDesignItems;
    }

    public void setDisplayMachineDesignItems(Boolean displayMachineDesignItems) {
        this.displayMachineDesignItems = displayMachineDesignItems;
    }

    public Boolean getDisplayCatalogItems() {
        return displayCatalogItems;
    }

    public void setDisplayCatalogItems(Boolean displayCatalogItems) {
        this.displayCatalogItems = displayCatalogItems;
    }

    public Boolean getDisplayInventoryItems() {
        return displayInventoryItems;
    }

    public void setDisplayInventoryItems(Boolean displayInventoryItems) {
        this.displayInventoryItems = displayInventoryItems;
    }

    public Boolean getDisplayLocationItems() {
        return displayLocationItems;
    }

    public void setDisplayLocationItems(Boolean displayLocationItems) {
        this.displayLocationItems = displayLocationItems;
    }

    public Boolean getDisplayCableCatalogItems() {
        return displayCableCatalogItems;
    }

    public void setDisplayCableCatalogItems(Boolean displayCableCatalogItems) {
        this.displayCableCatalogItems = displayCableCatalogItems;
    }

    public Boolean getDisplayCableInventoryItems() {
        return displayCableInventoryItems;
    }

    public void setDisplayCableInventoryItems(Boolean displayCableInventoryItems) {
        this.displayCableInventoryItems = displayCableInventoryItems;
    }

    public Boolean getDisplayCableDesignItems() {
        return displayCableDesignItems;
    }

    public void setDisplayCableDesignItems(Boolean displayCableDesignItems) {
        this.displayCableDesignItems = displayCableDesignItems;
    }

    public Boolean getDisplayItemTypes() {
        return displayItemTypes;
    }

    public void setDisplayItemTypes(Boolean displayItemTypes) {
        this.displayItemTypes = displayItemTypes;
    }

    public Boolean getDisplayItemCategories() {
        return displayItemCategories;
    }

    public void setDisplayItemCategories(Boolean displayItemCategories) {
        this.displayItemCategories = displayItemCategories;
    }

    public Boolean getDisplayItemElements() {
        return displayItemElements;
    }

    public void setDisplayItemElements(Boolean displayItemElements) {
        this.displayItemElements = displayItemElements;
    }

    public Boolean getDisplayPropertyTypes() {
        return displayPropertyTypes;
    }

    public void setDisplayPropertyTypes(Boolean displayPropertyTypes) {
        this.displayPropertyTypes = displayPropertyTypes;
    }

    public Boolean getDisplayPropertyTypeCategories() {
        return displayPropertyTypeCategories;
    }

    public void setDisplayPropertyTypeCategories(Boolean displayPropertyTypeCategories) {
        this.displayPropertyTypeCategories = displayPropertyTypeCategories;
    }

    public Boolean getDisplaySources() {
        return displaySources;
    }

    public void setDisplaySources(Boolean displaySources) {
        this.displaySources = displaySources;
    }

    public Boolean getDisplayUsers() {
        return displayUsers;
    }

    public void setDisplayUsers(Boolean displayUsers) {
        this.displayUsers = displayUsers;
    }

    public Boolean getDisplayUserGroups() {
        return displayUserGroups;
    }

    public void setDisplayUserGroups(Boolean displayUserGroups) {
        this.displayUserGroups = displayUserGroups;
    }

    @Override
    protected void settingsAreReloaded() {
        parentController.search();
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearListFilters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearSelectFilters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
