/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;

/**
 *
 * @author darek
 */
public class ItemTypeControllerUtility extends ItemTypeCategoryControllerUtility<ItemType, ItemTypeFacade> {

    @Override
    protected ItemTypeFacade getEntityDbFacade() {
        return ItemTypeFacade.getInstance(); 
    }
    
    @Override
    protected ItemType createItemTypeCategoryEntity() {
        ItemType itemType = new ItemType();        
        return itemType;
    }
       
    @Override
    public String getEntityTypeName() {
        return "itemType";
    }
    
}
