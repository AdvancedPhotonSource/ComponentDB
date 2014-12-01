
package gov.anl.aps.cdb.portal.utilities;

/**
 *
 * @author sveseli
 */
public class FileUtility 
{
    public static String getFileExtension(String fileName) 
    {
        String extension = "";
        if (fileName != null && !fileName.isEmpty()) {
            int extIndex = fileName.lastIndexOf('.');
            int dirIndex = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
            if (extIndex > dirIndex) {
                extension = fileName.substring(extIndex+1);    
            }
        }
        return extension.toLowerCase();
    }

}
