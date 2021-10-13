/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMAARCFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainMaarcQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.ArrayList;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainMAARCLazyDataModel extends ItemLazyDataModel<ItemDomainMAARCFacade, ItemDomainMaarcQueryBuilder> {
   
    public ItemDomainMAARCLazyDataModel(ItemDomainMAARCFacade facade, Domain itemDomain) {
        super(facade, itemDomain);
    }

    @Override
    protected void initalizeItemList() {
        updateItemList(new ArrayList<>());
    }

    @Override
    protected ItemDomainMaarcQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemDomainMaarcQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder); 
    }
}
