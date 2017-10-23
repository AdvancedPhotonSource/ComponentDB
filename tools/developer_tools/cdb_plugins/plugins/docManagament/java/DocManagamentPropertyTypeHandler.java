/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;

/**
 *
 * @author djarosz
 */
public class DocManagamentPropertyTypeHandler extends AbstractPropertyTypeHandler {
    
    public static final String HANDLER_NAME = "Document Managament";
    public static final String INFO_ACTION_COMMAND = "updateDocManagamentInfoDialog();";
    
    public DocManagamentPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.INFO_ACTION);
    }
    
    @Override
    public void setInfoActionCommand(PropertyValue propertyValue) {
        propertyValue.setInfoActionCommand(INFO_ACTION_COMMAND);
    }
    
    @Override
    public void setInfoActionCommand(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setInfoActionCommand(INFO_ACTION_COMMAND);
    }

    
}
