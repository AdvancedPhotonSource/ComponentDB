/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMAARC;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.List;

/**
 *
 * @author darek
 */
public class NewMAARCInformation {
    
    private String name;
    
    private String description; 
    
    private List<EntityType> entityTypeList;
    
    private List<ItemProject> itemProjectsList; 
    
    private UserInfo ownerUser; 
    
    private UserGroup ownerGroup; 
    
    private Integer parentItemId; 
   
    private String parentElementName; 
    
    private String experimentFilePath;
    
    private String experimentName; 

    public NewMAARCInformation() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<EntityType> getEntityTypeList() {
        return entityTypeList;
    }

    public List<ItemProject> getItemProjectsList() {
        return itemProjectsList;
    }

    public UserInfo getOwnerUser() {
        return ownerUser;
    }

    public UserGroup getOwnerGroup() {
        return ownerGroup;
    }

    public Integer getParentItemId() {
        return parentItemId;
    }
    
    public String getParentElementName() {
        return parentElementName;
    }

    public String getExperimentFilePath() {
        return experimentFilePath;
    }

    public String getExperimentName() {
        return experimentName;
    }
    
    public void updateItemDomainMAARCWithInformation(ItemDomainMAARC itemDomainMAARC) throws InvalidArgument, CdbException {
        if (name == null) {
            throw new InvalidArgument("Name for new item must be provided."); 
        }
        
        if (itemProjectsList == null) {
            throw new InvalidArgument("Item project list for new item must be provided."); 
        }
        
        if (entityTypeList == null) {
            throw new InvalidArgument("Entity type list for new item must be provided.");
        }
        
        itemDomainMAARC.setName(name);
        itemDomainMAARC.setItemIdentifier1(experimentName);
        itemDomainMAARC.setItemIdentifier2(experimentFilePath);
        itemDomainMAARC.setEntityTypeList(entityTypeList);
        itemDomainMAARC.setItemProjectList(itemProjectsList);
        itemDomainMAARC.setDescription(description);
                
        if (ownerUser != null) {
            itemDomainMAARC.setOwnerUser(ownerUser);
        }
        if (ownerGroup != null) {
            itemDomainMAARC.setOwnerUserGroup(ownerGroup);
        }        
    }
        
}
