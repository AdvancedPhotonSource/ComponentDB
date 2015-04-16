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

/**
 * Object utility class.
 */
public class ObjectUtility {

    /**
     * Verify that two objects are the same.
     *
     * Object references can be null.
     *
     * @param <Type> template type of given objects
     * @param object1 first object
     * @param object2 second object
     * @return true if objects are equal, false otherwise
     */
    public static <Type> boolean equals(Type object1, Type object2) {
        if (object1 == null && object2 == null) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        return object1.equals(object2);
    }
}
