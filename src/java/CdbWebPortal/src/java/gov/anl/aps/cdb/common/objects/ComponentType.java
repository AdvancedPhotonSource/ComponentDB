/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.objects;

/**
 * Component Type object.
 */
public class ComponentType extends CdbObject {
    
    private int componentTypeCategoryId; 
    private ComponentTypeCategory componentTypeCategory;
    
    public ComponentType() {
        
    }

    public ComponentTypeCategory getComponentTypeCategory() {
        return componentTypeCategory;
    }

    public void setComponentTypeCategory(ComponentTypeCategory componentTypeCategory) {
        this.componentTypeCategory = componentTypeCategory;
    } 

    public int getComponentTypeCategoryId() {
        return componentTypeCategoryId;
    }

    public void setComponentTypeCategoryId(int componentTypeCategoryId) {
        this.componentTypeCategoryId = componentTypeCategoryId;
    }
   
}
