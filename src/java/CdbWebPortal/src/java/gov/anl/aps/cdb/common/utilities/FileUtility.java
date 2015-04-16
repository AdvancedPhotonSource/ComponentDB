/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: $
 *   $Date: $
 *   $Revision: $
 *   $Author: $
 */
package gov.anl.aps.cdb.common.utilities;

/**
 * Utility class for manipulating files.
 */
public class FileUtility {

    /**
     * Get file extension.
     *
     * @param fileName file name
     * @return file extension
     */
    public static String getFileExtension(String fileName) {
        String extension = "";
        if (fileName != null && !fileName.isEmpty()) {
            int extIndex = fileName.lastIndexOf('.');
            int dirIndex = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
            if (extIndex > dirIndex) {
                extension = fileName.substring(extIndex + 1);
            }
        }
        return extension.toLowerCase();
    }

}
