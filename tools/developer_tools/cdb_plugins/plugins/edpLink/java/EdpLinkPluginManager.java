/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.edpLink;

import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;

public class EdpLinkPluginManager extends PluginManagerBase {
    
    private static final Properties EDP_PROPERTIES = getDefaultPropertiesForPlugin("edpLink"); 
    
    private static final String EDP_URL_KEY = "EdpUrl"; 
    private static final String URL_STRING_KEY = "urlString";
    
    public static String getUrlStringProperty() {
        return EDP_PROPERTIES.getProperty(URL_STRING_KEY, ""); 
    }
    
    public static String getEdpUrlProperty() {
        return EDP_PROPERTIES.getProperty(EDP_URL_KEY, ""); 
    }
    
    public String getEdpUrl() {
        return getEdpUrlProperty(); 
    }

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new EdpLinkPropertyTypeHandler();
        }
        return propertyTypeHandler; 
    }
    
}
