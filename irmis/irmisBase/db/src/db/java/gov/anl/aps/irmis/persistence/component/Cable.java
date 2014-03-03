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
 * This class is mapped via hibernate one-to-one with IRMIS cable table.
 */
public class Cable extends IRMISDataObject {

    private String label;
    private String color;
    private ComponentPort componentPortA = null;
    private ComponentPort componentPortB = null;
    private boolean pinDetail;
    private boolean virtual;
    private String destinationDescription;

    /**
     * Do-nothing constructor.
     */
    public Cable() {
    }

    /**
     * 
     */
    public String getLabel() {
        return label;
    }
    /**
     * 
     */
    public void setLabel(String value) {
        label = value;
    }

    /**
     * 
     */
    public String getColor() {
        return color;
    }
    /**
     * 
     */
    public void setColor(String value) {
        color = value;
    }

    /**
     * 
     */
    public String getDestinationDescription() {
        return destinationDescription;
    }
    /**
     * 
     */
    public void setDestinationDescription(String value) {
        destinationDescription = value;
    }

    /**
     *
     */
    public ComponentPort getComponentPortA() {
        return componentPortA;
    }
    /**
     *
     */
    public void setComponentPortA(ComponentPort value) {
        componentPortA = value;
    }

    /**
     *
     */
    public ComponentPort getComponentPortB() {
        return componentPortB;
    }
    /**
     *
     */
    public void setComponentPortB(ComponentPort value) {
        componentPortB = value;
    }

    public boolean getPinDetail() {
        return pinDetail;
    }
    public void setPinDetail(boolean value) {
        pinDetail = value;
    }

    public boolean getVirtual() {
        return virtual;
    }
    public void setVirtual(boolean value) {
        virtual = value;
    }

    /**
     * Cable is considered single ended if one or both of its
     * associated components are null.
     */
    public boolean isSingleEnded() {
        if (getComponentPortA() == null ||
            getComponentPortB() == null)
            return true;
        else
            return false;
    }

    public String toString() {
        return getLabel();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof Cable) ) return false;
        final Cable castOther = (Cable) other;
        ComponentPort tcpA = this.getComponentPortA();
        ComponentPort tcpB = this.getComponentPortB();
        ComponentPort ocpA = castOther.getComponentPortA();
        ComponentPort ocpB = castOther.getComponentPortB();
        
        // fairly complex endpoint port matching determines cable equality
        if ((tcpA == null && tcpB == null) ||
            (ocpA == null && ocpB == null)) {
            return false;

        } else if (this.isSingleEnded() != castOther.isSingleEnded()) {
            return false;

        } else if (this.isSingleEnded()) {  // both cables single-ended
            if (tcpA != null) {
                if (ocpA != null) {
                    return tcpA.equals(ocpA);
                } else if (ocpB != null) {
                    return tcpA.equals(ocpB);
                }
            } else {  // tcpB != null
                if (ocpA != null) {
                    return tcpB.equals(ocpA);
                } else if (ocpB != null) {
                    return tcpB.equals(ocpB);
                }

            }

        } else {  // both cables double-ended
            if (tcpA.equals(ocpA) && tcpB.equals(ocpB)) {
                return true;
            } else if (tcpA.equals(ocpB) && tcpB.equals(ocpA)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        //result = HashCodeUtil.hash(result, getLabel());
        result = HashCodeUtil.hash(result, getComponentPortA());
        result = HashCodeUtil.hash(result, getComponentPortB());
        result = HashCodeUtil.hash(result, getId());
        return result;
    }

}
