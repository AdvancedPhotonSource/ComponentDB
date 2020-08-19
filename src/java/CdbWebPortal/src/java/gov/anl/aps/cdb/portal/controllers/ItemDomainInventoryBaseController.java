/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyAllowedValue;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author craig
 */
public abstract class ItemDomainInventoryBaseController<ItemInventoryBaseDomainEntity extends ItemDomainInventoryBase, ItemDomainInventoryEntityBaseFacade extends ItemFacadeBase<ItemInventoryBaseDomainEntity>, ItemInventoryEntityBaseSettingsObject extends ItemSettings> extends ItemController<ItemInventoryBaseDomainEntity, ItemDomainInventoryEntityBaseFacade, ItemInventoryEntityBaseSettingsObject>  {
    
    // Inventory status variables
    protected InventoryStatusPropertyTypeInfo inventoryStatusPropertyTypeInfo = null;
    private PropertyType inventoryStatusPropertyType; 

    @EJB
    private PropertyTypeFacade propertyTypeFacade;
    
    protected abstract String getStatusPropertyTypeName();

    @Override
    protected void loadEJBResourcesManually() {
        super.loadEJBResourcesManually(); 
        propertyTypeFacade = PropertyTypeFacade.getInstance();
    }
    
    protected abstract String generatePaddedUnitName(int itemNumber);
    
    public String generateItemName(
            ItemDomainInventoryBase inventoryItem,
            ItemDomainCatalogBase catalogItem) {
        return generateItemName(inventoryItem, catalogItem, 1);
    }
    
    public String generateItemName(
            ItemDomainInventoryBase inventoryItem,
            ItemDomainCatalogBase catalogItem,
            int newInstanceCount) {
        
        int numExistingItems = 0;
        if (catalogItem != null) {
            numExistingItems = catalogItem.getDerivedFromItemList().size();
        }
        
        int itemNumber = numExistingItems + newInstanceCount;
        return generatePaddedUnitName(itemNumber);
    }

    public boolean getRenderedHistoryButton() {
        return getCurrentStatusPropertyValue() != null; 
    }

    @Override
    public ItemInventoryBaseDomainEntity createEntityInstance() {
        ItemInventoryBaseDomainEntity item = super.createEntityInstance();
        setCurrent(item);
        
        // set default value for status property
        String defaultValue = this.getInventoryStatusPropertyType().getDefaultValue();
        if (defaultValue != null && !defaultValue.isEmpty()) {
            prepareEditInventoryStatus();
            item.setInventoryStatusValue(defaultValue);
        }
        
        return item;
    }
    
    @Override
    public String prepareCreate() {
        ItemController derivedItemController = getDefaultDomainDerivedFromDomainController();
        if (derivedItemController != null) {
            derivedItemController.getSelectedObjectAndResetDataModel();
            derivedItemController.getSettingObject().clearListFilters();
            derivedItemController.setFilteredObjectList(null);
        }

        String createResult = super.prepareCreate();

        return createResult;
    }
    
    @Override
    public String getItemDisplayString(Item item) {
        if (item != null) {
            if (item instanceof ItemDomainInventoryBase) {
                if (item.getDerivedFromItem() != null) {
                    String result = item.getDerivedFromItem().getName();

                    //Tag to help user identify the item
                    String tag = item.getName();
                    if (tag != null && !tag.isEmpty()) {
                        result += " - [" + tag + "]";
                    }

                    return result;
                } else {
                    return "No inventory item defied";
                }
            } else {
                return getItemItemController(item).getItemDisplayString(item);
            }
        }
        return null;

    }

    // <editor-fold defaultstate="collapsed" desc="Inventory status implementation">
    
    protected abstract InventoryStatusPropertyTypeInfo initializeInventoryStatusPropertyTypeInfo();
    
    public InventoryStatusPropertyTypeInfo getInventoryStatusPropertyTypeInfo() {
        if (inventoryStatusPropertyTypeInfo == null) {
            inventoryStatusPropertyTypeInfo = initializeInventoryStatusPropertyTypeInfo();
        }
        return inventoryStatusPropertyTypeInfo;
    }
    
    private PropertyType prepareInventoryStatusPropertyType() {
        InventoryStatusPropertyTypeInfo propInfo = getInventoryStatusPropertyTypeInfo();
        PropertyTypeController propertyTypeController = PropertyTypeController.getInstance();
        
        PropertyType propertyType = propertyTypeController.createEntityInstance();
        propertyType.setIsInternal(true);
        propertyType.setName(getStatusPropertyTypeName());
        
        PropertyTypeCategory category = PropertyTypeCategoryController.getInstance().findByName("Status");
        if (category != null) {
            propertyType.setPropertyTypeCategory(category);
        }
        
        List<Domain> allowedDomainList = new ArrayList<>();
        allowedDomainList.add(getDefaultDomain());
        propertyType.setAllowedDomainList(allowedDomainList);
        
        List<AllowedPropertyValue> apvList = new ArrayList<>();
        for (InventoryStatusPropertyAllowedValue valInfo : propInfo.getValues()) {
            AllowedPropertyValue apv = new AllowedPropertyValue();
            apv.setValue(valInfo.getValue());
            apv.setSortOrder(valInfo.getSortOrder());
            apv.setPropertyType(propertyType);
            apvList.add(apv);
        }
        propertyType.setAllowedPropertyValueList(apvList);
        
        propertyType.setDefaultValue(propInfo.getDefaultValue());

        propertyTypeController.setCurrent(propertyType);
        propertyTypeController.create(true, false); 
        return propertyType; 
    }
        
    public PropertyType getInventoryStatusPropertyType() {
        if (inventoryStatusPropertyType == null) {
            inventoryStatusPropertyType = 
                    propertyTypeFacade.findByName(getStatusPropertyTypeName()); 
        }
        if (inventoryStatusPropertyType == null) {
            inventoryStatusPropertyType = prepareInventoryStatusPropertyType();
        }
        return inventoryStatusPropertyType;
    }        

    public PropertyValue getCurrentStatusPropertyValue() {
        return getCurrent().getInventoryStatusPropertyValue();
    }
    
    public void prepareEditInventoryStatus() {
        if (getCurrentStatusPropertyValue() == null) {
            PropertyValue preparePropertyTypeValueAdd = preparePropertyTypeValueAdd(getInventoryStatusPropertyType()); 
            getCurrent().setInventoryStatusPropertyValue(preparePropertyTypeValueAdd);
        }
    }
    
    public synchronized void prepareEditInventoryStatusFromApi(ItemInventoryBaseDomainEntity item) {
        setCurrent(item);
        prepareEditInventoryStatus();
    }
    
    // </editor-fold>      
    
    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return true; 
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return true;
    }

    @Override
    public String getNameTitle() {
        return "Tag";
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
    public boolean getEntityDisplayItemSources() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return true; 
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }    

    @Override
    public boolean isAllowedSetDerivedFromItemForCurrentItem() {
        if (getCurrent() != null) {
            return !getCurrent().isIsCloned();
        }

        return false;
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false;
    }

    @Override
    public String getStyleName() {
        return "inventory";
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return null;
    }
}
