/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.constants.ItemDisplayListDataModelScope;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemElementController;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
public abstract class ItemSettings<ItemControllerBase extends ItemController> extends CdbDomainEntitySettings<ItemControllerBase> {
    
    private static final Logger logger = LogManager.getLogger(ItemSettings.class.getName());
    
    // Name and image is always shown. 
    protected final int ItemElementItemInitialColumnCount = 2; 
    
    protected Boolean displayItemIdentifier1 = null;
    protected Boolean displayItemIdentifier2 = null;
    protected Boolean displayItemSources = null;
    protected Boolean displayItemType = null;
    protected Boolean displayItemCategory = null;
    protected Boolean displayName = null;
    protected Boolean displayDerivedFromItem = null;
    protected Boolean displayQrId = null;
    protected Boolean displayItemProject = null;
    protected Boolean displayItemEntityTypes = null;
    protected Boolean autoLoadListFilterValues = false;
    protected Boolean displayItemListTreeView = null;    
    protected Boolean displayItemElementListItemIdentifier1 = false; 
    protected Boolean displayItemElementListItemIdentifier2 = false; 
    protected Boolean displayItemElementListItemType = false; 
    protected Boolean displayItemElementListItemCategory = false; 
    protected Boolean displayItemElementListSource = false; 
    protected Boolean displayItemElementListProject = false; 
    protected Boolean displayItemElementListDescription = false; 
    protected Boolean displayItemElementListQrId = false;
    
    protected String displayListDataModelScope = ItemDisplayListDataModelScope.showAll.getValue();
    protected Integer displayListDataModelScopePropertyTypeId = null;

    protected String filterByItemSources = null;
    protected String filterByQrId = null;
    protected String filterByItemEntityType = null; 
    
    protected ItemElementSettings itemElementSettings = null;

    public ItemSettings(ItemControllerBase parentController) {
        super(parentController);
    }
    
    public void saveItemElementListSettingsForSessionSettingEntity(SettingEntity settingEntity) {
         if (getDisplayItemElementListItemIdentifier1Key() != null) {
            settingEntity.setSettingValue(getDisplayItemElementListItemIdentifier1Key(), displayItemElementListItemIdentifier1);
        }
        
        if (getDisplayItemElementListItemIdentifier2Key() != null) {
            settingEntity.setSettingValue(getDisplayItemElementListItemIdentifier2Key(), displayItemElementListItemIdentifier2);
        }
        
        if (getDisplayItemElementListItemTypeKey() != null) {
             settingEntity.setSettingValue(getDisplayItemElementListItemTypeKey(), displayItemElementListItemType);
        }
        
        if (getDisplayItemElementListItemCategoryKey() != null) {
            settingEntity.setSettingValue(getDisplayItemElementListItemCategoryKey(), displayItemElementListItemCategory);
        }
        
        if (getDisplayItemElementListProjectKey() != null) {
            settingEntity.setSettingValue(getDisplayItemElementListProjectKey(), displayItemElementListProject);
        }
        
        if (getDisplayItemElementListSourceKey() != null) {
            settingEntity.setSettingValue(getDisplayItemElementListSourceKey(), displayItemElementListSource);
        }
        
        if (getDisplayItemElementListDescriptionKey() != null) {
            settingEntity.setSettingValue(getDisplayItemElementListDescriptionKey(), displayItemElementListDescription);
        }
        
        if (getDisplayItemElementListQrIdKey() != null) {
            settingEntity.setSettingValue(getDisplayItemElementListQrIdKey(), displayItemElementListQrId);
        }
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);
        if (getDisplayItemElementListItemIdentifier1Key() != null) {
            displayItemElementListItemIdentifier1 = Boolean.parseBoolean(settingTypeMap.get(getDisplayItemElementListItemIdentifier1Key()).getDefaultValue()); 
        }
        
        if (getDisplayItemElementListItemIdentifier2Key() != null) {
            displayItemElementListItemIdentifier2 = Boolean.parseBoolean(settingTypeMap.get(getDisplayItemElementListItemIdentifier2Key()).getDefaultValue()); 
        }
        
        if (getDisplayItemElementListItemTypeKey() != null) {
            displayItemElementListItemType = Boolean.parseBoolean(settingTypeMap.get(getDisplayItemElementListItemTypeKey()).getDefaultValue()); 
        }
        
        if (getDisplayItemElementListItemCategoryKey() != null) {
            displayItemElementListItemCategory = Boolean.parseBoolean(settingTypeMap.get(getDisplayItemElementListItemCategoryKey()).getDefaultValue()); 
        }
        
        if (getDisplayItemElementListProjectKey() != null) {
            displayItemElementListProject = Boolean.parseBoolean(settingTypeMap.get(getDisplayItemElementListProjectKey()).getDefaultValue()); 
        }
        
        if (getDisplayItemElementListSourceKey() != null) {
            displayItemElementListSource = Boolean.parseBoolean(settingTypeMap.get(getDisplayItemElementListSourceKey()).getDefaultValue()); 
        }
        
        if (getDisplayItemElementListDescriptionKey() != null) {
            displayItemElementListDescription = Boolean.parseBoolean(settingTypeMap.get(getDisplayItemElementListDescriptionKey()).getDefaultValue()); 
        }
        
        if (getDisplayItemElementListQrIdKey() != null) {
            displayItemElementListQrId = Boolean.parseBoolean(settingTypeMap.get(getDisplayItemElementListQrIdKey()).getDefaultValue()); 
        }
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);
        if (getDisplayItemElementListItemIdentifier1Key() != null) {
            displayItemElementListItemIdentifier1 = settingEntity.getSettingValueAsBoolean(getDisplayItemElementListItemIdentifier1Key(), displayItemElementListItemIdentifier1); 
        }
        
        if (getDisplayItemElementListItemIdentifier2Key() != null) {
            displayItemElementListItemIdentifier2 = settingEntity.getSettingValueAsBoolean(getDisplayItemElementListItemIdentifier2Key(), displayItemElementListItemIdentifier2); 
        }
        
        if (getDisplayItemElementListItemTypeKey() != null) {
            displayItemElementListItemType = settingEntity.getSettingValueAsBoolean(getDisplayItemElementListItemTypeKey(), displayItemElementListItemType); 
        }
        
        if (getDisplayItemElementListItemCategoryKey() != null) {
            displayItemElementListItemCategory = settingEntity.getSettingValueAsBoolean(getDisplayItemElementListItemCategoryKey(), displayItemElementListItemCategory); 
        }
        
        if (getDisplayItemElementListProjectKey() != null) {
            displayItemElementListProject = settingEntity.getSettingValueAsBoolean(getDisplayItemElementListProjectKey(), displayItemElementListProject);         
        }
        
        if (getDisplayItemElementListSourceKey() != null) {
            displayItemElementListSource = settingEntity.getSettingValueAsBoolean(getDisplayItemElementListSourceKey(), displayItemElementListSource); 
        }
        
        if (getDisplayItemElementListDescriptionKey() != null) {
            displayItemElementListDescription = settingEntity.getSettingValueAsBoolean(getDisplayItemElementListDescriptionKey(), displayItemElementListDescription); 
        }
        
        if (getDisplayItemElementListQrIdKey() != null) {
            displayItemElementListQrId = settingEntity.getSettingValueAsBoolean(getDisplayItemElementListQrIdKey(), displayItemElementListQrId); 
        }
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        super.saveSettingsForSessionSettingEntity(settingEntity);
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByItemIdentifier1 = null;
        filterByItemIdentifier2 = null;

        filterByPropertyValue1 = null;
        filterByPropertyValue2 = null;
        filterByPropertyValue3 = null;
        filterByPropertyValue4 = null;
        filterByPropertyValue5 = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        filterByItemIdentifier1 = null;
        filterByItemIdentifier2 = null;
    }    
    
    public boolean isDisplayGlobalProjectWarning() {
        if (displayListDataModelScope != null) {
            if (displayListDataModelScope.equals(ItemDisplayListDataModelScope.showFavorites.getValue())) {
                return true;
            }
            if (displayListDataModelScope.equals(ItemDisplayListDataModelScope.showOwned.getValue())) {
                return true;
            }
            if (displayListDataModelScope.equals(ItemDisplayListDataModelScope.showOwnedPlusFavorites.getValue())) {
                return true;
            }
        }
        return false;
    }

    public ItemElementSettings getItemElementSettings() {
        if (itemElementSettings == null) {
            itemElementSettings = ItemElementController.getInstance().getSettingObject(); 
        }
        return itemElementSettings;
    }
    
    public void toggleSimpleComplexElementsList() {                
        getItemElementSettings().toggleGlobalSimpleComplexElementsList();
    }
    
    public Boolean getDisplayItemElementSimpleView() {
        return getItemElementSettings().getGlobalDisplayItemElementSimpleView();
    }

    public boolean isDisplayListDataModelScopePropertyTypeSelection() {
        if (displayListDataModelScope != null) {
            return displayListDataModelScope.equals(ItemDisplayListDataModelScope.showItemsWithPropertyType.getValue());
        }
        return false;
    }
    
    /**
     * Override for locatable item types as needed. 
     * 
     * Setting for displaying locatable item location in data table
     * 
     * @return 
     */
    public Boolean getDisplayLocation() {
        return false;
    }
    
    /**
     * Override for locatable item types as needed. 
     * 
     * Setting for displaying locatable item location in data table
     * 
     * @return 
     */
    public Boolean getDisplayLocationDetails() {
        return false; 
    }
    
    public String getDisplayItemElementListItemIdentifier1Key() {
        return null;
    }

    public Boolean getDisplayItemElementListItemIdentifier1() {
        return displayItemElementListItemIdentifier1;
    }

    public void setDisplayItemElementListItemIdentifier1(Boolean displayItemElementListItemIdentifier1){
        this.displayItemElementListItemIdentifier1 = displayItemElementListItemIdentifier1; 
    }

    public String getDisplayItemElementListItemIdentifier2Key() {
        return null; 
    }
    
    public Boolean getDisplayItemElementListItemIdentifier2() {
        return displayItemElementListItemIdentifier2;
    }

    public void setDisplayItemElementListItemIdentifier2(Boolean displayItemElementListItemIdentifier2) {
        this.displayItemElementListItemIdentifier2 = displayItemElementListItemIdentifier2;
    }

    public String getDisplayItemElementListItemTypeKey() {
        return null; 
    }
    
    public Boolean getDisplayItemElementListItemType() {
        return displayItemElementListItemType;
    }

    public void setDisplayItemElementListItemType(Boolean displayItemElementListItemType) {
        this.displayItemElementListItemType = displayItemElementListItemType;
    }
    
    public String getDisplayItemElementListItemCategoryKey() {
        return null; 
    }

    public Boolean getDisplayItemElementListItemCategory() {
        return displayItemElementListItemCategory;
    }

    public void setDisplayItemElementListItemCategory(Boolean displayItemElementListItemCategory) {
        this.displayItemElementListItemCategory = displayItemElementListItemCategory;
    }
    
    public String getDisplayItemElementListSourceKey() {
        return null;
    }

    public Boolean getDisplayItemElementListSource() {
        return displayItemElementListSource;
    }

    public void setDisplayItemElementListSource(Boolean displayItemElementListSource) {
        this.displayItemElementListSource = displayItemElementListSource;
    }
    
    public String getDisplayItemElementListProjectKey() {
        return null; 
    }

    public Boolean getDisplayItemElementListProject() {
        return displayItemElementListProject;
    }

    public void setDisplayItemElementListProject(Boolean displayItemElementListProject) {
        this.displayItemElementListProject = displayItemElementListProject;
    }
    
    public String getDisplayItemElementListDescriptionKey() {
        return null; 
    }

    public Boolean getDisplayItemElementListDescription() {
        return displayItemElementListDescription;
    }

    public void setDisplayItemElementListDescription(Boolean displayItemElementListDescription) {
        this.displayItemElementListDescription = displayItemElementListDescription;
    }
    
    public String getDisplayItemElementListQrIdKey() {
        return null; 
    }

    public Boolean getDisplayItemElementListQrId() {
        return displayItemElementListQrId;
    }

    public void setDisplayItemElementListQrId(Boolean displayItemElementListQrId) {
        this.displayItemElementListQrId = displayItemElementListQrId;
    }
    
    public int getItemElementItemColumnCount() {        
        int count = ItemElementItemInitialColumnCount; 
        
        if (getDisplayItemElementListItemIdentifier1()) {
            count++; 
        }
        if (getDisplayItemElementListItemIdentifier2()) {
            count++; 
        }
        if (getDisplayItemElementListDescription()) {
            count++; 
        }
        if (getDisplayItemElementListItemCategory()) {
            count++; 
        }
        if (getDisplayItemElementListItemType()) {
            count++; 
        }
        if (getDisplayItemElementListProject()) {
            count++; 
        }
        if (getDisplayItemElementListSource()) {
            count++; 
        }
        if (getDisplayItemElementListQrId()) {
            count++; 
        }
        return count; 
    }
    
    public Boolean getDisplayItemProject() {
        if (displayItemProject == null) {
            displayItemProject = parentController.getEntityDisplayItemProject();
        }
        return displayItemProject;
    }

    public void setDisplayItemProject(Boolean displayItemProject) {
        this.displayItemProject = displayItemProject;
    }

    public Boolean getDisplayItemEntityTypes() {
        if (displayItemEntityTypes == null) {
            displayItemEntityTypes = parentController.getEntityDisplayItemEntityTypes();
        }
        return displayItemEntityTypes;
    }

    public void setDisplayItemEntityTypes(Boolean displayItemEntityTypes) {
        this.displayItemEntityTypes = displayItemEntityTypes;
    }

    public Boolean getDisplayItemIdentifier1() {
        if (displayItemIdentifier1 == null) {
            displayItemIdentifier1 = parentController.getEntityDisplayItemIdentifier1();
        }
        return displayItemIdentifier1;
    }

    public void setDisplayItemIdentifier1(Boolean displayItemIdentifier1) {
        this.displayItemIdentifier1 = displayItemIdentifier1;
    }

    public Boolean getDisplayItemIdentifier2() {
        if (displayItemIdentifier2 == null) {
            displayItemIdentifier2 = parentController.getEntityDisplayItemIdentifier2();
        }
        return displayItemIdentifier2;
    }

    public void setDisplayItemIdentifier2(Boolean displayItemIdentifier2) {
        this.displayItemIdentifier2 = displayItemIdentifier2;
    }

    public Boolean getDisplayItemSources() {
        if (displayItemSources == null) {
            displayItemSources = parentController.getEntityDisplayItemSources();
        }
        return displayItemSources;
    }

    public void setDisplayItemSources(Boolean displayItemSources) {
        this.displayItemSources = displayItemSources;
    }

    public Boolean getDisplayName() {
        if (displayName == null) {
            displayName = parentController.getEntityDisplayItemName();
        }
        return displayName;
    }

    public void setDisplayName(Boolean displayItemName) {
        this.displayName = displayItemName;
    }

    public Boolean getDisplayItemType() {
        if (displayItemType == null) {
            displayItemType = parentController.getEntityDisplayItemType();
        }
        return displayItemType;
    }

    public void setDisplayItemType(Boolean displayItemType) {
        this.displayItemType = displayItemType;
    }

    public Boolean getDisplayItemCategory() {
        if (displayItemCategory == null) {
            displayItemCategory = parentController.getEntityDisplayItemCategory();
        }
        return displayItemCategory;
    }

    public void setDisplayItemCategory(Boolean displayItemCategory) {
        this.displayItemCategory = displayItemCategory;
    }

    public String getFilterByItemSources() {
        return filterByItemSources;
    }

    public void setFilterByItemSources(String filterByItemSources) {
        this.filterByItemSources = filterByItemSources;
    }

    public Boolean getDisplayDerivedFromItem() {
        if (displayDerivedFromItem == null) {
            displayDerivedFromItem = parentController.getEntityDisplayDerivedFromItem();
        }
        return displayDerivedFromItem;
    }

    public void setDisplayDerivedFromItem(Boolean displayDerivedFromItem) {
        this.displayDerivedFromItem = displayDerivedFromItem;
    }

    public Boolean getDisplayItemListTreeView() {
        return displayItemListTreeView;
    }

    public void setDisplayItemListTreeView(Boolean displayItemListTreeView) {
        this.displayItemListTreeView = displayItemListTreeView;
    }
    
    public String getFilterByItemIdentifier1() {
        return filterByItemIdentifier1;
    }

    public void setFilterByItemIdentifier1(String filterByItemIdentifier1) {
        this.filterByItemIdentifier1 = filterByItemIdentifier1;
    }

    public String getFilterByItemIdentifier2() {
        return filterByItemIdentifier2;
    }

    public void setFilterByItemIdentifier2(String filterByItemIdentifier2) {
        this.filterByItemIdentifier2 = filterByItemIdentifier2;
    }

    public Boolean getDisplayQrId() {
        if (displayQrId == null) {
            displayQrId = parentController.getEntityDisplayQrId();
        }

        return displayQrId;
    }

    public void setDisplayQrId(Boolean displayQrId) {
        this.displayQrId = displayQrId;
    }
    
    public String getFilterByQrId() {
        return filterByQrId;
    }

    public void setFilterByQrId(String filterByQrId) {
        this.filterByQrId = filterByQrId;
    }  

    public String getFilterByItemEntityType() {
        return filterByItemEntityType;
    }

    public void setFilterByItemEntityType(String filterByItemEntityType) {
        this.filterByItemEntityType = filterByItemEntityType;
    }
    
    public String getDisplayListDataModelScope() {
        return displayListDataModelScope;
    }

    public void setDisplayListDataModelScope(String listDataModelMode) {
        if (!this.displayListDataModelScope.equals(listDataModelMode)) {
            parentController.listDataModelScopeChanged();
        }
        this.displayListDataModelScope = listDataModelMode;
    }
    
    public Integer getDisplayListDataModelScopePropertyTypeId() {
        return displayListDataModelScopePropertyTypeId;
    }

    public void setDisplayListDataModelScopePropertyTypeId(Integer displayListDataModelScopePropertyTypeId) {
        if (!Objects.equals(this.displayListDataModelScopePropertyTypeId, displayListDataModelScopePropertyTypeId)) {
            parentController.resetListDataModel();
        }
        this.displayListDataModelScopePropertyTypeId = displayListDataModelScopePropertyTypeId;
    }
    
    public void setAutoLoadListFilterValues(Boolean autoLoadListFilterValues) {
        this.autoLoadListFilterValues = autoLoadListFilterValues;
    }

    public Boolean getAutoLoadListFilterValues() {
        return autoLoadListFilterValues;
    }
    
    public ItemMetadataPropertyInfo getCoreMetadataPropertyInfo() {
        return parentController.getCoreMetadataPropertyInfo();
    }
    
    public PropertyType getCoreMetadataPropertyType() {
        return parentController.getCoreMetadataPropertyType();
    }
}
