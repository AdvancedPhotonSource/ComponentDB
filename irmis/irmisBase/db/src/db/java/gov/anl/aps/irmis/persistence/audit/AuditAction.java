/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.audit;

import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * IRMIS business object that represents a single audit trail entry.
 * This class is mapped via hibernate almost one-to-one with IRMIS audit_action table.
 */
public class AuditAction extends IRMISDataObject {

    private AuditActionType actionType;
    private Person person;
    private Long actionKey;
    private String actionDescription;
    private Date actionDate;

    /**
     * Constructor
     */
    public AuditAction() {
    }

    /**
     * Get the type of this audit action.
     */
    public AuditActionType getActionType() {
        return this.actionType;
    }
    /**
     * Set the type of this audit action.
     */
    public void setActionType(AuditActionType value) {
        this.actionType = value;
    }

    /**
     * Get the person who committed this action.
     */
    public Person getPerson() {
        return this.person;
    }
    /**
     * Set the person who committed this action.
     */
    public void setPerson(Person value) {
        this.person = value;
    }

    /**
     * Get the foreign key for this action type.
     */
    public Long getActionKey() {
        return this.actionKey;
    }
    /**
     * Set the foreign key for this action type.
     */    
    public void setActionKey(Long value) {
        this.actionKey = value;
    }

    /**
     * Get the description for this action.
     */
    public String getActionDescription() {
        return this.actionDescription;
    }
    /**
     * Set the description for this action.
     */    
    public void setActionDescription(String value) {
        this.actionDescription = value;
    }

    /**
     * Get the date for this action.
     */
    public Date getActionDate() {
        return this.actionDate;
    }
    /**
     * Set the date for this action.
     */    
    public void setActionDate(Date value) {
        this.actionDate = value;
    }

    public String toString() {
        return getActionType().getActionName()+":"+getActionDescription()+":"+getPerson();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof AuditAction) ) return false;
        final AuditAction castOther = (AuditAction) other;
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

}
