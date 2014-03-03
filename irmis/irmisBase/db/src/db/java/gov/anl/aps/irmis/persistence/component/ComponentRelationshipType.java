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
 * This class is mapped via hibernate one-to-one with IRMIS component_rel_type table.
 */
public class ComponentRelationshipType extends IRMISDataObject {

    // Note: the below enumeration must match id column of mapped table
    public static final int CONTROL = 1;
    public static final int HOUSING = 2;
    public static final int POWER = 3;

    private String relationshipType;

    /**
     * Do-nothing constructor.
     */
    public ComponentRelationshipType() {
    }

    /**
     * 
     */
    public String getRelationshipType() {
        return this.relationshipType;
    }
    /**
     * 
     */
    public void setRelationshipType(String value) {
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
        Element crtElement = DocumentHelper.createElement("component-relationship-type");
        crtElement.addAttribute("type", getRelationshipType());
        return crtElement;
    }

    public String toString() {
        return getRelationshipType();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentRelationshipType) ) return false;
        final ComponentRelationshipType castOther = (ComponentRelationshipType) other;
        return this.getRelationshipType().equals(castOther.getRelationshipType());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getRelationshipType());
        return result;
    }

}
