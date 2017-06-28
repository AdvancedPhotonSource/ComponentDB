/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemEnforcedPropertiesDomainInventoryController.controllerNamed)
@SessionScoped
public class ItemEnforcedPropertiesDomainInventoryController extends ItemEnforcedPropertiesController implements Serializable {
    
    public final static String controllerNamed = "ItemEnforcedPropertiesDomainInventoryController";
    
    protected ItemDomainInventoryController itemDomainInventoryController = null;    

    @Override
    protected ItemController getItemController() {
        return getItemDomainInventoryController(); 
    }
    
    @Override
    protected String[] getElevatedCategoryNames() {
        return null; 
    }

    protected ItemDomainInventoryController getItemDomainInventoryController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = ItemDomainInventoryController.getInstance();
        }
        return itemDomainInventoryController; 
    }
    
     public static ItemEnforcedPropertiesDomainInventoryController getInstance() {
        return (ItemEnforcedPropertiesDomainInventoryController) SessionUtility.findBean(controllerNamed);
    }

    @Override
    public List<PropertyType> getRequiredPropertyTypeListForItem(Item item) {
        int domainId = getDomainId(); 
        Item catalogItem = item.getDerivedFromItem();
        return getRequiredPropertyTypeListForItem(catalogItem, domainId); 
    }
    
    public void addRequiredPropertiesToItemDomainInventory(Item item) {
        List<PropertyType> requiredPropertyTypeListForItem = getRequiredPropertyTypeListForItem(item);
        for (PropertyType propertyType : requiredPropertyTypeListForItem) {
            preparePropertyTypeValueAdd(propertyType); 
        }
    }
    
}
