/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.amosLink;

import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;

public class AmosLinkPluginManager extends PluginManagerBase {   
    
    private static final Properties AMOS_PROPERTIES = getDefaultPropertiesForPlugin("amosLink"); 
    private static final String AMOS_URL_STRING_KEY = "urlString";
    
    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new AmosLinkPropertyTypeHandler();
        }
        return propertyTypeHandler;
    }
    
    public static String getAmosUrlString() {
        return AMOS_PROPERTIES.getProperty(AMOS_URL_STRING_KEY, "");
    }
    
}
