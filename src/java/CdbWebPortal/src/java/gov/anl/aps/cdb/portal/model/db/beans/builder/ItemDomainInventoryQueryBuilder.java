/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Dariusz
 */
public class ItemDomainInventoryQueryBuilder extends ItemDomainInventoryBaseQueryBuilder {        

    public ItemDomainInventoryQueryBuilder(Integer domainId, Map filterMap, String sortField, SortOrder sortOrder, ItemSettings scopeSettings) {
        super(domainId, filterMap, sortField, sortOrder, scopeSettings);
    }

    @Override
    protected String getStatusPropertyTypeName() {
        return ItemDomainInventoryController.ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME; 
    }
}
