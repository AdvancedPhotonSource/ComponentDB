/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCable;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */

@Stateless
public class ItemDomainCableFacade extends ItemFacadeBase<ItemDomainCable> {
    
    @Override
    public String getDomainName() {
        return ItemDomainName.cable.getValue();
    }
    
    public ItemDomainCableFacade() {
        super(ItemDomainCable.class);
    }
    
}
