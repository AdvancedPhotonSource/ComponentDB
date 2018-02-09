/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainMachineDesignFacade extends ItemFacadeBase<ItemDomainMachineDesign> {
    
    public ItemDomainMachineDesignFacade() {
        super(ItemDomainMachineDesign.class);
    }
    
}
