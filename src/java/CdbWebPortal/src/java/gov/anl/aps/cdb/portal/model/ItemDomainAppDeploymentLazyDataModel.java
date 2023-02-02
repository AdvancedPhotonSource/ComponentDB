/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainAppDeploymentFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainAppDeploymentQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author djarosz
 */
public class ItemDomainAppDeploymentLazyDataModel extends ItemLazyDataModel<ItemDomainAppDeploymentFacade, ItemDomainAppDeploymentQueryBuilder> {

    public ItemDomainAppDeploymentLazyDataModel(ItemDomainAppDeploymentFacade facade, Domain itemDomain, ItemSettings settings) {
        super(facade, itemDomain, settings);
    }

    @Override
    protected ItemDomainAppDeploymentQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemDomainAppDeploymentQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder, settings);
    }
    
}
