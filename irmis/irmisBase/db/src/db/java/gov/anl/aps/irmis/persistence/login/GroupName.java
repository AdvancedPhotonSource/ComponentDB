/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.login;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;

/**
 * IRMIS business object that represents a single group name. 
 * This class is mapped via hibernate almost one-to-one with IRMIS group_name table.
 */
public class GroupName extends IRMISDataObject {

    // enum of group names
    public static final String CONTROLS = "controls";

    private String groupName;

    /**
     * Constructor
     */
    public GroupName() {
    }

    /**
     * Get the group name.
     */
    public String getGroupName() {
        return this.groupName;
    }
    /**
     * Set the group name.
     */    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String toString() {
        return getGroupName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof GroupName) ) return false;
        final GroupName castOther = (GroupName) other;
        return this.getGroupName().equals(castOther.getGroupName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getGroupName());
        return result;
    }

}
