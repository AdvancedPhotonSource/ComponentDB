/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainAppController;
import java.io.Serializable;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 *
 * @author djarosz
 */
@Named("itemFilterViewDomainAppController")
@SessionScoped
public class ItemFilterViewDomainAppController extends ItemFilterViewController implements Serializable {

    ItemDomainAppController itemDomainController = null; 
    @Override
    public ItemController getItemController() {
        if (itemDomainController == null) {
            itemDomainController = ItemDomainAppController.getInstance(); 
        }
        return itemDomainController;         
    }    
    
}
