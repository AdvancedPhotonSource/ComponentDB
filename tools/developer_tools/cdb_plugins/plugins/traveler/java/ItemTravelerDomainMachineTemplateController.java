/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author craig
 */
@Named(ItemTravelerDomainMachineTemplateController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainMachineTemplateController extends ItemTravelerDomainTemplateControllerBase implements Serializable {

    public final static String controllerNamed = "itemTravelerDomainMachineTemplateController";

    private ItemDomainMachineDesignController itemDomainMachineDesignController = null;

    @Override
    protected ItemController getItemController() {
        if (itemDomainMachineDesignController == null) {
            itemDomainMachineDesignController = ItemDomainMachineDesignController.getInstance();
        }
        return itemDomainMachineDesignController;
    }
}
