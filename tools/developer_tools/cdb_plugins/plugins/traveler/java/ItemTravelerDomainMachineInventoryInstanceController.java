/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignBaseController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignInventoryController;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named(ItemTravelerDomainMachineInventoryInstanceController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainMachineInventoryInstanceController extends ItemTravelerDomainMachineInstanceController implements Serializable {

    public final static String controllerNamed = "itemTravelerDomainMachineInventoryInstanceController";
  
    private static final Logger logger = LogManager.getLogger(ItemTravelerDomainMachineInventoryInstanceController.class.getName());
    
    private ItemDomainMachineDesignBaseController itemDomainMachineDesignController = null;

    @Override
    protected ItemController getItemController() {
        if (itemDomainMachineDesignController == null) {
            itemDomainMachineDesignController = ItemDomainMachineDesignInventoryController.getInstance();
        }

        return itemDomainMachineDesignController;
    }
}
