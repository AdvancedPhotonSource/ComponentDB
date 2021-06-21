/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.HashMap;
import java.util.List;
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
public abstract class ItemLazyDataModel<Facade extends ItemFacadeBase, QueryBuilder extends ItemQueryBuilder> extends LazyDataModel {

    private static final Logger LOGGER = LogManager.getLogger(ItemLazyDataModel.class.getName());

    List<Item> itemList;
    Facade facade;
    Domain itemDomain;

    Map<Object, Object> lastFilterMap = null;
    Map<Object, Integer> lastSortMap = null;

    public ItemLazyDataModel(Facade facade, Domain itemDomain) {
        this.facade = facade;
        this.itemDomain = itemDomain;
        initalizeItemList();
    }

    protected void initalizeItemList() {
        updateItemList(facade.findByDomain(itemDomain.getName()));
    }

    protected void updateItemList(List<Item> itemList) {
        this.itemList = itemList;
        setRowCount(itemList.size());
    }

    @Override
    public List load(int first, int pageSize, Map sortOrderMap, Map filterBy) {
        String sortField = null;
        SortMeta sortMeta;
        SortOrder sortOrder = null;

        if (!sortOrderMap.isEmpty()) {
            sortField = (String) sortOrderMap.keySet().toArray()[0];
            sortMeta = (SortMeta) sortOrderMap.get(sortField);
            sortOrder = sortMeta.getOrder();
        }

        if (needToReloadLastQuery(sortOrderMap, filterBy)) {
            QueryBuilder itemQueryBuilder = getQueryBuilder(filterBy, sortField, sortOrder);

            List<Item> results = facade.findByDataTableFilterQueryBuilder(itemQueryBuilder);
            updateItemList(results);
        }

        return paginate(first, pageSize);
    }

    private boolean needToReloadLastQuery(Map sortOrderMap, Map filterBy) {
        if (lastFilterMap == null || lastSortMap == null) {
            LOGGER.debug("Initialize lazy data model filter/sort for: " + itemDomain.getName());
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
        
        for (Object key: filterBy.keySet()) {
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

    private List paginate(int first, int pageSize) {
        int size = itemList.size();

        int last = first + pageSize;
        if (size < last) {
            last = size;
        }

        return itemList.subList(first, last);
    }

    protected abstract QueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder);

    /**
     * This defeats the purpose of a "lazy" data model, but we need to access
     * the filtered list of items for the export feature. We might want to
     * replace with some sort of iterator/cursor at some point, but for now this
     * is the most straightforward approach...
     */
    public List<Item> getFilteredEntities() {
        return itemList;
    }
}
