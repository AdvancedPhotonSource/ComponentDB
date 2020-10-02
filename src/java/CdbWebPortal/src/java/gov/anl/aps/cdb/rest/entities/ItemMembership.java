/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;

/**
 *
 * @author djarosz
 */
public class ItemMembership {

    private Item partOfItem;
    private ItemElement representedBy;

    public ItemMembership(ItemElement ie) {
        representedBy = ie;
        partOfItem = ie.getParentItem();
    }

    public Item getPartOfItem() {
        return partOfItem;
    }

    public ItemElement getRepresentedBy() {
        return representedBy;
    }

}
