/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.jsf.handlers;
import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.traveler.api.TravelerApi;
import gov.anl.aps.traveler.common.objects.Traveler;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class TravelerInstancePropertyTypeHandler extends AbstractPropertyTypeHandler {
    
    public static final String HANDLER_NAME = "Traveler Instance"; 
    
    private static final String PROPERTY_EDIT_PAGE = "travelerInstancePropertyValueEditPanel";
    
    private static final Logger logger = Logger.getLogger(TravelerInstancePropertyTypeHandler.class.getName()); 
    
    private TravelerApi travelerApi; 
    
    private final String TRAVELER_WEB_APP_URL = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_APPLICATION_URL_PROPERTY_NAME); 
    private final String TRAVELER_WEB_APP_TRAVELER_PATH = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_APPLICATION_TRAVELER_PATH_PROPERTY_NAME);
    
    public TravelerInstancePropertyTypeHandler() {
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
            Traveler traveler = travelerApi.getTraveler(value); 
            return traveler.getTitle(); 
        } catch (Exception ex) {
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
        
        String travelerInstanceUrl = TRAVELER_WEB_APP_URL + TRAVELER_WEB_APP_TRAVELER_PATH;
        return travelerInstanceUrl.replace("TRAVELER_ID", value);
    }
}
