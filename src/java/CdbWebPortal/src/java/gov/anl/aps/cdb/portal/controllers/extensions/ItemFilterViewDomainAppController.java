/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainAppController;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

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
