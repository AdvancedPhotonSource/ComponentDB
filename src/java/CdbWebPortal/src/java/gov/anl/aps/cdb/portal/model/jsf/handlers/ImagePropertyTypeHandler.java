/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/portal/model/jsf/handlers/ImagePropertyTypeHandler.java $
 *   $Date: 2015-05-06 09:46:53 -0500 (Wed, 06 May 2015) $
 *   $Revision: 628 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;

/**
 * Image property type handler.
 */
public class ImagePropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Image";

    public ImagePropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.IMAGE);
    }

    @Override
    public String getEditActionOncomplete() {
        return "PF('propertyValueImageUploadDialogWidget').show()";
    }

    @Override
    public String getEditActionIcon() {
        return "ui-icon-circle-arrow-n";
    }

    @Override
    public String getEditActionBean() {
        return "propertyValueImageUploadBean";
    } 
}
