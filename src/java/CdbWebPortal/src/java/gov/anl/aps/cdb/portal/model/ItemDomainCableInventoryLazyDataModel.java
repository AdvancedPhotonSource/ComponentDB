/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainCableInventoryQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainCableInventoryLazyDataModel extends ItemLazyDataModel<ItemDomainCableInventoryFacade, ItemDomainCableInventoryQueryBuilder> {
   
    public ItemDomainCableInventoryLazyDataModel(ItemDomainCableInventoryFacade facade, Domain itemDomain) {
        super(facade, itemDomain);
    }

    @Override
    protected ItemDomainCableInventoryQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemDomainCableInventoryQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder); 
    }
}
