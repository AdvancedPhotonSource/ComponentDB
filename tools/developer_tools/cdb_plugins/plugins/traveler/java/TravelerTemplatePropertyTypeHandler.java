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
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Form;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class TravelerTemplatePropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Traveler Template";

    private static final String PROPERTY_EDIT_PAGE = "travelerTemplatePropertyValueEditPanel";

    private static final Logger logger = LogManager.getLogger(TravelerTemplatePropertyTypeHandler.class.getName());

    private TravelerApi travelerApi;

    private final String TRAVELER_WEB_APP_URL = TravelerTemplatePluginManager.getTravelerWebApplicationUrl();
    private final String TRAVELER_WEB_APP_TEMPLATE_PATH = TravelerTemplatePluginManager.getTravelerWebApplicationTemplatePath();

    public TravelerTemplatePropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.HTTP_LINK);
        String webServiceUrl = TravelerTemplatePluginManager.getTravelerWebServiceUrl();
        String username = TravelerTemplatePluginManager.getTravelerBasicAuthUsername();
        String password = TravelerTemplatePluginManager.getTravelerBasicAuthPassword();
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
            Form form = travelerApi.getForm(value);
            return form.getTitle();
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

    private String getTargetValue(String value) {
        if (value.equals("")) {
            return value;
        }

        String travelerInstanceUrl = TRAVELER_WEB_APP_URL + TRAVELER_WEB_APP_TEMPLATE_PATH;
        return travelerInstanceUrl.replace("FORM_ID", value);
    }
}
