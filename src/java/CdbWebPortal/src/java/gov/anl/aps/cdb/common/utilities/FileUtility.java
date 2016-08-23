/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/common/utilities/FileUtility.java $
 *   $Date: 2015-04-16 10:32:53 -0500 (Thu, 16 Apr 2015) $
 *   $Revision: 589 $
 *   $Author: sveseli $
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
