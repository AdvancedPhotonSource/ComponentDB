/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableInventoryControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import java.util.List;
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
    
    public static final String ITEM_DOMAIN_CABLE_INVENTORY_STATUS_PROPERTY_TYPE_NAME = "Cable Instance Status";
    
    private transient String length = null;
    
    @Override
    public Item createInstance() {
        return new ItemDomainCableInventory(); 
    } 

    @Override
    public ItemDomainCableInventoryControllerUtility getItemControllerUtility() {
        return new ItemDomainCableInventoryControllerUtility(); 
    }
    
    @JsonIgnore
    public String getStatusPropertyTypeName() {
        return ITEM_DOMAIN_CABLE_INVENTORY_STATUS_PROPERTY_TYPE_NAME;
    }
    
    @JsonIgnore
    public static String generatePaddedUnitName(int itemNumber) {
        return String.format("Unit: %09d", itemNumber);
    }

    public String generateUnitName(int itemNumber) {
        return ItemDomainCableInventory.generatePaddedUnitName(itemNumber);
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

    @JsonIgnore
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
