/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import gov.anl.aps.irmis.persistence.pv.URI;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * IRMIS business object that represents 
 * This class is mapped via hibernate one-to-one with IRMIS component_type_document table.
 */
public class ComponentTypeDocument extends IRMISDataObject implements Cloneable {

    private URI uri;
    private ComponentType componentType;
    private String documentType;

    /**
     * Do-nothing constructor.
     */
    public ComponentTypeDocument() {
    }

    /**
     * 
     */
    public URI getUri() {
        return this.uri;
    }
    /**
     * 
     */
    public void setUri(URI value) {
        this.uri = value;
    }

    /**
     * 
     */
    public ComponentType getComponentType() {
        return this.componentType;
    }
    /**
     * 
     */
    public void setComponentType(ComponentType value) {
        this.componentType = value;
    }

    public String getDocumentType() {
        return this.documentType;
    }
    public void setDocumentType(String value) {
        this.documentType = value;
    }


    public String toString() {
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentTypeDocument) ) return false;
        final ComponentTypeDocument castOther = (ComponentTypeDocument)other;
        return this.getUri().getUri().equals(castOther.getUri().getUri());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getUri().getUri());
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
        
        ComponentTypeDocument ctdCopy = new ComponentTypeDocument();

        // copy attributes of this class    
        ctdCopy.setDocumentType(getDocumentType());
        URI uriCopy = (URI)getUri().clone();
        ctdCopy.setUri(uriCopy);

        return ctdCopy;
    }

}
