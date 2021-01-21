/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryBaseControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemStatusUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;

/**
 *
 * @author craig
 * @param <InventoryControllerUtility>
 * @param <ItemInventoryBaseDomainEntity>
 * @param <ItemDomainInventoryEntityBaseFacade>
 * @param <ItemInventoryEntityBaseSettingsObject>
 */
public abstract class ItemDomainInventoryBaseController
        <
            InventoryControllerUtility extends ItemDomainInventoryBaseControllerUtility<ItemInventoryBaseDomainEntity, ItemDomainInventoryEntityBaseFacade>, 
            ItemInventoryBaseDomainEntity extends ItemDomainInventoryBase, 
            ItemDomainInventoryEntityBaseFacade extends ItemFacadeBase<ItemInventoryBaseDomainEntity>, 
            ItemInventoryEntityBaseSettingsObject extends ItemSettings
        > 
        extends ItemController<InventoryControllerUtility, ItemInventoryBaseDomainEntity, ItemDomainInventoryEntityBaseFacade, ItemInventoryEntityBaseSettingsObject> implements IItemStatusController {

    // Inventory status variables
    protected InventoryStatusPropertyTypeInfo inventoryStatusPropertyTypeInfo = null;    
    
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

    @Override
    public ItemInventoryBaseDomainEntity createEntityInstance() {
        return super.createEntityInstance(); //To change body of generated methods, choose Tools | Templates.
    }

    // <editor-fold defaultstate="collapsed" desc="Inventory status implementation">
 
    @Override
    public final PropertyValue getCurrentStatusPropertyValue() {
        ItemInventoryBaseDomainEntity current = getCurrent();
        return getControllerUtility().getItemStatusPropertyValue(current); 
    }    

    @Override
    public final void prepareEditInventoryStatus() {
        ItemInventoryBaseDomainEntity current = getCurrent();
        prepareEditInventoryStatus(current);
    } 

    public final void prepareEditInventoryStatus(LocatableStatusItem item) {
        UserInfo user = SessionUtility.getUser();
        getControllerUtility().prepareEditInventoryStatus(item, user);
    }
    
    @Override
    public final PropertyType getInventoryStatusPropertyType() {
        return getControllerUtility().getInventoryStatusPropertyType(); 
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
    public boolean getRenderedHistoryButton() {
        return ItemStatusUtility.getRenderedHistoryButton(this);
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
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
