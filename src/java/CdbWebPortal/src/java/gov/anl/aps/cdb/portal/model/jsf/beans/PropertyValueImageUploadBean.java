/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.common.exceptions.ImageProcessingFailed;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.common.utilities.FileUtility;
import gov.anl.aps.cdb.common.utilities.ImageUtility;
import gov.anl.aps.cdb.portal.controllers.CdbDomainEntityController;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeDbFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeHandlerDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
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
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import org.primefaces.model.UploadedFile;

/**
 * JSF bean for property value image upload.
 */
@Named("propertyValueImageUploadBean")
@SessionScoped
public class PropertyValueImageUploadBean implements Serializable {

    private static final Logger logger = Logger.getLogger(PropertyValueImageUploadBean.class.getName());

    private UploadedFile uploadedFile;
    private CdbEntityController cdbEntityController;
    private List<PropertyType> imageHandlerPropertyTypes;
    private final String IMAGE_PROPERTY_TYPE_NAME = "Image";

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public void upload(PropertyValue propertyValue, UploadedFile localUploadedFile) {
        if (localUploadedFile == null) {
            localUploadedFile = this.uploadedFile;
        }

        Path uploadDirPath;
        try {
            if (localUploadedFile != null && !localUploadedFile.getFileName().isEmpty()) {
                String uploadedExtension = FileUtility.getFileExtension(localUploadedFile.getFileName());

                uploadDirPath = Paths.get(StorageUtility.getFileSystemPropertyValueImagesDirectory());
                logger.debug("Using property value images directory: " + uploadDirPath.toString());
                if (Files.notExists(uploadDirPath)) {
                    Files.createDirectory(uploadDirPath);
                }
                File uploadDir = uploadDirPath.toFile();

                String imageFormat = uploadedExtension;
                String originalExtension = "." + uploadedExtension + CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION;
                if (uploadedExtension.isEmpty()) {
                    originalExtension = CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION;
                    imageFormat = ImageUtility.DEFAULT_IMAGE_FORMAT;
                }
                File originalFile = File.createTempFile(CdbPropertyValue.IMAGE_PREFIX, originalExtension, uploadDir);
                String baseName = originalFile.getName().replace(CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION, "");
                InputStream input = localUploadedFile.getInputstream();
                Files.copy(input, originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.debug("Saved file: " + originalFile.toPath());
                byte[] originalData = Files.readAllBytes(originalFile.toPath());
                byte[] thumbData = ImageUtility.resizeImage(originalData, StorageUtility.THUMBNAIL_IMAGE_SIZE, imageFormat);
                String thumbFileName = originalFile.getAbsolutePath().replace(CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION, CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION);
                Path thumbPath = Paths.get(thumbFileName);
                Files.write(thumbPath, thumbData);
                byte[] scaledData;
                if (ImageUtility.verifyImageSizeBigger(originalData, StorageUtility.SCALED_IMAGE_SIZE)){
                    scaledData = ImageUtility.resizeImage(originalData, StorageUtility.SCALED_IMAGE_SIZE, imageFormat);
                } else {
                    scaledData = originalData; 
                }
                String scaledFileName = originalFile.getAbsolutePath().replace(CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION, CdbPropertyValue.SCALED_IMAGE_EXTENSION);
                Path scaledPath = Paths.get(scaledFileName);
                Files.write(scaledPath, scaledData);
                propertyValue.setValue(baseName);
                logger.debug("Uploaded file name: " + localUploadedFile.getFileName());
                SessionUtility.addInfoMessage("Success", "Uploaded file " + localUploadedFile.getFileName() + ".");
            }
        } catch (IOException | ImageProcessingFailed ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.toString());
        }
    }

    public void upload(PropertyValue propertyValue) {
        upload(propertyValue, null);
    }

    public CdbEntityController getCdbEntityController() {
        return cdbEntityController;
    }

    public void setCdbEntityController(CdbEntityController cdbEntityController) {
        this.cdbEntityController = cdbEntityController;
    }

    public List<PropertyType> getImageHandlerPropertyTypes() {
        if (imageHandlerPropertyTypes == null) {
            try {
                PropertyTypeDbFacade propertyTypeDbFacade = (PropertyTypeDbFacade) new InitialContext().lookup("java:module/PropertyTypeDbFacade");
                PropertyTypeHandlerDbFacade propertyTypeHandlerDbFacade = (PropertyTypeHandlerDbFacade) new InitialContext().lookup("java:module/PropertyTypeHandlerDbFacade");

                PropertyTypeHandler imagePropertyTypeHandler = propertyTypeHandlerDbFacade.findByName(IMAGE_PROPERTY_TYPE_NAME);
                imageHandlerPropertyTypes = propertyTypeDbFacade.findByPropertyTypeHandler(imagePropertyTypeHandler);
            } catch (NamingException ex) {
                logger.debug(ex);
            }
        }
        return imageHandlerPropertyTypes;
    }

    public void setImageHandlerPropertyTypes(List<PropertyType> imageHandlerPropertyTypes) {
        this.imageHandlerPropertyTypes = imageHandlerPropertyTypes;
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile localUploadedFile = event.getFile();
        
        if (cdbEntityController != null) {
            if (cdbEntityController instanceof CdbDomainEntityController) {
                CdbDomainEntityController cdbDomainEntityController = (CdbDomainEntityController) cdbEntityController;
                PropertyValue propertyValue = cdbDomainEntityController.preparePropertyTypeValueAdd(getImageHandlerPropertyTypes().get(0), null, null);
                this.upload(propertyValue, localUploadedFile);
            } 
        }

    }
}
