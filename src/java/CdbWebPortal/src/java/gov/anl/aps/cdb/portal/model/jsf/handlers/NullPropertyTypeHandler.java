/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

/**
 * Null property type handler, used as default handler.
 */
public class NullPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Null";

    public NullPropertyTypeHandler() {
        super(HANDLER_NAME);
    }

}
