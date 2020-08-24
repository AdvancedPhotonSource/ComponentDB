/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract class represents a locatable item that can have a status
 * 
 * @author djarosz
 */
public abstract class LocatableStatusItem extends LocatableItem {

    // Inventory status variables
    protected transient PropertyValue inventoryStatusPropertyValue;
    protected transient boolean loadedCurrentStatusPropertyValue = false;
    
    // A status can represent if spare (set by controller)
    protected transient Boolean sparePartIndicator = null;
    
    public abstract String getStatusPropertyTypeName();
    
    @JsonIgnore
    public PropertyValue getInventoryStatusPropertyValue() {
        if (!loadedCurrentStatusPropertyValue) {
            if (this.getPropertyValueInternalList() != null) {
                for (PropertyValue propertyValue : this.getPropertyValueInternalList()) {
                    String propertyTypeName = propertyValue.getPropertyType().getName();
                    if (propertyTypeName.equals(getStatusPropertyTypeName())) {
                        inventoryStatusPropertyValue = propertyValue;
                        break;
                    }
                }
            }
            loadedCurrentStatusPropertyValue = true;
        }
        return inventoryStatusPropertyValue;
    }

    public void setInventoryStatusPropertyValue(PropertyValue inventoryStatusPropertyValue) {
        this.inventoryStatusPropertyValue = inventoryStatusPropertyValue;
    }
    
     @JsonIgnore
    public String getInventoryStatusValue() {
        if (getInventoryStatusPropertyValue() != null) {
            String value = getInventoryStatusPropertyValue().getValue();
            if (value != null) {
                return value;
            }
        }
        return "";
    }

    public void setInventoryStatusValue(String status) {
        if (getInventoryStatusPropertyValue() != null) {
            getInventoryStatusPropertyValue().setValue(status);
            sparePartIndicator = null;
        }
    }

}
