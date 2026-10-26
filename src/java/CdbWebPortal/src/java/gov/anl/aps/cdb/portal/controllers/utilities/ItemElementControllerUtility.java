/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.view.objects.CatalogItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.List;

/**
 *
 * @author darek
 */
public class ItemElementControllerUtility extends CdbDomainEntityControllerUtility<ItemElement, ItemElementFacade> {
    
    ItemElementFacade itemElementFacade; 

    public ItemElementControllerUtility() {        
        itemElementFacade = ItemElementFacade.getInstance(); 
    }        
    
    @Override
    protected void prepareEntityDestroy(ItemElement itemElement, UserInfo userInfo) throws CdbException {
        super.prepareEntityDestroy(itemElement, userInfo);
        // Verify that item domain allows destroy of item element.   

        Item parentItem = itemElement.getParentItem();
        if (!(parentItem instanceof ItemDomainCatalog)) {
            ItemElementConstraintInformation ieci = getFreshItemElementConstraintInformation(itemElement);
            if (ieci.isSafeToRemove() == false) {
                String constraintMessage = generateSingleDeleteConstraintMessage(itemElement);
                throw new CdbException("Cannot remove item element. Constrains not met. " + constraintMessage);
            }
        }
    }

    @Override
    public void prepareEntityUpdateOnRemoval(ItemElement designElement) {
        EntityInfo entityInfo = designElement.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
    }

    private String generateSingleDeleteConstraintMessage(ItemElement itemElement) {
        ItemElementConstraintInformation constraint = getItemElementConstraintInformation(itemElement);
        String message = null;

        if (constraint instanceof CatalogItemElementConstraintInformation) {
            CatalogItemElementConstraintInformation catalogConstraint = (CatalogItemElementConstraintInformation) constraint;
            message = generateCatalogSpecificPreventDeleteUpdateContainedMessage(catalogConstraint);
        }
        if (message == null) {
            // Check set log and properties 
            if (constraint.isHasLogs()) {
                return "Item element has logs.";
            } else if (constraint.isHasProperties()) {
                return "Item element has properties";
            }

            List<ItemElementConstraintInformation> relatedConstraintInfo = constraint.getRelatedConstraintInfo();
            for (ItemElementConstraintInformation ittrConstraint : relatedConstraintInfo) {
                String itemElementIdentifyingString = "Related item element of: " + ittrConstraint.getItemElement().getParentItem().getName();
                if (ittrConstraint.isHasLogs()) {
                    return itemElementIdentifyingString + " has logs.";
                } else if (constraint.isHasProperties()) {
                    return itemElementIdentifyingString + " has properties.";
                }
            }

        } else {
            return message;
        }

        return "";
    }
    
    public ItemElementConstraintInformation getFreshItemElementConstraintInformation(ItemElement itemElement) {
        itemElement = findById(itemElement.getId());
        return getItemElementConstraintInformation(itemElement);
    }
    
    public String generateCatalogSpecificPreventDeleteUpdateContainedMessage(CatalogItemElementConstraintInformation catalogConstraint) {
        List<ItemElementConstraintInformation> relatedConstraintInfo = catalogConstraint.getRelatedConstraintInfo();

        for (ItemElementConstraintInformation ittrConstraint : relatedConstraintInfo) {
            if (ittrConstraint instanceof CatalogItemElementConstraintInformation) {
                if (((CatalogItemElementConstraintInformation) ittrConstraint).isHasInventoryItemAssigned()) {
                    String message = ittrConstraint.getItemElement().getParentItem().getName();
                    message += " has an inventory item assinged for this element.";
                    return message;
                }
            }
        }
        return null;
    }
    
    @Override
    protected void prepareEntityUpdate(ItemElement itemElement, UserInfo userInfo) throws CdbException {
        super.prepareEntityUpdate(itemElement, userInfo);
        
        EntityInfo entityInfo = itemElement.getEntityInfo();
                
        EntityInfoUtility.updateEntityInfo(entityInfo, userInfo);        
        
        ItemElement originalItemElement = null; 
        if (itemElement.getId() != null) {
            originalItemElement = findById(itemElement.getId());
        }
        
        // Check if history needs to be added
        ItemElementUtility.prepareItemElementHistory(originalItemElement, itemElement, entityInfo);        

        // Basic checks for updating an element must be verified with domain of item element. 
        Item parentItem = itemElement.getParentItem();
        ItemControllerUtility itemController = parentItem.getItemControllerUtility();
        //ItemController itemController = ItemController.findDomainControllerForItem(parentItem);
        itemController.checkItemElement(itemElement);

        if (itemElement.getId() != null) {
            ItemElement freshDbItemElement = findById(itemElement.getId());

            // Verify if contained item changed
            Item originalContainedItem = freshDbItemElement.getContainedItem();
            if (ObjectUtility.equals(originalContainedItem, itemElement.getContainedItem()) == false) {
                // Contained item has been updated.
                ItemElementConstraintInformation ieci = getItemElementConstraintInformation(freshDbItemElement);
                if (ieci.isSafeToUpdateContainedItem() == false) {
                    itemElement.setContainedItem(originalContainedItem);
                    throw new CdbException("Cannot update item element " + itemElement + " due to constraints not met. Please reload the item details page and try again.");
                }
            }

            //Verify if isRequred changed
            Boolean originalIsRequired = freshDbItemElement.getIsRequired();
            if (ObjectUtility.equals(originalIsRequired, itemElement.getIsRequired()) == false) {                
                itemController.finalizeItemElementRequiredStatusChanged(itemElement, userInfo);                
            }
        }
    }
    
    public ItemElementConstraintInformation getItemElementConstraintInformation(ItemElement itemElement) {
        ItemElementConstraintInformation itemElementConstraintInformation = null;
        if (itemElement != null) {
            itemElementConstraintInformation = itemElement.getConstraintInformation();
            if (itemElementConstraintInformation == null) {
                Item parentItem = itemElement.getParentItem();
                if (parentItem != null) {
                    ItemControllerUtility itemDomainControllerUtility = parentItem.getControllerUtility();
                    itemElementConstraintInformation = itemDomainControllerUtility.loadItemElementConstraintInformation(itemElement);
                    itemElement.setConstraintInformation(itemElementConstraintInformation);
                }
            }
        }
        return itemElementConstraintInformation;
    }
    
    @Override
    protected ItemElementFacade getEntityDbFacade() {
        return itemElementFacade; 
    }
    
    @Override
    public ItemElement createEntityInstance(UserInfo sessionUser) {
        ItemElement designElement = new ItemElement();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        designElement.setEntityInfo(entityInfo);

        // clear selection lists
        return designElement;
    }

    @Override
    public String getEntityInstanceName(ItemElement entity) {
        if (entity != null) {
            return entity.getName();
        }
        return "";
    }
        
    @Override
    public String getEntityTypeName() {
        return "itemElement";
    }
    
}
