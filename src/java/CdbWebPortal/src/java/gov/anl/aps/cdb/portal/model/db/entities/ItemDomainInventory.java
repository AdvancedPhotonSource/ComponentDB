/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryControllerUtility;
import gov.anl.aps.cdb.portal.model.jsf.beans.SparePartsBean;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.INVENTORY_ID + "")
public class ItemDomainInventory extends ItemDomainInventoryBase<ItemDomainCatalog> {

    public static final String ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME = "Component Instance Status";
   // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">        
    private transient List<ItemElementRelationship> relatedMAARCRelationshipsForCurrent = null;       
    // </editor-fold>
    
    @Override
    public Item createInstance() {
        return new ItemDomainInventory();
    } 

    @Override
    public ItemControllerUtility getItemControllerUtility() {
        return new ItemDomainInventoryControllerUtility(); 
    }
    
    @JsonIgnore
    public String getStatusPropertyTypeName() {
        return ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME;
    }

    @Override
    protected String getDerivedFromLabel() {
        return "Catalog"; 
    }

    public static String generatePaddedUnitName(int itemNumber) {
        return String.format("Unit: %04d", itemNumber);
    }
    
    public String generateUnitName(int itemNumber) {
        return ItemDomainInventory.generatePaddedUnitName(itemNumber);
    }
    
    @Override
    // TODO API Change back to json ignore and utilize the catalog item 
    //@JsonIgnore
    public Item getDerivedFromItem() {
        return super.getDerivedFromItem(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ItemController getItemDomainController() {
        return ItemDomainInventoryController.getInstance(); 
    }
    
    /**
     * This method is redundant to the generic method defined in the superclass,
     * ItemDomainInventoryBase.  It is needed here because the import wizard
     * uses reflection to invoke the setter method, and apparently the generic
     * method is not a valid match for invocation by reflection.
     * @param catalogItem 
     */
    public void setCatalogItem(ItemDomainCatalog catalogItem) {
        super.setCatalogItem(catalogItem);
    }

    public String getTag() {
        return getName();
    }

    public void setTag(String name) {
        setName(name);
    }

    public String getSerialNumber() {
        return this.getItemIdentifier1();
    }
    
    public void setSerialNumber(String serialNumber) {
        this.setItemIdentifier1(serialNumber);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Controller variables for current.">        
    @JsonIgnore
    public List<ItemElementRelationship> getRelatedMAARCRelationshipsForCurrent() {
        return relatedMAARCRelationshipsForCurrent;
    }

    public void setRelatedMAARCRelationshipsForCurrent(List<ItemElementRelationship> relatedMAARCRelationshipsForCurrent) {
        this.relatedMAARCRelationshipsForCurrent = relatedMAARCRelationshipsForCurrent;
    }
    
    // </editor-fold>
    
}
