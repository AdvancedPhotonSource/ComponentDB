/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named(ItemTravelerDomainMachineInstanceController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainMachineInstanceController extends ItemTravelerDomainInstanceControllerBase implements Serializable {

    public final static String controllerNamed = "itemTravelerDomainMachineInstanceController";
  
    private static final Logger logger = LogManager.getLogger(ItemTravelerDomainMachineInstanceController.class.getName());
    
    private ItemDomainMachineDesignController itemDomainMachineDesignController = null;

    @Override
    protected ItemController getItemController() {
        if (itemDomainMachineDesignController == null) {
            itemDomainMachineDesignController = ItemDomainMachineDesignController.getInstance();
        }

        return itemDomainMachineDesignController;
    }

    @Override
    protected List<PropertyValue> getInternalPropertyValueListForItem(Item item) {
        List<PropertyValue> pvList = new ArrayList<>();
        if (!(item instanceof ItemDomainMachineDesign)) {
            return pvList;
        }
        // add templates for machine design item and template that it's instantiated
        // from, if any
        pvList.addAll(item.getPropertyValueInternalList());
        if (item.getCreatedFromTemplate() != null) {
            pvList.addAll(item.getCreatedFromTemplate().getPropertyValueInternalList());
        }
        return pvList;
    }
}
