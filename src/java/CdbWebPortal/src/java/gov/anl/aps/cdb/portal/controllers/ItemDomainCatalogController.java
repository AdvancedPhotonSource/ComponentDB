/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import static gov.anl.aps.cdb.portal.controllers.CdbEntityController.parseSettingValueAsInteger;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.jsf.beans.SparePartsBean;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.CatalogItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author djarosz
 */
@Named("itemDomainCatalogController")
@SessionScoped
public class ItemDomainCatalogController extends ItemController<ItemDomainCatalog, ItemDomainCatalogFacade>  {

    private final String DOMAIN_TYPE_NAME = "Catalog";
    private final String DERIVED_DOMAIN_NAME = "Inventory";

    /*
     * Controller specific settings
     */
    private static final String DisplayItemElementListItemIdentifier1SettingTypeKey = "ItemDomainCatalog.ItemElementList.Display.ItemIdentifier1"; 
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
    private static final String FilterByPropertiesAutoLoadTypeKey = "ItemDomainCatalog.List.AutoLoad.FilterBy.Properties";    

    private static final String DisplayListPageHelpFragmentSettingTypeKey = "ItemDomainCatalog.Help.ListPage.Display.Fragment";

    private static final String DisplayListDataModelScopeSettingTypeKey = "ItemDomainCatalog.List.Scope.Display";
    private static final String DisplayListDataModelScopePropertyTypeIdSettingTypeKey = "ItemDomainCatalog.List.Scope.Display.PropertyTypeId";

    private static final Logger logger = Logger.getLogger(ItemDomainCatalogController.class.getName());

    private Boolean displayModelNumber = null;
    private Boolean displayAlternateName = null;

    private String filterByType = null;
    private String filterByCategory = null;
    private String filterByModelNumber = null;
    private String filterBySources = null;

    private Boolean selectDisplayType = true;
    private Boolean selectDisplayCategory = true;
    private Boolean selectDisplayModelNumber = true;

    private String selectFilterByType = null;
    private String selectFilterByCategory = null;
    private String selectFilterByModelNumber = null;

    private Boolean loadComponentInstanceRowExpansionPropertyValues = null;
    private Boolean displayComponentInstanceRowExpansion = null;
    
    // Set externally from item element controller
    private Boolean displayItemElementListItemIdentifier1 = null; 

    private List<ItemDomainInventory> inventorySparesList = null;
    private List<ItemDomainInventory> inventoryNonSparesList = null;
    private Boolean displayInventorySpares = null;
    
    @EJB
    ItemDomainCatalogFacade itemDomainCatalogFacade;    

    @Override
    public List<ItemDomainCatalog> getItemList() {
        List<ItemDomainCatalog> itemList = super.getItemList();
        return itemList; 
    }

    public ItemDomainCatalogController() {
        super();
    }

    @Override
    public String getEntityTypeName() {
        return "component";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Catalog Item";
    }

    @Override
    public String getItemItemTypeTitle() {
        return "Function";
    }

    @Override
    public String getItemItemCategoryTitle() {
        return "Technical System";
    }

    @Override
    public boolean isItemHasSimpleListView() {
        return true;
    }

    public Boolean getLoadComponentInstanceRowExpansionPropertyValues() {
        return loadComponentInstanceRowExpansionPropertyValues;
    }

    @Override
    public String getDisplayListPageHelpFragmentSettingTypeKey() {
        // No help fragment yet exists for cdb 3.0 catalog
        return null;
        //return DisplayListPageHelpFragmentSettingTypeKey;
    } 

    @Override
    protected ItemDomainCatalog cloneCreateItemElements(ItemDomainCatalog clonedItem, ItemDomainCatalog cloningFrom) {
        return cloneCreateItemElements(clonedItem, cloningFrom, true);
    } 

    @Override
    public ItemElement createItemElement(ItemDomainCatalog item) {
        ItemElement itemElement = super.createItemElement(item);
        itemElement.setIsRequired(true);
        itemElement.setDerivedFromItemElementList(new ArrayList<>());
               
        return itemElement; 
    }
    
    protected void addInventoryElementsForEachInventoryItem(ItemElement catalogItemElement) {
        // Get fresh db representation of parent item. 
        ItemDomainCatalog parentItem = findById(catalogItemElement.getParentItem().getId()); 
        
        
        List<ItemDomainInventory> inventoryItems = parentItem.getInventoryItemList();
        for (ItemDomainInventory inventoryItem : inventoryItems) {
            // verify item element for the particular element needs to be created. 
            if (inventoryItemContainsItemElementForCatalogElement(catalogItemElement, inventoryItem)) {
                continue; 
            } 
            
            ItemController inventoryItemController = ItemController.findDomainControllerForItem(inventoryItem); 
            ItemElement inventoryItemElement = inventoryItemController.createItemElement(inventoryItem); 
            
            inventoryItemElement.setDerivedFromItemElement(catalogItemElement);
            EntityInfo inventoryItemEntityInfo = inventoryItem.getEntityInfo(); 
            UserInfo inventoryItemOwner = inventoryItemEntityInfo.getOwnerUser(); 
            UserGroup inventoryOwnerGroup = inventoryItemEntityInfo.getOwnerUserGroup(); 
            inventoryItemElement.getEntityInfo().setOwnerUser(inventoryItemOwner);
            inventoryItemElement.getEntityInfo().setOwnerUserGroup(inventoryOwnerGroup);
            catalogItemElement.getDerivedFromItemElementList().add(inventoryItemElement); 
        }       
    }
    
    private boolean inventoryItemContainsItemElementForCatalogElement(ItemElement catalogElement, ItemDomainInventory inventoryItem) {
        for (ItemElement inventoryElement : inventoryItem.getItemElementDisplayList()) {
            if (inventoryElement.getDerivedFromItemElement().getId().equals(catalogElement.getId())) {
                return true; 
            }
        }
        return false; 
    }

    @Override
    public ItemElementConstraintInformation loadItemElementConstraintInformation(ItemElement itemElement) {
        return new CatalogItemElementConstraintInformation(itemElement);
    } 

    @Override
    public ItemElement finalizeItemElementRequiredStatusChanged(ItemElement itemElement) throws CdbException {
        itemElement = super.finalizeItemElementRequiredStatusChanged(itemElement); 
        
        if (itemElement.getIsRequired() == false) {
            ItemElementController itemElementController = ItemElementController.getInstance(); 
            
            List<ItemElement> derivedItemElementList = new ArrayList<>(); 
            derivedItemElementList.addAll(itemElement.getDerivedFromItemElementList());
            
            for (ItemElement inventoryItemElement : derivedItemElementList) {
                // Verify safe to remove
                ItemElementConstraintInformation ieci; 
                ieci = itemElementController.getItemElementConstraintInformation(inventoryItemElement); 
                if (ieci.isSafeToRemoveSelf()) {
                    itemElementController.destroy(inventoryItemElement);
                    // Will need to perform destroy on each inventory element. 
                    itemElement.getDerivedFromItemElementList().remove(inventoryItemElement); 
                }
            }
            itemElementController.setCurrent(itemElement);
        } else {
            // Item is required therefore should be part of each inventory item. 
            addInventoryElementsForEachInventoryItem(itemElement);
        }
        return itemElement;
    }
    
    @Override
    protected void checkItem(ItemDomainCatalog catalogItem) throws CdbException {
        super.checkItem(catalogItem);
        
    } 

    @Override
    protected void checkItemElementsForItem(ItemDomainCatalog item) throws CdbException {
        super.checkItemElementsForItem(item);
        
        // Item element name check occurs prior to this check. 
        for (ItemElement itemElement : item.getItemElementDisplayList()) {
            if (itemElement.getContainedItem() == null) {
                throw new CdbException("No item specified for element: " + itemElement.getName());
            }
        }
    }

    @Override
    public void prepareEntityUpdate(ItemDomainCatalog item) throws CdbException {
        super.prepareEntityUpdate(item); 
        
        for (ItemElement itemElement : item.getFullItemElementList()) {
            if (itemElement.getId() == null) {
                // New item
                if (itemElement.getIsRequired()) {
                    addInventoryElementsForEachInventoryItem(itemElement);
                    // Force reload constraint info
                    itemElement.setConstraintInformation(null);
                }
            }
        }
    }

    @Override
    protected ItemDomainCatalog createEntityInstance() {
        ItemDomainCatalog newItem = super.createEntityInstance();
        if (getCurrentItemProject() != null) {
            List<ItemProject> itemProjectList = new ArrayList<>();
            itemProjectList.add(getCurrentItemProject());
            newItem.setItemProjectList(itemProjectList);
        }
        return newItem;
    }

    @Override
    public String getNextStepForCreateItemWizard(FlowEvent event) {
        String currentStep = event.getOldStep();

        if (currentStep.equals(ItemCreateWizardSteps.classification.getValue())) {
            // Nothing needs to be verified for classification step at this point. 
        }

        return super.getNextStepForCreateItemWizard(event);
    }

    @Override
    public String getFirstCreateWizardStep() {
        return ItemCreateWizardSteps.basicInformation.getValue();
    }

    @Override
    protected void resetVariablesForCurrent() {
        super.resetVariablesForCurrent();
        inventoryNonSparesList = null;
        inventorySparesList = null;
        displayInventorySpares = null;
    }

    public void resetInventorySpares() {
        ItemDomainCatalog currentItem = getCurrent();
        if (currentItem != null) {
            List<ItemDomainInventory> inventoryItems = currentItem.getInventoryItemList();
            if (inventoryItems != null) {
                for (ItemDomainInventory inventoryItem : inventoryItems) {
                    inventoryItem.setSparePartIndicator(null);
                }
            }
        }
        inventoryNonSparesList = null;
        inventorySparesList = null;
    }

    public List<ItemDomainInventory> getInventorySparesList() {
        if (inventorySparesList == null) {
            ItemDomainCatalog currentItem = getCurrent();
            if (current != null) {
                inventorySparesList = new ArrayList<>();
                for (ItemDomainInventory inventoryItem : currentItem.getInventoryItemList()) {
                    if (inventoryItem.getSparePartIndicator()) {
                        inventorySparesList.add(inventoryItem);
                    }
                }
            }
        }
        return inventorySparesList;
    }

    public List<ItemDomainInventory> getInventoryNonSparesList() {
        if (inventoryNonSparesList == null) {
            ItemDomainCatalog currentItem = getCurrent();
            if (currentItem != null) {
                List<ItemDomainInventory> spareItems = getInventorySparesList();
                List<ItemDomainInventory> allInventoryItems = getCurrent().getInventoryItemList();
                inventoryNonSparesList = new ArrayList<>(allInventoryItems);
                inventoryNonSparesList.removeAll(spareItems);
            }
        }
        return inventoryNonSparesList;
    }

    public int getInventorySparesCount() {
        List<ItemDomainInventory> sparesList = getInventorySparesList();
        if (sparesList != null) {
            return sparesList.size();
        }
        return 0;
    }

    public void notifyUserIfMinimumSparesReachedForCurrent() {
        int sparesMin = SparePartsBean.getSparePartsMinimumForItem(getCurrent());
        if (sparesMin == -1) {
            // Either an error occured or no spare parts configuration was found.
            return;
        } else {
            int sparesCount = getInventorySparesCount();
            if (sparesCount < sparesMin) {
                String sparesMessage;
                sparesMessage = "You now have " + sparesCount;
                if (sparesCount == 1) {
                    sparesMessage += " spare";
                } else {
                    sparesMessage += " spares";
                }

                sparesMessage += " but require a minumum of " + sparesMin;

                SessionUtility.addWarningMessage("Spares Warning", sparesMessage);
            }
        }

    }

    public int getInventoryNonSparesCount() {
        List<ItemDomainInventory> nonSparesList = getInventoryNonSparesList();
        if (nonSparesList != null) {
            return nonSparesList.size();
        }
        return 0;
    }

    public Boolean getDisplayInventorySpares() {
        if (displayInventorySpares == null) {
            displayInventorySpares = SparePartsBean.isItemContainSparePartConfiguration(getCurrent());
        }
        return displayInventorySpares;
    }

    public void setLoadComponentInstanceRowExpansionPropertyValues(Boolean loadComponentInstanceRowExpansionPropertyValues) {
        this.loadComponentInstanceRowExpansionPropertyValues = loadComponentInstanceRowExpansionPropertyValues;
    }

    public Boolean getDisplayComponentInstanceRowExpansion() {
        return displayComponentInstanceRowExpansion;
    }

    public void setDisplayComponentInstanceRowExpansion(Boolean displayComponentInstanceRowExpansion) {
        this.displayComponentInstanceRowExpansion = displayComponentInstanceRowExpansion;
    }

    @Override
    public Boolean getDisplayItemIdentifier1() {
        return displayModelNumber;
    }

    @Override
    public void setDisplayItemIdentifier1(Boolean displayItemIdentifier1) {
        this.displayModelNumber = displayItemIdentifier1;
    }

    @Override
    public Boolean getDisplayItemIdentifier2() {
        return this.displayAlternateName;
    }

    @Override
    public void setDisplayItemIdentifier2(Boolean displayItemIdentifier2) {
        this.displayAlternateName = displayItemIdentifier2;
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);
        if (settingTypeMap == null) {
            return;
        }

        logger.debug("Updating list settings from setting type defaults");

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
        
        displayItemElementListItemIdentifier1 = Boolean.parseBoolean(settingTypeMap.get(DisplayItemElementListItemIdentifier1SettingTypeKey).getDefaultValue()); 
        autoLoadListFilterValues = Boolean.parseBoolean(settingTypeMap.get(AutoLoadListFilterValuesSettingTypeKey).getDefaultValue()); 

        resetDomainEntityPropertyTypeIdIndexMappings();
    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);
        if (settingEntity == null) {
            return;
        }

        logger.debug("Updating list settings from session user");

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
        
        displayItemElementListItemIdentifier1 = settingEntity.getSettingValueAsBoolean(DisplayItemElementListItemIdentifier1SettingTypeKey, displayItemElementListItemIdentifier1); 
        autoLoadListFilterValues = settingEntity.getSettingValueAsBoolean(AutoLoadListFilterValuesSettingTypeKey, autoLoadListFilterValues); 

        resetDomainEntityPropertyTypeIdIndexMappings();
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterByType = (String) filters.get("componentType");
        filterByCategory = (String) filters.get("componentTypeCategory");
        filterByModelNumber = (String) filters.get("modelNumber");

        filterByPropertyValue1 = (String) filters.get("propertyValue1");
        filterByPropertyValue2 = (String) filters.get("propertyValue2");
        filterByPropertyValue3 = (String) filters.get("propertyValue3");
        filterByPropertyValue4 = (String) filters.get("propertyValue4");
        filterByPropertyValue5 = (String) filters.get("propertyValue5");
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        super.saveSettingsForSessionSettingEntity(settingEntity);
        if (settingEntity == null) {
            return;
        }

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
    public String getDisplayItemElementListItemIdentifier1Key() {
        return DisplayItemElementListItemIdentifier1SettingTypeKey; 
    }

    @Override
    public Boolean getDisplayItemElementListItemIdentifier1() {
        return displayItemElementListItemIdentifier1; 
    }

    @Override
    public void setDisplayItemElementListItemIdentifier1(Boolean displayItemElementListItemIdentifier1) {
        this.displayItemElementListItemIdentifier1 = displayItemElementListItemIdentifier1;
    }

    @Override
    public boolean getEntityDisplayItemIdentifier1() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemType() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return false;
    }

    @Override
    public String getItemIdentifier1Title() {
        return "Model Number";
    }

    @Override
    public String getItemIdentifier2Title() {
        return "Alternate Name";
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        return "Inventory";
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public String getStyleName() {
        return "catalog";
    }

    @Override
    public String getDefaultDomainName() {
        return DOMAIN_TYPE_NAME;
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return null;
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return true;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false;
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return DERIVED_DOMAIN_NAME;
    }

    @Override
    protected ItemDomainCatalog instenciateNewItemDomainEntity() {
        return new ItemDomainCatalog();
    }

    @Override
    protected ItemDomainCatalogFacade getEntityDbFacade() {
        return itemDomainCatalogFacade; 
    }

}
