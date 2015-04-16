/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.common.utilities;

import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;

/**
 * Utility class for manipulating HTTP links.
 */
public class HttpLinkUtility {

    /**
     * Predefined link display length.
     */
    public static final int HTTP_LINK_DISPLAY_LENGTH = ConfigurationUtility.getPortalPropertyAsInteger(
            CdbProperty.HTTP_LINK_DISPLAY_LENGTH_PROPERTY_NAME);

    /**
     * Prepare display value for a given URL.
     *
     * @param url HTTP link
     * @return link display value
     */
    public static String prepareHttpLinkDisplayValue(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String displayValue = url;
        int length = url.length();
        if (length > HTTP_LINK_DISPLAY_LENGTH) {
            int partLength = HTTP_LINK_DISPLAY_LENGTH / 2 - 1;
            displayValue = displayValue.substring(0, partLength) + "..." + displayValue.substring(length - partLength);
        }
        return displayValue;
    }

    /**
     * Prepare target value for a given URL.
     *
     * @param url HTTP link
     * @return link target value
     */
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
