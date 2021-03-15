/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */

@Stateless
public class ItemDomainCatalogFacade extends ItemFacadeBase<ItemDomainCatalog> {
    
    @Override
    public String getDomainName() {
        return ItemDomainName.catalog.getValue();
    }
    
    public ItemDomainCatalogFacade() {
        super(ItemDomainCatalog.class);
    }
    
    public static ItemDomainCatalogFacade getInstance() {
        return (ItemDomainCatalogFacade) SessionUtility.findFacade(ItemDomainCatalogFacade.class.getSimpleName()); 
    }
    
    public List<ItemDomainCatalog> findByName(String name) {
        return findByDomainAndName(getDomainName(), name);
    }  
}
