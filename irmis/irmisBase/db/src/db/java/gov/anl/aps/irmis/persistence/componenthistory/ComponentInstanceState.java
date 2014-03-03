/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.componenthistory;

import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;

/**
 * IRMIS business object that represents a change in state (history) of a
 * component instance. Basically, this is for a time-based record of what
 * changes a component undergoes, including physical relocation, failure, etc.
 * This class is mapped via hibernate almost one-to-one with IRMIS 
 * component_instance_state table.
 */
public class ComponentInstanceState extends IRMISDataObject implements Comparable {

    private ComponentInstance componentInstance;
    private ComponentState componentState;
    private Person person;
    private Date enteredDate;
    private String comment;
    private String referenceData1;
    private Date referenceData2;

    /**
     * Do-nothing constructor.
     */
    public ComponentInstanceState() {
    }

    /**
     * 
     */
    public ComponentInstance getComponentInstance() {
        return componentInstance;
    }
    /**
     * 
     */
    public void setComponentInstance(ComponentInstance value) {
        componentInstance = value;
    }

    /**
     * 
     */
    public ComponentState getComponentState() {
        return componentState;
    }
    /**
     * 
     */
    public void setComponentState(ComponentState value) {
        componentState = value;
    }

    /**
     * 
     */
    public Person getPerson() {
        return person;
    }
    /**
     * 
     */
    public void setPerson(Person value) {
        person = value;
    }

    /**
     * 
     */
    public Date getEnteredDate() {
        return enteredDate;
    }
    /**
     * 
     */
    public void setEnteredDate(Date value) {
        enteredDate = value;
    }

    /**
     * 
     */
    public String getComment() {
        return comment;
    }
    /**
     * 
     */
    public void setComment(String value) {
        comment = value;
    }

    /**
     * 
     */
    public String getReferenceData1() {
        return referenceData1;
    }
    /**
     * 
     */
    public void setReferenceData1(String value) {
        referenceData1 = value;
    }

    /**
     * 
     */
    public Date getReferenceData2() {
        return referenceData2;
    }
    /**
     * 
     */
    public void setReferenceData2(Date value) {
        referenceData2 = value;
    }

    public String toString() {
        return getId().toString();
    }

    /**
     * Have any collection of these objects be sorted by the entered date.
     */
    public int compareTo(Object o) {
        ComponentInstanceState other = (ComponentInstanceState)o;
        return this.getEnteredDate().compareTo(other.getEnteredDate());
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        return false;  // we can always have multiple state entries, even duplicates
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getId());
        return result;
    }

}
