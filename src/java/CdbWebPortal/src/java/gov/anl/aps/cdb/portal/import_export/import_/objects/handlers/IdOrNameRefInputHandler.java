/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;

/**
 *
 * @author craig
 */
public class IdOrNameRefInputHandler extends RefInputHandler {

    public IdOrNameRefInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            String domainNameFilter) {
        
        super(columnIndex, propertyName, setterMethod, controller, paramType, domainNameFilter);
    }

    public IdOrNameRefInputHandler(
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

        CdbEntity objValue = null;
        if ((strValue != null) && (!strValue.isEmpty())) {

            if (strValue.charAt(0) == '#') {
                // process cell as name if first char is #
                
                if (idOnly) {
                    String msg = "Lookup by name not enabled for column: "
                            + columnNameForIndex(columnIndex);
                    return new ParseInfo<>(objValue, false, msg);
                }
                
                if (strValue.length() < 2) {
                    String msg = "Empty name string for: "
                            + columnNameForIndex(columnIndex);
                    return new ParseInfo<>(objValue, false, msg);
                }
                
                String name = strValue.substring(1);
                String info_o = null;
                objValue = getObjectWithName(name, info_o);
                if (objValue == null) {
                    String msg;
                    if (info_o != null) {
                        msg = info_o;
                    } else {
                        msg = "Unable to find object for: "
                                + columnNameForIndex(columnIndex)
                                + " with name: " + name;
                    }
                    return new ParseInfo<>(objValue, false, msg);
                }
                
            } else {
                // process cell as numeric
                
                if (strValue != null && strValue.length() > 0) {
                    String info_o = null;
                    objValue = getObjectWithId(strValue, info_o);
                    if (objValue == null) {
                        String msg;
                        if (info_o != null) {
                            msg = info_o;
                        } else {
                            msg = "Unable to find object for: "
                                    + columnNameForIndex(columnIndex)
                                    + " with id: " + strValue;
                        }
                        return new ParseInfo<>(objValue, false, msg);
                    }
                }
                return new ParseInfo<>(objValue, true, "");
            }
        }

        return new ParseInfo<>(objValue, true, "");
    }
}
