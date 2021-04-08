/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignQueryBuilder extends ItemQueryBuilder {
    
    public ItemDomainMachineDesignQueryBuilder(Domain domain, Map filterMap) {
        super(domain, filterMap, null, null); 
    }
    
    public ItemDomainMachineDesignQueryBuilder(Domain domain, Map filterMap, String sortField, SortOrder sortOrder) {
        super(domain, filterMap, sortField, sortOrder);
    }
    
}
