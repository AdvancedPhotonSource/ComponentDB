/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;

/**
 *
 * @author darek
 */
public class ItemCategoryControllerUtility extends ItemTypeCategoryControllerUtility<ItemCategory, ItemCategoryFacade>{
    
    ItemTypeFacade itemTypeFacade; 

    public ItemCategoryControllerUtility() {
        itemTypeFacade = ItemTypeFacade.getInstance(); 
    }

    @Override
    protected ItemCategoryFacade getEntityDbFacade() {
        return ItemCategoryFacade.getInstance(); 
    }

    @Override
    protected void clearCaches() {
        itemTypeFacade.clearCache();
    }
    
    @Override
    protected ItemCategory createItemTypeCategoryEntity() {
        ItemCategory itemCategory = new ItemCategory();
        return itemCategory; 
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
