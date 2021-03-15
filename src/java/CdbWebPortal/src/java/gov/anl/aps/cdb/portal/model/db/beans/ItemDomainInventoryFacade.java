/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainInventoryFacade extends ItemFacadeBase<ItemDomainInventory> {

    @Override
    public String getDomainName() {
        return ItemDomainName.inventory.getValue();
    }
    
    public ItemDomainInventoryFacade() {
        super(ItemDomainInventory.class);
    }
    
    public static ItemDomainInventoryFacade getInstance() {
        return (ItemDomainInventoryFacade) SessionUtility.findFacade(ItemDomainInventoryFacade.class.getSimpleName()); 
    }

    public List<ItemDomainInventory> findByName(String name) {
        return findByDomainAndName(getDomainName(), name);
    }  
}
