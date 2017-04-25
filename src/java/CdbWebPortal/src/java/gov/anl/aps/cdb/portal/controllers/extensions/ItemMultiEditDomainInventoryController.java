/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemMultiEditDomainInventoryController.controllerNamed)
@SessionScoped
public class ItemMultiEditDomainInventoryController extends ItemMultiEditController implements Serializable {
    
    public final static String controllerNamed = "itemMultiEditDomainInventoryController";
    
    private ItemDomainInventoryController itemDomainInventoryController = null; 

    @Override
    protected ItemController getItemController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = ItemDomainInventoryController.getInstance();
        }
        return itemDomainInventoryController; 
    }

    @Override
    protected String getControllerNamedConstant() {
        return controllerNamed; 
    }
    
    public static ItemMultiEditDomainInventoryController getInstance() {
        return (ItemMultiEditDomainInventoryController) SessionUtility.findBean(controllerNamed);
    }
    
}
