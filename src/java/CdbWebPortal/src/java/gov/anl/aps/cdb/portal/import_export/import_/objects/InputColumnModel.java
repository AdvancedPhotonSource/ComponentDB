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
    protected boolean requiredForCreate = false;
    protected boolean requiredForUpdate = false;
    protected boolean updateOnly = false;

    public InputColumnModel(
            int columnIndex,
            String name,
            boolean required,
            String description) {

        this.columnIndex = columnIndex;
        this.name = name;
        this.description = description;
        this.requiredForCreate = required;
    }

    public InputColumnModel(
            int columnIndex,
            String name,
            boolean requiredForCreate,
            String description,
            boolean updateOnly,
            boolean requiredForUpdate) {

        this(columnIndex, name, requiredForCreate, description);
        this.updateOnly = updateOnly;
        this.requiredForUpdate = requiredForUpdate;
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

    public boolean isRequiredForCreate() {
        return this.requiredForCreate;
    }
    
    public boolean isRequiredForUpdate() {
        return this.requiredForUpdate;
    }
    
    public boolean isUpdateOnly() {
        return this.updateOnly;
    }

}
