/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import java.util.ArrayList;
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
    
    private transient String url = null;
    private transient String imageUrl = null;
    private transient String manufacturer = null;
    private transient String weight = null;
    private transient String diameter = null;
    private transient String conductors = null;
    private transient String insulation = null;
    private transient String jacketColor = null;
    private transient String voltageRating = null;
    private transient String fireLoad = null;
    private transient String heatLimit = null;
    private transient String bendRadius = null;
    private transient String team = null;
    
    private final static String CABLE_INTERNAL_PROPERTY_TYPE = "cable_internal_property_type"; 
    private final static String CABLE_PROPERTY_URL_KEY = "url"; 
    private final static String CABLE_PROPERTY_IMAGE_URL_KEY = "imageUrl"; 
    private final static String CABLE_PROPERTY_MANUFACTURER_KEY = "manufacturer"; 
    private final static String CABLE_PROPERTY_WEIGHT_KEY = "weight"; 
    private final static String CABLE_PROPERTY_DIAMETER_KEY = "diameter"; 
    private final static String CABLE_PROPERTY_CONDUCTORS_KEY = "conductors"; 
    private final static String CABLE_PROPERTY_INSULATION_KEY = "insulation"; 
    private final static String CABLE_PROPERTY_JACKET_COLOR_KEY = "jacketColor"; 
    private final static String CABLE_PROPERTY_VOLTAGE_RATING_KEY = "voltageRating"; 
    private final static String CABLE_PROPERTY_FIRE_LOAD_KEY = "fireLoad"; 
    private final static String CABLE_PROPERTY_HEAT_LIMIT_KEY = "heatLimit"; 
    private final static String CABLE_PROPERTY_BEND_RADIUS_KEY = "bendRadius"; 

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
    
    public String getUrl() {
        if (url == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                url = "";
            } else {
                url = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_URL_KEY);
            }
        }
        return url;
    }
    
    public void setUrl(String w) {
        url = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_URL_KEY, w);
        }
    }
    
    public String getImageUrl() {
        if (imageUrl == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                imageUrl = "";
            } else {
                imageUrl = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_IMAGE_URL_KEY);
            }
        }
        return imageUrl;
    }
    
    public void setImageUrl(String w) {
        imageUrl = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_IMAGE_URL_KEY, w);
        }
    }
    
    public String getManufacturer() {
        if (manufacturer == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                manufacturer = "";
            } else {
                manufacturer = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_MANUFACTURER_KEY);
            }
        }
        return manufacturer;
    }
    
    public void setManufacturer(String w) {
        manufacturer = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_MANUFACTURER_KEY, w);
        }
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
    
    public String getConductors() {
        if (conductors == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                conductors = "";
            } else {
                conductors = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_CONDUCTORS_KEY);
            }
        }
        return conductors;
    }
    
    public void setConductors(String w) {
        conductors = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_CONDUCTORS_KEY, w);
        }
    }
    
    public String getInsulation() {
        if (insulation == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                insulation = "";
            } else {
                insulation = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_INSULATION_KEY);
            }
        }
        return insulation;
    }
    
    public void setInsulation(String w) {
        insulation = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_INSULATION_KEY, w);
        }
    }
    
    public String getJacketColor() {
        if (jacketColor == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                jacketColor = "";
            } else {
                jacketColor = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_JACKET_COLOR_KEY);
            }
        }
        return jacketColor;
    }
    
    public void setJacketColor(String w) {
        jacketColor = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_JACKET_COLOR_KEY, w);
        }
    }
    
    public String getVoltageRating() {
        if (voltageRating == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                voltageRating = "";
            } else {
                voltageRating = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_VOLTAGE_RATING_KEY);
            }
        }
        return voltageRating;
    }
    
    public void setVoltageRating(String w) {
        voltageRating = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_VOLTAGE_RATING_KEY, w);
        }
    }
    
    public String getFireLoad() {
        if (fireLoad == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                fireLoad = "";
            } else {
                fireLoad = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_FIRE_LOAD_KEY);
            }
        }
        return fireLoad;
    }
    
    public void setFireLoad(String w) {
        fireLoad = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_FIRE_LOAD_KEY, w);
        }
    }
    
    public String getHeatLimit() {
        if (heatLimit == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                heatLimit = "";
            } else {
                heatLimit = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_HEAT_LIMIT_KEY);
            }
        }
        return heatLimit;
    }
    
    public void setHeatLimit(String w) {
        heatLimit = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_HEAT_LIMIT_KEY, w);
        }
    }
    
    public String getBendRadius() {
        if (bendRadius == null) {
            PropertyValue propertyValue = getInternalCablePropertyValue();
            if (propertyValue == null) {
                bendRadius = "";
            } else {
                bendRadius = propertyValue.getPropertyMetadataValueForKey(CABLE_PROPERTY_BEND_RADIUS_KEY);
            }
        }
        return bendRadius;
    }
    
    public void setBendRadius(String w) {
        bendRadius = w;
        
        PropertyValue propertyValue = getInternalCablePropertyValue();

        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_BEND_RADIUS_KEY, w);
        }
    }
    
    public String getTeam() {
        if (team == null) {
            team = this.getItemCategoryString();
        }
        return team;
    }
    
    public void setTeam(String categoryId) {
        ItemCategory category = ItemCategoryController.getInstance().findById(Integer.valueOf(categoryId));
        if (category != null) {
            List<ItemCategory> categoryList = new ArrayList<>();
            categoryList.add(category);
            this.setItemCategoryList(categoryList);
            team = this.getItemCategoryString();
        }
    }
    
}
