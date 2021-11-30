/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author darek
 */
public class PromoteMachineElementInformation {
    
    private int parentMdItemId;
    private Integer assemblyElementId;
    private String newName; 

    public PromoteMachineElementInformation() {
    }

    public int getParentMdItemId() {
        return parentMdItemId;
    }

    public void setParentMdItemId(int parentMdItemId) {
        this.parentMdItemId = parentMdItemId;
    }

    public Integer getAssemblyElementId() {
        return assemblyElementId;
    }

    public void setAssemblyElementId(Integer assemblyElementId) {
        this.assemblyElementId = assemblyElementId;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
        
}
