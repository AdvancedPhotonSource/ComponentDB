/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
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
    public void setTargetValue(PropertyValue propertyValue) {
        String targetLink = propertyValue.getValue();
        if (targetLink != null && !targetLink.isEmpty()) {
            targetLink = StorageUtility.getApplicationPropertyValueDocumentPath(targetLink);
            propertyValue.setTargetValue(targetLink);
        }
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        String targetLink = propertyValueHistory.getValue();
        if (targetLink != null && !targetLink.isEmpty()) {
            targetLink = StorageUtility.getApplicationPropertyValueDocumentPath(targetLink);
            propertyValueHistory.setTargetValue(targetLink);
        }
    } 

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        String displayValue = propertyValue.getDisplayValue();
        if (displayValue == null || displayValue.isBlank()) {
            super.setDisplayValue(propertyValue);
        }
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        String displayValue = propertyValueHistory.getDisplayValue();
        if (displayValue == null || displayValue.isBlank()) {
            super.setDisplayValue(propertyValueHistory); 
        }
    }
}
