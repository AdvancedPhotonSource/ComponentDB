/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

/**
 *
 * @author craig
 */
public abstract class SingleColumnInputHandler extends InputHandler {

    protected int columnIndex;
    protected String columnName;

    public SingleColumnInputHandler(int columnIndex, String columnName) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }

    public int getColumnIndex() {
        return columnIndex;
    }
    
    public String getColumnName() {
        return columnName;
    }
}
