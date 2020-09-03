/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

/**
 * Supports the definition of a hierarchy using the indent levels in a range of
 * columns in the import spreadsheet. Each row is expected to contain a single
 * value in the range of columns. The level of hierarchy is determined by which
 * column of the range the value appears in. A value in the first column of the
 * range is at the top of the hierarchy. The "parent" of a row in the
 * spreadsheet is the last row that contains a value at the previous indent
 * level. E.g., the parent of an item whose name appears in the third column of
 * the range is the last item whose name was in the second column, etc.
 *
 * @author craig
 */
public class HierarchyHandler extends ColumnRangeInputHandler {

    protected int maxLength = 0;
    private String valueKey;
    private String indentKey;

    public HierarchyHandler(
            int firstIndex,
            int lastIndex,
            int maxLength,
            String valueKey,
            String indentKey) {
        super(firstIndex, lastIndex);
        this.maxLength = maxLength;
        this.valueKey = valueKey;
        this.indentKey = indentKey;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public ValidInfo handleInput(
            Row row,
            Map<Integer, String> cellValueMap,
            Map<String, Object> rowMap) {

        boolean isValid = true;
        String validString = "";

        int currentIndentLevel = 1;
        int itemIndentLevel = 0;
        String itemName = null;
        for (int colIndex = getFirstColumnIndex();
                colIndex <= getLastColumnIndex();
                colIndex++) {

            String parsedValue = cellValueMap.get(colIndex);
            if ((parsedValue != null) && (!parsedValue.isEmpty())) {
                if (itemName != null) {
                    // invalid, we have a value in 2 columns
                    isValid = false;
                    validString = "Found value in multiple 'Level' columns, only one allowed.";
                } else {
                    itemName = parsedValue;
                    itemIndentLevel = currentIndentLevel;
                }
            }

            currentIndentLevel = currentIndentLevel + 1;
        }

        if (itemName != null) {

            // check column length is valid
            if ((getMaxLength() > 0) && (itemName.length() > getMaxLength())) {
                isValid = false;
                validString = validString +
                        "Invalid name, length exceeds " + getMaxLength() + ".";
            }

            // set item info
            rowMap.put(valueKey, itemName);
            rowMap.put(indentKey, itemIndentLevel);
        }

        return new ValidInfo(isValid, validString);
    }
}
