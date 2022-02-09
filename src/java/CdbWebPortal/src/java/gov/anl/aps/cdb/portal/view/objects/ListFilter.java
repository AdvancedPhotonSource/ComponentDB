/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ListFilter {
    
    private Map<String, ListFilterCriteria> criteriaMap = null;
    
    public Map<String, ListFilterCriteria> getCriteriaMap() {
        if (criteriaMap == null) {
            criteriaMap = new HashMap<>();
        }
        return criteriaMap;
    }
    
    public Collection<ListFilterCriteria> getCriteria() {
        return getCriteriaMap().values();
    }
    
    public void addCriteria(String name) {
        ListFilterCriteria criteria = new ListFilterCriteria(name, null);
        getCriteriaMap().put(name, criteria);
    }
    
    public void reset() {
        for (ListFilterCriteria criteria : getCriteriaMap().values()) {
            criteria.setValue(null);
        }
    }
    
}
