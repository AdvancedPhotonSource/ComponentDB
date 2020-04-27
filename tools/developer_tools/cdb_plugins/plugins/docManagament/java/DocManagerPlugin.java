/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament;

import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.api.DocumentManagamentApi;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class DocManagerPlugin {
    
    private static final Logger logger = LogManager.getLogger(DocManagerPlugin.class.getName());
    
    private static final String DOC_MANAGAMENT_CONTAINER_URL_PATH = "/#/browsecontainers/";
    private static final String DOC_MANAGAMENT_COLLECTION_URL_PATH = "/#/reposearch/";
    
    private static final Properties DOC_MANAGAMENT_PROPERTIES = PluginManagerBase.getDefaultPropertiesForPlugin("docManagament");   
    
    private static final String CONTEXT_ROOT_URL_KEY = "ContextRootURL"; 
    
    protected static String getContextRootUrlProperty() {
        return DOC_MANAGAMENT_PROPERTIES.getProperty(CONTEXT_ROOT_URL_KEY, ""); 
    } 
    
    protected static DocumentManagamentApi createNewDocumentManagamentApi() {
        try {        
            return new DocumentManagamentApi(getContextRootUrlProperty());
        } catch (ConfigurationError ex) {
            String error = "DMS Service is not accessible:  " + ex.getErrorMessage();
            logger.error(error);
            return null; 
        }
    }
    
    protected static String generateContainerUrl(String containerId) {
        return getContextRootUrlProperty() + DOC_MANAGAMENT_CONTAINER_URL_PATH + containerId; 
    }
    
    protected static String generateCollectionUrl(String collectionId) {
        return getContextRootUrlProperty() + DOC_MANAGAMENT_COLLECTION_URL_PATH + collectionId; 
    }
     
}
