/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;

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
    public String getEntityTypeName() {
        return "itemElementRelationship";
    }
    
}
