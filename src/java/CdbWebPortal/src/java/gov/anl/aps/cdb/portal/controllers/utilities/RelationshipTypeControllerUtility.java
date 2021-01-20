/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author darek
 */
public class RelationshipTypeControllerUtility extends CdbEntityControllerUtility<RelationshipType, RelationshipTypeFacade> {

    @Override
    protected RelationshipTypeFacade getEntityDbFacade() {
        return RelationshipTypeFacade.getInstance();
    }
    
    @Override
    public String getEntityTypeName() {
        return "relationshipType";
    }

    @Override
    public RelationshipType createEntityInstance(UserInfo sessionUser) {
        return new RelationshipType();
    }
    
}
