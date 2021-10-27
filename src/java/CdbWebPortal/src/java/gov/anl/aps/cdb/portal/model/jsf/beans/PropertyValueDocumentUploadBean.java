/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.common.utilities.FileUtility;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeHandlerFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.utilities.GalleryUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;


/**
 * JSF bean for property value document upload.
 */
@Named("propertyValueDocumentUploadBean")
@SessionScoped
public class PropertyValueDocumentUploadBean implements Serializable {

    private static final Logger logger = LogManager.getLogger(PropertyValueDocumentUploadBean.class.getName());
    public static final String DOCUMENT_PROPERTY_TYPE_NAME = "Document";

    private UploadedFile uploadedFile;

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public static PropertyType getDocumentPropertyType(PropertyTypeHandlerFacade pthf) {
        PropertyTypeHandler imagePropertyTypeHandler = pthf.findByName(DOCUMENT_PROPERTY_TYPE_NAME);
        List<PropertyType> propertyTypeList = imagePropertyTypeHandler.getPropertyTypeList();
        if (propertyTypeList.size() > 0) {
            return propertyTypeList.get(0);
        }
        return null;
    }

    public void upload(PropertyValue propertyValue) {

        try {
            String fileName = uploadedFile.getFileName();
            if (uploadedFile != null && !uploadedFile.getFileName().isEmpty()) {                
                InputStream input = uploadedFile.getInputStream();                
                uploadDocument(propertyValue, fileName, input);                
                SessionUtility.addInfoMessage("Success", "Uploaded file " + uploadedFile.getFileName() + ".");
            }
        } catch (IOException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.toString());
        }
    }

    public static void uploadDocument(PropertyValue propertyValue, String filename, InputStream input) throws IOException {
        Path uploadDirPath;
        String uploadedExtension = FileUtility.getFileExtension(filename);
        uploadDirPath = Paths.get(StorageUtility.getFileSystemPropertyValueDocumentsDirectory());
        logger.debug("Using property value documents directory: " + uploadDirPath.toString());

        if (Files.notExists(uploadDirPath)) {
            Files.createDirectory(uploadDirPath);
        }
        File uploadDir = uploadDirPath.toFile();
        
        String originalExtension = "." + uploadedExtension;
        File originalFile = File.createTempFile("document.", originalExtension, uploadDir);
        Files.copy(input, originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        logger.debug("Saved file: " + originalFile.toPath());

        GalleryUtility.storeImagePreviews(originalFile, uploadedExtension);

        propertyValue.setValue(originalFile.getName());
        propertyValue.setDisplayValue(filename);

    }

    public void handleSingleFileUpload(FileUploadEvent event) {
        this.uploadedFile = event.getFile();
    }
}
