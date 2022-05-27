/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author craig
 */
public class UpdateCableDesignAssignedItemInformation {
    
    private int cdItemId;
    private Integer cableTypeId;
    private String cableTypeName;
    private Integer inventoryId;
    private String inventoryTag;
    private Boolean isInstalled; 

    public UpdateCableDesignAssignedItemInformation() {
    }

    public int getCdItemId() {
        return cdItemId;
    }

    public void setCdItemId(int cdItemId) {
        this.cdItemId = cdItemId;
    }

    public Integer getCableTypeId() {
        return cableTypeId;
    }

    public void setCableTypeId(Integer cableTypeId) {
        this.cableTypeId = cableTypeId;
    }

    public String getCableTypeName() {
        return cableTypeName;
    }

    public void setCableTypeName(String cableTypeName) {
        this.cableTypeName = cableTypeName;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getInventoryTag() {
        return inventoryTag;
    }

    public void setInventoryTag(String inventoryTag) {
        this.inventoryTag = inventoryTag;
    }

    public Boolean getIsInstalled() {
        return isInstalled;
    }

    public void setIsInstalled(Boolean isInstalled) {
        this.isInstalled = isInstalled;
    }
        
}
