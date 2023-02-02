/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainAppDeployment;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainAppDeploymentFacade extends ItemFacadeBase<ItemDomainAppDeployment> {   
    
    public ItemDomainAppDeploymentFacade() {
        super(ItemDomainAppDeployment.class);
    }
    
    @Override
    public ItemDomainName getDomain() {
        return ItemDomainName.appDeployment;
    }   
    
    public static ItemDomainAppDeploymentFacade getInstance() {
        return (ItemDomainAppDeploymentFacade) SessionUtility.findFacade(ItemDomainAppDeploymentFacade.class.getSimpleName()); 
    }
    
}
