/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * IRMIS business object that represents a binary semaphore implemented via a database
 * table and select-for-update technique. This class is not mapped using Hibernate
 * XML, but rather via explicit SQL query.
 */
public class SemaphoreValue {

    private long id;
    private int semaphoreValue;
    private String userid;
    private long modifiedDate;

    /**
     * Do-nothing constructor.
     */
    public SemaphoreValue() {
        id = 0;
        semaphoreValue = 0;
        userid = null;
    }

    public SemaphoreValue(long iv, int sv, String uv, long md) {
        id = iv;
        semaphoreValue = sv;
        userid = uv;
        modifiedDate = md;
    }

    public long getId() {
        return id;
    }
    public void setId(long value) {
        id = value;
    }

    public int getSemaphoreValue() {
        return semaphoreValue;
    }
    public void setSemaphoreValue(int value) {
        semaphoreValue = value;
    }

    /**
     * 
     */
    public String getUserid() {
        return userid;
    }
    /**
     * 
     */
    public void setUserid(String value) {
        userid = value;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }
    public void setModifiedDate(long value) {
        modifiedDate = value;
    }

}
