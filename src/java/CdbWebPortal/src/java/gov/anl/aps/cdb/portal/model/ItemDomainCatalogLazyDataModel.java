/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainCatalogQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainCatalogLazyDataModel extends ItemLazyDataModel<ItemDomainCatalogFacade, ItemDomainCatalogQueryBuilder> {
   
    public ItemDomainCatalogLazyDataModel(ItemDomainCatalogFacade facade, Domain itemDomain, ItemSettings settings) {
        super(facade, itemDomain, settings);
    }

    @Override
    protected ItemDomainCatalogQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemDomainCatalogQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder, settings); 
    }
}
