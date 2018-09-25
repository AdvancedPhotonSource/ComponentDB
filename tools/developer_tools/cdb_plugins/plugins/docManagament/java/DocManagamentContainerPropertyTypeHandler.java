/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.api.DocumentManagamentApi;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.Container;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class DocManagamentContainerPropertyTypeHandler extends AbstractPropertyTypeHandler {
    
    public static final String HANDLER_NAME = "DMS Container";
    public static final String INFO_ACTION_COMMAND = "updateDocManagamentInfoDialog();";
    
    private static final String PROPERTY_EDIT_PAGE = "dmsContainerPropertyValueEditPanel";
    
    private static final Logger logger = Logger.getLogger(DocManagamentContainerPropertyTypeHandler.class.getName());
    
    protected DocumentManagamentApi api = null;
    
    public DocManagamentContainerPropertyTypeHandler() {                
        super(HANDLER_NAME, DisplayType.HTTP_LINK);  
        api = DocManagerPlugin.createNewDocumentManagamentApi();
    }
    /*
    @Override
    public void setInfoActionCommand(PropertyValue propertyValue) {
        propertyValue.setInfoActionCommand(INFO_ACTION_COMMAND);
    }
    
    @Override
    public void setInfoActionCommand(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setInfoActionCommand(INFO_ACTION_COMMAND);
    } 
    */

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setDisplayValue(getDisplayValue(propertyValueHistory.getValue(), false));
    }

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        propertyValue.setDisplayValue(getDisplayValue(propertyValue.getValue(), true));
    } 

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        propertyValue.setTargetValue(DocManagerPlugin.generateContainerUrl(propertyValue.getValue()));
    } 

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setTargetValue(DocManagerPlugin.generateContainerUrl(propertyValueHistory.getValue()));
    }
    
    private String getDisplayValue(String value, Boolean showError) {
         if (value.equals("")) {
            return value;
        }
        try {
            Container container = api.getContainerById(Integer.valueOf(value));
            return container.getDossierName(); 
        } catch (CdbException ex) {
            logger.error(ex);
            if (showError) {
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
        return value;
    }
    
    @Override
    public String getPropertyEditPage() {
        return PROPERTY_EDIT_PAGE;
    }

    
}
