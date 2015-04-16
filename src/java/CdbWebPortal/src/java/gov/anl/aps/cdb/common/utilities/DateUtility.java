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
