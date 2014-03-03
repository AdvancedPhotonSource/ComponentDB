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
 * port pins. This is considered a "template" signal, since it is used to instantiate an 
 * actual signal when a component of this component type is added to IRMIS.
 * This class is mapped via hibernate one-to-one with IRMIS component_signal_template table.
 *
 * @see ComponentType
 */
public class ComponentSignalTemplate extends IRMISDataObject {

    private ComponentType componentType;
    private PortPinTemplate primaryPortPinTemplate;
    private PortPinTemplate secondaryPortPinTemplate;
    private String signalName;

    /**
     * Do-nothing constructor.
     */
    public ComponentSignalTemplate() {
    }

    /**
     * Get the component type this template is associated with.
     */
    public ComponentType getComponentType() {
        return this.componentType;
    }
    /**
     * Set the component type this template is associated with.
     */
    public void setComponentType(ComponentType value) {
        this.componentType = value;
    }

    /**
     * Get the primary pin for this signal (if single ended), otherwise
     * this is one of two pins of a signal.
     */
    public PortPinTemplate getPrimaryPortPinTemplate() {
        return this.primaryPortPinTemplate;
    }
    /**
     * Set the primary pin for this signal.
     */
    public void setPrimaryPortPinTemplate(PortPinTemplate value) {
        this.primaryPortPinTemplate = value;
    }

    /**
     * Get the secondary pin for this signal (if not single ended).
     */
    public PortPinTemplate getSecondaryPortPinTemplate() {
        return this.secondaryPortPinTemplate;
    }
    /**
     * Set the secondary pin for this signal.
     */
    public void setSecondaryPortPinTemplate(PortPinTemplate value) {
        this.secondaryPortPinTemplate = value;
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
        if ( !(other instanceof ComponentSignalTemplate) ) return false;
        final ComponentSignalTemplate castOther = (ComponentSignalTemplate)other;

        // allow us to manipulate collection of these without necessarily having a
        // parent component type yet...
        if (this.getComponentType() == null ||
            castOther.getComponentType() == null)
            return this.getSignalName().equals(castOther.getSignalName());
        else
            return this.getComponentType().getId() == castOther.getComponentType().getId() &&
                this.getSignalName().equals(castOther.getSignalName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponentType().getId());
        result = HashCodeUtil.hash(result, getSignalName());
        return result;
    }

}
