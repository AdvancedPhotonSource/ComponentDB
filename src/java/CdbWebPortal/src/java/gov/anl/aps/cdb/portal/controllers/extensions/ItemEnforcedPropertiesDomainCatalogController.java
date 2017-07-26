/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemEnforcedPropertiesDomainCatalogController.controllerNamed)
@SessionScoped
public class ItemEnforcedPropertiesDomainCatalogController extends ItemEnforcedPropertiesController implements Serializable {
    
    public final static String controllerNamed = "ItemEnforcedPropertiesDomainCatalogController";
    
    protected ItemDomainCatalogController itemDomainCatalogController = null;         
    
    private final String[] elevatedCategoryNames = {"QA"}; 
    
    @Override
    protected String[] getElevatedCategoryNames() {
        return elevatedCategoryNames;
    }

    @Override
    protected ItemController getItemController() {
        return getItemDomainCatalogController(); 
    } 

    @Override
    protected void prepareSaveChangesMadeToEnforcedPropertiesForCurrent() {
        super.prepareSaveChangesMadeToEnforcedPropertiesForCurrent(); 
        
        // Handle inventory items.
        ItemDomainInventoryController idic = ItemDomainInventoryController.getInstance();
        ItemEnforcedPropertiesController inventoryEnforcedPropertiesController = idic.getItemEnforcedPropertiesController();
        
        ItemDomainCatalog catalogItem = (ItemDomainCatalog) getCurrent(); 
       
        if (catalogItem.getInventoryItemList() != null) {
            for (Item inventoryItem : catalogItem.getInventoryItemList()) {
                inventoryEnforcedPropertiesController.setCurrent(inventoryItem);
                inventoryEnforcedPropertiesController.prepareSaveChangesMadeToEnforcedPropertiesForCurrent();
            }
        }               
    }

    protected ItemDomainCatalogController getItemDomainCatalogController() {
        if (itemDomainCatalogController == null) {
            itemDomainCatalogController = ItemDomainCatalogController.getInstance(); 
        }
        return itemDomainCatalogController;
    }
    
     public static ItemEnforcedPropertiesDomainCatalogController getInstance() {
        return (ItemEnforcedPropertiesDomainCatalogController) SessionUtility.findBean(controllerNamed);
    }   
    
}
