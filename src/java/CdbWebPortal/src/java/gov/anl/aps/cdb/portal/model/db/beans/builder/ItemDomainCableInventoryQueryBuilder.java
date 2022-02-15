/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableInventoryController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainCableInventoryQueryBuilder extends ItemDomainInventoryBaseQueryBuilder {

    public ItemDomainCableInventoryQueryBuilder(Integer domainId, Map filterMap, String sortField, SortOrder sortOrder, ItemSettings scopeSettings) {
        super(domainId, filterMap, sortField, sortOrder, scopeSettings);
    }
    
    @Override
    protected String getCoreMetadataPropertyName() {
        return ItemDomainCableInventory.CABLE_INVENTORY_INTERNAL_PROPERTY_TYPE; 
    }

    @Override
    protected String getStatusPropertyTypeName() {
        return ItemDomainCableInventoryController.ITEM_DOMAIN_CABLE_INVENTORY_STATUS_PROPERTY_TYPE_NAME;
    }
    
}
