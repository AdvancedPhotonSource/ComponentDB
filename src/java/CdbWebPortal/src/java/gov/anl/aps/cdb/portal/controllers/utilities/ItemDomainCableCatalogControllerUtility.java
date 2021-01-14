/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;

/**
 *
 * @author darek
 */
public class ItemDomainCableCatalogControllerUtility extends ItemDomainCatalogBaseControllerUtility<ItemDomainCableCatalog, ItemDomainCableCatalogFacade> {

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.cableCatalog.getValue();
    }

    @Override
    protected ItemDomainCableCatalogFacade getItemFacadeInstance() {
        return ItemDomainCableCatalogFacade.getInstance(); 
    }
    
    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getEntityTypeName() {
        return "cableCatalog"; 
    } 
         
    
}
