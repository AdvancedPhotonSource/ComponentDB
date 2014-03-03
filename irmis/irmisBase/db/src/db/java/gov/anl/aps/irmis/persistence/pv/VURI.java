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
 * IRMIS business object 
 * This class is mapped via hibernate almost one-to-one with IRMIS vuri table.
 */
public class VURI extends IRMISDataObject {

    private URI uri;
    private Set edges = new HashSet();

    /**
     * Constructor
     */
    public VURI() {
    }

    /**
     * Get the actual URI used to represent the file system resource.
     */
    public URI getUri() {
        return this.uri;
    }
    /**
     * Set the actual URI used to represent the file system resource.
     */
    public void setUri(URI value) {
        this.uri = value;
    }

    /**
     * Get the set of VURI relationship edges (parents) of this VURI.
     */
    public Set getEdges() {
        return this.edges;
    }
    /**
     * Set the set of VURI relationship edges (parents) of this VURI.
     */
    public void setEdges(Set value) {
        this.edges = value;
    }
    /**
     * Add one VURIRelationship to the set of associated edges. Establishes bidirectional
     * identity (ie. edge knows which VURI it belongs to as well).
     */
    public void addEdge(VURIRelationship vuriRel) {
        vuriRel.setChildVuri(this);
        edges.add(vuriRel);
    }    

    public String toString() {
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof VURI) ) return false;
        final VURI castOther = (VURI) other;
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
