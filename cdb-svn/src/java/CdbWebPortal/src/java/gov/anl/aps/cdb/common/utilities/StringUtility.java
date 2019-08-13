/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/common/utilities/StringUtility.java $
 *   $Date: 2015-04-16 10:32:53 -0500 (Thu, 16 Apr 2015) $
 *   $Revision: 589 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.common.utilities;

import java.util.List;
import java.util.regex.Pattern;

/**
 * String utility class.
 */
public class StringUtility {
    
    private static final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
   
    public static boolean isEmailAddressValid(String emailAddress){
        return emailPattern.matcher(emailAddress).matches();
    }

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
     * 
     * @param cdbEntityList
     * @return 
     */
    public static String getStringifyCdbList(List<?> cdbEntityList) {
        String stringRep = "";        
        if (cdbEntityList != null) {
            for (Object entity : cdbEntityList) {
                if (cdbEntityList.indexOf(entity) == cdbEntityList.size() - 1) {                   
                    stringRep += entity.toString();
                } else {
                    stringRep += entity.toString() + " | ";
                }
            };
        }
        
        if (stringRep.isEmpty()) {
            stringRep = "-";
        }

        return stringRep;
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
