/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainAppFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainAppQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author djarosz
 */
public class ItemDomainAppLazyDataModel extends ItemLazyDataModel<ItemDomainAppFacade, ItemDomainAppQueryBuilder> {

    public ItemDomainAppLazyDataModel(ItemDomainAppFacade facade, Domain itemDomain, ItemSettings settings) {
        super(facade, itemDomain, settings);
    }

    @Override
    protected ItemDomainAppQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemDomainAppQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder, settings);
    }
    
}
