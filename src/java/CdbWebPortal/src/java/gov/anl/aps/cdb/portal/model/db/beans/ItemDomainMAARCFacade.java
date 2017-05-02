/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMAARC;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainMAARCFacade extends ItemFacadeBase<ItemDomainMAARC> {
    
    public ItemDomainMAARCFacade() {
        super(ItemDomainMAARC.class);
    }
    
}
