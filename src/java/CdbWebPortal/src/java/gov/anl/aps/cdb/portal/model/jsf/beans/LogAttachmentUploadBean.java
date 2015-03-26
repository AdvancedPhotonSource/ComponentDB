
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.common.utilities.FileUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;

import org.primefaces.model.UploadedFile;

@Named("logAttachmentUploadBean")
@RequestScoped
public class LogAttachmentUploadBean {

    private static final Logger logger = Logger.getLogger(LogAttachmentUploadBean.class.getName());

    private UploadedFile uploadedFile;
    private Log logEntry;

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public Log getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(Log logEntry) {
        this.logEntry = logEntry;
    }

    public void upload(Log logEntry) {
        this.logEntry = logEntry;
        Path uploadDirPath;
        try {
            if (uploadedFile != null && !uploadedFile.getFileName().isEmpty()) {
                String uploadedExtension = FileUtility.getFileExtension(uploadedFile.getFileName());
      
                uploadDirPath = Paths.get(StorageUtility.getFileSystemLogAttachmentsDirectory());
                logger.debug("Using log attachments directory: " + uploadDirPath.toString());
                if (Files.notExists(uploadDirPath)) {
                    Files.createDirectory(uploadDirPath);
                }
                File uploadDir = uploadDirPath.toFile();
                
                String originalExtension = "." + uploadedExtension;
                File originalFile = File.createTempFile("attachment.", originalExtension, uploadDir);
                InputStream input = uploadedFile.getInputstream();
                Files.copy(input, originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.debug("Saved file: " + originalFile.toPath());
                Attachment attachment = new Attachment();
                attachment.setName(originalFile.getName());
                List<Attachment> attachmentList = logEntry.getAttachmentList();
                if (attachmentList == null) {
                    attachmentList = new ArrayList<>();
                    logEntry.setAttachmentList(attachmentList);
                }
                attachmentList.add(attachment);
                SessionUtility.addInfoMessage("Success", "Uploaded file " + uploadedFile.getFileName() + ".");
            }
        } 
        catch (IOException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.toString());
        }
    }
}
