/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.io.Serializable;
import java.util.List;
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

    @Override
    protected List<Item> getItemListForTravelersCreatedFromTemplate() {
        Item current = getCurrent();        
        return current.getItemsCreatedFromThisTemplateItem();        
    }
}
