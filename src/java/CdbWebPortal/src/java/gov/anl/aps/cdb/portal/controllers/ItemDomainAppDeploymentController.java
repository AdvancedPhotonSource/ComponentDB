/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainAppDeploymentSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainAppDeploymentControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainAppDeploymentLazyDataModel;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainAppDeploymentFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainAppDeployment;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.IOException;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainAppDeploymentController.controllerNamed)
@SessionScoped
public class ItemDomainAppDeploymentController extends ItemController<ItemDomainAppDeploymentControllerUtility, ItemDomainAppDeployment, ItemDomainAppDeploymentFacade, ItemDomainAppDeploymentSettings, ItemDomainAppDeploymentLazyDataModel>{   
    
    private final String DERIVED_FROM_DOMAIN_NAME = "App";
    
    @EJB
    ItemDomainAppDeploymentFacade itemDomainAppDeploymentFacade; 
    
    public final static String controllerNamed = "itemDomainAppDeploymentController";
    
    public static ItemDomainAppDeploymentController getInstance() {
        return (ItemDomainAppDeploymentController) SessionUtility.findBean(controllerNamed);
    }
    
    @Override
    public ItemDomainAppDeploymentLazyDataModel createItemLazyDataModel() {
        return new ItemDomainAppDeploymentLazyDataModel(itemDomainAppDeploymentFacade, getDefaultDomain(), settingObject);
    }
    
    @Override
    protected ItemDomainAppDeploymentControllerUtility createControllerUtilityInstance() {
        return new ItemDomainAppDeploymentControllerUtility(); 
    }

    @Override
    protected ItemDomainAppDeploymentSettings createNewSettingObject() {
        return new ItemDomainAppDeploymentSettings(this); 
    }

    @Override
    public boolean isAllowedSetDerivedFromItemForCurrentItem() {
        return false; 
    }

    @Override
    protected ItemDomainAppDeploymentFacade getEntityDbFacade() {
        return itemDomainAppDeploymentFacade; 
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return true; 
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
        return DERIVED_FROM_DOMAIN_NAME; 
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }   
    
    public void createEntityInstance(Item derivedFromItem) throws IOException {
        prepareCreate();
        
        ItemDomainAppDeployment entity = getCurrent(); 
        
        entity.setDerivedFromItem(derivedFromItem);
        
        Domain defaultDomain = getDefaultDomain();        
        String createPage = getDomainPath(defaultDomain) + "/create.xhtml?faces-redirect=true"; 
        SessionUtility.redirectTo(createPage);
    }
    
}
