/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class TravelerPluginManager extends PluginManagerBase {
    
    private static final Logger logger = LogManager.getLogger(TravelerPluginManager.class.getName());
    
    private static final Properties TRAVELER_PROPERTIES = getDefaultPropertiesForPlugin("traveler"); 
    
    private static final String TRAVELER_WEB_SERVICE_URL_PROPERTY_NAME = "webSerice.url"; 
    private static final String TRAVELER_WEB_SERVICE_BASIC_AUTH_USERNAME_PROPERTY_NAME = "webService.basicAuth.username"; 
    private static final String TRAVELER_WEB_SERVICE_BASIC_AUTH_PASSWORD_PROPERTY_NAME = "webService.basicAuth.password"; 
    private static final String TRAVELER_WEB_APPLICATION_URL_PROPERTY_NAME = "webApp.url";
    private static final String TRAVELER_WEB_APPLICATION_TEMPLATE_PATH_PROPERTY_NAME = "webApp.TemplatePath";     
    private static final String TRAVELER_WEB_APPLICATION_TRAVELER_PATH_PROPERTY_NAME = "webApp.TravelerPath";
    private static final String TRAVELER_WEB_APPLICATION_BINDER_PATH_PROPERTY_NAME = "webApp.BinderPath";
    private static final String TRAVELER_WEB_APPLICATION_TRAVELER_CONFIG_PATH_PROPERTY_NAME = "webApp.TravelerConfigPath";
    
    public static String getTravelerWebServiceUrl() {
        return TRAVELER_PROPERTIES.getProperty(TRAVELER_WEB_SERVICE_URL_PROPERTY_NAME); 
    }
    
    public static String getTravelerBasicAuthUsername() {
        return TRAVELER_PROPERTIES.getProperty(TRAVELER_WEB_SERVICE_BASIC_AUTH_USERNAME_PROPERTY_NAME); 
    }
    
    public static String getTravelerBasicAuthPassword() {
        return TRAVELER_PROPERTIES.getProperty(TRAVELER_WEB_SERVICE_BASIC_AUTH_PASSWORD_PROPERTY_NAME); 
    }
    
    public static String getTravelerWebApplicationUrl() {
        return TRAVELER_PROPERTIES.getProperty(TRAVELER_WEB_APPLICATION_URL_PROPERTY_NAME); 
    }
    
    public static String getTravelerWebApplicationTemplatePath() {
        return TRAVELER_PROPERTIES.getProperty(TRAVELER_WEB_APPLICATION_TEMPLATE_PATH_PROPERTY_NAME); 
    }
    
    public static String getTravelerWebApplicationTravelerPath() {
        return TRAVELER_PROPERTIES.getProperty(TRAVELER_WEB_APPLICATION_TRAVELER_PATH_PROPERTY_NAME); 
    }
    
    public static String getTravelerWebApplicationTravelerConfigPath(){
        return TRAVELER_PROPERTIES.getProperty(TRAVELER_WEB_APPLICATION_TRAVELER_CONFIG_PATH_PROPERTY_NAME);
    }
    
    public static String getTravelerWebApplicationBinderPath() {
        return TRAVELER_PROPERTIES.getProperty(TRAVELER_WEB_APPLICATION_BINDER_PATH_PROPERTY_NAME); 
    }
    
    /**
     * Gets URL to the traveler web application.  
     * Methods called from view cannot be static. 
     * 
     * @return 
     */
    public String getTravelerUrl() {
        return getTravelerWebApplicationUrl();
    }

    @Override
    public boolean pluginHasCatalogMultiEditExtras() {
        return true; 
    }

    @Override
    public boolean pluginHasInventoryMultiEditExtras() {
        return true; 
    }
    
}
