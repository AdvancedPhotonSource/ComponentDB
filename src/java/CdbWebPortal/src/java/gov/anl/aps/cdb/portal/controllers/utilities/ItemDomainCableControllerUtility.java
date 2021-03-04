/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCable;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;

/**
 *
 * @author darek
 */
public class ItemDomainCableControllerUtility extends ItemControllerUtility<ItemDomainCable, ItemDomainCableFacade> {

    @Override
    public boolean isEntityHasQrId() {
        return false;
    }

    @Override
    public boolean isEntityHasName() {
        return false; 
    }

    @Override
    public boolean isEntityHasProject() {
        return false; 
    }

    @Override
    public String getDefaultDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ItemDomainCableFacade getItemFacadeInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getEntityTypeName() {
        return "cable";
    }
    
    @Override
    public void checkItemUniqueness(ItemDomainCable entity) throws CdbException {        
        // Cables are only unique by primary key (id). 
    }
    
    @Override
    protected ItemDomainCable instenciateNewItemDomainEntity() {
        return new ItemDomainCable();
    }
    
}
