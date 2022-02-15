/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemGenericViewSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemGenericControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemGenericLazyDataModel;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.EntityTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named("itemGenericViewController")
@SessionScoped
public class ItemGenericViewController extends ItemController<ItemGenericControllerUtility, Item, ItemFacade, ItemGenericViewSettings, ItemGenericLazyDataModel> {
    
    @EJB
    private DomainFacade domainFacade; 
    
    @EJB
    private ItemFacade itemFacade; 
    
    @EJB
    private EntityTypeFacade entityTypeFacade; 
    
    public ItemGenericViewController() {
        super();
    }

    @Override
    public boolean getEntityDisplayItemType() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        return true;
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
        return true;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public String getItemIdentifier1Title() {
        return "Item Identifier 1";
    }

    @Override
    public String getItemIdentifier2Title() {
        return "Item Identifier 2";
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        return "Items derived from Item";
    }

    @Override
    public String getStyleName() {
        return "items"; 
    }

    @Override
    public Domain getDefaultDomain() {
        return null;
    }

    /**
     * Generic controller does not have a list domain. 
     * @return 
     */
    @Override
    public String getDefaultDomainName() {
        return null; 
    }

    @Override
    public List<EntityType> getFilterableEntityTypes() {
        return entityTypeFacade.findAll(); 
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return true;
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return null;
    }   

    @Override
    protected ItemFacade getEntityDbFacade() {
        return itemFacade;
    }
    
    @Override
    public boolean getEntityDisplayItemConnectors() {
        return true; 
    }
   
    @Override
    public ItemGenericLazyDataModel createItemLazyDataModel() {
        return new ItemGenericLazyDataModel(getEntityDbFacade(), getDefaultDomain(), settingObject); 
    }   

    @Override
    protected ItemGenericViewSettings createNewSettingObject() {
        return new ItemGenericViewSettings(this);
    }

    @Override
    protected ItemGenericControllerUtility createControllerUtilityInstance() {
        return new ItemGenericControllerUtility(); 
    }
    
}