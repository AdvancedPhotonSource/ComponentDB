/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
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
    
    public static String shortenFileNameIfNeeded(String fileName, String fallbackName, int maxSize) {
        if (fileName.length() <= maxSize) {
            return fileName; 
        }                 
                
        int extIndex = fileName.lastIndexOf('.');
        String extension = fileName.substring(extIndex + 1);
        
        if (extension.length() > maxSize) {
            return fallbackName; 
        }
        
        maxSize = maxSize - extension.length();        
        String fileNamePart = fileName.substring(0, maxSize - 1);         
        fileName = String.format("%s.%s", fileNamePart, extension); 
        
        return fileName; 
    }

}
