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
 * IRMIS business object that represents type of interest a beamline has
 * in a particular ComponentType.
 * This class is mapped via hibernate one-to-one with IRMIS chc_beamline_interest table.
 */
public class BeamlineInterest extends IRMISDataObject {

    private String interest;

    /**
     * Do-nothing constructor.
     */
    public BeamlineInterest() {
    }

    /**
     * 
     */
    public String getInterest() {
        return this.interest;
    }
    /**
     * 
     */
    public void setInterest(String value) {
        this.interest = value;
    }

    /**
     * Converts this data object to an XML Element, with all associated information
     * as attributes and sub-elements.
     */
    public Element toElement() {
        Element mElement = DocumentHelper.createElement("beamline-interest");
        mElement.addAttribute("interest", getInterest());
        return mElement;
    }

    public String toString() {
        return getInterest();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof BeamlineInterest) ) return false;
        final BeamlineInterest castOther = (BeamlineInterest) other;
        return this.getInterest().equals(castOther.getInterest());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getInterest());
        return result;
    }

}
