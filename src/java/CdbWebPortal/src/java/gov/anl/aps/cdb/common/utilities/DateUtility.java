/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/common/utilities/DateUtility.java $
 *   $Date: 2015-04-16 10:32:53 -0500 (Thu, 16 Apr 2015) $
 *   $Revision: 589 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.common.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for manipulating dates.
 */
public class DateUtility {

    private static final SimpleDateFormat DateTimeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Format current date.
     *
     * @return formatted date string
     */
    public static String getCurrentDateTime() {
        return DateTimeFormat.format(new Date());
    }
}
