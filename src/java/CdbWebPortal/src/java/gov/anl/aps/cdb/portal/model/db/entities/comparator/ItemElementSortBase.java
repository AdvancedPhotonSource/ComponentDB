/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities.comparator;

import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;

/**
 *
 * @author darek
 */
public abstract class ItemElementSortBase {
    
    public int compareSortOrder(ItemElement o1, ItemElement o2) {
        Float so1 = o1.getSortOrder();
        Float so2 = o2.getSortOrder();

        return compareOrders(so1, so2); 
    }
    
    public int compareRelevantSortOrder(ItemElement o1, ItemElement o2) {
        Float so1 = o1.getRelevantItemElementSortOrder();
        Float so2 = o2.getRelevantItemElementSortOrder();
        
        return compareOrders(so1, so2);
    }
    
    private int compareOrders(Float so1, Float so2) {
        if (so1 == null) {
            so1 = Float.MAX_VALUE;
        }
        if (so2 == null) {
            so2 = Float.MAX_VALUE;
        }

        if (so1 > so2) {
            return 1;
        } else if (so1 < so2) {
            return -1;
        }

        return 0;
    }
}
