/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 *
 * @author djarosz
 */
public abstract class ItemDomainCatalogBase<InventoryItem extends Item> extends Item {
    
    @JsonIgnore
    public List<InventoryItem> getInventoryItemList() {
        return (List<InventoryItem>)(List<?>) super.getDerivedFromItemList(); 
    }
    
}
