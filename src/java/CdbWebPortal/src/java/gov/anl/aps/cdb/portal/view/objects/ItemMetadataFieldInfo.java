/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.common.constants.ItemMetadataFieldType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cmcchesney
 */
public class ItemMetadataFieldInfo {

    protected String key;
    protected String label;
    protected String description;
    protected ItemMetadataFieldType type;
    protected String units;
    private List<String> allowedValues;

    public ItemMetadataFieldInfo(
            String key, 
            String label, 
            String description, 
            ItemMetadataFieldType type, 
            String units, 
            List<String> allowedValues) {
        
        this.key = key;
        this.label = label;
        this.description = description;
        this.type = type;
        this.units = units;
        if (allowedValues != null) {
            this.allowedValues = allowedValues;
        } else {
            this.allowedValues = new ArrayList<>();
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemMetadataFieldType getType() {
        return type;
    }
    
    public String getTypeString() {
        return type.toString();
    }

    public void setType(ItemMetadataFieldType type) {
        this.type = type;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<String> allowedValues) {
        if (allowedValues != null) {
            this.allowedValues = allowedValues;
        } else {
            allowedValues = new ArrayList<>();
        }
    }
    
    public boolean hasAllowedValues() {
        return ((allowedValues != null) && (!allowedValues.isEmpty()));
    }
    
    public boolean hasAllowedValue(String value) {
        if (hasAllowedValues()) {
            for (String allowedValue : allowedValues) {
                if (allowedValue.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
}
