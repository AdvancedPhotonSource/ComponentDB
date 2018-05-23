/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.icmsLink;

import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;

public class IcmsLinkPluginManager extends PluginManagerBase {
      
    private static final Properties ICMS_PROPERTIES = getDefaultPropertiesForPlugin("icmsLink");
    
    private static final String ICMS_URL_STRING_KEY = "urlString";
    private static final String ICMS_ENDPOINT_URL_KEY = "soapEndpointUrl";
    private static final String ICMS_GET_FILE_ACTION_URL_KEY = "soapGetFileByNameActionUrl";
    private static final String ICMS_SOAP_USERNAME_KEY = "soapUsername";
    private static final String ICMS_SOAP_PASSWORD_KEY = "soapPassword";

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new IcmsLinkPropertyTypeHandler();
        }
        return propertyTypeHandler; 
    }
    
    public static String getIcmsUrlString(){        
        return ICMS_PROPERTIES.getProperty(ICMS_URL_STRING_KEY, ""); 
    }
    
    public static String getSoapEndpointUrl(){        
        return ICMS_PROPERTIES.getProperty(ICMS_ENDPOINT_URL_KEY, ""); 
    }
    
    public static String getSoapGetFileActionUrl(){        
        return ICMS_PROPERTIES.getProperty(ICMS_GET_FILE_ACTION_URL_KEY, ""); 
    }
    
    public static String getSoapUsername(){        
        return ICMS_PROPERTIES.getProperty(ICMS_SOAP_USERNAME_KEY, ""); 
    }
    
    public static String getSoapPassword(){
        return ICMS_PROPERTIES.getProperty(ICMS_SOAP_PASSWORD_KEY, ""); 
    }        
    
}
