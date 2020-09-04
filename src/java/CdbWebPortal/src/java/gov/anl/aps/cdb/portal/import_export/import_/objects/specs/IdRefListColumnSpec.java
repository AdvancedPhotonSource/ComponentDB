/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.RefInputHandler;

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
        return new RefInputHandler(
                getColumnIndex(),
                getPropertyName(),
                getEntitySetterMethod(),
                controller,
                paramType,
                true,
                false);
    }
}
