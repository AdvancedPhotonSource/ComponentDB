/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities.comparator;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.Comparator;

/**
 *
 * @author darek
 */
public class ItemSelfElementSortOrderComparator extends ItemElementSortBase implements Comparator<Item> {

    @Override
    public int compare(Item o1, Item o2) {
        ItemElement selfElement = o1.getSelfElement();
        ItemElement selfElement1 = o2.getSelfElement();
        
        return compareSortOrder(selfElement, selfElement1);        
    }

}
