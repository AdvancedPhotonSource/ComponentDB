/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
