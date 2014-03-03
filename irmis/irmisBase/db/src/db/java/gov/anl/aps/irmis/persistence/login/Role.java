/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.login;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;

/**
 * IRMIS business object that represents a single role for a person.
 * This class is mapped via hibernate almost one-to-one with IRMIS role table.
 */
public class Role extends IRMISDataObject {

    private Person person;
    private RoleName roleName;

    /**
     * Constructor
     */
    public Role() {
    }

    /**
     * Get Person associated with this role.
     */
    public Person getPerson() {
        return this.person;
    }
    /**
     * Set Person associated with this role.
     */
    public void setPerson(Person value) {
        this.person = value;
    }

    /**
     * Get the name of this role.
     */
    public RoleName getRoleName() {
        return this.roleName;
    }

    /**
     * Set the name of this role.
     */
    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }

    public String toString() {
        return getRoleName().getRoleName();
    }


    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof Role) ) return false;
        final Role castOther = (Role) other;
        return this.getPerson().equals(castOther.getPerson()) &&
            this.getRoleName().equals(castOther.getRoleName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result,getPerson());
        result = HashCodeUtil.hash(result,getRoleName());
        return result;
    }

}
