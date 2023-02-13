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

    public ItemMembership(Item parentItem) {
        partOfItem = parentItem;
    }

    public Item getPartOfItem() {
        return partOfItem;
    }

    public ItemElement getRepresentedBy() {
        return partOfItem.getMembershipItemElement(); 
    }
    
    public Item getMembershipBy() {
        return partOfItem.getMembershipByItem(); 
    }

}
