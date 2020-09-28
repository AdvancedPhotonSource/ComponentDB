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
public class StringInputHandler extends SimpleInputHandler {

    protected int maxLength = 0;

    public StringInputHandler(
            int columnIndex,
            String columnName,
            String propertyName,
            String setterMethod,
            int maxLength) {
        super(columnIndex, columnName, propertyName, setterMethod);
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public ParseInfo parseCellValue(String value) {

        boolean isValid = true;
        String validString = "";

        if ((getMaxLength() > 0) && (value.length() > getMaxLength())) {
            isValid = false;
            validString = 
                    "Value length exceeds " + getMaxLength()
                    + " characters for column " + getColumnName();
        }
        return new ParseInfo<>(value, isValid, validString);
    }

    @Override
    public Class getParamType() {
        return String.class;
    }
}
