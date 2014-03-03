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
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * IRMIS business object that represents a physical port defined for a given component.
 * This class is mapped via hibernate one-to-one with IRMIS component_port table.
 *
 * @see ComponentType
 */
public class ComponentPort extends IRMISDataObject implements Cloneable {

    private Component component;
    private ComponentPortType componentPortType;
    // making this a sorted set ordered by designator's order field
    private SortedSet portPins = new TreeSet();
    private List cablesA = new ArrayList();
    private List cablesB = new ArrayList();

    private String componentPortName;
    private int componentPortOrder;

    /**
     * Do-nothing constructor.
     */
    public ComponentPort() {
    }

    /**
     * Construct a port based on the template from the component type.
     */
    public ComponentPort(ComponentPortTemplate cpt) {
        setComponentPortType(cpt.getComponentPortType());
        Set pps = cpt.getPortPinTemplates();
        Iterator ppsIt = pps.iterator();
        while (ppsIt.hasNext()) {
            PortPinTemplate ppt = (PortPinTemplate)ppsIt.next();
            PortPin pp = new PortPin(ppt);
            addPortPin(pp);
        }
        setComponentPortName(cpt.getComponentPortName());
        setComponentPortOrder(cpt.getComponentPortOrder());
    }

    /**
     * Set markForDelete value and propagate to pins.
     */
    public void setMarkForDeleteAndPropagate(boolean value) {
        setMarkForDelete(value);
        // propagate to pins
        Iterator pinIt = getPortPins().iterator();
        while (pinIt.hasNext()) {
            PortPin pp = (PortPin)pinIt.next();
            pp.setMarkForDelete(value);
        }
        // propagate delete to cables 
        // note: cable is considered deleted if component on either end is deleted
        Iterator cableAIt = cablesA.iterator();
        while (cableAIt.hasNext()) {
            Cable cable = (Cable)cableAIt.next();
            cable.setMarkForDelete(value);
        }
        Iterator cableBIt = cablesB.iterator();
        while (cableBIt.hasNext()) {
            Cable cable = (Cable)cableBIt.next();
            cable.setMarkForDelete(value);
        }

    }

    /**
     * Get the component this port is part of.
     */
    public Component getComponent() {
        return this.component;
    }
    /**
     * Set the component this port is part of.
     */
    public void setComponent(Component value) {
        this.component = value;
    }

    /**
     * Get the port type of this port.
     */
    public ComponentPortType getComponentPortType() {
        return this.componentPortType;
    }
    /**
     * Set the port type of this port.
     */
    public void setComponentPortType(ComponentPortType value) {
        this.componentPortType = value;
    }

    /**
     * Get the set of port pins that are part of this port.
     */
    public SortedSet getPortPins() {
        return this.portPins;
    }
    /**
     * Set the set of port pins that are part of this port.
     */
    public void setPortPins(SortedSet value) {
        this.portPins = value;
    }
    /**
     * Add one PortPin to set. Establishes bidirectional
     * identity.
     */
    public void addPortPin(PortPin pin) {
        pin.setComponentPort(this);
        portPins.add(pin);
    }

    // Hidden set of accessors 
    public List getCablesA() {
        return cablesA;
    }
    public void setCablesA(List value) {
        cablesA = value;
    }
    public void addCableA(Cable cable) {
        cable.setComponentPortA(this);
        cablesA.add(cable);
    }
    public List getCablesB() {
        return cablesB;
    }
    public void setCablesB(List value) {
        cablesB = value;
    }
    public void addCableB(Cable cable) {
        cable.setComponentPortB(this);
        cablesB.add(cable);
    }

    public void removeCableA(Cable cable) {
        cablesA.remove(cable); 
        cable.setComponentPortA(null);
    }

    public void removeCableB(Cable cable) {
        cablesB.remove(cable); 
        cable.setComponentPortB(null);
    }

    public List getCables() {
        List cA = getCablesA();
        List cB = getCablesB();
        List combined = new ArrayList(cA);
        combined.addAll(cB);
        return combined;
    }

    /**
     * Get the component port name.
     */
    public String getComponentPortName() {
        return this.componentPortName;
    }
    /**
     * Set the component port name.
     */
    public void setComponentPortName(String value) {
        this.componentPortName = value;
    }

    /**
     * Get the sequence number of this port relative to the
     * others defined for the associated component. Begins with 0.
     */
    public int getComponentPortOrder() {
        return this.componentPortOrder;
    }
    /**
     * Set the sequence number of this port relative to the
     * others defined for the associated component. Begins with 0.
     */
    public void setComponentPortOrder(int value) {
        this.componentPortOrder = value;
    }

    public String toString() {
        return getComponentPortName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentPort) ) return false;
        final ComponentPort castOther = (ComponentPort)other;

        // allow us to manipulate collection of these without necessarily having a
        // parent component type yet...
        if (this.getComponent() == null ||
            castOther.getComponent() == null)
            return this.getComponentPortOrder() == castOther.getComponentPortOrder();
        else
            return this.getComponent().getId() == castOther.getComponent().getId() &&
                this.getComponentPortOrder() == castOther.getComponentPortOrder();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponent().getId());
        result = HashCodeUtil.hash(result, getComponentPortOrder());
        return result;
    }

    /**
     * Performs deep copy of this component port. Does not preserve
     * object id fields in all cases. Does not copy the associated
     * component object, since we assume this clone is being invoked
     * from the component clone method.
     */
    public Object clone() throws CloneNotSupportedException {

        if (getId() == null)
            throw new CloneNotSupportedException();

        ComponentPort cpCopy = new ComponentPort();
        
        // copy port type, name, order
        cpCopy.setComponentPortType(getComponentPortType());
        cpCopy.setComponentPortName(getComponentPortName());
        cpCopy.setComponentPortOrder(getComponentPortOrder());

        // copy pins
        Iterator pinIt = getPortPins().iterator();
        while (pinIt.hasNext()) {
            PortPin pp = (PortPin)pinIt.next();
            PortPin ppCopy = (PortPin)pp.clone();
            cpCopy.addPortPin(ppCopy);
        }

        return cpCopy;
    }

}
