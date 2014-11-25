/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template uploadedFile, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.constants.CdbProperty;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;

import org.primefaces.model.UploadedFile;

@Named("propertyValueImageUploadBean")
@RequestScoped
public class PropertyValueImageUploadBean {

    private static final Logger logger = Logger.getLogger(PropertyValueImageUploadBean.class.getName());

    private UploadedFile uploadedFile;
    private PropertyValue propertyValue;

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(PropertyValue propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void upload() {
        Path uploadDirPath;
        try {
            if (uploadedFile != null && !uploadedFile.getFileName().isEmpty()) {
                uploadDirPath = Paths.get(StorageUtility.getFileSystemPropertyValueImagesDirectory());
                logger.debug("Using property value images directory: " + uploadDirPath.toString());
                if (Files.notExists(uploadDirPath)) {
                    Files.createDirectory(uploadDirPath);
                }
                File uploadDir = uploadDirPath.toFile();
                File tmpFile = File.createTempFile("tmp", ".tmp", uploadDir);
                InputStream input = uploadedFile.getInputstream();
                Files.copy(input, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.debug("Saved file: " + tmpFile.toPath());
                propertyValue.setValue(tmpFile.getName());
                SessionUtility.addInfoMessage("Success", "Uploaded file " + uploadedFile.getFileName() + ".");
            }
        } catch (IOException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.toString());
        }

    }
}
