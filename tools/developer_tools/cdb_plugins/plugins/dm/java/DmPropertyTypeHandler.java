/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.dm;

import gov.anl.aps.cdb.common.exceptions.ExternalServiceError;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;
import gov.anl.aps.dm.api.ExperimentDsApi;
import gov.anl.aps.dm.common.exceptions.ConfigurationError;
import gov.anl.aps.dm.common.exceptions.DmException;
import gov.anl.aps.dm.common.exceptions.ObjectNotFound;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author djarosz
 */
public class DmPropertyTypeHandler extends AbstractPropertyTypeHandler {

    private static final Logger logger = LogManager.getLogger(DmPropertyTypeHandler.class.getName());

    public static final String HANDLER_NAME = "Data Managament";
    public static final String EXPERIMENT_NAME_KEY = "experimentName";
    public static final String EXPERIMENT_FILE_PATH_KEY = "experimentFilePath";
    public static final String EXPERIMENT_FILE_NAME_KEY = "fileName";
    public static final String APP_MIME_CONENT_TYPE = "application/";
    public static final String PDF_EXTENSION = "pdf";
    public static final String DATA_EXTENSION = "octet-stream";
    
    private static final String INFO_ACTION_COMMAND = "showDMInfoActionDialog();";
    private static final String[] TEXT_FILE_EXTENSIONS = { "txt" , "log" }; 

    public static final String IMAGE_MIME_CONTENT_TYPE = "image/";
    public static final String[] IMAGE_FILE_EXTENSIONS = {
        "png", "jpg", "jpeg", "gif", "bmp", "svg", "tiff"
    };

    public static final String SERVICE_URL = DmPluginManager.getServiceUrlProperty();
    public static final String SERVICE_USER = DmPluginManager.getServiceUserProperty();
    public static final String SERVICE_PASS = DmPluginManager.getServicePassProperty();

    public DmPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.FILE_DOWNLOAD);
    }

    @Override
    public StreamedContent fileDownloadActionCommand(PropertyValue propertyValue) throws ExternalServiceError {
        try {
            ByteArrayOutputStream byteArrayOutputStream = getByteArrayOutputStream(propertyValue); 
            String fileName = propertyValue.getPropertyMetadataValueForKey(EXPERIMENT_FILE_NAME_KEY);            
            InputStream pipedInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            logger.debug("Serving file download for: " + fileName);
            String mimeContentType = generateMimeContentType(fileName);
            DefaultStreamedContent.Builder builder = DefaultStreamedContent.builder();
            builder.stream(() -> pipedInputStream); 
            builder.contentType(mimeContentType);
            builder.name(fileName); 
            return builder.build();             
        } catch (ExternalServiceError ex) {
            logger.error("ERROR: " + ex.getMessage());           
            throw ex; 
        }       
    }

    public ByteArrayOutputStream getByteArrayOutputStream(PropertyValue propertyValue) throws ExternalServiceError {
        try {
            ExperimentDsApi client = new ExperimentDsApi(SERVICE_URL, SERVICE_USER, SERVICE_PASS);
            ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
            String experimentName = propertyValue.getPropertyMetadataValueForKey(EXPERIMENT_NAME_KEY);
            String experimentFilePath = propertyValue.getPropertyMetadataValueForKey(EXPERIMENT_FILE_PATH_KEY);
            client.downloadFile(experimentName, experimentFilePath, responseOutputStream);
            return responseOutputStream;
        } catch (ConfigurationError ex) {            
            logger.error("ERROR: " + ex.getMessage());
            throw new ExternalServiceError(ex.getMessage()); 
        } catch (ObjectNotFound ex) {
            logger.error("ERROR: " + ex.getMessage());
            throw new ExternalServiceError(ex.getMessage());             
        } catch (DmException ex) {
            logger.error("ERROR: " + ex.getMessage());
            throw new ExternalServiceError(ex.getMessage()); 
        }
    }

    public String generateMimeContentType(String fileName) {
        String ext = getFileExtension(fileName);
        String result = APP_MIME_CONENT_TYPE;

        for (String imageExt : IMAGE_FILE_EXTENSIONS) {
            if (ext.equalsIgnoreCase(imageExt)) {
                result = IMAGE_MIME_CONTENT_TYPE;
                result += imageExt;
                return result;
            }
        }

        if (ext.equalsIgnoreCase(PDF_EXTENSION)) {
            result += PDF_EXTENSION;
        } else {
            result += DATA_EXTENSION;
        }

        return result;
    }
    
    private String getFileExtension(String fileName) {
        int extStart = fileName.lastIndexOf(".") + 1;
        String ext = fileName.substring(extStart);
        return ext;
    }
    
    private boolean isTextFile(String fileName)  {
        String ext = getFileExtension(fileName);
        for (String textExt : TEXT_FILE_EXTENSIONS) {
            if (ext.equalsIgnoreCase(textExt)) {
                return true; 
            }
        }
        return false; 
    }
    
    @Override
    public void setInfoActionCommand(PropertyValue propertyValue){
        if (isTextFile(propertyValue.getValue())) {
            propertyValue.setInfoActionCommand(INFO_ACTION_COMMAND);
        }        
    }
    
    @Override
    public void setInfoActionCommand(PropertyValueHistory propertyValueHistory){
        if (isTextFile(propertyValueHistory.getValue())) {
            propertyValueHistory.setInfoActionCommand(INFO_ACTION_COMMAND);
        }
    }

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        String fileName = propertyValue.getPropertyMetadataValueForKey(EXPERIMENT_FILE_NAME_KEY);
        propertyValue.setDisplayValue(fileName);
    }

}
