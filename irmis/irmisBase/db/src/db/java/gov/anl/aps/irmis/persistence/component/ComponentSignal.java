/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * IRMIS business object that represents a single signal composed of one or two component
 * port pins. 
 * This class is mapped via hibernate one-to-one with IRMIS component_signal table.
 *
 * @see Component
 */
public class ComponentSignal extends IRMISDataObject {

    private Component component;
    private PortPin primaryPortPin;
    private PortPin secondaryPortPin;
    private String signalName;

    /**
     * Do-nothing constructor.
     */
    public ComponentSignal() {
    }

    /**
     * Get the component this signal is associated with.
     */
    public Component getComponent() {
        return this.component;
    }
    /**
     * Set the component this signal is associated with.
     */
    public void setComponent(Component value) {
        this.component = value;
    }

    /**
     * Get the primary pin for this signal (if single ended), otherwise
     * this is one of two pins of a signal.
     */
    public PortPin getPrimaryPortPin() {
        return this.primaryPortPin;
    }
    /**
     * Set the primary pin for this signal.
     */
    public void setPrimaryPortPin(PortPin value) {
        this.primaryPortPin = value;
    }

    /**
     * Get the secondary pin for this signal (if not single ended).
     */
    public PortPin getSecondaryPortPin() {
        return this.secondaryPortPin;
    }
    /**
     * Set the secondary pin for this signal.
     */
    public void setSecondaryPortPin(PortPin value) {
        this.secondaryPortPin = value;
    }

    /**
     * Get the signal name.
     */
    public String getSignalName() {
        return this.signalName;
    }
    /**
     * Set the signal name.
     */
    public void setSignalName(String value) {
        this.signalName = value;
    }

    public String toString() {
        return getSignalName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentSignal) ) return false;
        final ComponentSignal castOther = (ComponentSignal)other;

        // allow us to manipulate collection of these without necessarily having a
        // parent component yet...
        if (this.getComponent() == null ||
            castOther.getComponent() == null)
            return this.getSignalName().equals(castOther.getSignalName());
        else
            return this.getComponent().getId() == castOther.getComponent().getId() &&
                this.getSignalName().equals(castOther.getSignalName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponent().getId());
        result = HashCodeUtil.hash(result, getSignalName());
        return result;
    }

}
