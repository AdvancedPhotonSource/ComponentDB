/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
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
@Named(ItemTravelerDomainInventoryController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainInventoryController extends ItemTravelerDomainInstanceControllerBase implements Serializable {

    public final static String controllerNamed = "itemTravelerDomainInventoryController";
  
    private static final Logger logger = LogManager.getLogger(ItemTravelerDomainInventoryController.class.getName());
    
    private ItemDomainInventoryController itemDomainInventoryController = null;

    @Override
    protected ItemController getItemController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = ItemDomainInventoryController.getInstance();
        }

        return itemDomainInventoryController;
    }
    
    @Override
    protected List<PropertyValue> getInternalPropertyValueListForItem(Item item) {
        List<PropertyValue> pvList = new ArrayList<>();
        if (!(item instanceof ItemDomainInventory)) {
            return pvList;
        }
        // add templates for inventory item and catalog item it's derived from
        pvList.addAll(item.getPropertyValueInternalList());
        if (item.getDerivedFromItem() != null) {
            pvList.addAll(item.getDerivedFromItem().getPropertyValueInternalList());
        }
        return pvList;
    }
}
