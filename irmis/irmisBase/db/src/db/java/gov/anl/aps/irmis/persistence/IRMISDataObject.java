/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence;

import java.io.Serializable;

/**
 * Base class for all IRMIS business objects that are backed by a relational data store.
 */
public class IRMISDataObject implements Serializable {

    private Long id;
    private boolean markForDelete = false;
    private int version;

    /**
     * Get primary key given by backing relational data store.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Set primary key for this object. Not to be used by user code.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the mark-for-delete state of this object.
     */
    public boolean getMarkForDelete() {
        return markForDelete;
    }
    /**
     * Set the mark-for-delete state of this object. This boolean may be
     * used by the underlying persistence mechanism to mark the database
     * record rather than actually deleting it.
     */
    public void setMarkForDelete(boolean value) {
        this.markForDelete = value;
    }

    /**
     * Used by hibernate to manage application scope transactions. 
     */
    public int getVersion() {
        return version;
    }

    /**
     * Used by hibernate to manage application scope transactions.
     * Not to be used by user code.
     */
    public void setVersion(int version) {
        this.version = version;
    }

    
}
