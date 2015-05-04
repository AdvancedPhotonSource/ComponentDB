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
