/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainCatalogQueryBuilder extends ItemQueryBuilder {
   
    public ItemDomainCatalogQueryBuilder(Integer domainId, Map filterMap, String sortField, SortOrder sortOrder) {
        super(domainId, filterMap, sortField, sortOrder);
    }
    
}
