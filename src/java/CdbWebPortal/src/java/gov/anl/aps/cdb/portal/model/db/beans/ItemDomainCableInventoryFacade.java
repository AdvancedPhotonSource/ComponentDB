/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainCableInventoryFacade extends ItemFacadeBase<ItemDomainCableInventory> {
    
    @Override
    public String getDomainName() {
        return ItemDomainName.cableInventory.getValue();
    }
    
    public ItemDomainCableInventoryFacade() {
        super(ItemDomainCableInventory.class);
    }
    
    public static ItemDomainCableInventoryFacade getInstance() {
        return (ItemDomainCableInventoryFacade) SessionUtility.findFacade(ItemDomainCableInventoryFacade.class.getSimpleName()); 
    }
    
    public List<ItemDomainCableInventory> findByName(String name) {
        return findByDomainAndName(getDomainName(), name);
    }  
}
