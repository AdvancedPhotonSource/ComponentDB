/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainCableDesignQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainCableDesignLazyDataModel extends ItemLazyDataModel<ItemDomainCableDesignFacade, ItemDomainCableDesignQueryBuilder> {
   
    public ItemDomainCableDesignLazyDataModel(ItemDomainCableDesignFacade facade, Domain itemDomain) {
        super(facade, itemDomain);
    }

    @Override
    protected ItemDomainCableDesignQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemDomainCableDesignQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder); 
    }
}
