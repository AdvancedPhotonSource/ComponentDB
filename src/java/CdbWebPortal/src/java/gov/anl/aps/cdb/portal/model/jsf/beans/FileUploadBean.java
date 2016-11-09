/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.log4j.Logger;

import org.primefaces.model.UploadedFile;

/**
 * JSF file upload bean.
 */
@Named("fileUploadBean")
@RequestScoped
public class FileUploadBean {

    private static final Logger logger = Logger.getLogger(FileUploadBean.class.getName());

    private UploadedFile uploadedFile;

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public void upload() {
        if (uploadedFile != null && !uploadedFile.getFileName().isEmpty()) {
            FacesMessage message = new FacesMessage("Succesful", uploadedFile.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            logger.debug("Uploading file: " + uploadedFile.getFileName()
                    + " (size: " + uploadedFile.getSize() + ")");
            File file = new File("/tmp/xyz");
            try (InputStream input = uploadedFile.getInputstream()) {
                Files.copy(input, file.toPath());
                logger.debug("Saved file: " + file.toPath());
            } catch (Exception ex) {
                logger.error(ex);
            }

        }
    }
}
