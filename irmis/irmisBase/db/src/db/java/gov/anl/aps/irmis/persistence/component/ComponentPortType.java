/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// dom4j XML
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * IRMIS business object that represents the type of a component port or port template.
 * This class is mapped via hibernate one-to-one with IRMIS component_port_type table.
 *
 * @see ComponentPortTemplate
 * @see ComponentPort
 */
public class ComponentPortType extends IRMISDataObject {

    private String componentPortType;
    private String componentPortGroup;
    private int componentPortPinCount;
    private List portPinDesignators = new ArrayList();

    /**
     * Do-nothing constructor.
     */
    public ComponentPortType() {
    }

    /**
     * Get the name of this port type.
     */
    public String getComponentPortType() {
        return this.componentPortType;
    }
    /**
     * Set the name of this port type.
     */
    public void setComponentPortType(String value) {
        this.componentPortType = value;
    }

    /**
     * Get the group to which this type belongs.
     */
    public String getComponentPortGroup() {
        return this.componentPortGroup;
    }
    /**
     * Set the group to which this type belongs.
     */
    public void setComponentPortGroup(String value) {
        this.componentPortGroup = value;
    }

    /**
     * Get the number of pins that a port of this type has.
     */
    public int getComponentPortPinCount() {
        return this.componentPortPinCount;
    }
    /**
     * Set the number of pins that a port of this type has.
     */
    public void setComponentPortPinCount(int value) {
        this.componentPortPinCount = value;
    }

    /**
     * Get the list of pin designators defined for this port type.
     */
    public List getPortPinDesignators() {
        return this.portPinDesignators;
    }
    /**
     * Set the list of pin designators defined for this port type.
     */
    public void setPortPinDesignators(List value) {
        this.portPinDesignators = value;
    }
    /**
     * Add one PortPinDesignator to list. Establishes bidirectional
     * identity.
     */
    public void addPortPinDesignator(PortPinDesignator ppd) {
        ppd.setComponentPortType(this);
        portPinDesignators.add(ppd);
    }

    /**
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements. This Element is then readily output as an XML
     * document, or added as an element to a larger document.
     *
     * @return dom4j <code>Element</code> containing a variety of sub-elements
     */
    public Element toElement() {
        Element cptElement = DocumentHelper.createElement("component-port-type");
        cptElement.addAttribute("type", getComponentPortType());
        cptElement.addAttribute("group", getComponentPortGroup());
        cptElement.addAttribute("pin-count", String.valueOf(getComponentPortPinCount()));

        Element ppdElement = DocumentHelper.createElement("port-pin-designators");
        Iterator ppdIt = getPortPinDesignators().iterator();
        while (ppdIt.hasNext()) {
            PortPinDesignator ppd = (PortPinDesignator)ppdIt.next();
            ppdElement.add(ppd.toElement());
        }
        cptElement.add(ppdElement);
        
        return cptElement;
    }

    public String toString() {
        return getComponentPortType();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentPortType) ) return false;
        final ComponentPortType castOther = (ComponentPortType) other;
        return this.getComponentPortType().equals(castOther.getComponentPortType());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponentPortType());
        return result;
    }

}
