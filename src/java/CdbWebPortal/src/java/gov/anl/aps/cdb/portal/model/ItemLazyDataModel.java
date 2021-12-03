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
public abstract class ItemLazyDataModel<Facade extends ItemFacadeBase, QueryBuilder extends ItemQueryBuilder> extends CdbLazyDataModel {

    private static final Logger LOGGER = LogManager.getLogger(ItemLazyDataModel.class.getName());

    List<Item> itemList;
    Facade facade;
    Domain itemDomain;

    public ItemLazyDataModel(Facade facade, Domain itemDomain) {
        this.facade = facade;
        this.itemDomain = itemDomain;
        updateItemList(new ArrayList<>());
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

        if (needToReloadLastQuery(sortOrderMap, filterBy, itemDomain.getName())) {
            QueryBuilder itemQueryBuilder = getQueryBuilder(filterBy, sortField, sortOrder);

            List<Item> results = facade.findByDataTableFilterQueryBuilder(itemQueryBuilder);
            updateItemList(results);
        }

        return paginate(first, pageSize);
    }

    @Override
    public int count(Map map) {
        return itemList.size();
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

    @Override
    public String getRowKey(Object object) {
        if (object instanceof Item) {
            return ((Item) object).getViewUUID();
        }
        return "";
    }

}
