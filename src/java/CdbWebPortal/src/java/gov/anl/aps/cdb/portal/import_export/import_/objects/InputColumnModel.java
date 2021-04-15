/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;

/**
 *
 * @author craig
 */
public class InputColumnModel {

    private ColumnSpec columnSpec;
    protected int columnIndex;
    protected String name;
    protected String description = null;

    public InputColumnModel(
            int columnIndex,
            String name,
            String description) {

        this.columnIndex = columnIndex;
        this.name = name;
        this.description = description;
    }

    public void setColumnSpec(ColumnSpec columnSpec) {
        this.columnSpec = columnSpec;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    public boolean isUsedForMode(ImportMode mode) {
        return columnSpec.isUsedForMode(mode);
    }
    
    public boolean isRequiredForMode(ImportMode mode) {
        return columnSpec.isRequiredForMode(mode);
    }

}
