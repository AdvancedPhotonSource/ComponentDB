/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemTravelerDomainInventoryController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainInventoryController extends ItemTravelerController implements Serializable {
    
    public final static String controllerNamed = "itemTravelerDomainInventoryController";
    
    private ItemDomainInventoryController itemDomainInventoryController = null; 

    @Override
    protected ItemController getItemController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = ItemDomainInventoryController.getInstance();           
        }
                
        return itemDomainInventoryController; 
    }
    
    public boolean getIsCollapsedTravelerInstances() {                
        return !(getTravelersForCurrent().size() > 0); 
    }
    
}
