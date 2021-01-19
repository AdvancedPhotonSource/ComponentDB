/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;

/**
 *
 * @author darek
 */
public class ItemCategoryControllerUtility extends ItemTypeCategoryControllerUtility<ItemCategory, ItemCategoryFacade>{

    @Override
    protected ItemCategoryFacade getEntityDbFacade() {
        return ItemCategoryFacade.getInstance(); 
    }

    @Override
    public String getEntityInstanceName(ItemCategory entity) {
        if (entity != null) {
            return entity.getName();
        }
        return "";
    }
        
    @Override
    public String getEntityTypeName() {
        return "itemCategory";
    }   
    
}
