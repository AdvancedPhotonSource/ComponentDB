/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainLocationFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;

/**
 *
 * @author darek
 */
public class ItemDomainLocationControllerUtility extends ItemControllerUtility<ItemDomainLocation, ItemDomainLocationFacade> {    

    @Override
    public boolean isEntityHasQrId() {
        return true; 
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
        return ItemDomainName.location.getValue(); 
    }

    @Override
    protected ItemDomainLocationFacade getItemFacadeInstance() {
        return ItemDomainLocationFacade.getInstance(); 
    }
    
    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
    @Override
    public String getEntityTypeName() {
        return "location";
    }   
    
}
