/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author djarosz
 */
public class ItemDomainMachineDesignIOCQueryBuilder extends ItemQueryBuilder {

    // Only show IOC type items. 
    String defaultFilter = QueryTranslator.entityTypeName.getParent() + ".id = " + EntityTypeName.IOC_ID;

    public ItemDomainMachineDesignIOCQueryBuilder(Integer domainId, Map filterMap, String sortField, SortOrder sortOrder, ItemSettings scopeSettings) {
        super(domainId, filterMap, sortField, sortOrder, scopeSettings);
    }

    @Override
    protected void generateWhereString() {
        super.generateWhereString();

        include_etl = true;
        appendRawWhere(defaultFilter);
    }

}
