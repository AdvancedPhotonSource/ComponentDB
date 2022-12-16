/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author darek
 */
public class RepresentingAssemblyElementForMachineInformation {
    
    private int machineId;
    private Integer assemblyElementId;
    private boolean templateMatchByNameForCustomBuild; 

    public RepresentingAssemblyElementForMachineInformation() {
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public Integer getAssemblyElementId() {
        return assemblyElementId;
    }

    public void setAssemblyElementId(Integer assemblyElementId) {
        this.assemblyElementId = assemblyElementId;
    }

    public boolean isTemplateMatchByNameForCustomBuild() {
        return templateMatchByNameForCustomBuild;
    }

    public void setTemplateMatchByNameForCustomBuild(boolean templateMatchByNameForCustomBuild) {
        this.templateMatchByNameForCustomBuild = templateMatchByNameForCustomBuild;
    }
        
}
