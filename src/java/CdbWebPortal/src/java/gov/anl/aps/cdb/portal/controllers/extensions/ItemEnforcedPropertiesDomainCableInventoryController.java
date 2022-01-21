/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author craig
 */
@Named(ItemEnforcedPropertiesDomainCableInventoryController.controllerNamed)
@SessionScoped
public class ItemEnforcedPropertiesDomainCableInventoryController 
        extends ItemEnforcedPropertiesController 
        implements Serializable {
    
    public final static String controllerNamed = "ItemEnforcedPropertiesDomainCableInventoryController";
    
    protected ItemDomainCableInventoryController itemDomainCableInventoryController = null;    

    @Override
    protected ItemController getItemController() {
        return getItemDomainCableInventoryController(); 
    }
    
    @Override
    protected String[] getElevatedCategoryNames() {
        return null; 
    }

    protected ItemDomainCableInventoryController getItemDomainCableInventoryController() {
        if (itemDomainCableInventoryController == null) {
            itemDomainCableInventoryController = ItemDomainCableInventoryController.getInstance();
        }
        return itemDomainCableInventoryController; 
    }
    
     public static ItemEnforcedPropertiesDomainCableInventoryController getInstance() {
        return (ItemEnforcedPropertiesDomainCableInventoryController) SessionUtility.findBean(controllerNamed);
    }

    @Override
    public List<PropertyType> getRequiredPropertyTypeListForItem(Item item) {
        int domainId = getDomainId(); 
        Item catalogItem = item.getDerivedFromItem();
        return getRequiredPropertyTypeListForItem(catalogItem, domainId); 
    }
    
}
