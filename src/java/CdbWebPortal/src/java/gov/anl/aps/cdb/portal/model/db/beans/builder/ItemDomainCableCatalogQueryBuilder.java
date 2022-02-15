/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainCableCatalogQueryBuilder extends ItemQueryBuilder {

    public ItemDomainCableCatalogQueryBuilder(Integer domainId, Map filterMap, String sortField, SortOrder sortOrder, ItemSettings scopeSettings) {
        super(domainId, filterMap, sortField, sortOrder, scopeSettings);
    }
    
    @Override
    protected String getCoreMetadataPropertyName() {
        return ItemDomainCableCatalog.CABLE_CATALOG_INTERNAL_PROPERTY_TYPE; 
    }
    
}
