/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.common.constants.ItemCoreMetadataFieldType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cmcchesney
 */
public class ItemCoreMetadataFieldInfo {

    protected String key;
    protected String label;
    protected String description;
    protected ItemCoreMetadataFieldType type;
    protected String units;
    private List<String> allowedValues;

    public ItemCoreMetadataFieldInfo(String k, String l, String d, ItemCoreMetadataFieldType t, String u, List<String> allowedValues) {
        key = k;
        label = l;
        description = d;
        type = t;
        units = u;
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

    public ItemCoreMetadataFieldType getType() {
        return type;
    }
    
    public String getTypeString() {
        return type.toString();
    }

    public void setType(ItemCoreMetadataFieldType type) {
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
}
