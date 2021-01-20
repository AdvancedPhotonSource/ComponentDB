/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeHandlerFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class PropertyTypeHandlerControllerUtility extends CdbEntityControllerUtility<PropertyTypeHandler, PropertyTypeHandlerFacade> {

    private static final Logger logger = LogManager.getLogger(PropertyTypeHandlerControllerUtility.class.getName());
    
    @Override
    public void prepareEntityInsert(PropertyTypeHandler propertyTypeHandler, UserInfo userInfo) throws ObjectAlreadyExists {
        PropertyTypeHandler existingPropertyTypeHandler = getEntityDbFacade().findByName(propertyTypeHandler.getName());
        if (existingPropertyTypeHandler != null) {
            throw new ObjectAlreadyExists("Property type handler " + propertyTypeHandler.getName() + " already exists.");
        }
        logger.debug("Inserting new property type " + propertyTypeHandler.getName());
    } 
    
    @Override
    protected PropertyTypeHandlerFacade getEntityDbFacade() {
        return PropertyTypeHandlerFacade.getInstance(); 
    }

    @Override
    public String getEntityInstanceName(PropertyTypeHandler entity) {
        if (entity != null) {
            return entity.getName();
        }
        return "";
    }
        
    @Override
    public String getEntityTypeName() {
        return "propertyTypeHandler";
    }

    @Override
    public PropertyTypeHandler createEntityInstance(UserInfo sessionUser) {
        return new PropertyTypeHandler(); 
    }
    
}
