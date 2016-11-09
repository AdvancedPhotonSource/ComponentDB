/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.objects;

/**
 *
 * @author djarosz
 */
public class Component extends CdbObject{
    
    private ComponentType componentType; 
    
    public Component(){
        
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType getComponentType() {
        return componentType;
    }
}
