/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.List;

/**
 *
 * @author darek
 */
public class ItemGenericControllerUtility extends ItemControllerUtility<Item, ItemFacade> {

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
        return true;
    } 

    @Override
    public boolean isEntityHasItemIdentifier2() {
        return true;
    }

    @Override
    public boolean isEntityHasItemIdentifier1() {
        return true;
    }
       
    @Override
    public String getDerivedFromItemTitle() {
        return "Derived from Item";
    }

    @Override
    public String getDefaultDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ItemFacade getItemFacadeInstance() {
        return ItemFacade.getInstance(); 
    }
        
    @Override
    public String getEntityTypeName() {
        return "item"; 
    }

    @Override
    public List<Item> getItemList() {
        return itemFacade.findAll(); 
    }
    
    @Override
    protected Item instenciateNewItemDomainEntity() {
        return new Item();         
    }
    
}
