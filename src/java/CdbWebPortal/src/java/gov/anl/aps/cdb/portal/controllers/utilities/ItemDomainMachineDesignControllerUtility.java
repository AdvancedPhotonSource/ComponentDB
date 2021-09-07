/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignControllerUtility extends ItemDomainMachineDesignBaseControllerUtility {

    private static final Logger logger = LogManager.getLogger(ItemDomainMachineDesignControllerUtility.class.getName());
    
    @EJB
    RelationshipTypeFacade relationshipTypeFacade; 

    public ItemDomainMachineDesignControllerUtility() {
        super();
        relationshipTypeFacade = RelationshipTypeFacade.getInstance(); 
    }
    
    public ItemElementRelationship applyRunningOnRelationship(ItemDomainMachineDesign mdRunningControlElement, ItemDomainMachineDesign controlElement) throws InvalidArgument {
        // Verify appropriate types of machines provided        
        boolean failedControlledNodeTypeCheck = true; 
        if (mdRunningControlElement instanceof ItemDomainMachineDesign) {
            List<EntityType> controlledEntityTypeList = mdRunningControlElement.getEntityTypeList();
            if (controlledEntityTypeList.isEmpty()) {
                failedControlledNodeTypeCheck = false; 
            }
        }
        if (failedControlledNodeTypeCheck) {
            throw new InvalidArgument("Only standard machine designs with no entity type can be running a control element."); 
        }
        
        boolean itemControl = ItemDomainMachineDesign.isItemControl(controlElement);
        if (itemControl == false) {
            throw new InvalidArgument("Only control type machine designs can be running on machine design."); 
        }
        
        // Verify is control element is already running on another device
        List<ItemDomainMachineDesign> machinesRunningControlElement = new ArrayList<>();
        String runningRelationshipTypeName = ItemElementRelationshipTypeNames.running.getValue();
        
        loadMachineItemsWithRelationship(runningRelationshipTypeName, controlElement, machinesRunningControlElement, true);
        if (machinesRunningControlElement.size() > 0) {
            ItemDomainMachineDesign otherMachine = machinesRunningControlElement.get(0); 
            if (otherMachine.equals(mdRunningControlElement)) {
                throw new InvalidArgument("Control machine is already running on requested machine"); 
            }
            throw new InvalidArgument("Control machine is already running on another machine: " + otherMachine.getName()); 
        }
        
        RelationshipType runningRelationship = relationshipTypeFacade.findByName(runningRelationshipTypeName);
         
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setRelationshipType(runningRelationship);
        itemElementRelationship.setFirstItemElement(mdRunningControlElement.getSelfElement());
        itemElementRelationship.setSecondItemElement(controlElement.getSelfElement());

        mdRunningControlElement.getItemElementRelationshipList().add(itemElementRelationship);
        controlElement.getItemElementRelationshipList1().add(itemElementRelationship);
        
        return itemElementRelationship; 
    }
    
}
