/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.parisLink;

import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;

/**
 *
 * @author djarosz
 */
public class ParisLinkPluginManager extends PluginManagerBase {
        
    private static final Properties PARIS_PROPERTIES = getDefaultPropertiesForPlugin("parisLink"); 
    private static final String PARIS_URL_STRING_KEY = "urlString";    

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new ParisLinkPropertyTypeHandler(); 
        }
        return propertyTypeHandler;
    }
    
    public static String getParisUrlString() {
        return PARIS_PROPERTIES.getProperty(PARIS_URL_STRING_KEY, ""); 
    }
    
}
