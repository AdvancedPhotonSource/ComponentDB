/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.api.CdbDbApi;
import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.objects.Design;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import org.apache.log4j.Logger;

public class ComponentDesignPropertyTypeHandler extends AbstractPropertyTypeHandler {
    
    public static final String HANDLER_NAME = "Component Design";
    
    private static final String PROPERTY_EDIT_PAGE = "componentDesignPropertyValueEditPanel"; 
    
    private static final Logger logger = Logger.getLogger(ComponentDesignPropertyTypeHandler.class.getName());
    
    private CdbDbApi cdbDbApi; 
    
    private Design design; 
    
    public ComponentDesignPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.TABLE_RECORD_REFERENCE);
        
        String webServiceUrl = ConfigurationUtility.getPortalProperty(CdbProperty.WEB_SERVICE_URL_PROPERTY_NAME); 
        try{
          cdbDbApi = new CdbDbApi(webServiceUrl);  
        } catch (ConfigurationError ex){
            String error = "Cdb web service is not accessible: " + ex.getErrorMessage(); 
            logger.error(error);
            SessionUtility.addErrorMessage("Error", error);
        }
    }
    
    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        design = getDesign(propertyValue.getValue(), true); 
        if (design == null){
            propertyValue.setDisplayValue("Design with id: \"" + propertyValue.getValue() + "\" could not be found"); 
        } else {
            propertyValue.setDisplayValue(design.getName());
        }
    }
    
    @Override
    public void setTargetValue (PropertyValue propertyValue) {
        design = getDesign(propertyValue.getValue(), false); 
        if (design == null){
            propertyValue.setTargetValue(null);
        }
        else {
            propertyValue.setTargetValue("/cdb/views/design/view.xhtml?id="+ propertyValue.getValue()); 
        }
    }
    
    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        design = getDesign(propertyValueHistory.getValue(), true); 
        if (design == null){
            propertyValueHistory.setDisplayValue("Design with id: \"" + propertyValueHistory.getValue() + "\" could not be found"); 
        } else {
            propertyValueHistory.setDisplayValue(design.getName());
        }
    }
    
    @Override
    public void setTargetValue (PropertyValueHistory propertyValueHistory) {
        design = getDesign(propertyValueHistory.getValue(), false); 
        if (design == null){
            propertyValueHistory.setTargetValue(null);
        }
        else {
            propertyValueHistory.setTargetValue("/cdb/views/design/view.xhtml?id="+ propertyValueHistory.getValue()); 
        }
    }
    
    @Override
    public String getPropertyEditPage(){
        return PROPERTY_EDIT_PAGE;  
    }
    
    @Override
    public void resetOneTimeUseVariables(){
        design = null; 
    }
    
    private Design getDesign(String value, boolean displayErrorIfNotFound) {
        if (value == null || value.isEmpty()) {
            return null; 
        }
        
        if(design != null){
            if(design.getId() == Integer.parseInt(value)){
                return design; 
            }
        }
        
        if (cdbDbApi == null) {
            logger.error("Cannot get design for id: " + value + ": Cdb service is not accessible.");
            return null; 
        }
        
        design = null; 
        int id = Integer.parseInt(value); 
        try{
            logger.debug("Getting design for id: " + id);
            design = cdbDbApi.getDesign(id); 
        } catch (CdbException ex){
            if(displayErrorIfNotFound) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            }
        }
            return design; 
        
    }
    
}
