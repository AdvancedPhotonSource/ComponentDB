/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.irmisLocation;

import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;
/**
 *
 * @author iusmani
 */
public class irmisLocationPluginManager extends PluginManagerBase {
    private static final Properties IRMIS_LOCATION_PROPERTIES = getDefaultPropertiesForPlugin("irmisLocation"); 
    private static final String IRMIS_LOCATION_URL_STRING_KEY = "urlString";
    
    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new irmisLocationPropertyTypeHandler();
        }
        return propertyTypeHandler;
    }
    
    public static String getIrmisLocationUrlString() {
        return IRMIS_LOCATION_PROPERTIES.getProperty(IRMIS_LOCATION_URL_STRING_KEY, "");
    }  
}
