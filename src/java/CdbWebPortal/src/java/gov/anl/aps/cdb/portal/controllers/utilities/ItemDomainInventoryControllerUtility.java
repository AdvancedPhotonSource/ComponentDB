/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainInventoryControllerUtility extends ItemDomainInventoryBaseControllerUtility<ItemDomainInventory, ItemDomainInventoryFacade> {

    private static final Logger logger = LogManager.getLogger(ItemDomainInventoryControllerUtility.class.getName());

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.inventory.getValue();
    }

    @Override
    protected ItemDomainInventoryFacade getItemFacadeInstance() {
        return ItemDomainInventoryFacade.getInstance();
    }

    @Override
    public String getDerivedFromItemTitle() {
        return "Catalog Item";
    }

    @Override
    public String getEntityTypeName() {
        return "componentInstance";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Inventory Item";
    }

    @Override
    public List<ItemDomainInventory> getItemList() {
        return itemFacade.findByDomainOrderByDerivedFromItem(getDefaultDomainName());
    }

    @Override
    protected ItemDomainInventory instenciateNewItemDomainEntity() {
        return new ItemDomainInventory();
    }

    @Override
    public String getStatusPropertyTypeName() {
        return ItemDomainInventory.ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME;
    }
}
