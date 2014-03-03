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

// dom4j XML
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * IRMIS business object that represents a physical port defined for a given component type.
 * This is considered a "template" port, since it is used to instantiate an actual port when
 * a component of this component type is added to IRMIS.
 * This class is mapped via hibernate one-to-one with IRMIS component_port_template table.
 *
 * @see ComponentType
 */
public class ComponentPortTemplate extends IRMISDataObject implements Cloneable {

    private ComponentType componentType;
    private ComponentPortType componentPortType;
    // making this a sorted set ordered by designator's order field
    private SortedSet portPinTemplates = new TreeSet();

    private String componentPortName;
    private int componentPortOrder;

    /**
     * Do-nothing constructor.
     */
    public ComponentPortTemplate() {
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
     * Get the port type of this template port.
     */
    public ComponentPortType getComponentPortType() {
        return this.componentPortType;
    }
    /**
     * Set the port type of this template port.
     */
    public void setComponentPortType(ComponentPortType value) {
        this.componentPortType = value;
    }

    /**
     * Get the set of port pin templates that are part of this port template.
     */
    public SortedSet getPortPinTemplates() {
        return this.portPinTemplates;
    }
    /**
     * Set the set of port pin templates that are part of this port template.
     */
    public void setPortPinTemplates(SortedSet value) {
        this.portPinTemplates = value;
    }
    /**
     * Add one PortPinTemplate to set. Establishes bidirectional
     * identity.
     */
    public void addPortPinTemplate(PortPinTemplate template) {
        template.setComponentPortTemplate(this);
        portPinTemplates.add(template);
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
     * Get the sequence number of this port template relative to the
     * others defined for the associated component type. Begins with 0.
     */
    public int getComponentPortOrder() {
        return this.componentPortOrder;
    }
    /**
     * Set the sequence number of this port template relative to the
     * others defined for the associated component type. Begins with 0.
     */
    public void setComponentPortOrder(int value) {
        this.componentPortOrder = value;
    }

    /**
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements. This Element is then readily output as an XML
     * document, or added as an element to a larger document.
     *
     * @return dom4j <code>Element</code> containing a variety of sub-elements
     */
    public Element toElement() {
        Element cptElement = DocumentHelper.createElement("component-port-template");
        cptElement.addAttribute("name", getComponentPortName());
        cptElement.addAttribute("order", String.valueOf(getComponentPortOrder()));
        cptElement.add(getComponentPortType().toElement());

        Element pptElement = DocumentHelper.createElement("port-pin-templates");
        Iterator pptIt = getPortPinTemplates().iterator();
        while (pptIt.hasNext()) {
            PortPinTemplate ppt = (PortPinTemplate)pptIt.next();
            pptElement.add(ppt.toElement());
        }
        cptElement.add(pptElement);
        
        return cptElement;
    }

    public String toString() {
        String portLabel = getComponentPortName() +
            ":"+getComponentPortType().getComponentPortType()+
            ":"+getComponentPortType().getComponentPortGroup();
        return portLabel;
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentPortTemplate) ) return false;
        final ComponentPortTemplate castOther = (ComponentPortTemplate)other;

        // allow us to manipulate collection of these without necessarily having a
        // parent component type yet...
        if (this.getComponentType() == null ||
            castOther.getComponentType() == null)
            return this.getComponentPortOrder() == castOther.getComponentPortOrder();
        else
            return this.getComponentType().getId() == castOther.getComponentType().getId() &&
                this.getComponentPortOrder() == castOther.getComponentPortOrder();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponentType().getId());
        result = HashCodeUtil.hash(result, getComponentPortOrder());
        return result;
    }

    /**
     * Performs deep copy of this port template. Does not preserve 
     * object id fields in all cases, since this copy is intended
     * to become part of a new component type.
     */
    public Object clone() throws CloneNotSupportedException {

        if (getId() == null)
            throw new CloneNotSupportedException();
        
        ComponentPortTemplate cptCopy = new ComponentPortTemplate();

        // copy attributes of this class
        cptCopy.setComponentPortType(getComponentPortType());
        cptCopy.setComponentPortName(getComponentPortName());
        cptCopy.setComponentPortOrder(getComponentPortOrder());

        // copy collections of associated data
        Iterator pinIt = getPortPinTemplates().iterator();
        while (pinIt.hasNext()) {
            PortPinTemplate ppt = (PortPinTemplate)pinIt.next();
            PortPinTemplate pptCopy = (PortPinTemplate)ppt.clone();
            cptCopy.addPortPinTemplate(pptCopy);
        }

        return cptCopy;
    }

}
