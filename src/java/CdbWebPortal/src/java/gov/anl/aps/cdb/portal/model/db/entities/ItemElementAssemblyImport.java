/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author craig
 */
public class ItemElementAssemblyImport extends ItemElement {

    private transient String partName;
    private transient String partDescription;
    private transient Boolean partRequired;
    private transient String partCatalogItemName;
    
    @Override
    public boolean equals(Object object) {

        if (object != null && object instanceof ItemElementAssemblyImport) {
            ItemElementAssemblyImport item = (ItemElementAssemblyImport) object;
            if (((this.getParentCatalogItem()) == item.getParentCatalogItem()) &&
                    (this.getName() == item.getName())) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getParentCatalogItem(), getName());
    }
    
    public ItemDomainCatalog getParentCatalogItem() {
        return (ItemDomainCatalog) getParentItem();
    }

    public void setParentCatalogItem(ItemDomainCatalog parentCatalogItem) {
        setParentItem(parentCatalogItem);
        parentCatalogItem.getFullItemElementList().add(this);
        parentCatalogItem.getItemElementDisplayList().add(0, this);
    }

    public String getPartName() {
        return getName();
    }

    public void setPartName(String partName) {
        setName(partName);
    }

    public String getPartDescription() {
        return partDescription;
    }

    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }

    public Boolean getPartRequired() {
        return partRequired;
    }

    public void setPartRequired(Boolean partRequired) {
        this.partRequired = partRequired;
    }

    public String getPartCatalogItemName() {
        return partCatalogItemName;
    }

    public void setPartCatalogItemName(String partCatalogItemName) {
        this.partCatalogItemName = partCatalogItemName;
    }

    public ItemDomainCatalog getPartCatalogItem() {
        return (ItemDomainCatalog) this.getContainedItem();
    }

    public void setPartCatalogItem(ItemDomainCatalog partCatalogItem) {
        this.setContainedItem(partCatalogItem);
        if (partCatalogItem.getItemElementMemberList() == null) {
            partCatalogItem.setItemElementMemberList(new ArrayList<>());
        }
        partCatalogItem.getItemElementMemberList().add(this);
    }
    
}
