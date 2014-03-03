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
 * This class is mapped via hibernate one-to-one with IRMIS port_pin_template table.
 */
public class PortPinTemplate extends IRMISDataObject implements Cloneable, Comparable {

    private ComponentPortTemplate componentPortTemplate;
    private PortPinType portPinType;
    private PortPinDesignator portPinDesignator;

    private String portPinUsage;

    /**
     * Do-nothing constructor.
     */
    public PortPinTemplate() {
    }


    /**
     *
     */
    public ComponentPortTemplate getComponentPortTemplate() {
        return this.componentPortTemplate;
    }
    /**
     *
     */
    public void setComponentPortTemplate(ComponentPortTemplate value) {
        this.componentPortTemplate = value;
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
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements. This Element is then readily output as an XML
     * document, or added as an element to a larger document.
     *
     * @return dom4j <code>Element</code> containing a variety of sub-elements
     */
    public Element toElement() {
        Element pptElement = DocumentHelper.createElement("port-pin-template");
        pptElement.addAttribute("usage", getPortPinUsage());
        if (getPortPinType() != null)
            pptElement.add(getPortPinType().toElement());
        pptElement.add(getPortPinDesignator().toElement());

        return pptElement;
    }


    public String toString() {
        PortPinDesignator ppd = getPortPinDesignator();
        String pinUsage = getPortPinUsage();
        String pinLabel = null;
        // show what we can
        if (ppd != null) {
            pinLabel = ppd.getDesignator() + ":" + pinUsage;
            
        } else {
            pinLabel = ":" + pinUsage;
        }
        return pinLabel;
    }

    // have the set of these objects be sorted by their designator's order
    public int compareTo(Object o) {
        PortPinTemplate other = (PortPinTemplate)o;
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
        if ( !(other instanceof PortPinTemplate) ) return false;
        final PortPinTemplate castOther = (PortPinTemplate) other;
        return this.getComponentPortTemplate().getId()==
            castOther.getComponentPortTemplate().getId() &&
            this.getPortPinDesignator().getId()==
            castOther.getPortPinDesignator().getId();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponentPortTemplate().getId());
        result = HashCodeUtil.hash(result, getPortPinDesignator().getId());
        return result;
    }

    /**
     * Performs deep copy of this pin template. Does not preserve 
     * object id fields in all cases, since this copy is intended
     * to become part of a new component type.
     */
    public Object clone() throws CloneNotSupportedException {

        if (getId() == null)
            throw new CloneNotSupportedException();
        
        PortPinTemplate pptCopy = new PortPinTemplate();

        // copy attributes of this class
        pptCopy.setPortPinType(getPortPinType());
        pptCopy.setPortPinDesignator(getPortPinDesignator());
        pptCopy.setPortPinUsage(getPortPinUsage());

        return pptCopy;
    }

}
