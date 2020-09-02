/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.IdRefListInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;

/**
 *
 * @author craig
 */
public class IdRefListColumnSpec extends IdRefColumnSpec {

    public IdRefListColumnSpec(int columnIndex, String header, String propertyName, String entitySetterMethod, boolean required, String description, CdbEntityController controller, Class paramType) {
        super(columnIndex, header, propertyName, entitySetterMethod, required, description, controller, paramType);
    }

    @Override
    public InputHandler createInputHandlerInstance() {
        return new IdRefListInputHandler(
                getColumnIndex(),
                getPropertyName(),
                getEntitySetterMethod(),
                controller,
                paramType);
    }
}
