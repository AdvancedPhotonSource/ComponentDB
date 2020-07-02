/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CABLE_INVENTORY_ID + "")   
public class ItemDomainCableInventory extends ItemDomainInventoryBase<ItemDomainCableCatalog> {

    public final static String CABLE_INVENTORY_INTERNAL_PROPERTY_TYPE = "cable_inventory_internal_property_type";
    public final static String CABLE_INVENTORY_PROPERTY_LENGTH_KEY = "length";
    
    private transient String length;
    
    @Override
    public Item createInstance() {
        return new ItemDomainCableInventory(); 
    }
    
    /**
     * This method is redundant to the generic method defined in the superclass,
     * ItemDomainInventoryBase.  It is needed here because the import wizard
     * uses reflection to invoke the setter method, and apparently the generic
     * method is not a valid match for invocation by reflection.
     * @param catalogItem 
     */
    public void setCatalogItem(ItemDomainCableCatalog catalogItem) {
        super.setCatalogItem(catalogItem);
    }

    public String getLength() throws CdbException {
        if (length == null) {
            length = getCoreMetadataPropertyFieldValue(CABLE_INVENTORY_PROPERTY_LENGTH_KEY);
        }
        return length;
    }

    public void setLength(String l) throws CdbException {
        length = l;
        setCoreMetadataPropertyFieldValue(CABLE_INVENTORY_PROPERTY_LENGTH_KEY, l);
    }
}
