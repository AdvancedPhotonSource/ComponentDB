/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainCableInventoryFacade extends ItemFacadeBase<ItemDomainCableInventory> {
    
    public ItemDomainCableInventoryFacade() {
        super(ItemDomainCableInventory.class);
    }
    
    public static ItemDomainCableInventoryFacade getInstance() {
        return (ItemDomainCableInventoryFacade) SessionUtility.findFacade(ItemDomainCableInventoryFacade.class.getSimpleName()); 
    }
    
}
