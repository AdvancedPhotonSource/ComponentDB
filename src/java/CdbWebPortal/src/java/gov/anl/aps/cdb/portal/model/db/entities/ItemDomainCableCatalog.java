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
    
    private transient String weight = null;
    private transient String diameter = null;
    private transient String source = null;
    private transient String url = null;
    
    private final static String CABLE_INTERNAL_PROPERTY_TYPE = "cable_internal_property_type"; 
    private final static String CABLE_PROPERTY_WEIGHT_KEY = "weight"; 
    private final static String CABLE_PROPERTY_DIAMETER_KEY = "diameter"; 

    @Override
    public Item createInstance() {
        return new ItemDomainCableCatalog(); 
    }

    public List<ItemDomainCableInventory> getCableInventoryItemList() {
        return (List<ItemDomainCableInventory>)(List<?>) super.getDerivedFromItemList();
    }
    
    private PropertyValue getInternalCablePropertyValue() {
        List<PropertyValue> propertyValueList = getPropertyValueList(); 
        for (PropertyValue propertyValue: propertyValueList) {
            if (propertyValue.getPropertyType().getName().equals(CABLE_INTERNAL_PROPERTY_TYPE)) {
                return propertyValue; 
            }
        }
        return null; 
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
    
    public String getWeight() {
        if (weight == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                weight = "";
            } else {
                weight = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_WEIGHT_KEY);
            }
        }
        return weight;
    }
    
    public void setWeight(String w) {
        weight = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_WEIGHT_KEY, w);
        }
    }
    
    public String getDiameter() {
        if (diameter == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                diameter = "";
            } else {
                diameter = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_DIAMETER_KEY);
            }
        }
        return diameter;
    }
    
    public void setDiameter(String d) {
        diameter = d;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_DIAMETER_KEY, d + "");
        }
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
