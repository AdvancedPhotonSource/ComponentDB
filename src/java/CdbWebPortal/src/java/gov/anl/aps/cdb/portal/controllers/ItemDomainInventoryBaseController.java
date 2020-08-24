/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemStatusUtility;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;
import javax.ejb.EJB;

/**
 *
 * @author craig
 */
public abstract class ItemDomainInventoryBaseController<ItemInventoryBaseDomainEntity extends ItemDomainInventoryBase, ItemDomainInventoryEntityBaseFacade extends ItemFacadeBase<ItemInventoryBaseDomainEntity>, ItemInventoryEntityBaseSettingsObject extends ItemSettings> extends ItemController<ItemInventoryBaseDomainEntity, ItemDomainInventoryEntityBaseFacade, ItemInventoryEntityBaseSettingsObject> implements IItemStatusController {

    // Inventory status variables
    protected InventoryStatusPropertyTypeInfo inventoryStatusPropertyTypeInfo = null;
    private PropertyType inventoryStatusPropertyType;

    @EJB
    private PropertyTypeFacade propertyTypeFacade;

    @Override
    protected void loadEJBResourcesManually() {
        super.loadEJBResourcesManually();
        propertyTypeFacade = PropertyTypeFacade.getInstance();
    }

    // <editor-fold defaultstate="collapsed" desc="Inventory status implementation">

    public InventoryStatusPropertyTypeInfo getInventoryStatusPropertyTypeInfo() {
        inventoryStatusPropertyTypeInfo = ItemStatusUtility.getInventoryStatusPropertyTypeInfo(this, inventoryStatusPropertyTypeInfo);
        return inventoryStatusPropertyTypeInfo;
    }

    public PropertyType getInventoryStatusPropertyType() {
        inventoryStatusPropertyType = ItemStatusUtility.getInventoryStatusPropertyType(this, propertyTypeFacade, inventoryStatusPropertyType);
        return inventoryStatusPropertyType;
    }

    public PropertyValue getCurrentStatusPropertyValue() {
        return ItemStatusUtility.getCurrentStatusPropertyValue(this);
    }
    
    public PropertyValue getItemStatusPropertyValue(LocatableStatusItem item) {
        return ItemStatusUtility.getItemStatusPropertyValue(item); 
    }

    public void prepareEditInventoryStatus() {
        ItemStatusUtility.prepareEditInventoryStatus(this);
    }

    public void prepareEditInventoryStatus(LocatableStatusItem item) {
        ItemStatusUtility.prepareEditInventoryStatus(this, item);
    }

    public synchronized void prepareEditInventoryStatusFromApi(ItemInventoryBaseDomainEntity item) {
        setCurrent(item);
        prepareEditInventoryStatus();
    }

    public boolean getRenderedHistoryButton() {
        return ItemStatusUtility.getRenderedHistoryButton(this);
    }

    // </editor-fold>        

}
