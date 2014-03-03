/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.RoleName;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * IRMIS business object that represents 
 * This class is mapped via hibernate one-to-one with IRMIS component_type_person table.
 */
public class ComponentTypePerson extends IRMISDataObject implements Cloneable {

    private Person person;
    private RoleName roleName;
    private ComponentType componentType;

    /**
     * Do-nothing constructor.
     */
    public ComponentTypePerson() {
    }

    /**
     * 
     */
    public Person getPerson() {
        return this.person;
    }
    /**
     * 
     */
    public void setPerson(Person value) {
        this.person = value;
    }

    /**
     * 
     */
    public RoleName getRoleName() {
        return this.roleName;
    }
    /**
     * 
     */
    public void setRoleName(RoleName value) {
        this.roleName = value;
    }

    /**
     * 
     */
    public ComponentType getComponentType() {
        return this.componentType;
    }
    /**
     * 
     */
    public void setComponentType(ComponentType value) {
        this.componentType = value;
    }


    public String toString() {
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentTypePerson) ) return false;
        final ComponentTypePerson castOther = (ComponentTypePerson)other;
        return this.getId() == castOther.getId();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getId());
        return result;
    }

    /**
     * Performs deep copy of this object. Does not preserve 
     * object id field, since this copy is intended
     * to become part of a new component type.
     */
    public Object clone() throws CloneNotSupportedException {

        if (getId() == null)
            throw new CloneNotSupportedException();
        
        ComponentTypePerson ctpCopy = new ComponentTypePerson();

        // copy attributes of this class    
        ctpCopy.setPerson(getPerson());
        ctpCopy.setRoleName(getRoleName());

        return ctpCopy;
    }

}
