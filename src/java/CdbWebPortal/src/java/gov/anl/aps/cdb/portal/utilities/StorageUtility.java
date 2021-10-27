/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.utilities;

import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import java.io.File;

/**
 * Utility class for manipulating file system storage.
 */
public class StorageUtility {

    public static final String STORAGE_DIRECTORY = ConfigurationUtility.getPortalProperty(CdbProperty.STORAGE_DIRECTORY_PROPERTY_NAME);
    public static final Integer SCALED_IMAGE_SIZE = ConfigurationUtility.getPortalPropertyAsInteger(CdbProperty.SCALED_IMAGE_SIZE_PROPERTY_NAME);
    public static final Integer THUMBNAIL_IMAGE_SIZE = ConfigurationUtility.getPortalPropertyAsInteger(CdbProperty.THUMBNAIL_IMAGE_PROPERTY_NAME);

    
    private static final String PropertyValueImagesDirectory = "/propertyValue/images";
    private static final String PropertyValueDocumentsDirectory = "/propertyValue/documents";
    private static final String LogAttachmentsDirectory = "/log/attachments";
    private static final String MAARCPreviewsDirectory = "/propertyValue/maarc";
    private static final String API_DOWNLOAD_GET_PATH = "/api/Downloads/PropertyValue/";

    public static String getFullFilePath(String rootPath, String fileName) {
        if (fileName != null) {
            return rootPath + "/" + fileName;
        }
        return null;
    }

    //
    // Log attachments
    //
    public static String getLogAttachmentsDirectory() {
        return LogAttachmentsDirectory;
    }

    public static String getLogAttachmentPath(String attachmentName) {
        return getFullFilePath(getLogAttachmentsDirectory(), attachmentName);
    }

    public static String getApplicationLogAttachmentPath(String attachmentName) {
        return SessionUtility.getContextRoot() + getLogAttachmentPath(attachmentName);
    }

    public static String getFileSystemLogAttachmentsDirectory() {
        return STORAGE_DIRECTORY + getLogAttachmentsDirectory();
    }

    public static String getFileSystemLogAttachmentPath(String attachmentName) {
        return getFullFilePath(getFileSystemLogAttachmentsDirectory(), attachmentName);
    }

    //
    // Property value documents
    //
    public static String getPropertyValueDocumentsDirectory() {
        return PropertyValueDocumentsDirectory;
    }

    public static String getPropertyValueDocumentPath(String documentName) {
        return getFullFilePath(getPropertyValueDocumentsDirectory(), documentName);
    }

    public static String getApplicationPropertyValueDocumentPath(String documentName) {
        return SessionUtility.getContextRoot() + getPropertyValueDocumentPath(documentName);
    }

    public static String getFileSystemPropertyValueDocumentsDirectory() {
        return STORAGE_DIRECTORY + getPropertyValueDocumentsDirectory();
    }

    public static String getFileSystemPropertyValueDocumentPath(String documentName) {
        return getFullFilePath(getFileSystemPropertyValueDocumentsDirectory(), documentName);
    }

    //
    // Property value images
    //
    public static String getPropertyValueImagesDirectory() {
        return PropertyValueImagesDirectory;
    }

    public static String getPropertyValueImagePath(String imageName, String type) {
        return getPropertyValueImagePath(imageName, type, true);
    }

    public static String getMAARCPreviewsDirectory() {
        return MAARCPreviewsDirectory;
    }

    public static String getMAARCPreviewsPath(String fileName) {
        return getFullFilePath(getMAARCPreviewsDirectory(), fileName);
    }

    public static String getApplicationMAARCPreviewsPath(String fileName) {
        return SessionUtility.getContextRoot() + getMAARCPreviewsPath(fileName);
    }

    public static String getFileSystemMAARCPreviewsDirectory() {
        return STORAGE_DIRECTORY + getMAARCPreviewsDirectory();
    }

    public static boolean isFileExist(String fileAppPath) {
        String fsPath = STORAGE_DIRECTORY + fileAppPath;
        return (new File(fsPath)).isFile();
    }

    public static String getPropertyValueImagePath(String imageName, String type, Boolean verifyFileExists) {
        String imagePath;

        if (imageName.startsWith(CdbPropertyValue.IMAGE_PREFIX)) {
            imagePath = getFullFilePath(getPropertyValueImagesDirectory(), imageName + type);
        } else {
            imagePath = getFullFilePath(getPropertyValueDocumentsDirectory(), imageName + type);
        }

        if (verifyFileExists) {            
            Boolean fileExists = isFileExist(imagePath);
            if (fileExists) {
                return imagePath;
            } else {
                return getMissingImageDefaultPreview(imageName, imagePath, type);
            }
        } else {
            return imagePath;
        }
    }

    public static String getMissingImageDefaultPreview(String imageName, String alternatePath, String type) {
        String format = GalleryUtility.getImageFormat(imageName);
        String dataType;

        if (format.equalsIgnoreCase("pdf")) {
            dataType = "pdf";
        } else {
            if (GalleryUtility.viewableFormat(format)) {
                dataType = "image";
            } else {
                return alternatePath;
            }
        }

        return "/resources/images/" + dataType + "-gallery.png" + type;
    }
    
    public static String getApiDownloadByPropertyValueIdPath(Integer propertyValueId) {
        String contextRoot = SessionUtility.getContextRoot();
        return contextRoot + API_DOWNLOAD_GET_PATH + propertyValueId;
    }

    public static String getApplicationPropertyValueImagePath(String imageName) {
        if (imageName == null) {
            return "";
        }
        if (imageName.startsWith(CdbPropertyValue.IMAGE_PREFIX)) {
            return SessionUtility.getContextRoot() + getPropertyValueImagePath(imageName, CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION, false);
        } else {
            return SessionUtility.getContextRoot() + getPropertyValueImagePath(imageName, "", false);
        }
    }

    public static String getFileSystemPropertyValueImagesDirectory() {
        return STORAGE_DIRECTORY + getPropertyValueImagesDirectory();
    }

    public static String getFileSystemPropertyValueImagePath(String imageName) {
        return getFullFilePath(getFileSystemPropertyValueImagesDirectory(), imageName);
    }

}
