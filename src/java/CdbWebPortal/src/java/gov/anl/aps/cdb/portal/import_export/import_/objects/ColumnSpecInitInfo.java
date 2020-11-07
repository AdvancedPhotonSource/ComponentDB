/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class ColumnSpecInitInfo {

    private ValidInfo validInfo;
    private int numColumns = 0;
    
    public ColumnSpecInitInfo(ValidInfo validInfo, int numColumns) {
        this.validInfo = validInfo;
        this.numColumns = numColumns;
    }
    
    public ValidInfo getValidInfo() {
        return validInfo;
    }

    public int getNumColumns() {
        return numColumns;
    }    
}
