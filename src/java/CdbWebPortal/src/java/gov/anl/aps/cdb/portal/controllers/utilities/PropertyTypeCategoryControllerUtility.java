/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class PropertyTypeCategoryControllerUtility extends CdbEntityControllerUtility<PropertyTypeCategory, PropertyTypeCategoryFacade> {        

    private static final Logger logger = LogManager.getLogger(PropertyTypeCategoryControllerUtility.class.getName());    

    @Override
    protected void prepareEntityInsert(PropertyTypeCategory propertyTypeCategory, UserInfo userInfo) throws CdbException {
        PropertyTypeCategory existingPropertyTypeCategory = getEntityDbFacade().findByName(propertyTypeCategory.getName());
        if (existingPropertyTypeCategory != null) {
            throw new ObjectAlreadyExists("Property type category " + propertyTypeCategory.getName() + " already exists.");
        }
        logger.debug("Inserting new property type category " + propertyTypeCategory.getName());
    }

    @Override
    public void prepareEntityUpdate(PropertyTypeCategory propertyTypeCategory, UserInfo userInfo) throws ObjectAlreadyExists {
        PropertyTypeCategory existingPropertyTypeCategory = getEntityDbFacade().findByName(propertyTypeCategory.getName());
        if (existingPropertyTypeCategory != null && !existingPropertyTypeCategory.getId().equals(propertyTypeCategory.getId())) {
            throw new ObjectAlreadyExists("Property type category " + propertyTypeCategory.getName() + " already exists.");
        }
        logger.debug("Updating property type category " + propertyTypeCategory.getName());
    }

    @Override
    protected PropertyTypeCategoryFacade getEntityDbFacade() {
        return PropertyTypeCategoryFacade.getInstance();
    } 

    @Override
    public String getEntityInstanceName(PropertyTypeCategory entity) {
        if (entity != null) {
            return entity.getName();
        }
        return "";
    }        

    @Override
    public String getEntityTypeName() {
        return "propertyTypeCategory";
    }

    @Override
    public PropertyTypeCategory createEntityInstance(UserInfo sessionUser) {
        return new PropertyTypeCategory();
    }
    
}
