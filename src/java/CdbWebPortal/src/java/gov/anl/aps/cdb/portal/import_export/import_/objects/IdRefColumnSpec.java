/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;

/**
 *
 * @author craig
 */
public class IdRefColumnSpec extends ColumnSpec {

    private CdbEntityController controller;
    private Class paramType;

    public IdRefColumnSpec(int columnIndex, String header, String propertyName, String entitySetterMethod, boolean required, String description, CdbEntityController controller, Class paramType) {
        super(columnIndex, header, propertyName, entitySetterMethod, required, description);
        this.controller = controller;
        this.paramType = paramType;
    }

    @Override
    public InputHandler createInputHandlerInstance() {
        return new IdRefInputHandler(
                getColumnIndex(),
                getPropertyName(),
                getEntitySetterMethod(),
                controller,
                paramType);
    }
}
