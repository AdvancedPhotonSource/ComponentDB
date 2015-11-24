/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.jsf.handlers;
import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.traveler.api.TravelerApi;
import gov.anl.aps.traveler.common.objects.Form;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class TravelerTemplatePropertyTypeHandler extends AbstractPropertyTypeHandler {
    
    public static final String HANDLER_NAME = "Traveler Template"; 
    
    private static final String PROPERTY_EDIT_PAGE = "travelerTemplatePropertyValueEditPanel";
    
    private static final Logger logger = Logger.getLogger(TravelerTemplatePropertyTypeHandler.class.getName()); 
    
    private TravelerApi travelerApi; 
    
        private final String TRAVELER_WEB_APP_URL = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_APPLICATION_URL_PROPERTY_NAME); 
    private final String TRAVELER_WEB_APP_TEMPLATE_PATH = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_APPLICATION_TEMPLATE_PATH_PROPERTY_NAME); 
    
    public TravelerTemplatePropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.HTTP_LINK); 
        String webServiceUrl = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_SERVICE_URL_PROPERTY_NAME);
        String username = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_SERVICE_BASIC_AUTH_USERNAME_PROPERTY_NAME);
        String password = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_SERVICE_BASIC_AUTH_PASSWORD_PROPERTY_NAME); 
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
    public void setDisplayValue(PropertyValue propertyValue){
        propertyValue.setDisplayValue(getDisplayValue(propertyValue.getValue(), true));
    }
    
    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory){
        propertyValueHistory.setDisplayValue(getDisplayValue(propertyValueHistory.getValue(), false));
    }
    
    private String getDisplayValue(String value, Boolean showError){
        if (value.equals("")) {
            return value;  
        }
        try{
            Form form = travelerApi.getForm(value); 
            return form.getTitle(); 
        } catch (CdbException ex) {
            logger.error(ex);
            if(showError) {
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
        return value; 
    }
    
    @Override
    public void setTargetValue(PropertyValue propertyValue){
        propertyValue.setTargetValue(getTargetValue(propertyValue.getValue()));
    }
    
    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory){
        propertyValueHistory.setTargetValue(getTargetValue(propertyValueHistory.getValue()));
    }
    
    private String getTargetValue(String value) {
        if(value.equals("")) {
            return value; 
        }
        
        String travelerInstanceUrl = TRAVELER_WEB_APP_URL + TRAVELER_WEB_APP_TEMPLATE_PATH;
        return travelerInstanceUrl.replace("FORM_ID", value);
    }
}
