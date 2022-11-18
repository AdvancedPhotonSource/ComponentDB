/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationshipHistory;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Date;

/**
 * DB utility class for item elements.
 */
public class ItemElementRelationshipUtility {
    
   public static ItemElementRelationshipHistory createItemElementHistoryRecord(ItemElementRelationship itemElementRelationship,
           UserInfo enteredByUser,
           Date enteredOnDateTime) {
       ItemElementRelationshipHistory ierh = new ItemElementRelationshipHistory(); 
       
       ierh.setDescription(itemElementRelationship.getDescription());
       ierh.setEnteredByUser(enteredByUser);
       ierh.setEnteredOnDateTime(enteredOnDateTime);
       ierh.setFirstItemConnector(itemElementRelationship.getFirstItemConnector());
       ierh.setFirstItemElement(itemElementRelationship.getFirstItemElement());
       ierh.setItemElementRelationship(itemElementRelationship);
       ierh.setLabel(itemElementRelationship.getLabel());
       ierh.setLinkItemElement(itemElementRelationship.getLinkItemElement());
       ierh.setRelationshipDetails(itemElementRelationship.getRelationshipDetails());
       ierh.setResourceType(itemElementRelationship.getResourceType());
       ierh.setSecondItemConnector(itemElementRelationship.getSecondItemConnector());
       ierh.setSecondItemElement(itemElementRelationship.getSecondItemElement());
       ierh.setSecondSortOrder(itemElementRelationship.getSecondSortOrder());
       ierh.setRelationshipForParent(itemElementRelationship.getRelationshipForParent());
       
       return ierh;       
    }    
}
