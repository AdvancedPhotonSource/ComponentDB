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
public class BooleanInputHandler extends SimpleInputHandler {

    public BooleanInputHandler(int columnIndex, String columnName, String propertyName, String setterMethod) {
        super(columnIndex, columnName, propertyName, setterMethod);
    }

    @Override
    public ParseInfo parseCellValue(String stringValue) {

        Boolean parsedValue = null;
        boolean isValid = true;
        String validString = "";

        if (stringValue.length() == 0) {
            parsedValue = null;
            isValid = true;
            validString = "";
        } else {
            if (stringValue.equalsIgnoreCase("true") || stringValue.equals("1") || stringValue.equalsIgnoreCase("yes")) {
                parsedValue = true;
            } else if (stringValue.equalsIgnoreCase("false") || stringValue.equals("0") || stringValue.equalsIgnoreCase("no")) {
                parsedValue = false;
            } else {
                parsedValue = null;
                isValid = false;
                validString = "unexpected boolean value: " + stringValue
                        + " for column: " + getColumnName();
            }
        }

        return new ParseInfo<>(parsedValue, isValid, validString);
    }

    @Override
    public Class getParamType() {
        return Boolean.class;
    }
}
