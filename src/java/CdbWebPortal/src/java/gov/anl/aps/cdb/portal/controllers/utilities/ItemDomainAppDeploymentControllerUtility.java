/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainAppDeploymentFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainAppDeployment;

/**
 *
 * @author djarosz
 */
public class ItemDomainAppDeploymentControllerUtility extends ItemControllerUtility<ItemDomainAppDeployment, ItemDomainAppDeploymentFacade> {

    @Override
    protected ItemDomainAppDeploymentFacade getItemFacadeInstance() {
        return ItemDomainAppDeploymentFacade.getInstance();
    }

    @Override
    protected ItemDomainAppDeployment instenciateNewItemDomainEntity() {
        return new ItemDomainAppDeployment(); 
    }

    @Override
    public boolean isEntityHasQrId() {
        return false;
    }

    @Override
    public boolean isEntityHasName() {
        return true;
    }

    @Override
    public boolean isEntityHasProject() {
        return false; 
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.appDeployment.getValue(); 
    }

    @Override
    public String getDerivedFromItemTitle() {
        return "App";
    }

    @Override
    public String getEntityTypeName() {
        return "itemAppDeployment";
    }
    
}
