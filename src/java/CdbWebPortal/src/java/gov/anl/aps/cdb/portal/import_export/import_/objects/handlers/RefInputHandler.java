/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author craig
 */
public abstract class RefInputHandler extends SimpleInputHandler {

    protected CdbEntityController controller;
    protected Class paramType;
    protected String domainNameFilter = null;
    protected boolean idOnly = false;

    protected Map<Object, CdbEntity> objectIdMap = new HashMap<>();

    public RefInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType) {
        
        super(columnIndex, propertyName, setterMethod);
        this.controller = controller;
        this.paramType = paramType;
    }

    public RefInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            boolean idOnly) {
        
        this(columnIndex, propertyName, setterMethod, controller, paramType);
        this.idOnly = idOnly;
    }

    public RefInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            String domainNameFilter) {
        
        this(columnIndex, propertyName, setterMethod, controller, paramType);
        this.domainNameFilter = domainNameFilter;
    }

    public RefInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            String domainNameFilter,
            boolean idOnly) {
        
        this(columnIndex, propertyName, setterMethod, controller, paramType, domainNameFilter);
        this.idOnly = idOnly;
    }

    @Override
    public Class getParamType() {
        return paramType;
    }

    protected CdbEntity getObjectWithId(String idString, String message_o) {
        
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
            message_o = "Invalid number format id: " + idString
                    + " for: " + columnNameForIndex(columnIndex);
        }
        
        return objValue;
    }
    
    protected CdbEntity getObjectWithName(String nameString, String message_o) {
        
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
                    + " for: " + columnNameForIndex(columnIndex)
                    + " reason: " + ex.getMessage();
        }
        
        return objValue;
    }

}
