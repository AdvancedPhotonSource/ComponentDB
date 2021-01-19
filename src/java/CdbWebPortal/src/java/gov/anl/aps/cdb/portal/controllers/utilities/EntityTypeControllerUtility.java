/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.beans.EntityTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;

/**
 *
 * @author darek
 */
public class EntityTypeControllerUtility extends CdbEntityControllerUtility<EntityType, EntityTypeFacade>{

    @Override
    protected EntityTypeFacade getEntityDbFacade() {
        return EntityTypeFacade.getInstance();
    }

    @Override
    public String getEntityTypeName() {
        return "entityType"; 
    }
    
}
