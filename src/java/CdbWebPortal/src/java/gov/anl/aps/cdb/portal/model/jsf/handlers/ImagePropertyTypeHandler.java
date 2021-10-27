/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

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
