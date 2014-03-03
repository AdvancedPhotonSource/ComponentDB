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

// dom4j XML
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * IRMIS business object that represents 
 * This class is mapped via hibernate one-to-one with IRMIS port_pin_designator table.
 */
public class PortPinDesignator extends IRMISDataObject {

    private ComponentPortType componentPortType;
    private String designator;
    private int designatorOrder;

    /**
     * Do-nothing constructor.
     */
    public PortPinDesignator() {
    }


    /**
     *
     */
    public ComponentPortType getComponentPortType() {
        return this.componentPortType;
    }
    /**
     *
     */
    public void setComponentPortType(ComponentPortType value) {
        this.componentPortType = value;
    }

    /**
     * 
     */
    public String getDesignator() {
        return this.designator;
    }
    /**
     * 
     */
    public void setDesignator(String value) {
        this.designator = value;
    }

    /**
     *
     */
    public int getDesignatorOrder() {
        return this.designatorOrder;
    }
    /**
     *
     */
    public void setDesignatorOrder(int value) {
        this.designatorOrder = value;
    }

    /**
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements. This Element is then readily output as an XML
     * document, or added as an element to a larger document.
     *
     * @return dom4j <code>Element</code> containing a variety of sub-elements
     */
    public Element toElement() {
        Element ppdElement = DocumentHelper.createElement("port-pin-designator");
        ppdElement.addAttribute("designator", getDesignator());
        ppdElement.addAttribute("order", String.valueOf(getDesignatorOrder()));

        return ppdElement;
    } 

    public String toString() {
        return getDesignator();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof PortPinDesignator) ) return false;
        final PortPinDesignator castOther = (PortPinDesignator) other;
        return this.getComponentPortType().getId() ==  castOther.getComponentPortType().getId() &&
            this.getDesignatorOrder() == castOther.getDesignatorOrder();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponentPortType().getId());
        result = HashCodeUtil.hash(result, getDesignatorOrder());
        return result;
    }

}
