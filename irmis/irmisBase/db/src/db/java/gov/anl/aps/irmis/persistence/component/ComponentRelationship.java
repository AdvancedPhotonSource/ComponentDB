/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import gov.anl.aps.irmis.persistence.login.Person;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import org.hibernate.*;
import org.hibernate.classic.Lifecycle;

/**
 * IRMIS business object that represents 
 * This class is mapped via hibernate one-to-one with IRMIS component_rel table.
 */
public class ComponentRelationship extends IRMISDataObject implements Lifecycle {

    private Component parentComponent;
    private Component childComponent;
    private ComponentRelationshipType relationshipType;
    private Person verifiedPerson;
    private int logicalOrder;
    private String logicalDescription;

    /**
     * Do-nothing constructor. 
     */
    public ComponentRelationship() {
    }

    /**
     *
     */
    public Component getParentComponent() {
        return parentComponent;
    }
    /**
     *
     */
    public void setParentComponent(Component value) {
        parentComponent = value;
    }

    /**
     *
     */
    public Component getChildComponent() {
        return childComponent;
    }
    /**
     *
     */
    public void setChildComponent(Component value) {
        childComponent = value;
    }

    /**
     *
     */
    public ComponentRelationshipType getRelationshipType() {
        return relationshipType;
    }
    /**
     *
     */
    public void setRelationshipType(ComponentRelationshipType value) {
        relationshipType = value;
    }

    /**
     * This person is the one who verified the correctness of this parent-child
     * component relationship. If null, this relationship has not been verified.
     */
    public Person getVerifiedPerson() {
        return this.verifiedPerson;
    }
    /**
     * 
     */
    public void setVerifiedPerson(Person value) {
        this.verifiedPerson = value;
    }

    /**
     *
     */
    public int getLogicalOrder() {
        return logicalOrder;
    }
    /**
     *
     */
    public void setLogicalOrder(int value) {
        logicalOrder = value;
    }

    /**
     *
     */
    public String getLogicalDescription() {
        return logicalDescription;
    }
    /**
     *
     */
    public void setLogicalDescription(String value) {
        logicalDescription = value;
    }

    public String toString() {
        /*
        return Integer.toString(getLogicalOrder()) + ":" +
            getLogicalDescription();
        */
        return getLogicalDescription();
    }


    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentRelationship) ) return false;
        final ComponentRelationship castOther = (ComponentRelationship) other;
        // dual condition: first enforces single parent for a given child for a given relationship
        //                 second enforces unique logical order for children of a given parent
        return ((this.getChildComponent().getId() == castOther.getChildComponent().getId() &&
                 this.getRelationshipType().equals(castOther.getRelationshipType())) ||
                (this.getParentComponent().getId() == castOther.getParentComponent().getId() &&
                 this.getRelationshipType().equals(castOther.getRelationshipType()) &&
                 this.getLogicalOrder() == castOther.getLogicalOrder()));
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getParentComponent().getId());
        result = HashCodeUtil.hash(result, getChildComponent().getId());
        result = HashCodeUtil.hash(result, getRelationshipType());
        result = HashCodeUtil.hash(result, getLogicalOrder());
        return result;
    }

    /************************************************************
     * Implementation of Hibernate Lifecycle interface. Yes,
     * this violates layers, but we are still on hibernate
     * 2.x. The newer version 3.x has an event driven lifecycle
     * mechanism, which keeps our business objects strictly
     * business. But what we have here is pretty trivial.
     ************************************************************/
    public boolean onDelete(Session s) throws CallbackException {
        // if we are referenced in either parent or child component, veto delete
        int hierarchy = getRelationshipType().getId().intValue();
        if (getParentComponent().getChildRelationships(hierarchy).contains(this) ||
            getChildComponent().getParentRelationships().contains(this))
            return Lifecycle.VETO;
        else
            return Lifecycle.NO_VETO;
    }
    public void onLoad(Session s, Serializable id) {
        return;
    }
    public boolean onSave(Session s) {
        return Lifecycle.NO_VETO;
    }
    public boolean onUpdate(Session s) {
        return Lifecycle.NO_VETO;
    }
    // end Lifecycle implementation
}
