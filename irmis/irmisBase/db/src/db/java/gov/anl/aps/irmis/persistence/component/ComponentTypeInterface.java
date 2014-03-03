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
 * This class is mapped via hibernate one-to-one with IRMIS component_type_if table.
 */
public class ComponentTypeInterface extends IRMISDataObject implements Cloneable {

    private ComponentTypeInterfaceType interfaceType;
    private ComponentRelationshipType relationshipType;
    private ComponentType componentType;

    private boolean required;
    private boolean presented;
    private int maxChildren;

    /**
     * Do-nothing constructor.
     */
    public ComponentTypeInterface() {
    }

    /**
     * 
     */
    public ComponentTypeInterfaceType getInterfaceType() {
        return this.interfaceType;
    }
    /**
     * 
     */
    public void setInterfaceType(ComponentTypeInterfaceType value) {
        this.interfaceType = value;
    }

    /**
     * 
     */
    public ComponentRelationshipType getRelationshipType() {
        return this.relationshipType;
    }
    /**
     * 
     */
    public void setRelationshipType(ComponentRelationshipType value) {
        this.relationshipType = value;
    }

    /**
     * 
     */
    public ComponentType getComponentType() {
        return this.componentType;
    }
    /**
     * 
     */
    public void setComponentType(ComponentType value) {
        this.componentType = value;
    }

    /**
     * 
     */
    public boolean getRequired() {
        return this.required;
    }
    public void setRequired(boolean value) {
        this.required = value;
    }

    /**
     * 
     */
    public boolean getPresented() {
        return this.presented;
    }
    public void setPresented(boolean value) {
        this.presented = value;
    }

    public int getMaxChildren() {
        return this.maxChildren;
    }
    public void setMaxChildren(int value) {
        this.maxChildren = value;
    }

    /**
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements. This Element is then readily output as an XML
     * document, or added as an element to a larger document.
     *
     * @return dom4j <code>Element</code> containing a variety of sub-elements
     */
    public Element toElement() {
        Element ctiElement = DocumentHelper.createElement("component-type-interface");
        ctiElement.addAttribute("required", ((getRequired()==true)?"true":"false"));
        ctiElement.addAttribute("presented", ((getPresented()==true)?"true":"false"));
        ctiElement.add(getInterfaceType().toElement());
        ctiElement.add(getRelationshipType().toElement());

        return ctiElement;
    }

    public String toString() {
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentTypeInterface) ) return false;
        final ComponentTypeInterface castOther = (ComponentTypeInterface)other;
        
        return this.getRequired() == castOther.getRequired() &&
            this.getPresented() == castOther.getPresented() &&
            this.getRelationshipType()
            .equals(castOther.getRelationshipType()) &&
            this.getInterfaceType()
            .equals(castOther.getInterfaceType());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getRequired());
        result = HashCodeUtil.hash(result, getPresented());
        result = HashCodeUtil.hash(result, getRelationshipType());
        result = HashCodeUtil.hash(result, getInterfaceType());
        return result;
    }

    /**
     * Performs deep copy of this object. Does not preserve 
     * object id field, since this copy is intended
     * to become part of a new component type.
     */
    public Object clone() throws CloneNotSupportedException {

        if (getId() == null)
            throw new CloneNotSupportedException();
        
        ComponentTypeInterface ctiCopy = new ComponentTypeInterface();

        // copy attributes of this class    
        ctiCopy.setRequired(getRequired());
        ctiCopy.setPresented(getPresented());
        ctiCopy.setRelationshipType(getRelationshipType());
        ctiCopy.setInterfaceType(getInterfaceType());
        ctiCopy.setMaxChildren(getMaxChildren());

        return ctiCopy;
    }

}
