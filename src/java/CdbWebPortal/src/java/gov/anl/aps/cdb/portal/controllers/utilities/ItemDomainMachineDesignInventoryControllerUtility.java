/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemStatusUtility;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignInventoryControllerUtility extends ItemDomainMachineDesignControllerUtility implements IItemStatusControllerUtility {
    
    private static final Logger logger = LogManager.getLogger(ItemDomainMachineDesignInventoryControllerUtility.class.getName());
        
    @Override
    public String getDerivedFromItemTitle() {
        return "Machine Template";
    }
        
    @Override
    public List<ItemDomainMachineDesign> getItemList() {
        return itemFacade.getTopLevelMachineDesignInventory();
    } 
    
    public void assignInventoryAttributes(ItemDomainMachineDesign newInventory, ItemDomainMachineDesign templateItem, UserInfo sessionUser) {
        newInventory.setDerivedFromItem(templateItem);
        assignInventoryAttributes(newInventory, sessionUser);
    }

    protected void assignInventoryAttributes(ItemDomainMachineDesign newInventory, UserInfo sessionUser) {
        String inventoryetn = EntityTypeName.inventory.getValue();
        EntityType inventoryet = entityTypeFacade.findByName(inventoryetn);
        if (newInventory.getEntityTypeList() == null) {
            try {
                newInventory.setEntityTypeList(new ArrayList());
            } catch (CdbException ex) {
                logger.error(ex);
            }
        }
        newInventory.getEntityTypeList().add(inventoryet);
        
        ItemStatusUtility.updateDefaultStatusProperty(newInventory, sessionUser, this);
    } 

    @Override
    public ItemDomainMachineDesign createEntityInstance(UserInfo sessionUser) {
        ItemDomainMachineDesign newInventory = super.createEntityInstance(sessionUser); 
        
        assignInventoryAttributes(newInventory, sessionUser);
        
        return newInventory; 
    }
    
    @Override
    public void prepareEditInventoryStatus(LocatableStatusItem item, UserInfo sessionUser) {
        ItemStatusUtility.prepareEditInventoryStatus(this, item, sessionUser);
    }

    @Override
    public String getStatusPropertyTypeName() {
        return ItemDomainMachineDesign.MD_INTERNAL_STATUS_PROPERTY_TYPE; 
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
    
}
