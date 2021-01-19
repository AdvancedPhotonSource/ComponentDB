/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;

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
       
    
}
