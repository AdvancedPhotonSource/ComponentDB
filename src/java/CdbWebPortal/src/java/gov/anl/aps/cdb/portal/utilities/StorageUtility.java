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

    public static String getApplicationLogAttachmentsDirectory() {
        return LogAttachmentsDirectory;
    }
    
    public static String getFullFilePath(String rootPath, String fileName) {
        if (fileName != null) {
            return rootPath + "/" + fileName;
        }
        return null;
    }
    
    public static String getApplicationLogAttachmentPath(String attachmentName) {
        return getFullFilePath(SessionUtility.getContextRoot() + getApplicationLogAttachmentsDirectory(), attachmentName);
    }

    public static String getFileSystemLogAttachmentsDirectory() {
        return STORAGE_DIRECTORY + getApplicationLogAttachmentsDirectory();
    }

    public static String getFileSystemLogAttachmentPath(String attachmentName) {
        return getFullFilePath(getFileSystemLogAttachmentsDirectory(), attachmentName);
    }

    public static String getApplicationPropertyValueDocumentsDirectory() {
        return PropertyValueDocumentsDirectory;
    }

    public static String getApplicationPropertyValueDocumentPath(String documentName) {
        return getFullFilePath(SessionUtility.getContextRoot() + getApplicationPropertyValueDocumentsDirectory(), documentName);
    }

    public static String getFileSystemPropertyValueDocumentsDirectory() {
        return STORAGE_DIRECTORY + getApplicationPropertyValueDocumentsDirectory();
    }

    public static String getFileSystemPropertyValueDocumentPath(String documentName) {
        return getFullFilePath(getFileSystemPropertyValueDocumentsDirectory(), documentName);
    }
    
    public static String getApplicationPropertyValueImagesDirectory() {
        return PropertyValueImagesDirectory;
    }
    
    public static String getApplicationPropertyValueImagePath(String imageName) {
        return getFullFilePath(SessionUtility.getContextRoot() + getApplicationPropertyValueImagesDirectory(), imageName);
    }
    
    public static String getFileSystemPropertyValueImagesDirectory() {
        return STORAGE_DIRECTORY + getApplicationPropertyValueImagesDirectory();
    }

    public static String getFileSystemPropertyValueImagePath(String imageName) {
        return getFullFilePath(getFileSystemPropertyValueImagesDirectory(), imageName);
    }
    
}
