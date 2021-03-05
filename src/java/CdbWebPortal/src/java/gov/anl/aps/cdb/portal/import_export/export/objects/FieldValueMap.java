/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author craig
 */
public class FieldValueMap {
    
    // use sorted map to simplify comparison
    private Map<String, String> valueMap = new TreeMap<>();

    public Set<String> keySet() {
        return valueMap.keySet();
    }
    
    public void put(String key, String value) {
        valueMap.put(key, value);
    }
    
    public String get(String key) {
        return valueMap.get(key);
    }
    
    public static FieldValueDifferenceMap difference(FieldValueMap first, FieldValueMap second) {
        FieldValueDifferenceMap diffMap = new FieldValueDifferenceMap();
        for (String key : first.keySet()) {
            if (!second.get(key).equals(first.get(key))) {
                FieldValueDifference diff = new FieldValueDifference(first.get(key), second.get(key));
                diffMap.put(key, diff);
            }
        }
        return diffMap;
    }
    
}
