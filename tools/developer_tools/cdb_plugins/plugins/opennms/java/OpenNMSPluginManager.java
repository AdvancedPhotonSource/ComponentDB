/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.opennms;

import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;

/**
 *
 * @author darek
 */
public class OpenNMSPluginManager extends PluginManagerBase {
    
    private static final Properties OPENNMS_PROPERTIES = getDefaultPropertiesForPlugin("opennms"); 
    
    private static final String OPENNMS_SERVICE_URL_PROPERTY_NAME = "opennms.service.url"; 
    private static final String OPENNMS_SERVICE_URL_BASIC_AUTH_USERNAME_PROPERTY_NAME = "opennms.service.basicAuth.username"; 
    private static final String OPENNMS_SERVICE_URL_BASIC_AUTH_PASSWORD_PROPERTY_NAME = "opennms.service.basicAuth.password"; 
    private static final String BUSINESS_SERVICE_ID_PROPERTY_NAME = "businessServiceId"; 
    
    public static String getOpenNMSServiceUrl() {
        return OPENNMS_PROPERTIES.getProperty(OPENNMS_SERVICE_URL_PROPERTY_NAME); 
    }
    
    public static String getOpenNMSBasicAuthUsername() {
        return OPENNMS_PROPERTIES.getProperty(OPENNMS_SERVICE_URL_BASIC_AUTH_USERNAME_PROPERTY_NAME); 
    }
    
    public static String getOpenNMSBasicAuthPassword() {
        return OPENNMS_PROPERTIES.getProperty(OPENNMS_SERVICE_URL_BASIC_AUTH_PASSWORD_PROPERTY_NAME); 
    }
    
    public static Integer getOpenNMSBusinessServiceId() {
        String serviceIdString = OPENNMS_PROPERTIES.getProperty(BUSINESS_SERVICE_ID_PROPERTY_NAME); 
        return Integer.parseInt(serviceIdString); 
    }
    
}
