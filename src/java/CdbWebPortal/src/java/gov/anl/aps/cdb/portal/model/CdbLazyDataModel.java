/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public abstract class CdbLazyDataModel extends LazyDataModel {

    private static final Logger LOGGER = LogManager.getLogger(CdbLazyDataModel.class.getName());

    Map<Object, Object> lastFilterMap = null;
    Map<Object, Integer> lastSortMap = null;       

    protected boolean needToReloadLastQuery(Map sortOrderMap, Map filterBy, String entityName) {
        if (lastFilterMap == null || lastSortMap == null) {
            LOGGER.debug("Initialize lazy data model filter/sort for " + entityName);
            copyLastMaps(sortOrderMap, filterBy);
            return true;
        }

        Set filterKeySet = filterBy.keySet();
        Set sortKeySet = sortOrderMap.keySet();

        Set lastFilerKeySet = lastFilterMap.keySet();
        Set lastSortKeySet = lastSortMap.keySet();

        if (lastFilerKeySet.size() != filterKeySet.size() || lastSortKeySet.size() != sortKeySet.size()) {
            copyLastMaps(sortOrderMap, filterBy);
            return true;
        }

        for (Object key : filterKeySet) {
            FilterMeta curFilter = (FilterMeta) filterBy.get(key);
            Object filterValue = curFilter.getFilterValue();
            Object lastFilterValue = lastFilterMap.get(key);

            if (lastFilterValue == null || lastFilterValue.equals(filterValue) == false) {
                copyLastMaps(sortOrderMap, filterBy);
                return true;
            }
        }

        for (Object key : sortKeySet) {
            SortMeta curValue = (SortMeta) sortOrderMap.get(key);
            SortOrder sortOrder = curValue.getOrder();
            int intValue = sortOrder.intValue();
            Integer lastIntValue = lastSortMap.get(key);

            if (intValue != lastIntValue) {
                copyLastMaps(sortOrderMap, filterBy);
                return true;
            }
        }

        return false;
    }
    
    private void copyLastMaps(Map sortOrderMap, Map filterBy) {
        lastFilterMap = new HashMap();
        lastSortMap = new HashMap();

        for (Object key : filterBy.keySet()) {
            FilterMeta filterMeta = (FilterMeta) filterBy.get(key);
            Object filterValue = filterMeta.getFilterValue();
            lastFilterMap.put(key, filterValue);
        }

        for (Object key : sortOrderMap.keySet()) {
            SortMeta curValue = (SortMeta) sortOrderMap.get(key);
            SortOrder sortOrder = curValue.getOrder();

            int intValue = sortOrder.intValue();
            lastSortMap.put(key, intValue);
        }
    }

}
