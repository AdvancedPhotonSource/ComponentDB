package gov.anl.aps.cdb.portal.utilities;

import gov.anl.aps.cdb.portal.constants.CdbProperty;

public class StorageUtility
{
    public static final String StorageDirectory = ConfigurationUtility.getPortalProperty(CdbProperty.StorageDirectoryPropertyName);
    
    public static String getApplicationPropertyValueImagesDirectory() {
        return "/propertyValue/images";
    }
    
        public static String getFileSystemPropertyValueImagesDirectory() {
        return StorageDirectory + getApplicationPropertyValueImagesDirectory();
    }
}
