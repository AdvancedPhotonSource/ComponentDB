/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class BooleanColumnSpec extends ColumnSpec {

    public BooleanColumnSpec(int columnIndex, String header, String propertyName, String entitySetterMethod, boolean required, String description) {
        super(columnIndex, header, propertyName, entitySetterMethod, required, description);
    }

    @Override
    public InputHandler createInputHandlerInstance() {
        return new BooleanInputHandler(
                getColumnIndex(), getPropertyName(), getEntitySetterMethod());
    }
}
