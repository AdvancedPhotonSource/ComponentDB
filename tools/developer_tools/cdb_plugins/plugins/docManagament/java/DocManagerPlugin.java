/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament;

import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import java.util.Properties;

/**
 *
 * @author djarosz
 */
public class DocManagerPlugin {
    
    private static final Properties DOC_MANAGAMENT_PROPERTIES = PluginManagerBase.getDefaultPropertiesForPlugin("docManagament");   
    
    private static final String CONTEXT_ROOT_URL_KEY = "ContextRootURL"; 
    
    protected static String getContextRootUrlProperty() {
        return DOC_MANAGAMENT_PROPERTIES.getProperty(CONTEXT_ROOT_URL_KEY, ""); 
    } 
    
}
