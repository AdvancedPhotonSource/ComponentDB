/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.view.objects.InventoryBillOfMaterialItem;
import java.util.List;

/**
 *
 * @author djarosz
 */
public abstract class ItemDomainInventoryBase<CatalogItemType extends ItemDomainCatalogBase> extends LocatableStatusItem {

    private transient List<InventoryBillOfMaterialItem> inventoryDomainBillOfMaterialList = null;

    private transient InventoryBillOfMaterialItem containedInBOM;

    public CatalogItemType getCatalogItem() {
        return (CatalogItemType) getDerivedFromItem();
    }

    public void setCatalogItem(CatalogItemType catalogItem) {
        setDerivedFromItem(catalogItem);
    }

    @JsonIgnore
    public InventoryBillOfMaterialItem getContainedInBOM() {
        return containedInBOM;
    }

    public void setContainedInBOM(InventoryBillOfMaterialItem containedInBOM) {
        this.containedInBOM = containedInBOM;
    }

    @JsonIgnore
    public List<InventoryBillOfMaterialItem> getInventoryDomainBillOfMaterialList() {
        return inventoryDomainBillOfMaterialList;
    }

    public void setInventoryDomainBillOfMaterialList(List<InventoryBillOfMaterialItem> inventoryDomainBillOfMaterialList) {
        this.inventoryDomainBillOfMaterialList = inventoryDomainBillOfMaterialList;
    }
}
