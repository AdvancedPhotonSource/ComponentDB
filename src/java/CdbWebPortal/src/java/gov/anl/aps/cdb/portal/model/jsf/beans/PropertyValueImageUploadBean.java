/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.common.utilities.FileUtility;
import gov.anl.aps.cdb.common.utilities.ImageUtility;
import gov.anl.aps.cdb.portal.controllers.CdbDomainEntityController;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
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
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;


/**
 * JSF bean for property value image upload.
 */
@Named("propertyValueImageUploadBean")
@SessionScoped
public class PropertyValueImageUploadBean implements Serializable {

    private static final Logger logger = LogManager.getLogger(PropertyValueImageUploadBean.class.getName());

    private UploadedFile uploadedFile;
    private CdbEntityController cdbEntityController;
    private List<PropertyType> imageHandlerPropertyTypes;
    private PropertyType selectedPropertyType;
    public static final String IMAGE_PROPERTY_TYPE_NAME = "Image";
    private final List<Integer> uploadHashList = new ArrayList<>();

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

        if (localUploadedFile != null && !localUploadedFile.getFileName().isEmpty()) {
            String fileName = localUploadedFile.getFileName();

            try {
                InputStream input = localUploadedFile.getInputStream();
                uploadImage(propertyValue, fileName, input);
                SessionUtility.addInfoMessage("Success", "Uploaded file " + fileName + ".");
            } catch (IOException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.toString());
            }
        }

    }

    public static void uploadImage(PropertyValue propertyValue, String fileName, InputStream input) throws IOException {
        Path uploadDirPath;
        String uploadedExtension = FileUtility.getFileExtension(fileName);

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
        Files.copy(input, originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        logger.debug("Saved file: " + originalFile.toPath());
        GalleryUtility.storeImagePreviews(originalFile);
        propertyValue.setValue(baseName);
        propertyValue.setDisplayValue(fileName);
        logger.debug("Uploaded file name: " + fileName);        
    }
    
    public static PropertyType getImagePropertyType(PropertyTypeHandlerFacade pthf) {
        PropertyTypeHandler imagePropertyTypeHandler = pthf.findByName(IMAGE_PROPERTY_TYPE_NAME);
        List<PropertyType> propertyTypeList = imagePropertyTypeHandler.getPropertyTypeList();
        if (propertyTypeList.size() > 0) {
            return propertyTypeList.get(0);
        }
        return null; 
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
                PropertyTypeFacade propertyTypeFacade = (PropertyTypeFacade) new InitialContext().lookup("java:module/PropertyTypeFacade");
                PropertyTypeHandlerFacade propertyTypeHandlerDbFacade = (PropertyTypeHandlerFacade) new InitialContext().lookup("java:module/PropertyTypeHandlerFacade");

                PropertyTypeHandler imagePropertyTypeHandler = propertyTypeHandlerDbFacade.findByName(IMAGE_PROPERTY_TYPE_NAME);
                imageHandlerPropertyTypes = propertyTypeFacade.findByPropertyTypeHandler(imagePropertyTypeHandler);
            } catch (NamingException ex) {
                logger.debug(ex);
            }
            if (imageHandlerPropertyTypes.size() > 0) {
                selectedPropertyType = imageHandlerPropertyTypes.get(0);
            }
        }
        return imageHandlerPropertyTypes;
    }

    public void setImageHandlerPropertyTypes(List<PropertyType> imageHandlerPropertyTypes) {
        this.imageHandlerPropertyTypes = imageHandlerPropertyTypes;
    }

    public boolean showPropertyTypeSelectOneMenu() {
        return getImageHandlerPropertyTypes().size() > 1;
    }

    public PropertyType getSelectedPropertyType() {
        return selectedPropertyType;
    }

    public void setSelectedPropertyType(PropertyType selectedPropertyType) {
        this.selectedPropertyType = selectedPropertyType;
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile localUploadedFile = event.getFile();
        uploadHashList.add(localUploadedFile.hashCode());

        if (cdbEntityController != null) {
            if (cdbEntityController instanceof CdbDomainEntityController) {
                CdbDomainEntityController cdbDomainEntityController = (CdbDomainEntityController) cdbEntityController;
                PropertyValue propertyValue = cdbDomainEntityController.preparePropertyTypeValueAdd(selectedPropertyType, null, null);
                this.upload(propertyValue, localUploadedFile);
            }
        }

        int removeIndex = uploadHashList.indexOf(localUploadedFile.hashCode());
        uploadHashList.remove(removeIndex);
    }

    public void handleSingleFileUpload(FileUploadEvent event) {
        this.uploadedFile = event.getFile();
    }

    /**
     * Called when user is done uploading multiple images. Checks to make sure
     * all downloads have completed before saving.
     *
     * @throws InterruptedException
     * @throws gov.anl.aps.cdb.common.exceptions.CdbException
     */
    public void done() throws InterruptedException, CdbException {
        while (true) {
            if (uploadHashList.isEmpty()) {
                if (cdbEntityController != null) {
                    cdbEntityController.update();
                    return;
                } else {
                    throw new CdbException("Controller not set to update the gallery.");
                }

            }

            // List is not empty, wait and check again... 
            Thread.sleep(500);

        }
    }
}
