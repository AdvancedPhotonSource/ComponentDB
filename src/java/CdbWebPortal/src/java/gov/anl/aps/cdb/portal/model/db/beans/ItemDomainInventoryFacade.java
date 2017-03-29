/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */

@Stateless
public class ItemDomainInventoryFacade extends ItemFacadeBase<ItemDomainInventory> {
    
    public ItemDomainInventoryFacade() {
        super(ItemDomainInventory.class);
    }
    
}
