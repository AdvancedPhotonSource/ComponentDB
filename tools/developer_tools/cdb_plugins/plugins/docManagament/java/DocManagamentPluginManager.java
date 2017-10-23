/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament;

import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;

/**
 *
 * @author djarosz
 */
public class DocManagamentPluginManager extends PluginManagerBase {
    
    private static final Properties DOC_MANAGAMENT_PROPERTIES = getDefaultPropertiesForPlugin("docManagament"); 
    
    private static final String DOC_MANAGAMENT_CONTAINER_KEY = "docManagamentContainerURL"; 
    
    public static String getContainerUrlStringProperty() {
        return DOC_MANAGAMENT_PROPERTIES.getProperty(DOC_MANAGAMENT_CONTAINER_KEY, ""); 
    }

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new DocManagamentPropertyTypeHandler();
        }
        return propertyTypeHandler; 
    }
    
    public String getDocumentManagamentContainerURL() {
        return getContainerUrlStringProperty();
    }
        
}
