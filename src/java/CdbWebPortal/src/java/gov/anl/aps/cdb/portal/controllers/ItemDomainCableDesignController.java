/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCableDesignSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainCableDesignController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCableDesignController extends ItemController<ItemDomainCableDesign, ItemDomainCableDesignFacade, ItemDomainCableDesignSettings> {
    
    public static final String CONTROLLER_NAMED = "itemDomainCableDesignController";
    
    @EJB
    ItemDomainCableDesignFacade itemDomainCableDesignFacade; 

    @Override
    protected ItemDomainCableDesign instenciateNewItemDomainEntity() {
        return new ItemDomainCableDesign(); 
    }

    @Override
    protected ItemDomainCableDesignSettings createNewSettingObject() {
        return new ItemDomainCableDesignSettings(this);
    }

    @Override
    protected ItemDomainCableDesignFacade getEntityDbFacade() {
        return itemDomainCableDesignFacade; 
    }

    @Override
    public String getEntityTypeName() {
        return "cableDesign"; 
    } 

    @Override
    public String getDisplayEntityTypeName() {
        return "Cable Design";
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.cableDesign.getValue(); 
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true; 
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true; 
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return true; 
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false; 
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getStyleName() {
        return "machineDesign"; 
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        throw new UnsupportedOperationException("Not supported yet.");       
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); 
    } 
    
}
