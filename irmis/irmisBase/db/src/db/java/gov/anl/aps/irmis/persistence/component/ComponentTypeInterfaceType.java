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
 * This class is mapped via hibernate one-to-one with IRMIS component_type_if_type table.
 */
public class ComponentTypeInterfaceType extends IRMISDataObject {

    private String interfaceType;
    private ComponentRelationshipType relationshipType;

    /**
     * Do-nothing constructor.
     */
    public ComponentTypeInterfaceType() {
    }

    /**
     * 
     */
    public String getInterfaceType() {
        return this.interfaceType;
    }
    /**
     * 
     */
    public void setInterfaceType(String value) {
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
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements. This Element is then readily output as an XML
     * document, or added as an element to a larger document.
     *
     * @return dom4j <code>Element</code> containing a variety of sub-elements
     */
    public Element toElement() {
        Element ctitElement = DocumentHelper.createElement("component-type-interface-type");
        ctitElement.addAttribute("type", getInterfaceType());
        return ctitElement;
    }

    public String toString() {
        return getInterfaceType();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentTypeInterfaceType) ) return false;
        final ComponentTypeInterfaceType castOther = (ComponentTypeInterfaceType) other;
        return this.getInterfaceType().equals(castOther.getInterfaceType()) &&
            this.getRelationshipType().equals(castOther.getRelationshipType());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getInterfaceType());
        result = HashCodeUtil.hash(result, getRelationshipType());
        return result;
    }

}
