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
 * IRMIS business object that represents an category of component states.
 * Basically, just a category name with an associated set of states.
 * This class is mapped via hibernate almost one-to-one with IRMIS 
 * component_state_category table.
 */
public class ComponentStateCategory extends IRMISDataObject {

    private Set componentStates = new HashSet();
    private String category;

    /**
     * Do-nothing constructor.
     */
    public ComponentStateCategory() {
    }

    /**
     * 
     */
    public Set getComponentStates() {
        return this.componentStates;
    }
    /**
     * 
     */
    public void setComponentStates(Set value) {
        this.componentStates = value;
    }

    /**
     * Clear current set of ComponentState.
     */
    public void clearComponentStates() {
        this.componentStates.clear();
    }

    /**
     * Add one ComponentState to set. Establishes bidirectional
     * identity.
     */
    public void addComponentState(ComponentState cs) {
        cs.setComponentStateCategory(this);
        componentStates.add(cs);
    }    


    /**
     * 
     */
    public String getCategory() {
        return category;
    }
    /**
     * 
     */
    public void setCategory(String value) {
        category = value;
    }

    public String toString() {
        return getCategory();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentStateCategory) ) return false;
        final ComponentStateCategory castOther = (ComponentStateCategory) other;
        return this.getCategory().equals(castOther.getCategory());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getCategory());
        return result;
    }

}
