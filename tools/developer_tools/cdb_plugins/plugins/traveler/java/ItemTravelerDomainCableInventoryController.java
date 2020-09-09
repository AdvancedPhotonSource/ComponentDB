/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
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
@Named(ItemTravelerDomainCableInventoryController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainCableInventoryController extends ItemTravelerDomainInstanceControllerBase implements Serializable {

    public final static String controllerNamed = "itemTravelerDomainCableInventoryController";
  
    private static final Logger logger = LogManager.getLogger(ItemTravelerDomainCableInventoryController.class.getName());
    
    private ItemDomainCableInventoryController itemDomainInventoryController = null;

    @Override
    protected ItemController getItemController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = ItemDomainCableInventoryController.getInstance();
        }

        return itemDomainInventoryController;
    }    
    
    protected List<PropertyValue> getInternalPropertyValueListForItem(Item item) {
        List<PropertyValue> pvList = new ArrayList<>();
        if (!(item instanceof ItemDomainCableInventory)) {
            return pvList;
        }
        // add templates for machine design item and template that it's instantiated
        // from, if any
        pvList.addAll(item.getPropertyValueInternalList());
        if (item.getDerivedFromItem() != null) {
            pvList.addAll(item.getDerivedFromItem().getPropertyValueInternalList());
        }
        return pvList;
    }

    @Override
    public boolean isRenderMoveTraveler() {
        return false;
    }
}
