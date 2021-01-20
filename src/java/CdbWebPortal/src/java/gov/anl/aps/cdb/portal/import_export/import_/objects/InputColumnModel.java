/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class InputColumnModel {

    protected int columnIndex;
    protected String name;
    protected String description = null;
    protected boolean required = false;
    protected boolean updateOnly = false;

    public InputColumnModel(
            int columnIndex,
            String name,
            boolean required,
            String description) {

        this.columnIndex = columnIndex;
        this.name = name;
        this.description = description;
        this.required = required;
    }

    public InputColumnModel(
            int columnIndex,
            String name,
            boolean required,
            String description,
            boolean updateOnly) {

        this(columnIndex, name, required, description);
        this.updateOnly = updateOnly;
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

    public boolean isRequired() {
        return this.required;
    }
    
    public boolean isUpdateOnly() {
        return this.updateOnly;
    }

}
