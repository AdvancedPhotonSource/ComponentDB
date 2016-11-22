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
    
    
    
}
