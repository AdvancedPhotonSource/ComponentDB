/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.common.utilities.FileUtility;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;


/**
 * JSF bean for log attachment uploads.
 */
@Named("logAttachmentUploadBean")
@SessionScoped
public class LogAttachmentUploadBean implements Serializable {

    private static final Logger logger = LogManager.getLogger(LogAttachmentUploadBean.class.getName());

    private Log logEntry;

    public Log getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(Log logEntry) {
        this.logEntry = logEntry;
    }

    public void upload(UploadedFile uploadedFile) {
        Path uploadDirPath;
        try {
            if (uploadedFile != null && !uploadedFile.getFileName().isEmpty()) {
                String fileName = uploadedFile.getFileName();
                String uploadedExtension = FileUtility.getFileExtension(fileName);

                uploadDirPath = Paths.get(StorageUtility.getFileSystemLogAttachmentsDirectory());
                logger.debug("Using log attachments directory: " + uploadDirPath.toString());
                if (Files.notExists(uploadDirPath)) {
                    Files.createDirectory(uploadDirPath);
                }
                File uploadDir = uploadDirPath.toFile();

                String originalExtension = "." + uploadedExtension;
                File originalFile = File.createTempFile("attachment.", originalExtension, uploadDir);
                InputStream input = uploadedFile.getInputStream();
                Files.copy(input, originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.debug("Saved file: " + originalFile.toPath());
                Attachment attachment = new Attachment();
                attachment.setName(originalFile.getName());
                attachment.setOriginalFilename(fileName);
                List<Attachment> attachmentList = logEntry.getAttachmentList();
                if (attachmentList == null) {
                    attachmentList = new ArrayList<>();
                    logEntry.setAttachmentList(attachmentList);
                }
                attachmentList.add(attachment);
                SessionUtility.addInfoMessage("Success", "Uploaded file " + fileName + ".");
            }
        } catch (IOException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.toString());
        }
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        upload(event.getFile());
    }
}
