/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.componenthistory;

import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * IRMIS business object that represents an instance of a component. This is
 * distinct from just a component, which represents a (potentially) empty slot
 * in the configuration of components required to operate the machine. The
 * component instance (defined here) is an actual serial-numbered instance of
 * a component that can be inserted into the component "slots" of the machine.
 * This class is mapped via hibernate almost one-to-one with IRMIS 
 * component_instance table.
 */
public class ComponentInstance extends IRMISDataObject {

    private Set componentInstanceStates = new HashSet();
    private ComponentType componentType;
    private Component component;
    private String serialNumber;
    private String currentLocation; // derived value - not mapped from db

    /**
     * Do-nothing constructor.
     */
    public ComponentInstance() {
    }

    /**
     * 
     */
    public Set getComponentInstanceStates() {
        return this.componentInstanceStates;
    }
    /**
     * 
     */
    public void setComponentInstanceStates(Set value) {
        this.componentInstanceStates = value;
    }

    /**
     * Clear current set of ComponentInstanceState.
     */
    public void clearComponentInstanceStates() {
        this.componentInstanceStates.clear();
    }

    /**
     * Add one ComponentInstanceState to set. Establishes bidirectional
     * identity.
     */
    public void addComponentInstanceState(ComponentInstanceState cis) {
        cis.setComponentInstance(this);
        componentInstanceStates.add(cis);
    }    

    /**
     * 
     */
    public Component getComponent() {
        return component;
    }
    /**
     * 
     */
    public void setComponent(Component value) {
        component = value;
    }

    /**
     * 
     */
    public ComponentType getComponentType() {
        return componentType;
    }
    /**
     * 
     */
    public void setComponentType(ComponentType value) {
        componentType = value;
    }

    /**
     * 
     */
    public String getSerialNumber() {
        return serialNumber;
    }
    /**
     * 
     */
    public void setSerialNumber(String value) {
        serialNumber = value;
    }

    /**
     * 
     */
    public String getCurrentLocation() {
        return currentLocation;
    }
    /**
     * 
     */
    public void setCurrentLocation(String value) {
        currentLocation = value;
    }

    public String toString() {
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentInstance) ) return false;
        final ComponentInstance castOther = (ComponentInstance) other;
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
