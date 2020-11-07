/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;

/**
 *
 * @author craig
 */
public class FloatInputHandler extends SimpleInputHandler {

    public FloatInputHandler(int columnIndex, String columnName, String propertyName, String setterMethod) {
        super(columnIndex, columnName, propertyName, setterMethod);
    }

    @Override
    public ParseInfo parseCellValue(String stringValue) {

        Float parsedValue = null;
        boolean isValid = true;
        String validString = "";

        if (stringValue.length() == 0) {
            parsedValue = null;
            isValid = true;
            validString = "";
        } else {
            try {
                parsedValue = Float.valueOf(stringValue);
            } catch (NumberFormatException ex) {
                isValid = false;
                validString = "invalid float format: " + stringValue;
            }
        }

        return new ParseInfo<>(parsedValue, isValid, validString);
    }

    @Override
    public Class getParamType() {
        return Float.class;
    }
}
