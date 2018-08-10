/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author djarosz
 */
public class ItemHierarchyCache {
    Item parentItem = null; 
    List<ItemHierarchyCache> childrenItem = null; 

    public ItemHierarchyCache(Item parentItem) {
        // TODO add infinite ref prevention
        this.parentItem = parentItem; 
        
        List<ItemElement> itemElementDisplayList = parentItem.getItemElementDisplayList();
        List<ItemHierarchyCache> itemHierarchyCaches = new ArrayList<>(); 
        if (itemElementDisplayList.size() > 0) {
            for (ItemElement itemElement : itemElementDisplayList) {
                Item containedItem = itemElement.getContainedItem();
                if (containedItem != null) {
                    ItemHierarchyCache itemHierarchyCache = new ItemHierarchyCache(containedItem); 
                    itemHierarchyCaches.add(itemHierarchyCache); 
                }
            }
            this.childrenItem = itemHierarchyCaches; 
        }
        
    }

    public Item getParentItem() {
        return parentItem;
    }

    public List<ItemHierarchyCache> getChildrenItem() {
        return childrenItem;
    }
    
}
