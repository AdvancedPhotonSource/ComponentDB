/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * IRMIS business object that represents an edge in the VURI relationship graph.
 * This class is mapped via hibernate almost one-to-one with IRMIS vuri_rel table.
 */
public class VURIRelationship extends IRMISDataObject {

    private VURI parentVuri;
    private VURI childVuri;
    private String relationshipInfo;

    /**
     * Constructor
     */
    public VURIRelationship() {
    }

    /**
     * Get the parent VURI 
     */
    public VURI getParentVuri() {
        return this.parentVuri;
    }
    /**
     * Set the parent VURI.
     */
    public void setParentVuri(VURI vuri) {
        this.parentVuri = vuri;
    }

    /**
     * Get the child VURI 
     */
    public VURI getChildVuri() {
        return this.childVuri;
    }
    /**
     * Set the child VURI.
     */
    public void setChildVuri(VURI vuri) {
        this.childVuri = vuri;
    }

    /**
     * Get the 
     */
    public String getRelationshipInfo() {
        return this.relationshipInfo;
    }
    /**
     * Set the 
     */
    public void setRelationshipInfo(String relInfo) {
        this.relationshipInfo = relInfo;
    }

    public String toString() {
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof VURIRelationship) ) return false;
        final VURIRelationship castOther = (VURIRelationship) other;
        return this.getId()==castOther.getId();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result,getId());
        return result;
    }

}

