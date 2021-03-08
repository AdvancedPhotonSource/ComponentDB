/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.constants.ItemMetadataFieldType;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
import java.util.List;

/**
 *
 * @author darek
 */
public class ItemDomainCableInventoryControllerUtility extends ItemDomainInventoryBaseControllerUtility<ItemDomainCableInventory, ItemDomainCableInventoryFacade> {

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.cableInventory.getValue(); 
    }

    @Override
    protected ItemDomainCableInventoryFacade getItemFacadeInstance() {
        return ItemDomainCableInventoryFacade.getInstance(); 
    }
    
    @Override
    public String getDerivedFromItemTitle() {
        return "Cable Catalog Item";
    }
    
    @Override
    public String getEntityTypeName() {
        return "cableInventory"; 
    } 

    @Override
    public String getDisplayEntityTypeName() {
        return "Cable Inventory Item";
    }        
    
    @Override
    public List<ItemDomainCableInventory> getItemList() {
        return itemFacade.findByDomainOrderByDerivedFromItemAndItemName(getDefaultDomainName());
    }   

    @Override
    public ItemMetadataPropertyInfo createCoreMetadataPropertyInfo() {
        ItemMetadataPropertyInfo info = new ItemMetadataPropertyInfo("Cable Inventory Metadata", ItemDomainCableInventory.CABLE_INVENTORY_INTERNAL_PROPERTY_TYPE);
        info.addField(ItemDomainCableInventory.CABLE_INVENTORY_PROPERTY_LENGTH_KEY, "Length", "Installed length of cable.", ItemMetadataFieldType.STRING, "", null);
        return info;
    }
            
    @Override
    protected ItemDomainCableInventory instenciateNewItemDomainEntity() {
        return new ItemDomainCableInventory();
    }
    
    @Override
    public String getStatusPropertyTypeName() {
        return ItemDomainCableInventory.ITEM_DOMAIN_CABLE_INVENTORY_STATUS_PROPERTY_TYPE_NAME;
    }
            
    @Override
    public InventoryStatusPropertyTypeInfo initializeInventoryStatusPropertyTypeInfo() {
        InventoryStatusPropertyTypeInfo info = super.initializeInventoryStatusPropertyTypeInfo(); 
        
        info.setDefaultValue("Planned");
        
        return info;
    }
       
    
}
