/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.view.objects.InventoryBillOfMaterialItem;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
public abstract class ItemDomainInventoryBase<CatalogItemType extends ItemDomainCatalogBase> extends LocatableStatusItem {

    private transient List<InventoryBillOfMaterialItem> inventoryDomainBillOfMaterialList = null;

    private transient InventoryBillOfMaterialItem containedInBOM;
    
    private transient ItemElement selectedItemElementForUpdate = null;    

    private transient TreeNode itemElementAssemblyRootTreeNode = null;   

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
    
    @JsonIgnore
    public ItemElement getSelectedItemElementForUpdate() {
        return selectedItemElementForUpdate;
    }

    public void setSelectedItemElementForUpdate(ItemElement selectedItemElementForUpdate) {
        this.selectedItemElementForUpdate = selectedItemElementForUpdate;
    }
  
    @JsonIgnore
    public TreeNode getItemElementAssemblyRootTreeNode() throws CdbException {
        if (itemElementAssemblyRootTreeNode == null) {
            if (getItemElementDisplayList().size() > 0) {
                itemElementAssemblyRootTreeNode = ItemElementUtility.createItemElementRoot(this);
            }
        }
        return itemElementAssemblyRootTreeNode;
    }
}
