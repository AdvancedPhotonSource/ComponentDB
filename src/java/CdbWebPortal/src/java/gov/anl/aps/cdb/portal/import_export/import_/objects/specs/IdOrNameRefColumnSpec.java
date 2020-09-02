/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.IdOrNameRefInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;

/**
 *
 * @author craig
 */
public class IdOrNameRefColumnSpec extends ColumnSpec {

    private CdbEntityController controller;
    private Class paramType;
    private String domainNameFilter = null;

    public IdOrNameRefColumnSpec(int columnIndex, String header, String propertyName, String entitySetterMethod, boolean required, String description, CdbEntityController controller, Class paramType, String domainNameFilter) {
        super(columnIndex, header, propertyName, entitySetterMethod, required, description);
        this.controller = controller;
        this.paramType = paramType;
        this.domainNameFilter = domainNameFilter;
    }

    @Override
    public InputHandler createInputHandlerInstance() {
        return new IdOrNameRefInputHandler(
                getColumnIndex(),
                getPropertyName(),
                getEntitySetterMethod(),
                controller,
                paramType,
                domainNameFilter);
    }
}
