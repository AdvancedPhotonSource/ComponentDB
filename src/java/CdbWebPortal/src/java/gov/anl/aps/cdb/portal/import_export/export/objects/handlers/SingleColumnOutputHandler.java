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
    private String description;

    public SingleColumnOutputHandler(String columnName, String description) {
        this.columnName = columnName;
        this.description = description;
    }
    
    public String getColumnName() {
        return columnName;
    }

    public String getDescription() {
        return description;
    }
}
