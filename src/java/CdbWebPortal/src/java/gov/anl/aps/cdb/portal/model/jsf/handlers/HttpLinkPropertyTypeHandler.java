/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

/**
 * HTTP link property type handler.
 */
public class HttpLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "HTTP Link";
    
    private String[] lastProcessedValue = null; 

    public HttpLinkPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.HTTP_LINK);
    }
    
    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        String linkValue = getDisplayValueForLink(propertyValue.getValue()); 
        propertyValue.setDisplayValue(linkValue);
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        String linkValue = getDisplayValueForLink(propertyValueHistory.getValue());
        propertyValueHistory.setDisplayValue(linkValue);
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        propertyValue.setTargetValue(getTargetValueForLink(propertyValue.getValue()));
    }
    
    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setTargetValue(getTargetValueForLink(propertyValueHistory.getValue()));
    }
    
    private String getDisplayValueForLink(String value) {
        return getAppropriateValueForLink(value, 1);
    }
    
    private String getTargetValueForLink(String value) {
        return getAppropriateValueForLink(value, 0);
    }
    
    /**
     * Checks for wiki link format otherwise returns just a standard formatted link. 
     * 
     * @param value
     * @param index - 0 for link, 1 for display 
     * @return 
     */
    private String getAppropriateValueForLink(String value, int index) {
        if (lastProcessedValue == null || !lastProcessedValue[0].equals(value)) {
            lastProcessedValue = null; 
        }
        
        if (lastProcessedValue == null) {
            lastProcessedValue = new String[3]; 
            lastProcessedValue[0] = value; 

            if (value.contains(" | ") && value.startsWith("[") && value.endsWith("]")) {
                value = value.substring(1, value.length() -1); 
                
                String[] stringSplit = value.split(" \\| "); 
                
                lastProcessedValue[1] = stringSplit[0];
                lastProcessedValue[2] = stringSplit[1]; 
            } else {
                lastProcessedValue[1] = value; 
                lastProcessedValue[2] = shortenHttpLinkDisplayValueIfNeeded(value);
            }     
        }
        
        return lastProcessedValue[index+1];  
    }
}
