/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class IdRefListInputHandler extends IdRefInputHandler {
    
    private static final String SEPARATOR = ",";

    public IdRefListInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType) {
        
        super(columnIndex, propertyName, setterMethod, controller, paramType);
    }

    /**
     * Tokenizes the cell value using comma separator, looks up each token as
     * a CDB id of specified type, adds corresponding items to list and
     * returns it.
     * 
     * @param strValue
     * @return 
     */
    @Override
    public ParseInfo parseCellValue(String strValue) {
        
        boolean isValid = true;
        String validString = "";
        List<CdbEntity> objValueList = new ArrayList<>();
        if (strValue != null && strValue.length() > 0) {

            String[] idTokens = strValue.split(SEPARATOR);
            for (String idToken : idTokens) {
                
                CdbEntity objValue = super.getObjectWithId(idToken);
                
                if (objValue == null) {
                    isValid = false;
                    validString
                            = "Unable to find object for: "
                            + columnNameForIndex(columnIndex)
                            + " with id: " + idToken;
                    return new ParseInfo<>(objValueList, isValid, validString);
                } else {
                    objValueList.add(objValue);
                }
            }
        }
        
        return new ParseInfo<>(objValueList, isValid, validString);                    
    }
}
