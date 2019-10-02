/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.LinkedList;

/**
 *
 * @author djarosz
 */
public class ItemSearchResults {
    
    LinkedList<SearchResult> itemDomainCatalogResults;
    LinkedList<SearchResult> itemDomainInventoryResults;

    public ItemSearchResults(LinkedList<SearchResult> itemDomainCatalogResults, LinkedList<SearchResult> itemDomainInventoryResults) {
        this.itemDomainCatalogResults = itemDomainCatalogResults;
        this.itemDomainInventoryResults = itemDomainInventoryResults;
    }

    public LinkedList<SearchResult> getItemDomainCatalogResults() {
        return itemDomainCatalogResults;
    }

    public LinkedList<SearchResult> getItemDomainInventoryResults() {
        return itemDomainInventoryResults;
    }
    
}
