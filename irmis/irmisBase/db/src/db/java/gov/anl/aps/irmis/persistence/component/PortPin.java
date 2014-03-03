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

/**
 * IRMIS business object that represents 
 * This class is mapped via hibernate one-to-one with IRMIS port_pin table.
 */
public class PortPin extends IRMISDataObject implements Comparable, Cloneable {

    private ComponentPort componentPort;
    private String portPinUsage;
    private String signalName;
    private PortPinType portPinType;
    private PortPinDesignator portPinDesignator;

    /**
     * Do-nothing constructor.
     */
    public PortPin() {
        super();
    }

    /**
     * Construct a port-pin based on the template from the component type.
     */
    public PortPin(PortPinTemplate ppt) {
        setPortPinUsage(ppt.getPortPinUsage());
        setPortPinType(ppt.getPortPinType());
        setPortPinDesignator(ppt.getPortPinDesignator());
    }


    /**
     *
     */
    public ComponentPort getComponentPort() {
        return this.componentPort;
    }
    /**
     *
     */
    public void setComponentPort(ComponentPort value) {
        this.componentPort = value;
    }

    /**
     * 
     */
    public String getPortPinUsage() {
        return this.portPinUsage;
    }
    /**
     * 
     */
    public void setPortPinUsage(String value) {
        this.portPinUsage = value;
    }

    /**
     * 
     */
    public String getSignalName() {
        return this.signalName;
    }
    /**
     * 
     */
    public void setSignalName(String value) {
        this.signalName = value;
    }

    /**
     *
     */
    public PortPinType getPortPinType() {
        return this.portPinType;
    }
    /**
     *
     */
    public void setPortPinType(PortPinType value) {
        this.portPinType = value;
    }

    /**
     *
     */
    public PortPinDesignator getPortPinDesignator() {
        return this.portPinDesignator;
    }
    /**
     *
     */
    public void setPortPinDesignator(PortPinDesignator value) {
        this.portPinDesignator = value;
    }

    public String toString() {
        return getComponentPort().getComponentPortName() +  ":" +
            getPortPinDesignator().getDesignator();
    }

    // have the set of these objects be sorted by their designator's order
    public int compareTo(Object o) {
        PortPin other = (PortPin)o;
        if (getPortPinDesignator() == null ||
            other.getPortPinDesignator() == null)
            return 1;

        int d1 = this.getPortPinDesignator().getDesignatorOrder();
        int d2 = other.getPortPinDesignator().getDesignatorOrder();
        if (d1 < d2)
            return -1;
        else if (d1 == d2)
            return 0;
        else
            return 1;
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof PortPin) ) return false;
        final PortPin castOther = (PortPin) other;
        return this.getComponentPort().getId()==
            castOther.getComponentPort().getId() &&
            this.getPortPinDesignator().getId()==
            castOther.getPortPinDesignator().getId();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponentPort().getId());
        result = HashCodeUtil.hash(result, getPortPinDesignator().getId());
        return result;
    }

    /**
     * Performs deep copy of this port pin. Does not preserve
     * object id fields in all cases. Does not copy the associated
     * component port object, since we assume this clone is being 
     * invoked from the component port clone method.
     */
    public Object clone() throws CloneNotSupportedException {

        if (getId() == null)
            throw new CloneNotSupportedException();

        PortPin ppCopy = new PortPin();
        
        // copy port type, usage, signal
        ppCopy.setPortPinUsage(getPortPinUsage());
        ppCopy.setSignalName(getSignalName());
        ppCopy.setPortPinType(getPortPinType());
        ppCopy.setPortPinDesignator(getPortPinDesignator());

        return ppCopy;
    }

}
