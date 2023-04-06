/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author darek
 */
public abstract class ItemDomainMachineDesignRelationshipBaseControllerUtility extends ItemDomainMachineDesignBaseControllerUtility {

    private RelationshipTypeFacade relationshipTypeFacade;

    public abstract ItemElementRelationshipTypeNames getRelationshipTypeName();

    public ItemDomainMachineDesignRelationshipBaseControllerUtility() {
        relationshipTypeFacade = RelationshipTypeFacade.getInstance();
    }

    public ItemElementRelationship applyRelationship(ItemDomainMachineDesign relatedElement, ItemDomainMachineDesign relatingElement) throws InvalidArgument, InvalidObjectState {
        return applyRelationship(relatedElement, relatingElement, null);
    }

    public ItemElementRelationship applyRelationship(ItemDomainMachineDesign relatedElement, ItemDomainMachineDesign relatingElement, Integer linkedParentRelationshipId) throws InvalidArgument, InvalidObjectState {
        // Verify the controlled node is not assigned to any entity type. Only standard machine design supported. 
        boolean failedControlledNodeTypeCheck = true;
        if (relatedElement instanceof ItemDomainMachineDesign) {
            List<EntityType> controlledEntityTypeList = relatedElement.getEntityTypeList();
            if (controlledEntityTypeList.isEmpty()) {
                failedControlledNodeTypeCheck = false;
            }
        }
        if (failedControlledNodeTypeCheck) {
            throw new InvalidArgument("Only standard machine designs with no entity type can be controlled.");
        }

        if (relatedElement.equals(relatingElement)) {
            throw new InvalidArgument("Machine cannot be related to itself.");
        }

        // Verify the controlling node is associated with the control hierarchy.
        ItemDomainMachineDesign controllingControlTypeItem = relatingElement;
        Integer parentItemId = relatingElement.getId();
        Integer proposedChildItemId = relatedElement.getId();
        ItemElementRelationshipTypeNames relationshipTypeName = getRelationshipTypeName();
        String relationshipName = relationshipTypeName.getValue();
        int relationshipId = relationshipTypeName.getDbId();

        while (controllingControlTypeItem != null) {
            if (ItemDomainMachineDesign.isItemControl(controllingControlTypeItem)) {
                break;
            }

            Integer itemId = controllingControlTypeItem.getId();
            List<ItemDomainMachineDesign> controllingParents = itemFacade.fetchRelationshipParentItems(itemId, relationshipId);

            int controlParentsCount = controllingParents.size();
            if (isAllowMultipleRelationships()) {
                if (controlParentsCount > 0) {
                    controllingControlTypeItem = controllingParents.get(controlParentsCount - 1);
                    continue;
                }
            } else {
                if (controlParentsCount > 1) {
                    throw new InvalidArgument("Invalid data. Item is controlled by multiple items.");
                } else if (controlParentsCount == 1) {
                    controllingControlTypeItem = controllingParents.get(0);
                    continue;
                }
            }

            controllingControlTypeItem = null;
        }

        if (controllingControlTypeItem == null) {
            throw new InvalidArgument("Controlling element provided is not associated with the machine control hierarchy.");
        }

        RelationshipType templateRelationship
                = relationshipTypeFacade.findByName(relationshipName);

        List<ItemDomainMachineDesign> machineItems = itemFacade.fetchRelationshipParentItems(relatedElement.getId(), relationshipId);

        switch (machineItems.size()) {
            case 0:
                // Perfect to proceed
                break;
            case 1:
                ItemDomainMachineDesign relatedItem = machineItems.get(0);
                String name = relatedItem.getName();

                verifyExistingRelationship(machineItems, relatingElement, relatedElement, linkedParentRelationshipId, relationshipId);

                if (isAllowMultipleRelationships()) {
                    verifyCircularRelationship(relationshipId, parentItemId, proposedChildItemId);
                    break;
                }
                throw new InvalidArgument("The item is already related by: " + name);
            default:
                verifyExistingRelationship(machineItems, relatingElement, relatedElement, linkedParentRelationshipId, relationshipId);
                // Verify if any of the existing relationship already exists.                 
                if (isAllowMultipleRelationships()) {
                    verifyCircularRelationship(relationshipId, parentItemId, proposedChildItemId);
                    break;
                }
                throw new InvalidArgument("The item already has relationship defined");
        }

        // Todo check if a relationship already exists.
        // Create item element relationship between the template and the clone 
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setRelationshipType(templateRelationship);
        itemElementRelationship.setFirstItemElement(relatedElement.getSelfElement());
        itemElementRelationship.setSecondItemElement(relatingElement.getSelfElement());

        relatedElement.getItemElementRelationshipList().add(itemElementRelationship);
        relatingElement.getItemElementRelationshipList1().add(itemElementRelationship);

        return itemElementRelationship;
    }

    private void verifyExistingRelationship(List<ItemDomainMachineDesign> machineItems, ItemDomainMachineDesign relatingElement, ItemDomainMachineDesign relatedElement, Integer linkedParentRelationshipId, Integer relationshipTypeId) throws InvalidArgument {        
        for (ItemDomainMachineDesign parentItem : machineItems) {
            String name = parentItem.getName();
            if (parentItem.equals(relatingElement)) {
                boolean failed = true; 
                if (linkedParentRelationshipId != null) {
                    List<ItemElementRelationship> itemElementRelationshipList = relatedElement.getItemElementRelationshipList();
                    failed = false; 
                    for (ItemElementRelationship ier : itemElementRelationshipList) {
                        if (Objects.equals(ier.getRelationshipType().getId(), relationshipTypeId)) {
                            ItemElementRelationship relationshipForParent = ier.getRelationshipForParent();
                            if (relationshipForParent == null) {
                                // Non-linked relationship to the item. 
                                Item secondItem = ier.getSecondItem();
                                if (secondItem.equals(relatingElement)) {
                                    failed = true; 
                                    break; 
                                }
                                continue; 
                            }                            
                            if (Objects.equals(relationshipForParent.getId(), linkedParentRelationshipId)) {
                                failed = true; 
                                break; 
                            }
                        }
                    }
                } 

                if (failed) {
                    throw new InvalidArgument("Relationship with " + name + " already exists");
                }
            }

        }
    }

    public void verifyCircularRelationship(Integer relationshipTypeId, Integer parentItemId, Integer proposedChildItemId) throws InvalidObjectState {
        List<Item> problemItems = itemFacade.isItemRelationshipHaveCircularReference(relationshipTypeId, parentItemId, proposedChildItemId);
        if (!problemItems.isEmpty()) {
            Item problemItem = problemItems.get(0);
            throw new InvalidObjectState("Entry has a circular reference problem. The item has relationship with " + problemItem + " is on the same relationship branch.");
        }
    }

    protected boolean isAllowMultipleRelationships() {
        return false;
    }

}
