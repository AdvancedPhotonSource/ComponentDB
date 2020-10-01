/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item")
@DiscriminatorValue(value = ItemDomainName.LOCATION_ID + "")
public class ItemDomainLocation extends Item {
    
    private transient ItemDomainLocation importParentItem = null;
    private transient String importPath = null;
    private transient Integer importSortOrder = null;

    @Override
    public Item createInstance() {
        return new ItemDomainLocation();
    }

    @Override
    public ItemController getItemDomainController() {
        return ItemDomainLocationController.getInstance(); 
    }

    @JsonIgnore
    public ItemDomainLocation getImportParentItem() {
        return importParentItem;
    }

    @JsonIgnore
    public String getImportParentItemString() {
        if (getImportParentItem() != null) {
            return getImportParentItem().getName();
        } else {
            return "";
        }
    }

    public void setImportParentItem(ItemDomainLocation importParentItem) {
        this.importParentItem = importParentItem;
    }

    @JsonIgnore
    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }

    @JsonIgnore
    public Integer getImportSortOrder() {
        return importSortOrder;
    }

    public void setImportSortOrder(Integer importSortOrder) {
        this.importSortOrder = importSortOrder;
    }
    
    /**
     * Establishes parent/child relationship, with this item as child of specified parentItem.
     * 
     * @param childItem 
     */
    public void setImportChildParentRelationship(
            ItemDomainLocation parentItem,
            String childName,
            Float sortOrder,
            UserInfo user,
            UserGroup group) {       
        
        if (parentItem != null) {
            ItemElement itemElement = new ItemElement();
            itemElement.setName(childName);
            itemElement.setImportParentItem(parentItem, sortOrder, user, group);
            itemElement.setImportChildItem(this);
        }
    }
    
}
