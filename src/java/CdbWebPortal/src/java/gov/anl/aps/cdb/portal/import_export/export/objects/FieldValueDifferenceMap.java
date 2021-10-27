/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author craig
 */
public class FieldValueDifferenceMap {
    
    private Map<String, FieldValueDifference> differenceMap = new LinkedHashMap<>();
    
    public Set<String> keySet() {
        return differenceMap.keySet();
    }
    
    public FieldValueDifference get(String key) {
        return differenceMap.get(key);
    }
    
    public void put(String key, FieldValueDifference diff) {
        differenceMap.put(key, diff);
    }
    
}
