/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainAppFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainApp;

/**
 *
 * @author djarosz
 */
public class ItemDomainAppControllerUtility extends ItemControllerUtility<ItemDomainApp, ItemDomainAppFacade> {

    @Override
    protected ItemDomainAppFacade getItemFacadeInstance() {
        return ItemDomainAppFacade.getInstance();
    }

    @Override
    protected ItemDomainApp instenciateNewItemDomainEntity() {
        return new ItemDomainApp(); 
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
        return ItemDomainName.app.getValue(); 
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getEntityTypeName() {
        return "itemApp";
    }
    
}
