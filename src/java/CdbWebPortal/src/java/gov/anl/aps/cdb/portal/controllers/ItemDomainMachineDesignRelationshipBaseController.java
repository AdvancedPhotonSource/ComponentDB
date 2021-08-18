/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemDomainMachineDesignRelationshipBaseController extends ItemDomainMachineDesignBaseController {
    
    protected abstract String getRelationshipTypeName(); 
    
    public List<ItemDomainMachineDesign> getMachineElementsRelatedToCurrent() {
        ItemDomainMachineDesign current = getCurrent();
        List<ItemDomainMachineDesign> machineElementsRelatedToCurrent = null; 
        if (current != null) {
            machineElementsRelatedToCurrent = current.getMachineElementsRelatedToThis();
            
            // Todo use loaded boolean. 
            if (machineElementsRelatedToCurrent == null) {
                machineElementsRelatedToCurrent = new ArrayList<>(); 
                
                List<ItemElementRelationship> itemElementRelationshipList = current.getItemElementRelationshipList1();
                
                for (ItemElementRelationship ier : itemElementRelationshipList) {
                    RelationshipType relationshipType = ier.getRelationshipType();
                    String name = relationshipType.getName();
                    
                    if (name.equals(getRelationshipTypeName())) {
                        ItemElement firstItemElement = ier.getFirstItemElement();
                        ItemDomainMachineDesign machine = (ItemDomainMachineDesign) firstItemElement.getParentItem();
                        machineElementsRelatedToCurrent.add(machine);
                    }
                    
                    current.setMachineElementsRelatedToThis(machineElementsRelatedToCurrent); 
                }
            }
        }
        
        return machineElementsRelatedToCurrent; 
    }

}
