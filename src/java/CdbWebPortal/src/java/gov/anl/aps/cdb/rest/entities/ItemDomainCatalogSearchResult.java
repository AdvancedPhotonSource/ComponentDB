/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.List;

/**
 *
 * @author djarosz
 */
public class ItemDomainCatalogSearchResult extends SearchResult {
    
    ItemDomainCatalog catalogItem; 
    List<ItemDomainInventory> inventoryList; 
    
    public ItemDomainCatalogSearchResult(SearchResult result, ItemDomainCatalog catalogItem) {
        super(result.getObjectId(), result.getObjectName());
        this.catalogItem = catalogItem; 
        inventoryList = catalogItem.getInventoryItemList();
    }

    public ItemDomainCatalog getCatalogItem() {
        return catalogItem;
    }

    public List<ItemDomainInventory> getInventoryList() {
        return inventoryList;
    }
    
}
