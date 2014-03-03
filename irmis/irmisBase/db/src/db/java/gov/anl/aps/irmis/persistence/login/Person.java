/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.login;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * IRMIS business object that represents a single person (user account).
 * This class is mapped via hibernate almost one-to-one with IRMIS person table.
 */
public class Person extends IRMISDataObject implements Comparable {

    private Set roles = new HashSet();
    private String userid;
    private String firstName;
    private String middleName;
    private String lastName;

    /**
     * Constructor
     */
    public Person() {
    }

    /**
     * Get the complete set of Roles associated with this person.
     *
     * @see Role
     */
    public Set getRoles() {
        return this.roles;
    }
    /**
     * Set the complete set of Roles associated with this person.
     *
     * @see Role
     */
    public void setRoles(Set value) {
        this.roles = value;
    }
    /**
     * Add one Role to set of known roles. Establishes bidirectional
     * identity (ie. role knows which Person it belongs to as well).
     */
    public void addRole(Role role) {
        role.setPerson(this);
        roles.add(role);
    }

    /**
     * Get the person's userid.
     */
    public String getUserid() {
        return this.userid;
    }
    /**
     * Set the person's userid.
     */    
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * Get first name.
     */
    public String getFirstName() {
        return this.firstName;
    }
    public void setFirstName(String value) {
        this.firstName = value;
    }
    /**
     * Get middle name.
     */
    public String getMiddleName() {
        return this.middleName;
    }
    public void setMiddleName(String value) {
        this.middleName = value;
    }
    /**
     * Get last name.
     */
    public String getLastName() {
        return this.lastName;
    }
    public void setLastName(String value) {
        this.lastName = value;
    }

    public String toString() {
        return getLastName()+", "+getFirstName()+" ("+getUserid()+")";
    }

    // have the set of these objects be sorted by the persons last name
    public int compareTo(Object o) {
        Person other = (Person)o;

        String n1 = this.getLastName();
        String n2 = other.getLastName();

        return n1.compareTo(n2);
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof Person) ) return false;
        final Person castOther = (Person) other;
        return this.getUserid().equals(castOther.getUserid());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getUserid());
        return result;
    }

}
