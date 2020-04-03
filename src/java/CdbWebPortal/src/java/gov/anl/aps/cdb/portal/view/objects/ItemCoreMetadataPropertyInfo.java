/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.common.constants.ItemCoreMetadataFieldType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cdb
 */
public class ItemCoreMetadataPropertyInfo {
    
    protected String displayName = "";
    protected String propertyName = "";
    
    protected List<ItemCoreMetadataFieldInfo> fieldList = new ArrayList<>();
    protected Map<String, ItemCoreMetadataFieldInfo> fieldMap = new HashMap<>();
    
    public ItemCoreMetadataPropertyInfo(String dn, String pn) {
        displayName = dn;
        propertyName = pn;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public void addField(String k, String l, String d, ItemCoreMetadataFieldType t, String u) {
        ItemCoreMetadataFieldInfo info = new ItemCoreMetadataFieldInfo(k, l, d, t, u);
        fieldList.add(info);
        fieldMap.put(k, info);
    }
    
    public List<ItemCoreMetadataFieldInfo> getFields() {
        return fieldList;
    }
    
    public ItemCoreMetadataFieldInfo getField(String key) {
        return fieldMap.get(key);
    }
    
    public boolean hasKey(String key) {
        return fieldMap.containsKey(key);
    }
    
}
