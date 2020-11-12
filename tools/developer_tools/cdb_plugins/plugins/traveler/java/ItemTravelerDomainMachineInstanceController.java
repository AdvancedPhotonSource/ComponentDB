/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
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

    protected List<PropertyValue> getInternalPropertyValueListForItem(Item item) {
        List<PropertyValue> pvList = new ArrayList<>();
        if (!(item instanceof ItemDomainMachineDesign)) {
            return pvList;
        }
        
        ItemDomainMachineDesign machineItem = (ItemDomainMachineDesign) item; 
        
        // add templates for machine design item and template that it's instantiated      
        if (machineItem.getCreatedFromTemplate() != null) {
            pvList.addAll(machineItem.getCreatedFromTemplate().getPropertyValueInternalList());
        } else {
            // Try to see assigned item. 
            Item assignedItem = machineItem.getAssignedItem();
            if (assignedItem instanceof ItemDomainCatalog) {
                if (((ItemDomainCatalog) assignedItem).getInventoryItemList().isEmpty()) {
                    pvList.addAll(assignedItem.getPropertyValueInternalList());
                }                
            }            
        }
        return pvList;
    }

    @Override
    public boolean isRenderMoveTraveler() {
        return false;
    }

}
