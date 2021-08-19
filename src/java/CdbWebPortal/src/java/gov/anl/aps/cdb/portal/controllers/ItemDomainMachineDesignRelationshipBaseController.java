/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.event.NodeSelectEvent;

public abstract class ItemDomainMachineDesignRelationshipBaseController extends ItemDomainMachineDesignBaseController {
    
    private ItemDomainMachineDesign machineRelatedByCurrent; 
    private boolean displaySelectMachineRelatedByCurrent; 
    
    
    protected abstract ItemElementRelationshipTypeNames getRelationshipTypeName(); 
    protected abstract EntityTypeName getRelationshipMachineEntityType(); 
    
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
                    String relationshipTypeName = getRelationshipTypeName().getValue();
                    
                    if (name.equals(relationshipTypeName)) {
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
    
    @Override
    public boolean isDisplayFollowInstructionOnRightOnBlockUI() {
        return super.isDisplayFollowInstructionOnRightOnBlockUI() 
                || displaySelectMachineRelatedByCurrent; 
    } 

    @Override
    public void resetListConfigurationVariables() {
        super.resetListConfigurationVariables(); 
        displaySelectMachineRelatedByCurrent = false; 
    }
       
    public void prepareSelectMachinedRelatedByNode() {
        prepareAddNewMachineDesignListConfiguration();        
        displaySelectMachineRelatedByCurrent = true;
    }
    
      public void saveSelectedMachineRelatedByCurrent() {
        if (machineRelatedByCurrent == null) {
            SessionUtility.addWarningMessage("No machine element selected", "Please select machine and try again.");
            return;
        }
        
        updateCurrentUsingSelectedItemInTreeTable();
        
        applyRelationship(machineRelatedByCurrent, getCurrent());
        
        update();

        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();
    }
    
    public void applyRelationship(ItemDomainMachineDesign machineElement, ItemDomainMachineDesign relatedElement) {
        ItemElementRelationshipTypeNames relationshipTypeName = getRelationshipTypeName();
        RelationshipType templateRelationship
                = relationshipTypeFacade.findByName(relationshipTypeName.getValue());
        
        // Todo check if a relationship already exists.

        // Create item element relationship between the template and the clone 
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setRelationshipType(templateRelationship);
        itemElementRelationship.setFirstItemElement(machineElement.getSelfElement());
        itemElementRelationship.setSecondItemElement(relatedElement.getSelfElement());
        
        machineElement.getItemElementRelationshipList().add(itemElementRelationship);
        relatedElement.getItemElementRelationshipList().add(itemElementRelationship); 
    }
    
    @Override
    protected final ItemDomainMachineDesign performItemRedirection(ItemDomainMachineDesign item, String paramString, boolean forceRedirection) {
        boolean matchEntityType = isMachineDesignAndEntityType(item, getRelationshipMachineEntityType());
        if (matchEntityType) {
            setCurrent(item);
            prepareView(item);
            resetListDataModel();
            return item;
        }

        // Do default action. 
        return super.performItemRedirection(item, paramString, forceRedirection); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected final void prepareEntityView(ItemDomainMachineDesign entity) {
        processPreRenderList();
        boolean matchEntityType = isMachineDesignAndEntityType(entity, getRelationshipMachineEntityType());
        if (matchEntityType) {
            loadViewModeUrlParameter();
        }
    }
    
    public void machineRelatedByCurrentItemSelected(NodeSelectEvent nodeSelection) {
        machineRelatedByCurrent = getMachineFromNodeSelectEvent(nodeSelection);
    }

    public boolean isDisplaySelectMachineRelatedByCurrent() {
        return displaySelectMachineRelatedByCurrent;
    }

    public ItemDomainMachineDesign getMachineRelatedByCurrent() {
        return machineRelatedByCurrent;
    }

    public void setMachineRelatedByCurrent(ItemDomainMachineDesign machineRelatedByCurrent) {
        this.machineRelatedByCurrent = machineRelatedByCurrent;
    }

}
