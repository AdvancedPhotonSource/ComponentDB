/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.jsf.beans.SparePartsBean;
import gov.anl.aps.cdb.portal.view.objects.InventoryBillOfMaterialItem;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.primefaces.model.TreeNode;
import org.primefaces.model.menu.DefaultMenuModel;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item")
@DiscriminatorValue(value = ItemDomainName.INVENTORY_ID + "")
public class ItemDomainInventory extends Item {
    
    private transient List<InventoryBillOfMaterialItem> inventoryDomainBillOfMaterialList = null;
    
    private transient TreeNode locationTree = null;
    private transient String locationDetails = null;
    private transient ItemDomainLocation location;
    private transient String locationString;
    private transient DefaultMenuModel locationMenuModel;
    // Needed to determine whenever location was removed in edit process. 
    private transient Boolean originalLocationLoaded = false;    
           
    private transient TreeNode itemElementAssemblyRootTreeNode = null;
    
    private transient InventoryBillOfMaterialItem containedInBOM;
    
    private transient Boolean sparePartIndicator = null;
    private transient SparePartsBean sparePartsBean = null;

    @Override
    public Item createInstance() {
        return new ItemDomainInventory();
    }
    
    public ItemDomainCatalog getCatalogItem() {
        return (ItemDomainCatalog) getDerivedFromItem(); 
    }
    
    @Override
    public Item clone() throws CloneNotSupportedException {
        ItemDomainInventory clonedItem = (ItemDomainInventory) super.clone();
        
        clonedItem.setLocationDetails(null);
        clonedItem.setLocation(null);
        
        return clonedItem; 
    }
    
    public TreeNode getLocationTree() {
        return locationTree;
    }

    public void setLocationTree(TreeNode locationTree) {
        this.locationTree = locationTree;
    }

    public DefaultMenuModel getLocationMenuModel() {
        return locationMenuModel;
    }

    public void setLocationMenuModel(DefaultMenuModel locationMenuModel) {
        this.locationMenuModel = locationMenuModel;
    }

    public Boolean getOriginalLocationLoaded() {
        return originalLocationLoaded;
    }

    public void setOriginalLocationLoaded(Boolean originalLocationLoaded) {
        this.originalLocationLoaded = originalLocationLoaded;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public ItemDomainLocation getLocation() {
        return location;
    }

    public void setLocation(ItemDomainLocation location) {
        this.location = location;
    }

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }
    
    public List<InventoryBillOfMaterialItem> getInventoryDomainBillOfMaterialList() {
        return inventoryDomainBillOfMaterialList;
    }

    public void setInventoryDomainBillOfMaterialList(List<InventoryBillOfMaterialItem> inventoryDomainBillOfMaterialList) {
        this.inventoryDomainBillOfMaterialList = inventoryDomainBillOfMaterialList;
    }

    public TreeNode getItemElementAssemblyRootTreeNode() throws CdbException {
        if (itemElementAssemblyRootTreeNode == null) {
            if (getItemElementDisplayList().size() > 0) {
                itemElementAssemblyRootTreeNode = ItemElementUtility.createItemElementRoot(this);
            }
        }
        return itemElementAssemblyRootTreeNode;
    }
          
    public InventoryBillOfMaterialItem getContainedInBOM() {
        return containedInBOM;
    }

    public void setContainedInBOM(InventoryBillOfMaterialItem containedInBOM) {
        this.containedInBOM = containedInBOM;
    }
    
    public Boolean getSparePartIndicator() {
        if (sparePartIndicator == null) {
            sparePartIndicator = getSparePartsBean().getSparePartsIndication(this);
        }
        return sparePartIndicator;
    }

    public void setSparePartIndicator(Boolean sparePartIndicator) {
        this.sparePartIndicator = sparePartIndicator;
    }

    public void updateSparePartsIndication() {
        getSparePartsBean().setSparePartsIndication(this);
    }

    public SparePartsBean getSparePartsBean() {
        if (sparePartsBean == null) {
            sparePartsBean = SparePartsBean.getInstance(); 
        }
        return sparePartsBean;
    }
    
}
