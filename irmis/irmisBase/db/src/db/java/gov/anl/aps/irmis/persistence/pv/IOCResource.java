/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;

/**
 * IRMIS business object that represents a single file that was part of the
 * ioc's startup command file. Typically represents a dbd, db, or template file.
 * This class is mapped via hibernate almost one-to-one with IRMIS ioc_resource table.
 */
public class IOCResource extends IRMISDataObject {

    private IOCBoot iocBoot;
    private int loadOrder;
    private String substStr;
    private URI uri;

    /**
     * Constructor
     */
    public IOCResource() {
    }

    /**
     * Get the parent IOCBoot associated with this resource.
     */
    public IOCBoot getIocBoot() {
        return this.iocBoot;
    }
    /**
     * Set the parent IOCBoot associated with this resource.
     */
    public void setIocBoot(IOCBoot value) {
        this.iocBoot = value;
    }

    /**
     * Get the actual URI used to represent the file system resource.
     * We use URI's to be Unix/Window agnostic, and also in case some day the
     * ioc's don't directly read from a file system.
     */
    public URI getUri() {
        return this.uri;
    }
    /**
     * Set the actual URI used to represent the file system resource.
     * We use URI's to be Unix/Window agnostic, and also in case some day the
     * ioc's don't directly read from a file system.
     */
    public void setUri(URI value) {
        this.uri = value;
    }

    /**
     * An integer (starting from 0) which represents where we come from in 
     * the multi-resource sequence of an ioc boot.
     */
    public int getLoadOrder() {
        return this.loadOrder;
    }
    public void setLoadOrder(int value) {
        this.loadOrder = value;
    }

    /**
     * Get the substitution string used to instantiate this resource. Applies
     * only if resource is a db or template file. Null if resource is a dbd file.
     */
    public String getSubstStr() {
        return this.substStr;
    }
    /**
     * Set the substitution string used to instantiate this resource. Applies
     * only if resource is a db or template file. Null if resource is a dbd file.
     */
    public void setSubstStr(String value) {
        this.substStr = value;
    }

    public String toString() {
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof IOCResource) ) return false;
        final IOCResource castOther = (IOCResource) other;
        return this.getIocBoot().getId()==castOther.getIocBoot().getId() &&
            this.getLoadOrder()==castOther.getLoadOrder();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result,getIocBoot().getId());
        result = HashCodeUtil.hash(result,getLoadOrder());
        return result;
    }

}
