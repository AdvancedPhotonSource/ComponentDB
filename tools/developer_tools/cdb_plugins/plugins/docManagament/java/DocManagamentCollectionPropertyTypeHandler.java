/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class DocManagamentCollectionPropertyTypeHandler extends AbstractPropertyTypeHandler {
    
    public static final String HANDLER_NAME = "DMS Collection";
    public static final String INFO_ACTION_COMMAND = "updateDocManagamentInfoDialog();";
    
    private static final Logger logger = Logger.getLogger(DocManagamentCollectionPropertyTypeHandler.class.getName());    
    
    public DocManagamentCollectionPropertyTypeHandler() {                
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
