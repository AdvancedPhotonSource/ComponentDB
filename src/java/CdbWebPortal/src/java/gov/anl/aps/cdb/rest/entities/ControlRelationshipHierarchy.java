/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;

/**
 *
 * @author darek
 */
public class ControlRelationshipHierarchy {
    
    ControlRelationshipHierarchy childItem; 
    ItemDomainMachineDesign machineItem; 
    String interfaceToParent; 

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

    
}
