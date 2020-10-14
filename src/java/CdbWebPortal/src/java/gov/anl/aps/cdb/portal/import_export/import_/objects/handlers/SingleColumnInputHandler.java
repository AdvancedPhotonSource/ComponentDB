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

    protected String columnName;

    public SingleColumnInputHandler(String columnName) {
        super();
        this.columnName = columnName;
    }
    
    public SingleColumnInputHandler(int colIndex, String columnName) {
        super(colIndex);
        this.columnName = columnName;
    }

    public int getColumnIndex() {
        return getFirstColumnIndex();
    }
    
    public String getColumnName() {
        return columnName;
    }
}
