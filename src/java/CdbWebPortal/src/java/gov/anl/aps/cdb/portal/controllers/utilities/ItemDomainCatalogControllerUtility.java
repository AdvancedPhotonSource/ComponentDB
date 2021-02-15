/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;

/**
 *
 * @author darek
 */
public class ItemDomainCatalogControllerUtility extends ItemDomainCatalogBaseControllerUtility<ItemDomainCatalog, ItemDomainCatalogFacade> {

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.catalog.getValue(); 
    }

    @Override
    protected ItemDomainCatalogFacade getItemFacadeInstance() {
        return ItemDomainCatalogFacade.getInstance(); 
    }
    
    @Override
    protected ItemDomainCatalog instenciateNewItemDomainEntity() {
        return new ItemDomainCatalog();
    }
       
}
