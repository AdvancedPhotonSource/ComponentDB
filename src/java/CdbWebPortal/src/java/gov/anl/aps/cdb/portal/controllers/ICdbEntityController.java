/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
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
    
    /**
    * Perform all necessary operations to update an item.
    * 
    * @param entity
    * @throws CdbException
    * @throws RuntimeException 
    */
    public void performUpdateOperations(EntityType entity) throws CdbException, RuntimeException;
    
    /**
     * Perform all necessary operations to create an item.
     * 
     * @param item
     * @throws CdbException
     * @throws RuntimeException 
     */
    public void performCreateOperations(EntityType item) throws CdbException, RuntimeException; 
    
    /**
     * Fetches edit row style for a specific entity
     * @param entity
     * @return 
     */
    public String getEntityEditRowStyle(EntityType entity);    

}
