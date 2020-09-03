/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;

/**
 *
 * @author craig
 */
public class IdRefInputHandler extends RefInputHandler {

    public IdRefInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType) {
        
        super(columnIndex, propertyName, setterMethod, controller, paramType);
    }
    
    protected CdbEntity getObjectWithId(String idString) {
        
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
        }
        
        return objValue;
    }

    @Override
    public ParseInfo parseCellValue(String strValue) {
        
        boolean isValid = true;
        String validString = "";
        CdbEntity objValue = null;
        if (strValue != null && strValue.length() > 0) {
            objValue = getObjectWithId(strValue);
            if (objValue == null) {
                isValid = false;
                validString
                        = "Unable to find object for: "
                        + columnNameForIndex(columnIndex)
                        + " with id: " + strValue;
            }
        }
        return new ParseInfo<>(objValue, isValid, validString);
    }
}
