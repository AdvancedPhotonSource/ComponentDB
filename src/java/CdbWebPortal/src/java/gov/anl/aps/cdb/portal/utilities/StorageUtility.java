/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.utilities;

import gov.anl.aps.cdb.common.constants.CdbProperty;

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
    
    public static String getPropertyValueImagePath(String imageName) {
        return getFullFilePath(getPropertyValueImagesDirectory(), imageName);
    }
    
    public static String getApplicationPropertyValueImagePath(String imageName) {
        return SessionUtility.getContextRoot() + getPropertyValueImagePath(imageName);
    }
    
    public static String getFileSystemPropertyValueImagesDirectory() {
        return STORAGE_DIRECTORY + getPropertyValueImagesDirectory();
    }

    public static String getFileSystemPropertyValueImagePath(String imageName) {
        return getFullFilePath(getFileSystemPropertyValueImagesDirectory(), imageName);
    }
    
}
