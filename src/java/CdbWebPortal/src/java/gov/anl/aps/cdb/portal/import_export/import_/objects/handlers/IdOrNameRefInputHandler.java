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

    String domainNameFilter = null;

    public IdOrNameRefInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            String domainNameFilter) {
        super(columnIndex, propertyName, setterMethod, controller, paramType);
        this.domainNameFilter = domainNameFilter;
    }

    @Override
    public ParseInfo parseCellValue(String strValue) {

        CdbEntity objValue = null;
        if ((strValue != null) && (!strValue.isEmpty())) {

            if (strValue.charAt(0) == '#') {
                // process cell as name if first char is #
                if (strValue.length() < 2) {
                    String msg
                            = "Invalid name format for: "
                            + columnNameForIndex(columnIndex)
                            + " name: " + strValue;
                    return new ParseInfo<>(objValue, false, msg);
                }
                String name = strValue.substring(1);
                try {
                    objValue = controller.findUniqueByName(name, domainNameFilter);
                    if (objValue == null) {
                        String msg
                                = "Unable to find object for: "
                                + columnNameForIndex(columnIndex)
                                + " with name: " + name;
                        return new ParseInfo<>(objValue, false, msg);
                    } else {
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
                    String msg = "Exception searching for object with name: " + name
                            + " for: " + columnNameForIndex(columnIndex)
                            + " reason: " + ex.getMessage();
                    return new ParseInfo<>(objValue, false, msg);
                }

            } else {
                // process cell as numeric
                int id = 0;
                try {
                    id = Integer.parseInt(strValue);
                    // check cache for object so different references use same instance
                    if (objectIdMap.containsKey(id)) {
                        objValue = objectIdMap.get(id);
                    } else {
                        objValue = controller.findById(id);
                        if (objValue == null) {
                            String msg
                                    = "Unable to find object for: "
                                    + columnNameForIndex(columnIndex)
                                    + " with id: " + id;
                            return new ParseInfo<>(objValue, false, msg);
                        } else {
                            // add to cache
                            objectIdMap.put(id, objValue);
                        }
                    }
                } catch (NumberFormatException ex) {
                    String msg = "Invalid number format"
                            + " for: " + columnNameForIndex(columnIndex)
                            + " id: " + strValue;
                    return new ParseInfo<>(objValue, false, msg);
                }
            }
        }

        return new ParseInfo<>(objValue, true, "");
    }
}
