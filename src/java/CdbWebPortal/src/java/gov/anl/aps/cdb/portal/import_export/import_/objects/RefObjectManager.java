/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author craig
 */
public class RefObjectManager {
    
    private CdbEntityController controller;
    private Map<Object, CdbEntity> objectIdMap = new HashMap<>();
    
    public RefObjectManager(
            CdbEntityController controller, 
            String domainNameFilter) {
        
        this.controller = controller;
    }

    public CdbEntity getObjectWithId(String idString) throws CdbException {
        
        CdbEntity objValue = null;
        try {
            int id = Integer.valueOf(idString.trim());
            if (objectIdMap.containsKey(id)) {
                objValue = objectIdMap.get(id);
            } else {
                objValue = controller.findById(id);
                if (objValue != null) {
                    if (objValue.getIsItemDeleted()) {
                        objValue = null;
                        throw new CdbException("Item with id " + id + " is deleted");
                    }
                    objectIdMap.put(objValue.getId(), objValue);
                }
            }
        } catch (NumberFormatException ex) {
            throw new CdbException("Invalid number format id: " + idString);
        }
        
        return objValue;
    }
    
    public CdbEntity getCacheObject(CdbEntity entity) {
        CdbEntity result = null;
        if (entity != null) {
            // check cache for object so different references use same instance
            int id = (Integer) entity.getId();

            if (objectIdMap.containsKey(id)) {
                result = objectIdMap.get(id);
            } else {
                // add this instance to cache
                objectIdMap.put(id, entity);
                result = entity;
            }
        }
        return result;
    }
    
    public CdbEntity getObjectWithName(String nameString, String domainNameFilter) throws CdbException {
        
        CdbEntity objValue = null;
                
        objValue = controller.findUniqueByName(nameString, domainNameFilter);
        if (objValue != null) {
            if (objValue.getIsItemDeleted()) {
                objValue = null;
                throw new CdbException("Item with name " + nameString + " is deleted");
            }
            return getCacheObject(objValue);
        }
        
        return objValue;
    }

    public CdbEntity getObjectWithAttributes(Map<String,String> attributeMap) throws CdbException {
        
        CdbEntity objValue = null;
                
        objValue = controller.findUniqueWithAttributes(attributeMap);
        if (objValue != null) {
            if (objValue.getIsItemDeleted()) {
                objValue = null;
                throw new CdbException("Item with attributes " + attributeMap.toString() + " is deleted");
            }
            return getCacheObject(objValue);
        }
        
        return objValue;
    }

    public CdbEntity getObjectWithPath(String pathString) throws CdbException {
        
        CdbEntity objValue = null;
                
        objValue = controller.findUniqueByPath(pathString);
        if (objValue != null) {
            if (objValue.getIsItemDeleted()) {
                objValue = null;
                throw new CdbException("Item with path " + pathString + " is deleted");
            }
            return getCacheObject(objValue);
        }
        
        return objValue;
    }
    
}
