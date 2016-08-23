/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/common/utilities/ArgumentUtility.java $
 *   $Date: 2015-04-16 10:32:53 -0500 (Thu, 16 Apr 2015) $
 *   $Revision: 589 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.common.utilities;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;

/**
 * Utility class for processing and checking function arguments.
 */
public class ArgumentUtility {

    /**
     * Check that input string is not null and not empty.
     *
     * @param arg input argument to be checked
     * @return true if input string is not null and not empty, false otherwise
     */
    public static boolean isNonEmptyString(String arg) {
        return arg != null && !arg.isEmpty();
    }

    /**
     * Convert input argument to string.
     *
     * @param arg input argument to be checked
     * @return original argument string representation, or empty string if
     * argument is null
     */
    public static String toNonNullString(Object arg) {
        if (arg == null) {
            return "";
        }
        return arg.toString();
    }

    /**
     * Verify that input string is not null and not empty.
     *
     * @param argName name of the argument to be verified; used for error
     * message
     * @param arg input argument to be checked
     * @throws InvalidArgument if input string is null or empty
     */
    public static void verifyNonEmptyString(String argName, String arg) throws InvalidArgument {
        if (arg == null || arg.isEmpty()) {
            throw new InvalidArgument(argName + " must be non-empty string.");
        }
    }

    /**
     * Verify that input string contains given pattern.
     *
     * @param argName name of the argument to be verified; used for error
     * message
     * @param arg input argument to be checked
     * @param pattern string that must be contained in the input argument
     * @throws InvalidArgument if input string is null or empty, or if it does
     * not contain specified pattern
     */
    public static void verifyStringContainsPattern(String argName, String arg, String pattern) throws InvalidArgument {
        verifyNonEmptyString(argName, arg);
        verifyNonEmptyString("Pattern", pattern);
        if (!arg.contains(pattern)) {
            throw new InvalidArgument(argName + " must contain pattern " + pattern + ".");
        }
    }

    /**
     * Verify that input integer is not null and greater than zero.
     *
     * @param argName name of the argument to be verified; used for error
     * message
     * @param arg input argument to be checked
     * @throws InvalidArgument if input number is null or not positive
     */
    public static void verifyPositiveInteger(String argName, Integer arg) throws InvalidArgument {
        if (arg == null || arg <= 0) {
            throw new InvalidArgument(argName + " must be a positive number.");
        }
    }

    /**
     * Verify that input double is not null and greater than zero.
     *
     * @param argName name of the argument to be verified; used for error
     * message
     * @param arg input argument to be checked
     * @throws InvalidArgument if input number is null or not positive
     */
    public static void verifyPositiveDouble(String argName, Double arg) throws InvalidArgument {
        if (arg == null || arg <= 0) {
            throw new InvalidArgument(argName + " must be a positive number.");
        }
    }

    /**
     * Verify that input object is not null.
     *
     * @param argName name of the argument to be verified; used for error
     * message
     * @param arg input argument to be checked
     * @throws InvalidArgument if input string is null or empty
     */
    public static void verifyNonNullObject(String argName, Object arg) throws InvalidArgument {
        if (arg == null) {
            throw new InvalidArgument(argName + " cannot be null.");
        }
    }

    /**
     * Add (key,value) pair to map if value is not null or empty.
     *
     * @param map target map
     * @param key key
     * @param value string that will be added to map if it is not null or empty
     */
    public static void addNonEmptyKeyValuePair(Map<String, String> map, String key, String value) {
        if (value != null && !value.isEmpty()) {
            map.put(key, value);
        }
    }

    /**
     * Add (key,value) pair to map if value is not null or empty.
     *
     * @param map target map
     * @param key key
     * @param valueObject object that will be added to map if it has non-empty
     * string representation
     */
    public static void addNonEmptyKeyValuePair(Map<String, String> map, String key, Object valueObject) {
        if (valueObject != null) {
            String value = valueObject.toString();
            if (!value.isEmpty()) {
                map.put(key, value);
            }
        }
    }

    /**
     * Base 64 encode.
     *
     * @param input input string
     * @return base 64 encoded string
     * @throws CdbException in case of any errors
     */
    public static String encode(String input) throws CdbException {
        try {
            // Input is twice encoded in order to avoid issues like
            // '+' being interpreted as space
            if (input == null) {
                return input;
            }
            String s1 = DatatypeConverter.printBase64Binary(input.getBytes());
            String s2 = DatatypeConverter.printBase64Binary(s1.getBytes());
            return s2;
        } catch (Exception ex) {
            throw new CdbException(ex);
        }
    }

    /**
     * Base 64 decode.
     *
     * @param input base 64 encoded string
     * @return decoded string
     * @throws CdbException in case of any errors
     */
    public static String decode(String input) throws CdbException {
        try {
            // Input is twice encoded in order to avoid issues like
            // '+' being interpreted as space
            byte[] ba1 = DatatypeConverter.parseBase64Binary(input);
            byte[] ba2 = DatatypeConverter.parseBase64Binary(new String(ba1));
            return new String(ba2);
        } catch (Exception ex) {
            throw new CdbException(ex);
        }
    }
}
