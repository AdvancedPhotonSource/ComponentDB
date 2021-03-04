/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.EntityTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author darek
 */
public class EntityTypeControllerUtility extends CdbEntityControllerUtility<EntityType, EntityTypeFacade>{
    
    public EntityType findByName(String name) {
        return getEntityDbFacade().findByName(name);
    }

    @Override
    protected EntityTypeFacade getEntityDbFacade() {
        return EntityTypeFacade.getInstance();
    }
    
    @Override
    public EntityType createEntityInstance(UserInfo sessionUser) {
        EntityType entityType = new EntityType(); 
        return entityType; 
    }

    @Override
    public String getEntityTypeName() {
        return "entityType"; 
    }
    
}
