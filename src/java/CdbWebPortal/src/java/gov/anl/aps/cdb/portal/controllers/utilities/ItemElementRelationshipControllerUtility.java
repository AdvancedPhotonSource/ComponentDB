/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author darek
 */
public class ItemElementRelationshipControllerUtility extends CdbEntityControllerUtility<ItemElementRelationship, ItemElementRelationshipFacade> {

    @Override
    protected ItemElementRelationshipFacade getEntityDbFacade() {
        return ItemElementRelationshipFacade.getInstance(); 
    }

    @Override
    public ItemElementRelationship createEntityInstance(UserInfo sessionUser) {
        return new ItemElementRelationship(); 
    }
    
    @Override
    public String getEntityTypeName() {
        return "itemElementRelationship";
    }
    
}
