/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainMachineDesignIOCQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author djarosz
 */
public class ItemDomainMachineDesignIOCLazyDataModel extends ItemLazyDataModel<ItemDomainMachineDesignFacade, ItemDomainMachineDesignIOCQueryBuilder> {

    public ItemDomainMachineDesignIOCLazyDataModel(ItemDomainMachineDesignFacade facade, Domain itemDomain, ItemSettings settings) {
        super(facade, itemDomain, settings);
    }

    @Override
    protected ItemDomainMachineDesignIOCQueryBuilder getQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder) {
        return new ItemDomainMachineDesignIOCQueryBuilder(itemDomain.getId(), filterMap, sortField, sortOrder, settings);
    }

}
