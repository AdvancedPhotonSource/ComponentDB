/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.common.constants.ItemMetadataFieldType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cdb
 */
public class ItemMetadataPropertyInfo {
    
    protected String displayName = "";
    protected String propertyName = "";
    
    protected List<ItemMetadataFieldInfo> fieldList = new ArrayList<>();
    protected Map<String, ItemMetadataFieldInfo> fieldMap = new HashMap<>();
    
    public ItemMetadataPropertyInfo(String dn, String pn) {
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
    
    public void addField(String k, String l, String d, ItemMetadataFieldType t, String u, List<String> allowedValues) {
        ItemMetadataFieldInfo info = new ItemMetadataFieldInfo(k, l, d, t, u, allowedValues);
        fieldList.add(info);
        fieldMap.put(k, info);
    }
    
    public List<ItemMetadataFieldInfo> getFields() {
        return fieldList;
    }
    
    public ItemMetadataFieldInfo getField(String key) {
        return fieldMap.get(key);
    }
    
    public boolean hasKey(String key) {
        return fieldMap.containsKey(key);
    }
    
}
