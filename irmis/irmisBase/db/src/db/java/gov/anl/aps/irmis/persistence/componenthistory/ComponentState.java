/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.componenthistory;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * IRMIS business object that represents a possible component state within
 * a given category.
 * This class is mapped via hibernate almost one-to-one with IRMIS 
 * component_state table.
 */
public class ComponentState extends IRMISDataObject {

    private ComponentStateCategory componentStateCategory;
    private String state;

    /**
     * Do-nothing constructor.
     */
    public ComponentState() {
    }

    /**
     * 
     */
    public ComponentStateCategory getComponentStateCategory() {
        return componentStateCategory;
    }
    /**
     * 
     */
    public void setComponentStateCategory(ComponentStateCategory value) {
        componentStateCategory = value;
    }

    /**
     * 
     */
    public String getState() {
        return state;
    }
    /**
     * 
     */
    public void setState(String value) {
        state = value;
    }

    public String toString() {
        return getState();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentState) ) return false;
        final ComponentState castOther = (ComponentState) other;
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
