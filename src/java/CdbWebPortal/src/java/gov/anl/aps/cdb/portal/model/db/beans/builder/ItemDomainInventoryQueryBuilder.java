/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Dariusz
 */
public class ItemDomainInventoryQueryBuilder extends ItemDomainInventoryBaseQueryBuilder {        

    public ItemDomainInventoryQueryBuilder(Integer domainId, Map filterMap, String sortField, SortOrder sortOrder) {
        super(domainId, filterMap, sortField, sortOrder);
    }

    @Override
    protected String getStatusPropertyTypeName() {
        return ItemDomainInventoryController.ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME; 
    }
}
