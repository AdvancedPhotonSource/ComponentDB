/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.dm;

import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;

/**
 *
 * @author djarosz
 */
public class DmPluginManager extends PluginManagerBase {
    
    private static final Properties DM_PROPERTIES = getDefaultPropertiesForPlugin("dm"); 
    
    private static final String DM_SERVICE_URL_KEY = "serviceUrl"; 
    private static final String DM_SERVICE_USER_KEY = "serviceUser";     
    private static final String DM_SERVICE_PASS_KEY = "servicePass"; 
    
    public static String getServiceUrlProperty() {
        return DM_PROPERTIES.getProperty(DM_SERVICE_URL_KEY, ""); 
    }
    
    public static String getServiceUserProperty() {
        return DM_PROPERTIES.getProperty(DM_SERVICE_USER_KEY, ""); 
    }
    
    public static String getServicePassProperty() {
        return DM_PROPERTIES.getProperty(DM_SERVICE_PASS_KEY, ""); 
    }
    
    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new DmPropertyTypeHandler();
        }
        return propertyTypeHandler;
    }
    
}
