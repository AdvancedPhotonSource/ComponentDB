/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author djarosz
 */
public abstract class ItemDomainInventoryBase <CatalogItemType extends ItemDomainCatalogBase> extends LocatableStatusItem {

    public CatalogItemType getCatalogItem() {
        return (CatalogItemType) getDerivedFromItem();
    } 
    
    public void setCatalogItem(CatalogItemType catalogItem) {
        setDerivedFromItem(catalogItem);
    }
}
