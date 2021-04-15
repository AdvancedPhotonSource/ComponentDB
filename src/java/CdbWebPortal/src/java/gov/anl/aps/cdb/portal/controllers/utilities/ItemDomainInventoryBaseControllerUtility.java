/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemStatusUtility;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;

/**
 *
 * @author darek
 * @param <ItemInventoryEntityType>
 * @param <ItemDomainEntityFacade>
 */
public abstract class ItemDomainInventoryBaseControllerUtility<ItemInventoryEntityType extends ItemDomainInventoryBase, ItemDomainEntityFacade extends ItemFacadeBase<ItemInventoryEntityType>> 
        extends ItemControllerUtility<ItemInventoryEntityType, ItemDomainEntityFacade> implements IItemStatusControllerUtility {               

    @Override
    public boolean isEntityHasName() {
        return true; 
    }

    @Override
    public boolean isEntityHasQrId() {
        return true; 
    }

    @Override
    public boolean isEntityHasProject() {
        return true; 
    }
    
    @Override
    public void prepareEditInventoryStatus(LocatableStatusItem item, UserInfo sessionUser) {
        ItemStatusUtility.prepareEditInventoryStatus(this, item, sessionUser);
    }

    @Override
    public PropertyValue getItemStatusPropertyValue(LocatableStatusItem item) {
        return ItemStatusUtility.getItemStatusPropertyValue(item); 
    }

    @Override
    public PropertyType getInventoryStatusPropertyType() {
        return ItemStatusUtility.getInventoryStatusPropertyType(this, propertyTypeFacade); 
    }

    @Override
    public InventoryStatusPropertyTypeInfo getInventoryStatusPropertyTypeInfo() {
        return ItemStatusUtility.getInventoryStatusPropertyTypeInfo(this);
    }

    @Override
    public InventoryStatusPropertyTypeInfo initializeInventoryStatusPropertyTypeInfo() {
        return ItemStatusUtility.initializeInventoryStatusPropertyTypeInfo(); 
    }

    @Override
    public ItemInventoryEntityType createEntityInstance(UserInfo sessionUser) {
        ItemInventoryEntityType item = super.createEntityInstance(sessionUser); 
        
        ItemStatusUtility.updateDefaultStatusProperty(item, sessionUser, this);
        
        return item; 
    }        
}
