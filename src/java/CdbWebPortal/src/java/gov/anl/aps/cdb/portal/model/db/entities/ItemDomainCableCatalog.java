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
        return this.getName();
    }
    
    public void setCableType(String t) {
        this.setName(t);
    }
    
    public String getPartNumber() {
        return this.getItemIdentifier1();
    }
    
    public void setPartNumber(String n) {
        this.setItemIdentifier1(n);
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
