/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author craig
 */
public abstract class RefInputHandler extends SimpleInputHandler {

    protected CdbEntityController controller;
    protected Class paramType;

    protected Map<Object, CdbEntity> objectIdMap = new HashMap<>();

    public RefInputHandler(
            int columnIndex,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType) {
        super(columnIndex, propertyName, setterMethod);
        this.controller = controller;
        this.paramType = paramType;
    }

    @Override
    public Class getParamType() {
        return paramType;
    }
}
