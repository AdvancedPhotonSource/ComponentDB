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
    
    protected static final String CONTAINER_IFRAME_PATH = "/iframe.jsp#/container/";    
    
    private static final String CONTEXT_ROOT_URL_KEY = "ContextRootURL"; 
    
    public static String getContextRootUrlProperty() {
        return DOC_MANAGAMENT_PROPERTIES.getProperty(CONTEXT_ROOT_URL_KEY, ""); 
    }          

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new DocManagamentPropertyTypeHandler();
        }
        return propertyTypeHandler; 
    }
    
    public String getDocumentManagamentIFrameContainerURL(Integer containerId) {
        return getContextRootUrlProperty() + CONTAINER_IFRAME_PATH + containerId; 
    }
        
}
