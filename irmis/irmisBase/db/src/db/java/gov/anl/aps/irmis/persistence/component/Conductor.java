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
 * This class is mapped via hibernate one-to-one with IRMIS conductor table.
 */
public class Conductor extends IRMISDataObject  {

    private Cable cable;
    private PortPin portPinA;
    private PortPin portPinB;

    /**
     * Do-nothing constructor.
     */
    public Conductor() {
    }


    /**
     *
     */
    public Cable getCable() {
        return cable;
    }
    /**
     *
     */
    public void setCable(Cable value) {
        cable = value;
    }

    /**
     *
     */
    public PortPin getPortPinA() {
        return portPinA;
    }
    /**
     *
     */
    public void setPortPinA(PortPin value) {
        portPinA = value;
    }

    /**
     *
     */
    public PortPin getPortPinB() {
        return portPinB;
    }
    /**
     *
     */
    public void setPortPinB(PortPin value) {
        portPinB = value;
    }


    public String toString() {
        return getId().toString();
    }


    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof Conductor) ) return false;
        final Conductor castOther = (Conductor) other;
        return this.getPortPinA().equals(castOther.getPortPinA()) &&
            this.getPortPinB().equals(castOther.getPortPinB());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getPortPinA());
        result = HashCodeUtil.hash(result, getPortPinB());
        return result;
    }

}
