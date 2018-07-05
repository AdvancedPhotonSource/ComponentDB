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
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.Document;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class DocManagamentDocumentPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "DMS Document";

    private static final String PROPERTY_EDIT_PAGE = "dmsDocumentPropertyValueEditPanel";
    
    public static final String INFO_ACTION_COMMAND = "updateDmsDocumentInfoDialog();";

    private static final Logger logger = Logger.getLogger(DocManagamentDocumentPropertyTypeHandler.class.getName());

    protected DocumentManagamentApi api = null;

    public DocManagamentDocumentPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.INFO_ACTION);
        api = DocManagerPlugin.createNewDocumentManagamentApi();
    }

    @Override
    public String getPropertyEditPage() {
        return PROPERTY_EDIT_PAGE;
    } 
    
    @Override
    public void setInfoActionCommand(PropertyValue propertyValue) {
        propertyValue.setInfoActionCommand(INFO_ACTION_COMMAND);
    }
    
    @Override
    public void setInfoActionCommand(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setInfoActionCommand(INFO_ACTION_COMMAND);
    } 

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setDisplayValue(getDisplayValue(propertyValueHistory.getValue(), false));
    }

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        propertyValue.setDisplayValue(getDisplayValue(propertyValue.getValue(), true));
    }

    private String getDisplayValue(String value, Boolean showError) {
        if (value.equals("")) {
            return value;
        }
        
        try {
            Document document = api.getDocumentById(Integer.parseInt(value)); 
            return document.getDocNumCode();
        } catch (CdbException ex) {
            logger.error(ex);
            if (showError) {
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        } catch (NumberFormatException ex) {
           logger.error(ex);
           if (showError) {
               SessionUtility.addErrorMessage("Corrupted property value: " + value, "Try updating the property value and see if the issue persists.");
           }
        }

        return value;
    }

}
