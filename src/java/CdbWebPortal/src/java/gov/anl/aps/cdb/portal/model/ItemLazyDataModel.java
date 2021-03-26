/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public abstract class ItemLazyDataModel<Facade extends ItemFacadeBase, QueryBuilder extends ItemQueryBuilder> extends LazyDataModel {

    List<Item> itemList;
    Facade facade;
    Domain itemDomain;

    Map lastFilterMap = null;

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
    public List load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filterBy) {
        if (filterBy.size() == 0) {
            // Innitial set
            lastFilterMap = filterBy;
        }

        if (lastFilterMap.equals(filterBy) == false) {
            QueryBuilder itemQueryBuilder = getQueryBuilder(filterBy, sortField, sortOrder); 

            List<Item> results = facade.findByDataTableFilterQueryBuilder(itemQueryBuilder);
            updateItemList(results);

        }

        return paginate(first, pageSize);
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
     * This defeats the purpose of a "lazy" data model, but we need to access the
     * filtered list of items for the export feature.  We might want to replace with
     * some sort of iterator/cursor at some point, but for now this is the most
     * straightforward approach...
     */
    public List<Item> getFilteredEntities() {
        return itemList;
    }
}
