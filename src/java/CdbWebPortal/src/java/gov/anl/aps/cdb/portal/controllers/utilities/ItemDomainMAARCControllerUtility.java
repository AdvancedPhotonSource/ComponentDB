/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemElementController;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMAARCFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMAARC;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
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
            List<ItemElement> itemElementDisplayList = item.getItemElementDisplayList();
            while (itemElementDisplayList.size() > 0) {
                ItemElement ie = itemElementDisplayList.get(0);
                itemElementDisplayList.remove(0);

                destroyFile(ie, userInfo);
            }

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
