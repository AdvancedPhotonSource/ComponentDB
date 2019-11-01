/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainCableCatalogFacade extends ItemFacadeBase<ItemDomainCableCatalog> {
    
    public ItemDomainCableCatalogFacade() {
        super(ItemDomainCableCatalog.class);
    }
    
    public static ItemDomainCableCatalogFacade getInstance() {
        return (ItemDomainCableCatalogFacade) SessionUtility.findFacade(ItemDomainCableCatalogFacade.class.getSimpleName()); 
    }
    
}
