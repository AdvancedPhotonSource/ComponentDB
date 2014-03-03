/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;


/**
 * IRMIS business object that represents ioc status enumeration.
 * This class is mapped via hibernate one-to-one with IRMIS ioc_status table.
 */
public class IOCStatus extends IRMISDataObject {

    // Note: the below enumeration must match id column of mapped table
    public static final int PRODUCTION = 1;
    public static final int INACTIVE = 2;
    public static final int ANCILLARY = 3;
    public static final int DEVELOPMENT = 4;

    private String name;

    /**
     * Do-nothing constructor.
     */
    public IOCStatus() {
    }

    /**
     * 
     */
    public String getName() {
        return this.name;
    }
    /**
     * 
     */
    public void setName(String value) {
        this.name = value;
    }

    public String toString() {
        return name;
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof IOCStatus) ) return false;
        final IOCStatus castOther = (IOCStatus) other;
        return this.getName().equals(castOther.getName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getName());
        return result;
    }

}
