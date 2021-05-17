/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.common.constants.ItemMetadataFieldType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cdb
 */
public class ItemMetadataPropertyInfo {
    
    private static final String DEFAULT_GROUP = "default";
    
    private String displayName = "";
    private String propertyName = "";
    
    private List<ItemMetadataFieldInfo> fieldList = new ArrayList<>();
    private Map<String, ItemMetadataFieldInfo> fieldMap = new HashMap<>();
    private Map<String, List<ItemMetadataFieldInfo>> groupMap = new HashMap<>();
    private List<String> groupList = new ArrayList<>();
    
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
    
    public void addField(
            String key, 
            String label, 
            String description, 
            ItemMetadataFieldType type, 
            String units, 
            List<String> allowedValues) {
        
        addField(key, label, description, type, units, allowedValues, null);
    }
    
    public void addField(
            String key, 
            String label, 
            String description, 
            ItemMetadataFieldType type, 
            String units, 
            List<String> allowedValues,
            String group) {
        
        ItemMetadataFieldInfo info = 
                new ItemMetadataFieldInfo(key, label, description, type, units, allowedValues);
        fieldList.add(info);
        fieldMap.put(key, info);
        
        if (group == null) {
            group = DEFAULT_GROUP;
        }
        List<ItemMetadataFieldInfo> groupFields = groupMap.get(group);
        if (groupFields == null) {
            groupFields = new ArrayList<>();
            groupMap.put(group, groupFields);
        }
        groupFields.add(info);
        if (!groupList.contains(group)) {
            groupList.add(group);
        }
    }
    
    public List<ItemMetadataFieldInfo> getFields() {
        return fieldList;
    }
    
    public List<String> getGroupList() {
        return groupList;
    }
    
    public List<ItemMetadataFieldInfo> getFieldsInDefaultGroup() {
        return getFieldsInGroup(DEFAULT_GROUP);
    }
    
    public List<ItemMetadataFieldInfo> getFieldsInGroup(String group) {
        List<ItemMetadataFieldInfo> groupFields = groupMap.get(group);
        if (groupFields == null) {
            return new ArrayList<>();
        } else {
            return groupFields;
        }
    }
    
    public ItemMetadataFieldInfo getField(String key) {
        return fieldMap.get(key);
    }
    
    public boolean hasKey(String key) {
        return fieldMap.containsKey(key);
    }
    
}
