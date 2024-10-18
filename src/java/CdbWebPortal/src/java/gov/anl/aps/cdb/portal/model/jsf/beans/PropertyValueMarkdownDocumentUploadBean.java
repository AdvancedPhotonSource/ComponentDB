/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.common.utilities.FileUtility;
import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;


/**
 * JSF bean for property value document upload.
 */
@Named("propertyValueMarkdownzDocumentUploadBean")
@SessionScoped
public class PropertyValueMarkdownDocumentUploadBean implements Serializable {

    private static final Logger logger = LogManager.getLogger(PropertyValueMarkdownDocumentUploadBean.class.getName());   
    
    private static final String MARKDOWN_FILE_PREFIX = "md.";
    public static final String MARKDOWN_ATTACHMENT_PREFIX = "/property/";    
    private static final String MARKDOWN_FILE_REF_FORMAT = String.format("[%s](%s)", "%s", MARKDOWN_ATTACHMENT_PREFIX + "%s");
     
    
    private PropertyValue propertyValue; 
    
    private String lastFileReference;

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(PropertyValue propertyValue) {
        this.propertyValue = propertyValue;
    }        
    
    public String getLastFileReference() {
        return lastFileReference;
    }

    public void setLastFileReference(String lastFileReference) {
        this.lastFileReference = lastFileReference;
    }   

    public void upload(UploadedFile uploadedFile) {
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

    public void uploadDocument(PropertyValue propertyValue, String filename, InputStream input) throws IOException {
        Path uploadDirPath;
        String uploadedExtension = FileUtility.getFileExtension(filename);
        uploadDirPath = Paths.get(StorageUtility.getFileSystemPropertyValueMarkdownDocumentsDirectory());
        logger.debug("Using property value markdown documents directory: " + uploadDirPath.toString());

        if (Files.notExists(uploadDirPath)) {
            Files.createDirectory(uploadDirPath);
        }
        File uploadDir = uploadDirPath.toFile();
        
        String originalExtension = "." + uploadedExtension;
        File originalFile = File.createTempFile(MARKDOWN_FILE_PREFIX, originalExtension, uploadDir);
        Files.copy(input, originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        logger.debug("Saved file: " + originalFile.toPath());

        GalleryUtility.storeImagePreviews(originalFile, uploadedExtension);
        String generatedFileName = originalFile.getName();
        
        lastFileReference = String.format(MARKDOWN_FILE_REF_FORMAT, filename, generatedFileName);
        
        if (GalleryUtility.viewableFileName(filename)) {
            lastFileReference = String.format("!%s", lastFileReference); 
        }
        
        Attachment attachment = new Attachment(); 
        attachment.setName(generatedFileName);
        attachment.setOriginalFilename(filename); 
        
        List<Attachment> attachmentList = propertyValue.getAttachmentList();
        if (attachmentList == null) {
            attachmentList = new ArrayList<>(); 
            propertyValue.setAttachmentList(attachmentList); 
        }
        attachmentList.add(attachment); 
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        
        upload(file);
    }
}
