/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.dm;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;
import gov.anl.aps.dm.api.ExperimentDsApi;
import gov.anl.aps.dm.common.exceptions.ConfigurationError;
import gov.anl.aps.dm.common.exceptions.DmException;
import gov.anl.aps.dm.common.exceptions.ObjectNotFound;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author djarosz
 */
public class DmPropertyTypeHandler extends AbstractPropertyTypeHandler {
    
    private static final Logger logger = Logger.getLogger(DmPropertyTypeHandler.class.getName());

    public static final String HANDLER_NAME = "Data Managament";
    public static final String EXPERIMENT_NAME_KEY = "experimentName"; 
    public static final String EXPERIMENT_FILE_PATH_KEY = "experimentFilePath";
    public static final String EXPERIMENT_FILE_NAME_KEY = "fileName"; 
    public static final String MIME_CONENT_TYPE = "application/octet-stream";
    
    public static final String SERVICE_URL = DmPluginManager.getServiceUrlProperty();
    public static final String SERVICE_USER = DmPluginManager.getServiceUserProperty();
    public static final String SERVICE_PASS = DmPluginManager.getServicePassProperty();
    
    private ExperimentDsApi client;

    public DmPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.FILE_DOWNLOAD);        
    }
    
    public StreamedContent fileDownloadActionCommand(PropertyValue propertyValue) {               
        try {
            client = new ExperimentDsApi(SERVICE_URL, SERVICE_USER, SERVICE_PASS);
            ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();            
            String experimentName = propertyValue.getPropertyMetadataValueForKey(EXPERIMENT_NAME_KEY);
            String experimentFilePath = propertyValue.getPropertyMetadataValueForKey(EXPERIMENT_FILE_PATH_KEY);            
            String fileName = propertyValue.getPropertyMetadataValueForKey(EXPERIMENT_FILE_NAME_KEY); 
            client.downloadFile(experimentName, experimentFilePath, responseOutputStream);            
            InputStream pipedInputStream = new ByteArrayInputStream(responseOutputStream.toByteArray());
            logger.debug("Serving file download for: " + fileName);
            return new DefaultStreamedContent(pipedInputStream, MIME_CONENT_TYPE, fileName);
        } catch (ConfigurationError ex) {
            logger.error("ERROR: " + ex.getMessage());
        } catch (ObjectNotFound ex) {
            logger.error("ERROR: " + ex.getMessage());
        } catch (DmException ex) {
            logger.error("ERROR: " + ex.getMessage());
        }            
                
        return null; 
    }

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        String fileName = propertyValue.getPropertyMetadataValueForKey(EXPERIMENT_FILE_NAME_KEY); 
        propertyValue.setDisplayValue(fileName);
    }

}
