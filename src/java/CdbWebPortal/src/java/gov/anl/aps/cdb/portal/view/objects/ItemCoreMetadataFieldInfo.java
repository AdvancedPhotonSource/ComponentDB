/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.common.constants.ItemCoreMetadataFieldType;

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

    public ItemCoreMetadataFieldInfo(String k, String l, String d, ItemCoreMetadataFieldType t, String u) {
        key = k;
        label = l;
        description = d;
        type = t;
        units = u;
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
}
