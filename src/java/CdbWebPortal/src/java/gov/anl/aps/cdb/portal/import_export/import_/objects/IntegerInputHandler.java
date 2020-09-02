/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class IntegerInputHandler extends SimpleInputHandler {

    public IntegerInputHandler(int columnIndex, String propertyName, String setterMethod) {
        super(columnIndex, propertyName, setterMethod);
    }

    @Override
    public ParseInfo parseCellValue(String stringValue) {

        Integer parsedValue = null;
        boolean isValid = true;
        String validString = "";

        if (stringValue.length() == 0) {
            parsedValue = null;
            isValid = true;
            validString = "";
        } else {
            try {
                parsedValue = Integer.valueOf(stringValue);
            } catch (NumberFormatException ex) {
                isValid = false;
                validString = "invalid integer format: " + stringValue;
            }
        }

        return new ParseInfo<>(parsedValue, isValid, validString);
    }

    @Override
    public Class getParamType() {
        return Integer.class;
    }
}
