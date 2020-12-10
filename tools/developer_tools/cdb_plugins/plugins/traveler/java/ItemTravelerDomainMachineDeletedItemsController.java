/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignDeletedItemsController;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named(ItemTravelerDomainMachineDeletedItemsController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainMachineDeletedItemsController extends ItemTravelerDomainMachineInstanceController implements Serializable {

    public final static String controllerNamed = "itemTravelerDomainMachineDeletedItemsController";
  
    private static final Logger logger = LogManager.getLogger(ItemTravelerDomainMachineDeletedItemsController.class.getName());
    
    private ItemDomainMachineDesignDeletedItemsController itemDomainMachineDesignController = null;

    @Override
    protected ItemController getItemController() {
        if (itemDomainMachineDesignController == null) {
            itemDomainMachineDesignController = ItemDomainMachineDesignDeletedItemsController.getInstance();
        }

        return itemDomainMachineDesignController;
    }
}
