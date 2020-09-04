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
public class IdOrNameRefListInputHandler extends RefInputHandler {

    private static final String SEPARATOR = ",";

    public IdOrNameRefListInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            String domainNameFilter) {
        
        super(columnIndex, propertyName, setterMethod, controller, paramType, domainNameFilter);
    }

    public IdOrNameRefListInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            String domainNameFilter,
            boolean idOnly) {
        
        super(columnIndex, propertyName, setterMethod, controller, paramType, domainNameFilter, idOnly);
    }

    @Override
    public ParseInfo parseCellValue(String strValue) {

        List<CdbEntity> objValueList = new ArrayList<>();
        
        if (strValue != null && strValue.length() > 0) {
            
            if (strValue.charAt(0) == '#') {
                // process cell as list of names if first char is #
                
                if (idOnly) {
                    String msg = "Lookup by name not enabled for column: "
                            + columnNameForIndex(columnIndex);
                    return new ParseInfo<>(objValueList, false, msg);
                }
                
                String nameListString = strValue.substring(1);
                if (!nameListString.isEmpty()) {
                    String[] nameTokens = strValue.split(SEPARATOR);
                    for (String nameToken : nameTokens) {
                        String info_o = null;
                        CdbEntity objValue = super.getObjectWithName(nameToken, info_o);                        
                        if (objValue == null) {
                            String msg;
                            if (info_o != null) {
                                msg = info_o;
                            } else {
                                msg = "Unable to find object for: "
                                    + columnNameForIndex(columnIndex)
                                    + " with name: " + nameToken;
                            }
                            return new ParseInfo<>(objValueList, false, msg);
                        } else {
                            objValueList.add(objValue);
                        }
                    }
                }
                
            } else {
                // process as list of ids
                
                String[] idTokens = strValue.split(SEPARATOR);
                for (String idToken : idTokens) {
                    String info_o = null;
                    CdbEntity objValue = super.getObjectWithId(idToken, info_o);
                    if (objValue == null) {
                        String msg;
                        if (info_o != null) {
                            msg = info_o;
                        } else {
                            msg = "Unable to find object for: "
                                    + columnNameForIndex(columnIndex)
                                    + " with id: " + idToken;
                        }
                        return new ParseInfo<>(objValueList, false, msg);
                    } else {
                        objValueList.add(objValue);
                    }
                }
            }
        }
        
        return new ParseInfo<>(objValueList, true, "");                    
    }
}
