/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.plugins.support.traveler.api.TravelerApi;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Traveler;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class TravelerInstancePropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Traveler Instance";

    private static final String PROPERTY_EDIT_PAGE = "travelerInstancePropertyValueEditPanel";
    
    private static final String INFO_ACTION_COMMAND = "updateTravelerInstancePropertyValueInfoDialog();";

    private static final Logger logger = Logger.getLogger(TravelerInstancePropertyTypeHandler.class.getName());

    private TravelerApi travelerApi;

    private final String TRAVELER_WEB_APP_URL = TravelerInstancePluginManager.getTravelerWebApplicationUrl();
    private final String TRAVELER_WEB_APP_TRAVELER_PATH = TravelerInstancePluginManager.getTravelerWebApplicationTravelerPath();

    public TravelerInstancePropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.HTTP_LINK);
        String webServiceUrl = TravelerInstancePluginManager.getTravelerWebServiceUrl();
        String username = TravelerInstancePluginManager.getTravelerBasicAuthUsername();
        String password = TravelerInstancePluginManager.getTravelerBasicAuthPassword(); 

        try {
            travelerApi = new TravelerApi(webServiceUrl, username, password);
        } catch (ConfigurationError ex) {
            String error = "Traveler Service is not accessible:  " + ex.getErrorMessage();
            logger.error(error);
        }
    }

    @Override
    public String getPropertyEditPage() {
        return PROPERTY_EDIT_PAGE;
    }

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        propertyValue.setDisplayValue(getDisplayValue(propertyValue.getValue(), true));
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setDisplayValue(getDisplayValue(propertyValueHistory.getValue(), false));
    }

    private String getDisplayValue(String value, Boolean showError) {
        if (value.equals("")) {
            return value;
        }
        try {
            Traveler traveler = travelerApi.getTraveler(value);
            return traveler.getTitle();
        } catch (CdbException ex) {
            logger.error(ex);
            if (showError) {
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
        return value;
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        propertyValue.setTargetValue(getTargetValue(propertyValue.getValue()));
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setTargetValue(getTargetValue(propertyValueHistory.getValue()));
    }
    
    @Override
    public void setInfoActionCommand(PropertyValue propertyValue) {
        propertyValue.setInfoActionCommand(INFO_ACTION_COMMAND);
    }
    
    @Override
    public void setInfoActionCommand(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setInfoActionCommand(INFO_ACTION_COMMAND);
    }

    private String getTargetValue(String value) {
        if (value.equals("")) {
            return value;
        }

        String travelerInstanceUrl = TRAVELER_WEB_APP_URL + TRAVELER_WEB_APP_TRAVELER_PATH;
        return travelerInstanceUrl.replace("TRAVELER_ID", value);
    }
}
