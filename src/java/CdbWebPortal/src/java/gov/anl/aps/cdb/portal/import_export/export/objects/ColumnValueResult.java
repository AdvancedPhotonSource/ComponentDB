/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;

/**
 *
 * @author craig
 */
public class ColumnValueResult {
    
    private final ValidInfo validInfo;
    private final String columnValue;
    
    public ColumnValueResult(ValidInfo validInfo, String columnValue) {
        this.validInfo = validInfo;
        this.columnValue = columnValue;
    }

    public ValidInfo getValidInfo() {
        return validInfo;
    }

    public String getColumnValue() {
        return columnValue;
    }
    
}
