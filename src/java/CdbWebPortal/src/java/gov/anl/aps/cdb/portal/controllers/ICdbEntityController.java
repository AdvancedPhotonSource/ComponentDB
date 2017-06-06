/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;

/**
 *
 * @author djarosz
 */
public interface ICdbEntityController<EntityType extends CdbEntity> {
    
    /**
     * Get entity currently being viewed/edited by the user.
     * 
     * @param current 
     */
    public void setCurrent(EntityType current);        
    
    /**
     * Set entity currently being viewed/edited by the user.
     * 
     * @return 
     */
    public EntityType getCurrent();
    
}
