/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemGenericQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemGenericLazyDataModel extends ItemLazyDataModel<ItemFacadeBase, ItemGenericQueryBuilder> {

    public ItemGenericLazyDataModel(ItemFacadeBase facade, Domain itemDomain) {
        super(facade, itemDomain);
    }

    @Override
    protected ItemGenericQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemGenericQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder);  
    }

    
}
