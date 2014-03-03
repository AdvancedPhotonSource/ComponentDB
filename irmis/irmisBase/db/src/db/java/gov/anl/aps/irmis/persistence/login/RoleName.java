/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.login;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;

/**
 * IRMIS business object that represents a single role name. A <code>Person</code> has
 * zero or more "roles" associated with them. This class represents the text name of
 * that role.
 * This class is mapped via hibernate almost one-to-one with IRMIS role_name table.
 */
public class RoleName extends IRMISDataObject {

    // some role names required to do security within IRMIS desktop
    public static final String ADMIN = "irmis:admin";
    public static final String IOC_EDITOR = "irmis:ioc-editor";
    public static final String COMPONENT_TYPE_EDITOR = "irmis:component-type-editor";
    public static final String COMPONENT_TYPE_PORT_EDITOR = "irmis:component-type-port-editor";
    public static final String COMPONENT_EDITOR = "irmis:component-editor";
    public static final String CABLE_EDITOR = "irmis:cable-editor";

    private String roleName;

    /**
     * Constructor
     */
    public RoleName() {
    }

    /**
     * Get the role name.
     */
    public String getRoleName() {
        return this.roleName;
    }
    /**
     * Set the role name.
     */    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String toString() {
        return getRoleName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof RoleName) ) return false;
        final RoleName castOther = (RoleName) other;
        return this.getRoleName().equals(castOther.getRoleName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getRoleName());
        return result;
    }

}
