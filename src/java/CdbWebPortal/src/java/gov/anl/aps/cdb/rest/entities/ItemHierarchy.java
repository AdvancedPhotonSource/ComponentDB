/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author djarosz
 */
public class ItemHierarchy {

    private Item parentItem;
    private List<ItemHierarchy> childItems;
    
    public ItemHierarchy(Item parentItem, boolean autocreateHierarchy) {
        this.parentItem = parentItem;
        childItems = new ArrayList<>();
        
        if (autocreateHierarchy) {
            for (ItemElement element: parentItem.getItemElementDisplayList()) {
                Item containedItem = element.getContainedItem();
                if (containedItem != null) {
                    ItemHierarchy child = new ItemHierarchy(containedItem, true); 
                    addChildItem(child);
                }
            }
        }
    }

    public Item getParentItem() {
        return parentItem;
    }

    public void setParentItem(Item parentItem) {
        this.parentItem = parentItem;
    }

    public List<ItemHierarchy> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<ItemHierarchy> childItems) {
        this.childItems = childItems;
    }

    public void addChildItem(ItemHierarchy item) {
        childItems.add(item); 
    }

}
