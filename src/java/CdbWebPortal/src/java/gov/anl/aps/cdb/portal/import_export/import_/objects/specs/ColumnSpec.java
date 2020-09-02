/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;

/**
 *
 * @author craig
 */
public abstract class ColumnSpec {

    private int columnIndex;
    private String header;
    private String propertyName;
    private String entitySetterMethod;
    private boolean required;
    private String description;

    public ColumnSpec(
            int columnIndex,
            String header,
            String propertyName,
            String entitySetterMethod,
            boolean required,
            String description) {

        this.columnIndex = columnIndex;
        this.header = header;
        this.propertyName = propertyName;
        this.entitySetterMethod = entitySetterMethod;
        this.required = required;
        this.description = description;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getHeader() {
        return header;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getEntitySetterMethod() {
        return entitySetterMethod;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDescription() {
        return description;
    }

    public abstract InputHandler createInputHandlerInstance();

}
