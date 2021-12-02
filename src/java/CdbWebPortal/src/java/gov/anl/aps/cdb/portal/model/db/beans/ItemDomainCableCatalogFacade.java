/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainCableCatalogFacade extends ItemFacadeBase<ItemDomainCableCatalog> {
    
    @Override
    public ItemDomainName getDomain() {
        return ItemDomainName.cableCatalog;
    }
    
    public ItemDomainCableCatalogFacade() {
        super(ItemDomainCableCatalog.class);
    }
    
    public static ItemDomainCableCatalogFacade getInstance() {
        return (ItemDomainCableCatalogFacade) SessionUtility.findFacade(ItemDomainCableCatalogFacade.class.getSimpleName()); 
    }
    
    public List<ItemDomainCableCatalog> findByName(String name) {
        return findByDomainAndName(getDomainName(), name);
    }  
}
