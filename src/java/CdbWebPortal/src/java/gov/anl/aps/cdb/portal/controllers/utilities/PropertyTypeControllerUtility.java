/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class PropertyTypeControllerUtility extends CdbEntityControllerUtility<PropertyType, PropertyTypeFacade> {

    private static final Logger logger = LogManager.getLogger(PropertyTypeControllerUtility.class.getName());

    @Override
    public void prepareEntityInsert(PropertyType propertyType, UserInfo userInfo) throws ObjectAlreadyExists {
        PropertyType existingPropertyType = getEntityDbFacade().findByName(propertyType.getName());
        if (existingPropertyType != null) {
            throw new ObjectAlreadyExists("Property type " + propertyType.getName() + " already exists.");
        }
        logger.debug("Inserting new property type " + propertyType.getName());
        verifyAndApplyPropertyTypeHandlerRequirements(propertyType);
    }

    @Override
    public void prepareEntityUpdate(PropertyType propertyType, UserInfo userInfo) throws ObjectAlreadyExists {
        propertyType.resetCachedVales();
        PropertyType existingPropertyType = getEntityDbFacade().findByName(propertyType.getName());
        if (existingPropertyType != null && !existingPropertyType.getId().equals(propertyType.getId())) {
            throw new ObjectAlreadyExists("Property type " + propertyType.getName() + " already exists.");
        }
        logger.debug("Updating property type " + propertyType.getName());
        verifyAndApplyPropertyTypeHandlerRequirements(propertyType);
    }

    private void verifyAndApplyPropertyTypeHandlerRequirements(PropertyType propertyType) {
        PropertyTypeHandler propertyTypeHandler = propertyType.getPropertyTypeHandler();
        if (propertyTypeHandler != null) {
            // Remove invalid id used to identify the new handler. 
            if (propertyTypeHandler.getId() < 0) {
                propertyTypeHandler.setId(null);
            }

            PropertyTypeHandlerInterface handlerClass = PropertyTypeHandlerFactory.getHandler(propertyTypeHandler.getName());
            List<String> requiredMetadataKeys = handlerClass.getRequiredMetadataKeys();
            if (requiredMetadataKeys != null && requiredMetadataKeys.size() > 0) {
                if (propertyType.getPropertyTypeMetadataList() == null) {
                    propertyType.setPropertyTypeMetadataList(new ArrayList<>());
                }

                List<PropertyTypeMetadata> propertyTypeMetadataList = propertyType.getPropertyTypeMetadataList();

                List<String> keysToCreate = new LinkedList<>();
                for (String requiredKey : requiredMetadataKeys) {
                    boolean found = false;
                    for (PropertyTypeMetadata ptm : propertyTypeMetadataList) {
                        if (ptm.getMetadataKey().equals(requiredKey)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        keysToCreate.add(requiredKey);
                    }
                }

                for (String key : keysToCreate) {
                    PropertyTypeMetadata ptm = new PropertyTypeMetadata();
                    ptm.setPropertyType(propertyType);
                    ptm.setMetadataKey(key);
                    propertyTypeMetadataList.add(ptm);
                }
            }
        }
    }
    
    @Override
    public PropertyType createEntityInstance(UserInfo sessionUser) {
        PropertyType propertyType = new PropertyType();
        propertyType.setIsInternal(false);
        propertyType.setIsActive(true);
        propertyType.setIsUserWriteable(false);
        propertyType.setIsDynamic(false);
        return propertyType;
    } 

    @Override
    protected PropertyTypeFacade getEntityDbFacade() {
        return PropertyTypeFacade.getInstance();
    }

    @Override
    public String getEntityInstanceName(PropertyType entity) {
        if (entity != null) {
            return entity.getName();
        }
        return "";
    }

    @Override
    public String getEntityTypeName() {
        return "propertyType";
    }
    
}
