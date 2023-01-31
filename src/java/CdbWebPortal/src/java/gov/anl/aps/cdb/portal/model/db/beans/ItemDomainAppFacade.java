/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainApp;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainAppFacade extends ItemFacadeBase<ItemDomainApp> {   
    
    public ItemDomainAppFacade() {
        super(ItemDomainApp.class);
    }
    
    @Override
    public ItemDomainName getDomain() {
        return ItemDomainName.app;
    }   
    
    public static ItemDomainAppFacade getInstance() {
        return (ItemDomainAppFacade) SessionUtility.findFacade(ItemDomainAppFacade.class.getSimpleName()); 
    }
    
}
