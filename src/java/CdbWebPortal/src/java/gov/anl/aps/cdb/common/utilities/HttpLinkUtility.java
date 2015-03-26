package gov.anl.aps.cdb.common.utilities;

import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.constants.CdbServiceProtocol;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;


public class HttpLinkUtility {

    public static final int HttpLinkDisplayLength = ConfigurationUtility.getPortalPropertyAsInteger(
            CdbProperty.HTTP_LINK_DISPLAY_LENGTH_PROPERTY_NAME);

    
    public static String prepareHttpLinkDisplayValue(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String displayValue = url;
        int length = url.length();
        if (length > HttpLinkDisplayLength) {
            int partLength = HttpLinkDisplayLength / 2 - 1;
            displayValue = displayValue.substring(0, partLength) + "..." + displayValue.substring(length - partLength);
        }
        return displayValue;
    }

    public static String prepareHttpLinkTargetValue(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String targetValue = url;
        if (!targetValue.contains("://")) {
            targetValue = "http://" + url;
        }
        return targetValue;
    }    
    
}
