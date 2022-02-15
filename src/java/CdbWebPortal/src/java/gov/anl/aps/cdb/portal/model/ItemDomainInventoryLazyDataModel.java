/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainInventoryQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainInventoryLazyDataModel extends ItemLazyDataModel<ItemDomainInventoryFacade, ItemDomainInventoryQueryBuilder> {
   
    public ItemDomainInventoryLazyDataModel(ItemDomainInventoryFacade facade, Domain itemDomain, ItemSettings settings) {
        super(facade, itemDomain, settings);
    }

    @Override
    protected ItemDomainInventoryQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemDomainInventoryQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder, settings); 
    }
}
