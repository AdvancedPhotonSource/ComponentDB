/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author djarosz
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CABLE_CATALOG_ID + "")   
public class ItemDomainCableCatalog extends ItemDomainCatalogBase<ItemDomainCableInventory> {
    
    private transient String cableType = null;
    private transient String partNumber = null;
    private transient double weight = 0;
    private transient double diameter = 0;
    private transient String source = null;
    private transient String url = null;

    @Override
    public Item createInstance() {
        return new ItemDomainCableCatalog(); 
    }

    public List<ItemDomainCableInventory> getCableInventoryItemList() {
        return (List<ItemDomainCableInventory>)(List<?>) super.getDerivedFromItemList();
    }
    
    public String getCableType() {
        return cableType;
    }
    
    public void setCableType(String t) {
        cableType = t;
    }
    
    public String getPartNumber() {
        return partNumber;
    }
    
    public void setPartNumber(String n) {
        partNumber = n;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double w) {
        weight = w;
    }
    
    public double getDiameter() {
        return diameter;
    }
    
    public void setDiameter(double d) {
        diameter = d;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String s) {
        source = s;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String u) {
        url = u;
    }
}
