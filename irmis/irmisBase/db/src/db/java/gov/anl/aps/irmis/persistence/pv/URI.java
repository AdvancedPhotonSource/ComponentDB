/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;

/**
 * IRMIS business object that represents a reference to a file system resource.
 * A URI string is used, although there is no guarantee the underlying filesystem
 * still has the resource.
 * This class is mapped via hibernate almost one-to-one with IRMIS uri table.
 */
public class URI extends IRMISDataObject implements Cloneable {

    private String uri;
    private Date uriModifiedDate;
    private Date modifiedDate;
    private String modifiedBy;

    /**
     * Constructor
     */
    public URI() {
    }

    /**
     * Get the URI string. Example: file://usr/local/iocapps/foo.dbd 
     */
    public String getUri() {
        return this.uri;
    }
    /**
     * Set the URI string. Example: file://usr/local/iocapps/foo.dbd 
     */
    public void setUri(String value) {
        this.uri = value;
    }

    /**
     * Get the file modification date of the underlying file pointed to by the
     * URI. This corresponds to the Unix mtime attribute of the file.
     */
    public Date getUriModifiedDate() {
        return this.uriModifiedDate;
    }
    /**
     * Set the file modification date of the underlying file pointed to by the
     * URI. This corresponds to the Unix mtime attribute of the file.
     */
    public void setUriModifiedDate(Date value) {
        this.uriModifiedDate = value;
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
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if ( !(other instanceof URI) ) return false;
        URI castOther = (URI) other;
        if (this.getId() == castOther.getId())
            return true;
        else
            return false;
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result,getId());
        return result;
    }

    /**
     * Performs deep copy of this object. Does not preserve 
     * object id field, since this copy is intended
     * to become part of a new component type.
     */
    public Object clone() throws CloneNotSupportedException {

        if (getId() == null)
            throw new CloneNotSupportedException();
        
        URI uriCopy = new URI();

        // copy attributes of this class    
        uriCopy.setUri(getUri());
        uriCopy.setUriModifiedDate(getUriModifiedDate());

        return uriCopy;
    }

}
