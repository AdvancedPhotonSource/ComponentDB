/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;

/**
 * Boolean property type handler.
 */
public class BooleanPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Boolean";

    public BooleanPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.BOOLEAN);
    }

}
