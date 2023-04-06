/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author djarosz
 */
public class NewControlRelationshipInformation {
    
    private int controlledMachineId; 
    private int controllingMachineId;     
    private Integer linkedParentMachineRelationshipId = null; 
    private String controlInterfaceToParent; 

    public NewControlRelationshipInformation() {
    }

    public int getControlledMachineId() {
        return controlledMachineId;
    }

    public void setControlledMachineId(int controlledMachineId) {
        this.controlledMachineId = controlledMachineId;
    }

    public int getControllingMachineId() {
        return controllingMachineId;
    }

    public void setControllingMachineId(int controllingMachineId) {
        this.controllingMachineId = controllingMachineId;
    }

    public String getControlInterfaceToParent() {
        return controlInterfaceToParent;
    }

    public void setControlInterfaceToParent(String controlInterfaceToParent) {
        this.controlInterfaceToParent = controlInterfaceToParent;
    } 

    public Integer getLinkedParentMachineRelationshipId() {
        return linkedParentMachineRelationshipId;
    }

    public void setLinkedParentMachineRelationshipId(Integer linkedParentMachineRelationshipId) {
        this.linkedParentMachineRelationshipId = linkedParentMachineRelationshipId;
    }
          
}
