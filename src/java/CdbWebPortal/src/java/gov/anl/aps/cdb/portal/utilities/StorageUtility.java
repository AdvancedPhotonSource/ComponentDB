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

    public static String getApplicationPropertyValueImagesDirectory() {
        return "/propertyValue/images";
    }

    public static String getFileSystemPropertyValueImagesDirectory() {
        return STORAGE_DIRECTORY + getApplicationPropertyValueImagesDirectory();
    }

    public static String getApplicationLogAttachmentsDirectory() {
        return "/log/attachments";
    }

    public static String getApplicationLogAttachmentPathDirectory(String attachmentName) {
        if (attachmentName != null) {
            return SessionUtility.getContextRoot() + getApplicationLogAttachmentsDirectory() + "/" + attachmentName;
        }
        return null;
    }

    public static String getFileSystemLogAttachmentsDirectory() {
        return STORAGE_DIRECTORY + getApplicationLogAttachmentsDirectory();
    }

    public static String getFileSystemLogAttachmentPathDirectory(String attachmentName) {
        if (attachmentName != null) {
            return getFileSystemLogAttachmentsDirectory() + "/" + attachmentName;
        }
        return null;
    }

    public static String getApplicationPropertyValueDocumentsDirectory() {
        return "/propertyValue/documents";
    }

    public static String getApplicationPropertyValueDocumentPathDirectory(String documentName) {
        if (documentName != null) {
            return SessionUtility.getContextRoot() + getApplicationPropertyValueDocumentsDirectory() + "/" + documentName;
        }
        return null;
    }

    public static String getFileSystemPropertyValueDocumentsDirectory() {
        return STORAGE_DIRECTORY + getApplicationPropertyValueDocumentsDirectory();
    }

    public static String getFileSystemPropertyValueDocumentPathDirectory(String documentName) {
        if (documentName != null) {
            return getFileSystemPropertyValueDocumentsDirectory() + "/" + documentName;
        }
        return null;
    }
}
