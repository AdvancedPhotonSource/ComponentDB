/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cdb
 */
public class ItemCoreMetadataPropertyInfo {
    
    public enum FieldType {
        STRING, NUMERIC, URL
    }
    
    public class FieldInfo {

        protected String key;
        protected String label;
        protected String description;
        protected FieldType type;
        protected String units;
        
        public FieldInfo(String k, String l, String d, FieldType t, String u) {
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

        public FieldType getType() {
            return type;
        }

        public void setType(FieldType type) {
            this.type = type;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }
    }

    protected String displayName = "";
    protected String propertyName = "";
    
    protected List<FieldInfo> fieldList = new ArrayList<>();
    protected Map<String, FieldInfo> fieldMap = new HashMap<>();
    
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
    
    public void addField(String k, String l, String d, FieldType t, String u) {
        FieldInfo info = new FieldInfo(k, l, d, t, u);
        fieldList.add(info);
        fieldMap.put(k, info);
    }
    
    public List<FieldInfo> getFields() {
        return fieldList;
    }
    
    public FieldInfo getField(String key) {
        return fieldMap.get(key);
    }
    
    public boolean hasKey(String key) {
        return fieldMap.containsKey(key);
    }
    
}
