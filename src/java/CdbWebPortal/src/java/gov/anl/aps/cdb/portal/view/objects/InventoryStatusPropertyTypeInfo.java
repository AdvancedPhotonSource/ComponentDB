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
 * @author craig
 */
public class InventoryStatusPropertyTypeInfo {
    
    private List<InventoryStatusPropertyAllowedValue> valueList = new ArrayList<>();
    protected Map<String, InventoryStatusPropertyAllowedValue> valueMap = new HashMap<>();
    private String defaultValue;
    
    public void addValue(String value, Float sortOrder) {
        InventoryStatusPropertyAllowedValue av = new InventoryStatusPropertyAllowedValue(value, sortOrder);
        valueList.add(av);
        valueMap.put(value, av);
    }
    
    public List<InventoryStatusPropertyAllowedValue> getValues() {
        return valueList;
    }
    
    public InventoryStatusPropertyAllowedValue getValue(String key) {
        return valueMap.get(key);
    }
    
    public boolean hasValue(String key) {
        return valueMap.containsKey(key);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
