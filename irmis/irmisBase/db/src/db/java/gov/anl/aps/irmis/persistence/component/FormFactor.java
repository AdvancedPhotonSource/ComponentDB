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
 * This class is mapped via hibernate one-to-one with IRMIS form_factor table.
 */
public class FormFactor extends IRMISDataObject {

    private String formFactor;

    /**
     * Do-nothing constructor.
     */
    public FormFactor() {
    }

    /**
     * 
     */
    public String getFormFactor() {
        return this.formFactor;
    }
    /**
     * 
     */
    public void setFormFactor(String value) {
        this.formFactor = value;
    }

    /**
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements.
     */
    public Element toElement() {
        Element ffElement = DocumentHelper.createElement("form-factor");
        ffElement.addAttribute("name", getFormFactor());
        return ffElement;
    }

    public String toString() {
        return getFormFactor();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof FormFactor) ) return false;
        final FormFactor castOther = (FormFactor) other;
        return this.getFormFactor().equals(castOther.getFormFactor());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getFormFactor());
        return result;
    }

}
