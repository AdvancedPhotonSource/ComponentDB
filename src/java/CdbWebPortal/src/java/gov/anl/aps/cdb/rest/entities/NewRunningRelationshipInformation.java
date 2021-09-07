/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author djarosz
 */
public class NewRunningRelationshipInformation {
    
    private int machineDesignId; 
    private int controlTypeMachineId;         

    public NewRunningRelationshipInformation() {
    }

    public int getMachineDesignId() {
        return machineDesignId;
    }

    public void setMachineDesignId(int machineDesignId) {
        this.machineDesignId = machineDesignId;
    }

    public int getControlTypeMachineId() {
        return controlTypeMachineId;
    }

    public void setControlTypeMachineId(int controlTypeMachineId) {
        this.controlTypeMachineId = controlTypeMachineId;
    }
          
}
