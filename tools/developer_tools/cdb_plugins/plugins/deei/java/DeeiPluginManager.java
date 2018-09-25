/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.deei;

import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;

public class DeeiPluginManager extends PluginManagerBase { 
    
    private static final String DEEI_URL_KEY = "urlString"; 
    
    private static final Properties DEEI_PROPERTIES = PluginManagerBase.getDefaultPropertiesForPlugin("deei");   
    
    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new DeeiPropertyTypeHandler();
        }
        return propertyTypeHandler;
    }
    
    protected static String getUrlStringProperty() {
        return DEEI_PROPERTIES.getProperty(DEEI_URL_KEY, ""); 
    } 
    
}
