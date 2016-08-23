/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/portal/model/jsf/handlers/BooleanPropertyTypeHandler.java $
 *   $Date: 2015-05-04 13:50:25 -0500 (Mon, 04 May 2015) $
 *   $Revision: 618 $
 *   $Author: sveseli $
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
