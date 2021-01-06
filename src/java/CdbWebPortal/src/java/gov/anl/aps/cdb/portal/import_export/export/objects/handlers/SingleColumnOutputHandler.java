/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

/**
 *
 * @author craig
 */
public abstract class SingleColumnOutputHandler extends OutputHandler {
    
    protected String columnName;

    public SingleColumnOutputHandler(String columnName) {
        super();
        this.columnName = columnName;
    }
    
    public SingleColumnOutputHandler(int colIndex, String columnName) {
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
