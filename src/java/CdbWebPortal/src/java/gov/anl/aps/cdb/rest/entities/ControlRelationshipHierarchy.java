/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darek
 */
public class ControlRelationshipHierarchy {
    
    ControlRelationshipHierarchy childItem; 
    ItemDomainMachineDesign machineItem; 
    String interfaceToParent; 
    PropertyValue interfaceToParentPv; 

    public ControlRelationshipHierarchy(ItemDomainMachineDesign machineDesign, PropertyValue interfaceToParent) {
        initBasic(machineDesign, interfaceToParent);
    }

    public ControlRelationshipHierarchy(ControlRelationshipHierarchy childItem, ItemDomainMachineDesign machineDesign, PropertyValue interfaceToParent) {
        this.childItem = childItem;
        initBasic(machineDesign, interfaceToParent);
    }
    
    private void initBasic(ItemDomainMachineDesign machineDesign, PropertyValue interfaceToParentPv) {
        this.machineItem = machineDesign; 
        if (interfaceToParentPv != null) {
            this.interfaceToParentPv = interfaceToParentPv; 
            interfaceToParent = interfaceToParentPv.getValue(); 
        } else {
            interfaceToParent = ""; 
        }
        
    }

    public ControlRelationshipHierarchy getChildItem() {
        return childItem;
    }

    public ItemDomainMachineDesign getMachineItem() {
        return machineItem;
    }

    public String getInterfaceToParent() {
        return interfaceToParent;
    }

    @JsonIgnore
    public PropertyValue getInterfaceToParentPv() {
        return interfaceToParentPv;
    }
    
    @JsonIgnore
    public ControlRelationshipHierarchy thisOnlyClone() {
        ControlRelationshipHierarchy child = this.getChildItem();
        ItemDomainMachineDesign machine = this.getMachineItem();
        PropertyValue pv = this.getInterfaceToParentPv();
        ControlRelationshipHierarchy clone = new ControlRelationshipHierarchy(child, machine, pv); 
        
        return clone; 
    }

    
}
