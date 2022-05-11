/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author djarosz
 */
public abstract class ItemDomainCatalogBase<InventoryItem extends ItemDomainInventoryBase> extends Item {

    private transient String sourceString = null;
    private transient Boolean displayInventorySpares = null;
    private transient List<InventoryItem> inventorySparesList = null;
    private transient List<InventoryItem> inventoryNonSparesList = null;

    @JsonIgnore
    public List<InventoryItem> getInventoryItemList() {
        return (List<InventoryItem>) (List<?>) super.getDerivedFromItemList();
    }
    
    @JsonIgnore
    public InventoryItem getInventoryItemNamed(String name) {
        for (InventoryItem inventoryItem : getInventoryItemList()) {
            if (inventoryItem.getName().equals(name)) {
                return inventoryItem;
            }
        }
        return null;
    }

    @JsonIgnore
    public String getSourceString() {
        if ((sourceString == null) && (getId() != null)) {
            return getExportManufacturerName();
        }
        return sourceString;
    }

    @JsonIgnore
    public Source getExportManufacturer() {
        List<ItemSource> itemSourceList = getItemSourceList();
        if (itemSourceList != null) {
            for (ItemSource source : itemSourceList) {
                if (source.getIsManufacturer()) {
                    return source.getSource();
                }
            }
        }
        return null;
    }
    
    @JsonIgnore
    public String getExportManufacturerName() {
        Source mfrSource = getExportManufacturer();
        if (mfrSource != null) {
            return mfrSource.getName();
        } else {
            return null;
        }
    }
    
    public void setManufacturerInfo(Source source, String partNum) {
        if (source != null) {
            List<ItemSource> itemSourceList = new ArrayList<>();
            ItemSource itemSource = new ItemSource();
            itemSource.setItem(this);
            itemSource.setSource(source);
            if ((partNum != null) && (!partNum.isBlank())) {
                itemSource.setPartNumber(partNum);
            }
            itemSource.setIsManufacturer(true);
            itemSourceList.add(itemSource);
            this.setItemSourceList(itemSourceList);
            sourceString = source.getName();
        }
    }

    public void updateManufacturerInfo(Source source, String partNum) {

        if (source != null) {

            // create source list if it doesn't exist
            List<ItemSource> itemSourceList = getItemSourceList();
            if (itemSourceList == null) {
                itemSourceList = new ArrayList<>();
                this.setItemSourceList(itemSourceList);
            }

            // remove mfr flag for existing source if any
            ItemSource newMfrSource = null;
            ItemSource oldMfrSource = null;
            for (ItemSource itemSource : itemSourceList) {
                if (itemSource.getIsManufacturer()) {
                    itemSource.setIsManufacturer(false);
                    oldMfrSource = itemSource;
                }
                if (itemSource.getSource().getName().equals(source.getName())) {
                    newMfrSource = itemSource;
                }
            }

            // create new ItemSource if we didn't find existing one
            if (newMfrSource == null) {
                newMfrSource = new ItemSource();
                newMfrSource.setItem(this);
                newMfrSource.setSource(source);
                itemSourceList.add(newMfrSource);
            }

            // set mfr flag and part number
            newMfrSource.setIsManufacturer(true);
            if ((partNum != null) && (!partNum.isBlank())) {
                newMfrSource.setPartNumber(partNum);
            }

            sourceString = source.getName();
        }
    }

    public void removeManufactuterInfo() {
        List<ItemSource> itemSourceList = getItemSourceList();
        if (itemSourceList != null) {
            for (ItemSource itemSource : itemSourceList) {
                if (itemSource.getIsManufacturer()) {
                    itemSource.setIsManufacturer(false);
//                    itemSourceList.remove(itemSource);
                }
            }
        }
    }

    public String getAlternateName() {
        return getItemIdentifier2();
    }

    public void setAlternateName(String n) {
        setItemIdentifier2(n);
    }

    @JsonIgnore
    public String getPartNumber() {
        return this.getItemIdentifier1();
    }

    public void setPartNumber(String n) {
        this.setItemIdentifier1(n);
    }

    @JsonIgnore
    public Boolean getDisplayInventorySpares() {
        return displayInventorySpares;
    }

    public void setDisplayInventorySpares(Boolean displayInventorySpares) {
        this.displayInventorySpares = displayInventorySpares;
    }
    
    @JsonIgnore
    public List<InventoryItem> getInventorySparesList() {
        return inventorySparesList;
    }

    public void setInventorySparesList(List<InventoryItem> inventorySparesList) {
        this.inventorySparesList = inventorySparesList;
    }

    @JsonIgnore
    public List<InventoryItem> getInventoryNonSparesList() {
        return inventoryNonSparesList;
    }

    public void setInventoryNonSparesList(List<InventoryItem> inventoryNonSparesList) {
        this.inventoryNonSparesList = inventoryNonSparesList;
    }

}
