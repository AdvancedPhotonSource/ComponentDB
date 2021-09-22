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
public class ItemDomainMachineDesignQueryBuilder extends ItemQueryBuilder {
    
    public ItemDomainMachineDesignQueryBuilder(Integer domainId, Map filterMap) {
        super(domainId, filterMap, null, null); 
    }
    
    public ItemDomainMachineDesignQueryBuilder(Integer domainId, Map filterMap, String sortField, SortOrder sortOrder) {
        super(domainId, filterMap, sortField, sortOrder);
    }
    
}
