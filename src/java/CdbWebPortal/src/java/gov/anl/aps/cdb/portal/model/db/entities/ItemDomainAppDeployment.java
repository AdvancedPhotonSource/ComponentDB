/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainAppDeploymentControllerUtility;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.APP_DEPLOYMENT_ID + "")
public class ItemDomainAppDeployment extends Item {

    public ItemDomainAppDeployment() {
    }
    
    @Override
    public Item createInstance() {
        return new ItemDomainAppDeployment(); 
    } 
    
    @Override
    public ItemDomainAppDeploymentControllerUtility getItemControllerUtility() {
        return new ItemDomainAppDeploymentControllerUtility(); 
    }
}
