/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMAARCFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMAARC;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darek
 */
public class ItemDomainMAARCControllerUtility extends ItemControllerUtility<ItemDomainMAARC, ItemDomainMAARCFacade> {   
    
    protected final String FILE_ENTITY_TYPE_NAME = "File";
    
    @Override
    protected void prepareEntityDestroy(ItemDomainMAARC item, UserInfo userInfo) throws CdbException {
        if (isEntityTypeFile(item)) {
            List<ItemElement> itemElementMemberList = item.getItemElementMemberList();

            // A file should be a member of 1 Study 
            if (itemElementMemberList.size() == 1) {
                // Destroy the element before proceeding to destroy the file. 
                ItemElement itemElement = itemElementMemberList.get(0);
                ItemElementControllerUtility instance = new ItemElementControllerUtility(); 
                instance.destroy(itemElement, userInfo);
                itemElementMemberList.clear();
            } else if (itemElementMemberList.size() == 0) {
                // Do Nothing. 
            } else {
                throw new CdbException("File should be a member of one study. Something went wrong! please notify Admin of this error.");
            }
        } else {
            // Study            
            List<ItemElement> fiel = item.getFullItemElementList(); 
            ItemElement selfElement = item.getSelfElement();
            while (fiel.size() > 0) {
                ItemElement ie = fiel.get(0);
                fiel.remove(0);
                if (!ie.equals(selfElement)) {
                    destroyFile(ie, userInfo);
                }
            }
            // Add back self element since its deleted with the item. 
            fiel.add(selfElement); 
            item.resetItemElementVars();

            // Clear Relationships
            List<ItemElementRelationship> ierList = item.getItemElementRelationshipList1();
            if (ierList.size() > 0) {
                ItemElementRelationshipControllerUtility ierc = new ItemElementRelationshipControllerUtility(); 

                while (ierList.size() > 0) {
                    ItemElementRelationship ier = ierList.get(0);
                    ierList.remove(0);

                    ierc.destroy(ier, userInfo);
                }

            }
        }       
        super.prepareEntityDestroy(item, userInfo);
    }
    
    /**
     * Destroys a full file reference from a study
     *
     * @param itemElement
     * @param userInfo
     */
    public void destroyFile(ItemElement itemElement, UserInfo userInfo) throws CdbException {
        ItemDomainMAARC containedItem = (ItemDomainMAARC) itemElement.getContainedItem();

        ItemElementControllerUtility instance = new ItemElementControllerUtility(); 
        instance.destroy(itemElement, userInfo);

        destroy(containedItem, userInfo);        
    }
    
    public boolean isEntityTypeFile(ItemDomainMAARC item) {
        List<EntityType> entityTypeList = item.getEntityTypeList();
        for (EntityType entityType : entityTypeList) {
            if (entityType.getName().equals(FILE_ENTITY_TYPE_NAME)) {
                return true;

            }
        }

        return false;
    }
    
    protected String getMAARCConnectionRelationshipName() {
        return ItemElementRelationshipTypeNames.maarc.getValue();
    }
    
    public ItemElementRelationship addMAARCConnectionRelationshipToItem(ItemDomainMAARC maarcItem, Item relatedMAARCItem) throws InvalidArgument {
        if ((relatedMAARCItem instanceof ItemDomainInventory || relatedMAARCItem instanceof ItemDomainMachineDesign) == false) {
            throw new InvalidArgument("Item related to MAARC item can only be of domain inventory or machine design."); 
        }
        
        RelationshipType maarcConnectionRelationship
                = relationshipTypeFacade.findByName(getMAARCConnectionRelationshipName());
        
        // Verify if already exists
        List<ItemElementRelationship> ierList = maarcItem.getItemElementRelationshipList1();
        for (ItemElementRelationship existingIER : ierList) {
            RelationshipType relationshipType = existingIER.getRelationshipType();
            
            if (relationshipType.equals(maarcConnectionRelationship)) {
                Item firstItem = existingIER.getFirstItem();
                if (firstItem.equals(relatedMAARCItem)) {
                    throw new InvalidArgument("MAARC Relationship already exists.");
                }
            }
        }

        // Create item element relationship between the template and the clone 
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setRelationshipType(maarcConnectionRelationship);
        itemElementRelationship.setFirstItemElement(relatedMAARCItem.getSelfElement());
        itemElementRelationship.setSecondItemElement(maarcItem.getSelfElement());

        maarcItem.setItemElementRelationshipList1(new ArrayList<>());
        maarcItem.getItemElementRelationshipList1().add(itemElementRelationship);
        
        return itemElementRelationship;
    }
    
    public List<ItemElementRelationship> getMAARCRelationshipsForItem(ItemDomainMAARC item) {
        List<ItemElementRelationship> maarcRelationships = new ArrayList<>();
        List<ItemElementRelationship> itemElementRelationshipList1 = item.getItemElementRelationshipList1();
        String relationshipName = getMAARCConnectionRelationshipName();
        
        maarcRelationships = new ArrayList<>();            
       
        for (ItemElementRelationship ier : itemElementRelationshipList1) {
            if (ier.getRelationshipType().getName().equals(relationshipName)) {
                maarcRelationships.add(ier);
            }
        }

        return maarcRelationships;
    }

    @Override
    public boolean isEntityHasQrId() {
        return false; 
    }

    @Override
    public boolean isEntityHasName() {
        return true; 
    }

    @Override
    public boolean isEntityHasProject() {
        return false;
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.maarc.getValue(); 
    }

    @Override
    protected ItemDomainMAARCFacade getItemFacadeInstance() {
        return ItemDomainMAARCFacade.getInstance(); 
    }
    
    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getEntityTypeName() {
        return "itemMAARC";
    }
    
    @Override
    public String getDisplayEntityTypeName() {
        return "MAARC Item";
    }
    
    @Override
    protected ItemDomainMAARC instenciateNewItemDomainEntity() {
        return new ItemDomainMAARC();
    }
    
}
