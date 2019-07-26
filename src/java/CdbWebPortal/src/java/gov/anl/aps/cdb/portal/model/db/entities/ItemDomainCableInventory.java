/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CABLE_INVENTORY_ID + "")   
public class ItemDomainCableInventory extends Item {

    @Override
    public Item createInstance() {
        return new ItemDomainCableInventory(); 
    }

    public ItemDomainCableCatalog getCableCatalogItem() {
        return (ItemDomainCableCatalog) getDerivedFromItem();
    }
    
}
