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
 * String utility class.
 */
public class StringUtility {

    /**
     * Verify that two char sequences are the same.
     *
     * Input string references can be null.
     *
     * @param cs1 first sequence
     * @param cs2 second sequence
     * @return true if char sequences are the same, false otherwise
     */
    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        if (cs1 == null && cs2 == null) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        return cs1.equals(cs2);
    }

    /**
     * Capitalize first letter of a given string.
     *
     * @param input input string
     * @return capitalized string
     */
    public static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
