/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Dariusz
 */
public class ItemDomainMaarcQueryBuilder extends ItemQueryBuilder {
    
    public ItemDomainMaarcQueryBuilder(Integer domainId, Map filterMap, String sortField, SortOrder sortOrder) {
        super(domainId, filterMap, sortField, sortOrder);
    }
    
}
