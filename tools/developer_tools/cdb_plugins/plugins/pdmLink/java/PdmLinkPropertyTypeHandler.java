/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.pdmLink;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;

/**
 * PDMLink property type handler.
 *
 * This handler uses CDB web service.
 */
public class PdmLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "PDMLink";
    
    private static final String INFO_ACTION_COMMAND = "updatePdmLinkDrawingPropertyInfoDialog();";
    
    private static final String PROPERTY_EDIT_PAGE = "pdmLinkPropertyValueEditPanel";

    public PdmLinkPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.INFO_ACTION);
    }
    
    @Override
    public void setInfoActionCommand(PropertyValue propertyValue){
        propertyValue.setInfoActionCommand(INFO_ACTION_COMMAND);
    }
    
    @Override
    public void setInfoActionCommand(PropertyValueHistory propertyValueHistory){
        propertyValueHistory.setInfoActionCommand(INFO_ACTION_COMMAND);
    }
    
    @Override
    public String getPropertyEditPage() {
        return PROPERTY_EDIT_PAGE;
    }
    
}
