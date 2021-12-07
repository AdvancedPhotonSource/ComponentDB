/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 * @param <Facade>
 * @param <QueryBuilder>
 */
public abstract class ItemLazyDataModel<Facade extends ItemFacadeBase, QueryBuilder extends ItemQueryBuilder> extends CdbLazyDataModel {   

    List<Item> itemList;
    Facade facade;
    Domain itemDomain;

    QueryBuilder queryBuilder = null;
    Integer rowCount = 0;

    public ItemLazyDataModel(Facade facade, Domain itemDomain) {
        this.facade = facade;
        this.itemDomain = itemDomain;
        updateItemList(new ArrayList<>());
    }

    protected void updateItemList(List<Item> itemList) {
        this.itemList = itemList;
        rowCount = itemList.size();
        setRowCount(rowCount);
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

        if (needToReloadLastQuery(sortOrderMap, filterBy, itemDomain.getName())) {
            queryBuilder = getQueryBuilder(filterBy, sortField, sortOrder);

            if (isPaginationQueryBased()) {
                Long countForQuery = facade.getCountForQuery(queryBuilder);
                rowCount = countForQuery.intValue(); 
                setRowCount(rowCount);
            } else {
                List<Item> results = facade.findByDataTableFilterQueryBuilder(queryBuilder);
                updateItemList(results);
            }
        }

        return paginate(first, pageSize);
    }

    @Override
    public int count(Map map) {
        return rowCount;
    }

    private List paginate(int first, int pageSize) {
        if (isPaginationQueryBased()) {
            itemList = facade.findByDataTableFilterQueryBuilderWithPagination(queryBuilder, first, pageSize);
            return itemList;
        } 
        
        int size = itemList.size();

        int last = first + pageSize;
        if (size < last) {
            last = size;
        }

        return itemList.subList(first, last);        
    }

    protected abstract QueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder);

    protected boolean isPaginationQueryBased() {
        return false;
    }

    /**
     * This defeats the purpose of a "lazy" data model, but we need to access
     * the filtered list of items for the export feature. We might want to
     * replace with some sort of iterator/cursor at some point, but for now this
     * is the most straightforward approach...
     */
    public List<Item> getFilteredEntities() {
        return itemList;
    }

    @Override
    public String getRowKey(Object object) {
        if (object instanceof Item) {
            return ((Item) object).getViewUUID();
        }
        return "";
    }

}
