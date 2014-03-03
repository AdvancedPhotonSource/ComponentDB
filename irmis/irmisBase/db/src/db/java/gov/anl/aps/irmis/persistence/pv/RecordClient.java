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
 * IRMIS business object that represents a single client reference to a process variable 
 * This class is mapped via hibernate almost one-to-one with IRMIS rec_client table.
 */
public class RecordClient extends IRMISDataObject {

    private String recordName;
    private String fieldType;
    private Long clientTypeId;
    private VURI vuri;
    private boolean currentLoad;

    /**
     * Constructor
     */
    public RecordClient() {
    }

    /**
     * Get the VURI 
     */
    public VURI getVuri() {
        return this.vuri;
    }
    /**
     * Set the VURI.
     */
    public void setVuri(VURI vuri) {
        this.vuri = vuri;
    }

    /**
     * Get the actual process variable name.
     */
    public String getRecordName() {
        return this.recordName;
    }
    /**
     * Set the actual process variable name.
     */
    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    /**
     * Get the actual process variable name.
     */
    public String getFieldType() {
        return this.fieldType;
    }
    /**
     * Set the actual process variable name.
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Get the rec client type id.
     */
    public Long getClientTypeId() {
        return this.clientTypeId;
    }
    /**
     * Set the rec client type id
     */
    public void setClientTypeId(Long id) {
        this.clientTypeId = id;
    }

    /**
     * Get 
     */
    public boolean getCurrentLoad() {
        return this.currentLoad;
    }
    /**
     * Set 
     */
    public void setCurrentLoad(boolean currentLoad) {
        this.currentLoad = currentLoad;
    }

    public String toString() {
        return getId().toString() + ":" + getRecordName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof RecordClient) ) return false;
        final RecordClient castOther = (RecordClient) other;
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

