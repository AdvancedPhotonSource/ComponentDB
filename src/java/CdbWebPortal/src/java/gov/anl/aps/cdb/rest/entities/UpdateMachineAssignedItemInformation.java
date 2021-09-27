/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author darek
 */
public class UpdateMachineAssignedItemInformation {
    
    private int mdItemId;
    private Integer assignedItemId;
    private Boolean isInstalled; 

    public UpdateMachineAssignedItemInformation() {
    }

    public int getMdItemId() {
        return mdItemId;
    }

    public void setMdItemId(int mdItemId) {
        this.mdItemId = mdItemId;
    }

    public Integer getAssignedItemId() {
        return assignedItemId;
    }

    public void setAssignedItemId(Integer assignedItemId) {
        this.assignedItemId = assignedItemId;
    }

    public Boolean getIsInstalled() {
        return isInstalled;
    }

    public void setIsInstalled(Boolean isInstalled) {
        this.isInstalled = isInstalled;
    }
        
}
