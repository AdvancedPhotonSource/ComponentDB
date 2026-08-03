/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.utilities;

import gov.anl.aps.cdb.common.utilities.FileUtility;
import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;
import gov.anl.aps.cdb.rest.entities.FileUploadObject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

/**
 * Reusable helpers for handling REST file uploads.
 *
 * @author djarosz
 */
public class FileUploadUtility {

    private FileUploadUtility() {
    }

    /**
     * Decode the base64-encoded payload of a REST file upload into a stream.
     *
     * @param fileUpload upload object carrying the base64 binary
     * @return stream over the decoded bytes
     */
    public static ByteArrayInputStream decodeFileUpload(FileUploadObject fileUpload) {
        byte[] decode = Base64.getDecoder().decode(fileUpload.getBase64Binary());
        return new ByteArrayInputStream(decode);
    }

    /**
     * Persist an uploaded file to the log attachment storage directory and build
     * the corresponding (unmanaged) Attachment entity.
     *
     * @param originalFileName original name of the uploaded file
     * @param input stream over the file contents
     * @return an Attachment referencing the stored file (not yet persisted)
     * @throws IOException if the file cannot be written
     */
    public static Attachment writeLogAttachmentFile(String originalFileName, InputStream input) throws IOException {
        Path uploadDirPath = Paths.get(StorageUtility.getFileSystemLogAttachmentsDirectory());
        if (Files.notExists(uploadDirPath)) {
            Files.createDirectory(uploadDirPath);
        }
        File storedFile = File.createTempFile(
                "attachment.", "." + FileUtility.getFileExtension(originalFileName), uploadDirPath.toFile());
        Files.copy(input, storedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Attachment attachment = new Attachment();
        attachment.setName(storedFile.getName());
        attachment.setOriginalFilename(originalFileName);
        return attachment;
    }

}
