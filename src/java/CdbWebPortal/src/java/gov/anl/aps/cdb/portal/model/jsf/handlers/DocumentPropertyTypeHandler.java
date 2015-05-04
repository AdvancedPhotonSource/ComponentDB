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
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;

/**
 * Document property type handler.
 */
public class DocumentPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Document";

    public DocumentPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.DOCUMENT);
    }

    @Override
    public String getEditActionOncomplete() {
        return "PF('propertyValueDocumentUploadDialogWidget').show()";
    }

    @Override
    public String getEditActionIcon() {
        return "ui-icon-circle-arrow-n";
    }

    @Override
    public String getEditActionBean() {
        return "propertyValueDocumentUploadBean";
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        String targetLink = StorageUtility.getApplicationPropertyValueDocumentPathDirectory(propertyValue.getValue());
        propertyValue.setTargetValue(targetLink);
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        String targetLink = StorageUtility.getApplicationPropertyValueDocumentPathDirectory(propertyValueHistory.getValue());
        propertyValueHistory.setTargetValue(targetLink);
    }
}
