/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * IRMIS business object that represents a single EPICS IOC.
 * This class is mapped via hibernate almost one-to-one with IRMIS ioc table.
 */
public class IOC extends IRMISDataObject implements Comparable {

    private String iocName;
    private boolean active;
    private String system;
    private IOCStatus status;
    private Component component;
    private Date modifiedDate;
    private String modifiedBy;

    /**
     * Constructor
     */
    public IOC() {
    }

    /**
     * Get the ioc's name. Example: iocbbpm1 or iocacis.
     */
    public String getIocName() {
        return this.iocName;
    }
    /**
     * Set the ioc's name. Example: iocbbpm1 or iocacis.
     */    
    public void setIocName(String iocName) {
        this.iocName = iocName;
    }

    /**
     * Indicates whether this ioc is considered in use or not. There may still
     * be other database entities which refer to an ioc which has been taken out
     * of commission from a controls standpoint. 
     */
    public boolean getActive() {
        return this.active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * An arbitrary system name under which this ioc belongs.
     */
    public String getSystem() {
        return this.system;
    }
    public void setSystem(String system) {
        this.system = system;
    }

    public IOCStatus getStatus() {
        return status;
    }
    public void setStatus(IOCStatus value) {
        status = value;
    }

    public Component getComponent() {
        return component;
    }
    public void setComponent(Component value) {
        component = value;
    }

    /**
     * Direct reflection of last modification of underlying database row.
     */
    public Date getModifiedDate() {
        return this.modifiedDate;
    }
    public void setModifiedDate(Date value) {
        this.modifiedDate = value;
    }

    /**
     * Represent user name or application name which last modified (created)
     * underlying database row.
     */
    public String getModifiedBy() {
        return this.modifiedBy;
    }
    public void setModifiedBy(String value) {
        this.modifiedBy = value;
    }

    public String toString() {
        return getId().toString() + ":" + getIocName();
    }

    /**
     * Have any collection of these objects be sorted by the ioc name.
     * This is to be a case-insensitive sort.
     */
    public int compareTo(Object o) {
        IOC other = (IOC)o;
        return this.getIocName().toUpperCase().
            compareTo(other.getIocName().toUpperCase());
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof IOC) ) return false;
        final IOC castOther = (IOC) other;
        return this.getIocName().equals(castOther.getIocName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getIocName());
        return result;
    }

}
