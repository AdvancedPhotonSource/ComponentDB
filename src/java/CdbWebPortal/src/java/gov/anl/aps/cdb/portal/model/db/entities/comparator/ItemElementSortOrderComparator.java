/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities.comparator;

import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.Comparator;

/**
 *
 * @author darek
 */
public class ItemElementSortOrderComparator extends ItemElementSortBase implements Comparator<ItemElement> {

    @Override
    public int compare(ItemElement o1, ItemElement o2) {
        return compareSortOrder(o1, o2);
    }
    
}
