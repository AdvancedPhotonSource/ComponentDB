/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCableCatalogSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainCableCatalogController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCableCatalogController extends ItemController<ItemDomainCableCatalog, ItemDomainCableCatalogFacade, ItemDomainCableCatalogSettings> {
    
    public static final String CONTROLLER_NAMED = "itemDomainCableCatalogController";
    
    @EJB
    ItemDomainCableCatalogFacade itemDomainCableCatalogFacade; 

    @Override
    protected ItemDomainCableCatalog instenciateNewItemDomainEntity() {
        return new ItemDomainCableCatalog(); 
    }

    @Override
    protected ItemDomainCableCatalogSettings createNewSettingObject() {
        return new ItemDomainCableCatalogSettings(this);
    }

    @Override
    protected ItemDomainCableCatalogFacade getEntityDbFacade() {
        return itemDomainCableCatalogFacade; 
    }

    @Override
    public String getEntityTypeName() {
        return "cableCatalog"; 
    } 

    @Override
    public String getDisplayEntityTypeName() {
        return "Cable Catalog";
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.cableCatalog.getValue(); 
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return true; 
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
        return true;
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
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false; 
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        return "Cable Inventory";
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        return "catalog"; 
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return null; 
               
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return ItemDomainName.cableInventory.getValue(); 
    } 
    
}
