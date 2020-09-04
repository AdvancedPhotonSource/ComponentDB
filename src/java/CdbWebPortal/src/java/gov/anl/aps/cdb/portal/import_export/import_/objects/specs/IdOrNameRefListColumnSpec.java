/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.IdOrNameRefListInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;

/**
 *
 * @author craig
 */
public class IdOrNameRefListColumnSpec extends IdOrNameRefColumnSpec {

    public IdOrNameRefListColumnSpec(int columnIndex, String header, String propertyName, String entitySetterMethod, boolean required, String description, CdbEntityController controller, Class paramType, String domainNameFilter) {
        super(columnIndex, header, propertyName, entitySetterMethod, required, description, controller, paramType, domainNameFilter);
    }

    @Override
    public InputHandler createInputHandlerInstance() {
        return new IdOrNameRefListInputHandler(
                getColumnIndex(),
                getPropertyName(),
                getEntitySetterMethod(),
                controller,
                paramType,
                domainNameFilter);
    }
}
