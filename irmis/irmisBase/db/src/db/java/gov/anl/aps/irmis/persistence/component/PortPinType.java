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
 * This class is mapped via hibernate one-to-one with IRMIS port_pin_type table.
 */
public class PortPinType extends IRMISDataObject {

    private String portPinType;

    /**
     * Do-nothing constructor.
     */
    public PortPinType() {
    }

    /**
     * 
     */
    public String getPortPinType() {
        return this.portPinType;
    }
    /**
     * 
     */
    public void setPortPinType(String value) {
        this.portPinType = value;
    }

    /**
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements. This Element is then readily output as an XML
     * document, or added as an element to a larger document.
     *
     * @return dom4j <code>Element</code> containing a variety of sub-elements
     */
    public Element toElement() {
        Element pptElement = DocumentHelper.createElement("port-pin-type");
        pptElement.addAttribute("type", getPortPinType());
        return pptElement;
    }

    public String toString() {
        return getPortPinType();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof PortPinType) ) return false;
        final PortPinType castOther = (PortPinType) other;
        return this.getPortPinType().equals(castOther.getPortPinType());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getPortPinType());
        return result;
    }

}
