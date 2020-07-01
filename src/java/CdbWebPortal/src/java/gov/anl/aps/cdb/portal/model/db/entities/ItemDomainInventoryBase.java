/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

/**
 *
 * @author djarosz
 */
public abstract class ItemDomainInventoryBase <CatalogItemType extends Item> extends LocatableItem {

    public CatalogItemType getCatalogItem() {
        return (CatalogItemType) getDerivedFromItem();
    } 
    
    public void setCatalogItem(CatalogItemType catalogItem) {
        setDerivedFromItem(catalogItem);
    }

}
