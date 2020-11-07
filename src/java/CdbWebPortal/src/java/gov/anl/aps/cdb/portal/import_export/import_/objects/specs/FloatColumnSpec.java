/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.FloatInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;

/**
 *
 * @author craig
 */
public class FloatColumnSpec extends ColumnSpec {

    public FloatColumnSpec(String header, String propertyName, String entitySetterMethod, boolean required, String description) {
        super(header, propertyName, entitySetterMethod, required, description);
    }

    @Override
    public InputHandler getInputHandler(int colIndex) {
        return new FloatInputHandler(
                colIndex, getHeader(), getPropertyName(), getEntitySetterMethod());
    }
}

