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
    private String domainNameFilter = null;
    private Map<Object, CdbEntity> objectIdMap = new HashMap<>();
    
    public RefObjectManager(
            CdbEntityController controller, 
            String domainNameFilter) {
        
        this.controller = controller;
        this.domainNameFilter = domainNameFilter;
    }

    public CdbEntity getObjectWithId(String idString, String message_o) {
        
        CdbEntity objValue = null;
        try {
            int id = Integer.valueOf(idString.trim());
            if (objectIdMap.containsKey(id)) {
                objValue = objectIdMap.get(id);
            } else {
                objValue = controller.findById(id);
                if (objValue != null) {
                    objectIdMap.put(objValue.getId(), objValue);
                }
            }
        } catch (NumberFormatException ex) {
            message_o = "Invalid number format id: " + idString;
        }
        
        return objValue;
    }
    
    public CdbEntity getObjectWithName(String nameString, String message_o) {
        
        CdbEntity objValue = null;
                
        try {
            objValue = controller.findUniqueByName(nameString, domainNameFilter);
            if (objValue != null) {
                // check cache for object so different references use same instance
                int id = (Integer) objValue.getId();
                if (objectIdMap.containsKey(id)) {
                    objValue = objectIdMap.get(id);
                } else {
                    // add this instance to cache
                    objectIdMap.put(id, objValue);
                }
            }
        } catch (CdbException ex) {
            message_o = "Exception searching for object with name: " + nameString
                    + " reason: " + ex.getMessage();
        }
        
        return objValue;
    }

}
