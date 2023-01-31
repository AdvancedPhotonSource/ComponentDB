/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainAppSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainAppControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainAppLazyDataModel;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainAppFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainApp;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named("itemDomainAppController")
@SessionScoped
public class ItemDomainAppController extends ItemController<ItemDomainAppControllerUtility, ItemDomainApp, ItemDomainAppFacade, ItemDomainAppSettings, ItemDomainAppLazyDataModel>{

    @EJB
    ItemDomainAppFacade itemDomainAppFacade; 
    
    @Override
    public ItemDomainAppLazyDataModel createItemLazyDataModel() {
        return new ItemDomainAppLazyDataModel(itemDomainAppFacade, getDefaultDomain(), settingObject);
    }
    
    @Override
    protected ItemDomainAppControllerUtility createControllerUtilityInstance() {
        return new ItemDomainAppControllerUtility(); 
    }

    @Override
    protected ItemDomainAppSettings createNewSettingObject() {
        return new ItemDomainAppSettings(this); 
    }

    @Override
    protected ItemDomainAppFacade getEntityDbFacade() {
        return itemDomainAppFacade; 
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
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
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false; 
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getStyleName() {
        return "app"; 
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }   
    
}
