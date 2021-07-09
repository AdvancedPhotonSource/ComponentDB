/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.SystemLogLevel;
import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 * @param <EntityType>
 * @param <FacadeType>
 */
public abstract class CdbEntityControllerUtility<EntityType extends CdbEntity, FacadeType extends CdbEntityFacade<EntityType>> {
    
    private static final Logger logger = LogManager.getLogger(CdbEntityControllerUtility.class.getName());
    
    LogControllerUtility logControllerUtility; 
    
    /**
     * Abstract method for returning entity DB facade.
     *
     * @return entity DB facade
     */
    protected abstract FacadeType getEntityDbFacade();
    
    /**
     * Abstract method for creating new entity instance.
     *
     * @return created entity instance
     */
    public abstract EntityType createEntityInstance(UserInfo sessionUser);        
    
    public EntityType create(EntityType entity, UserInfo createdByUserInfo) throws CdbException, RuntimeException {
        try {            
            prepareEntityInsert(entity, createdByUserInfo);
            getEntityDbFacade().create(entity);
           
            addCreatedSystemLog(entity, createdByUserInfo);
            entity.setPersitanceErrorMessage(null);
            
            return entity; 
        } catch (CdbException ex) {
            logger.error("Could not create " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            addCreatedWarningSystemLog(ex, entity, createdByUserInfo);
            entity.setPersitanceErrorMessage(ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            logger.error("Could not create " + getDisplayEntityTypeName() + ": " + t.getMessage());
            addCreatedWarningSystemLog(ex, entity, createdByUserInfo);
            entity.setPersitanceErrorMessage(ex.getMessage());
            throw ex;
        }
    }
    
    public void createList(List<EntityType> entities, UserInfo createdByUserInfo) throws CdbException, RuntimeException {
        try {
            for (EntityType entity : entities) {
                prepareEntityInsert(entity, createdByUserInfo);
            }
            getEntityDbFacade().create(entities);            
            
            addCdbEntitySystemLog(SystemLogLevel.entityInfo, "Created " + entities.size() + " entities.", createdByUserInfo);            
            setPersistenceErrorMessageForList(entities, null);

        } catch (CdbException ex) {
            logger.error("Could not create " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            setPersistenceErrorMessageForList(entities, ex.getMessage());
            addCdbEntityWarningSystemLog("Failed to create list of entities: " + getDisplayEntityTypeName(), ex, null, createdByUserInfo);
            throw ex;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            logger.error("Could not create list of " + getDisplayEntityTypeName() + ": " + t.getMessage());
            setPersistenceErrorMessageForList(entities, ex.getMessage());
            addCdbEntityWarningSystemLog("Failed to create list of entities: " + getDisplayEntityTypeName(), ex, null, createdByUserInfo);
            throw ex;
        }
    }
    
    public EntityType update(EntityType entity, UserInfo updatedByUserInfo) throws CdbException, RuntimeException {
        try {            
            logger.debug("Updating " + getDisplayEntityTypeName() + " " + getEntityInstanceName(entity));
            prepareEntityUpdate(entity, updatedByUserInfo);
            EntityType updatedEntity = getEntityDbFacade().edit(entity);
            addCdbEntitySystemLog(SystemLogLevel.entityInfo, "Updated: " + entity.getSystemLogString(), updatedByUserInfo);
            entity.setPersitanceErrorMessage(null);
            
            return updatedEntity; 
        } catch (CdbException ex) {
            entity.setPersitanceErrorMessage(ex.getMessage());
            addCdbEntityWarningSystemLog("Failed to update", ex, entity, updatedByUserInfo);
            logger.error("Could not update " + getDisplayEntityTypeName() + " "
                    + getEntityInstanceName(entity)+ ": " + ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);            
            logger.error("Could not update " + getDisplayEntityTypeName() + " "
                    + getEntityInstanceName(entity)+ ": " + t.getMessage());
            addCdbEntityWarningSystemLog("Failed to update", ex, entity, updatedByUserInfo);
            entity.setPersitanceErrorMessage(t.getMessage());
            throw ex;
        }
    }
    
    public EntityType updateOnRemoval(EntityType entity, UserInfo updatedByUserInfo) throws CdbException, RuntimeException {
        try {
            logger.debug("Updating " + getDisplayEntityTypeName() + " " + getEntityInstanceName(entity));
            prepareEntityUpdateOnRemoval(entity);
            EntityType updatedEntity = getEntityDbFacade().edit(entity);
                        
            return updatedEntity; 
        } catch (CdbException ex) {
            entity.setPersitanceErrorMessage(ex.getMessage());
            addCdbEntityWarningSystemLog("Failed to update", ex, entity, updatedByUserInfo);
            logger.error("Could not update " + getDisplayEntityTypeName() + " "
                    + getEntityInstanceName(entity)+ ": " + ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);            
            logger.error("Could not update " + getDisplayEntityTypeName() + " "
                    + getEntityInstanceName(entity)+ ": " + t.getMessage());
            addCdbEntityWarningSystemLog("Failed to update", ex, entity, updatedByUserInfo);
            entity.setPersitanceErrorMessage(t.getMessage());
            throw ex;
        }
    }
    
    public void updateList(List<EntityType> entities, UserInfo updatedByUserInfo) throws CdbException, RuntimeException {
        try {
            for (EntityType entity : entities) {
                logger.debug("Updating " + getDisplayEntityTypeName() + " " + getEntityInstanceName(entity));
                prepareEntityUpdate(entity, updatedByUserInfo);
            }
            getEntityDbFacade().edit(entities);
            for (EntityType entity : entities) {                
                entity.setPersitanceErrorMessage(null);
                addCdbEntitySystemLog(SystemLogLevel.entityInfo, "Updated: " + entity.getSystemLogString(), updatedByUserInfo);
            }            
        } catch (CdbException ex) {
            logger.error("Could not update " + getDisplayEntityTypeName() + " entities: " + ex.getMessage());
            setPersistenceErrorMessageForList(entities, ex.getMessage());
            addCdbEntityWarningSystemLog("Failed to update list of " + getDisplayEntityTypeName(), ex, null, updatedByUserInfo);
            throw ex;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            logger.error("Could not update list of " + getDisplayEntityTypeName() + ": " + t.getMessage());
            addCdbEntityWarningSystemLog("Failed to update list of " + getDisplayEntityTypeName(), ex, null, updatedByUserInfo); 
            setPersistenceErrorMessageForList(entities, t.getMessage());
            throw ex;
        }
    }
    
    public void destroy(EntityType entity, UserInfo destroyedByUserInfo) throws CdbException, RuntimeException {
        try {
            prepareEntityDestroy(entity, destroyedByUserInfo);
            getEntityDbFacade().remove(entity);
            
            addCdbEntitySystemLog(SystemLogLevel.entityInfo, "Deleted: " + entity.getSystemLogString(), destroyedByUserInfo);            
        } catch (CdbException ex) {
            entity.setPersitanceErrorMessage(ex.getMessage());
            addCdbEntityWarningSystemLog("Failed to destroy", ex, entity, destroyedByUserInfo);
            logger.error("Could not destroy " + getDisplayEntityTypeName() + " "
                    + getEntityInstanceName(entity)+ ": " + ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);            
            logger.error("Could not destroy " + getDisplayEntityTypeName() + " "
                    + getEntityInstanceName(entity)+ ": " + t.getMessage());
            addCdbEntityWarningSystemLog("Failed to destroy", ex, entity, destroyedByUserInfo);
            entity.setPersitanceErrorMessage(t.getMessage());
            throw ex;
        }
    }
    
    public void destroyList(
            List<EntityType> entities,
            EntityType updateEntity, UserInfo destroyedByUserInfo)
            throws CdbException, RuntimeException {

        try {
            if (updateEntity != null) {
                prepareEntityUpdate(updateEntity, destroyedByUserInfo);
            }

            for (EntityType entity : entities) {
                if ((entity != null) && entity.getId() != null) {
                    prepareEntityDestroy(entity, destroyedByUserInfo);
                }
            }

            getEntityDbFacade().remove(entities, updateEntity);

            addCdbEntitySystemLog(SystemLogLevel.entityInfo, "Deleted: " + entities.size() + " entities.", destroyedByUserInfo);
            setPersistenceErrorMessageForList(entities, null);
        } catch (CdbException ex) {
            logger.error("Could not delete list of " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            setPersistenceErrorMessageForList(entities, ex.getMessage());
            addCdbEntityWarningSystemLog("Failed to delete list of " + getDisplayEntityTypeName(), ex, updateEntity, destroyedByUserInfo);
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Could not delete list of " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            setPersistenceErrorMessageForList(entities, ex.getMessage());
            addCdbEntityWarningSystemLog("Failed to delete list of " + getDisplayEntityTypeName(), ex, updateEntity, destroyedByUserInfo);
            throw ex;
        }
    }
    
    /**
     * Find entity instance by id.
     *
     * @param id entity instance id
     * @return entity instance
     */
    public EntityType findById(Integer id) {
        return getEntityDbFacade().find(id);
    }
    
    /**
     * Used by import framework.  Looks up entity by path.  Default implementation
     * raises exception.  Subclasses should override to provide support for lookup
     * by path.
     */
    public EntityType findByPath(String path) throws CdbException {
        throw new CdbException("controller utility does not support lookup by path");
    }
        
    public String getEntityInstanceName(EntityType entity) {
        if (entity != null) {
            return entity.toString(); 
        }
        return ""; 
    } 
    
    public abstract String getEntityTypeName();     
    
    public String getDisplayEntityTypeName() {
        String entityTypeName = getEntityTypeName();
        
        entityTypeName = entityTypeName.substring(0, 1).toUpperCase() + entityTypeName.substring(1); 
        
        String displayEntityTypeName = ""; 
        
        int prevEnd = 0; 
        for (int i = 1; i < entityTypeName.length(); i++) {
            Character c = entityTypeName.charAt(i); 
            if (Character.isUpperCase(c)) {
                displayEntityTypeName += entityTypeName.substring(prevEnd, i) + " "; 
                prevEnd = i; 
            }
        }
        
        displayEntityTypeName += entityTypeName.substring(prevEnd);        
                
        return displayEntityTypeName; 
    }
    
    /**
     * Prepare entity insert.
     *
     * This method should be overridden in the derived controller.
     *
     * @param entity entity instance
     * @throws CdbException in case of any errors
     */
    protected void prepareEntityInsert(EntityType entity, UserInfo userInfo) throws CdbException {        
    }
    
    /**
     * Prepare entity update.
     *
     * This method should be overridden in the derived controller.
     *
     * @param entity entity instance
     * @throws CdbException in case of any errors
     */
    protected void prepareEntityUpdate(EntityType entity, UserInfo updatedByUser) throws CdbException {        
    }
    
    protected void prepareEntityUpdateOnRemoval(EntityType entity) throws CdbException {
    }
    
    protected void prepareEntityDestroy(EntityType entity, UserInfo userInfo) throws CdbException {        
    }
    
    protected void addCreatedSystemLog(EntityType entity, UserInfo createdByUserInfo) throws CdbException {
        String message = "Created: " + entity.getSystemLogString(); 
        addCdbEntitySystemLog(SystemLogLevel.entityInfo, message, createdByUserInfo);  
    }
    
    protected void addCreatedWarningSystemLog(Exception exception, EntityType entity, UserInfo createdByUserInfo) throws CdbException {
        addCdbEntityWarningSystemLog("Failed to create", exception, entity, createdByUserInfo);
    }
    
    /**
     * Allows the controller to quickly add a warning log entry while
     * automatically appending appropriate info.
     *
     * @param warningMessage - Generic warning message.
     * @param exception - [OPTIONAL] will append the message of the exception.
     * @param entity - [OPTIONAL] will append the toString of the entity.
     * @param sessionUser
     */
    protected void addCdbEntityWarningSystemLog(String warningMessage, Exception exception, CdbEntity entity, UserInfo sessionUser) throws CdbException {
        if (entity != null) {
            warningMessage += ": " + entity.toString();
        }
        if (exception != null) {
            warningMessage += ". Exception - " + exception.getMessage();
        }

        addCdbEntitySystemLog(SystemLogLevel.entityWarning, warningMessage, sessionUser);
    }
    
     /**
     * Allows the controller to quickly add a log entry to system logs with
     * current session user stamp.
     *
     * @param logLevel
     * @param message
     * @param sessionUser
     */
    protected void addCdbEntitySystemLog(SystemLogLevel logLevel, String message, UserInfo sessionUser) throws CdbException {        
        if (sessionUser != null) {
            String username = sessionUser.getUsername();
            message = "User: " + username + " | " + message;
        }
        LogControllerUtility logControllerUtility = LogControllerUtility.getSystemLogInstance();
        logControllerUtility.addSystemLog(logLevel, message);
    }
    
    protected void setPersistenceErrorMessageForList(List<EntityType> entities, String msg) {
        for (EntityType entity : entities) {
            entity.setPersitanceErrorMessage(msg);
        }
    }        

    public List<EntityType> getAllEntities() {
        return getEntityDbFacade().findAll();
    }
    
    /**
     * Search all entities for a given string.
     *
     * @param searchString search string
     * @param caseInsensitive use case insensitive search
     * @return 
     */
    public LinkedList<SearchResult> performEntitySearch(String searchString, boolean caseInsensitive) {
        LinkedList<SearchResult> searchResultList = new LinkedList<>(); 
        if (searchString == null || searchString.isEmpty()) {            
            return searchResultList;
        }
        
        // Start new search        
        Pattern searchPattern;
        if (caseInsensitive) {
            searchPattern = Pattern.compile(Pattern.quote(searchString), Pattern.CASE_INSENSITIVE);
        } else {
            searchPattern = Pattern.compile(Pattern.quote(searchString));
        }
        List<EntityType> allObjectList = getAllEntities();
        for (EntityType entity : allObjectList) {
            try {
                SearchResult searchResult = entity.search(searchPattern);
                if (!searchResult.isEmpty()) {
                    searchResultList.add(searchResult);
                }
            } catch (RuntimeException ex) {
                logger.warn("Could not search entity " + entity.toString() + " (Error: " + ex.toString() + ")");
            }

        }
        
        return searchResultList; 
    }
    
    public PropertyValue preparePropertyTypeValueAdd(EntityType cdbDomainEntity, PropertyType propertyType) {
        return preparePropertyTypeValueAdd(cdbDomainEntity, propertyType, propertyType.getDefaultValue(), null);
    }

    public PropertyValue preparePropertyTypeValueAdd(EntityType cdbDomainEntity,
            PropertyType propertyType, String propertyValueString, String tag) {
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        return preparePropertyTypeValueAdd(cdbDomainEntity, propertyType, propertyValueString, tag, lastModifiedByUser);
    }

    public PropertyValue preparePropertyTypeValueAdd(EntityType cdbEntity,
            PropertyType propertyType, String propertyValueString, String tag,
            UserInfo updatedByUser) {
        Date lastModifiedOnDateTime = new Date();

        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setPropertyType(propertyType);
        propertyValue.setValue(propertyValueString);
        propertyValue.setUnits(propertyType.getDefaultUnits());
        cdbEntity.addPropertyValueToPropertyValueList(propertyValue);
        propertyValue.setEnteredByUser(updatedByUser);
        propertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
        if (tag != null) {
            propertyValue.setTag(tag);
        }

        cdbEntity.resetPropertyValueLists();

        // Get method called by GUI populates metadata
        // Needed for multi-edit or API to also populate metadata
        propertyValue.getPropertyValueMetadataList();

        return propertyValue;
    }

}
