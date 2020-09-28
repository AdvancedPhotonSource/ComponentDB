/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.IntegerInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;

/**
 *
 * @author craig
 */
public class IntegerColumnSpec extends ColumnSpec {

    public IntegerColumnSpec(int columnIndex, String header, String propertyName, String entitySetterMethod, boolean required, String description) {
        super(columnIndex, header, propertyName, entitySetterMethod, required, description);
    }

    @Override
    public InputHandler createInputHandlerInstance() {
        return new IntegerInputHandler(
                getColumnIndex(), getHeader(), getPropertyName(), getEntitySetterMethod());
    }
}
