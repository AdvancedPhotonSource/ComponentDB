/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
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
