/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMAARCFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainMaarcQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainMAARCLazyDataModel extends ItemLazyDataModel<ItemDomainMAARCFacade, ItemDomainMaarcQueryBuilder> {

    public ItemDomainMAARCLazyDataModel(ItemDomainMAARCFacade facade, Domain itemDomain, ItemSettings settings) {
        super(facade, itemDomain, settings);
    }

    @Override
    protected ItemDomainMaarcQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemDomainMaarcQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder, settings); 
    }

    @Override
    protected boolean isPaginationQueryBased() {
        return true; 
    }
}
