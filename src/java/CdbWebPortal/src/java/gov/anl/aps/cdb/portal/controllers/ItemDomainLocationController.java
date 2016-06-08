/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("itemDomainLocationController")
@SessionScoped
public class ItemDomainLocationController extends ItemController {
    
    private final String ENTITY_TYPE_NAME = "Location";
    private final String DOMAIN_TYPE_NAME = "Location";
    private final String DOMAIN_HANDLER_NAME = "Location"; 
    
    @EJB
    ItemFacade itemFacade; 
    
    @EJB
    DomainFacade domainFacade; 
    
    public ItemDomainLocationController() {
        super();
        displayNumberOfItemsPerPage = 25; 
    }
    

    @Override
    public Domain getDefaultDomain() {
        return domainFacade.findByName(DOMAIN_TYPE_NAME); 
    }

    @Override
    public boolean getEntityDisplayItemIdentifier1() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true; 
    }

    @Override
    public boolean getEntityDisplayItemType() {
        return true; 
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayQrId() {
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
        return false;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true; 
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
    public String getItemIdentifier1Title() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemIdentifier2Title() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        return "content";
    }

    @Override
    public String getDomainHandlerName() {
        return DOMAIN_HANDLER_NAME; 
    }

    @Override
    public String getEntityTypeName() {
        return "location"; 
    }
    
    @Override
    public String getDisplayEntityTypeName() {
        return "Location"; 
    }

    @Override
    public List<Item> getItemList() {
        return itemFacade.findByDomainAndEntityType(DOMAIN_TYPE_NAME, ENTITY_TYPE_NAME); 
    }
    
}
