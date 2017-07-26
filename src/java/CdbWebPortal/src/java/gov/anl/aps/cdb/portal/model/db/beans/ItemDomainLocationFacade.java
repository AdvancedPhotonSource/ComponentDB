/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */

@Stateless
public class ItemDomainLocationFacade extends ItemFacadeBase<ItemDomainLocation> {
    
    public ItemDomainLocationFacade() {
        super(ItemDomainLocation.class);
    }
    
}
