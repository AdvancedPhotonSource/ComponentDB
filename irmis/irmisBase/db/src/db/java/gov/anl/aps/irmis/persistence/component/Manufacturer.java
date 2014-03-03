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
 * This class is mapped via hibernate one-to-one with IRMIS mfg table.
 */
public class Manufacturer extends IRMISDataObject {

    private String manufacturerName;

    /**
     * Do-nothing constructor.
     */
    public Manufacturer() {
    }

    /**
     * 
     */
    public String getManufacturerName() {
        return this.manufacturerName;
    }
    /**
     * 
     */
    public void setManufacturerName(String value) {
        this.manufacturerName = value;
    }

    /**
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements.
     */
    public Element toElement() {
        Element mElement = DocumentHelper.createElement("manufacturer");
        mElement.addAttribute("name", getManufacturerName());
        return mElement;
    }

    public String toString() {
        return getManufacturerName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof Manufacturer) ) return false;
        final Manufacturer castOther = (Manufacturer) other;
        return this.getManufacturerName().equals(castOther.getManufacturerName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getManufacturerName());
        return result;
    }

}
