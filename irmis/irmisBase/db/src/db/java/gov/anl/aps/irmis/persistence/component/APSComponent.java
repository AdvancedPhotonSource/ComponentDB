/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.component;

import gov.anl.aps.irmis.persistence.login.GroupName;
import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

/**
 * IRMIS business object that represents APS-specific component instance details.
 * This class is mapped via hibernate one-to-one with IRMIS aps_component table.
 */
public class APSComponent extends IRMISDataObject implements Cloneable {

    private Component component;
    //private Long componentId;

    private GroupName groupName;
    private String serialNumber;
    private boolean verified = false;


    /**
     * Do-nothing constructor.
     */
    public APSComponent() {
    }


    /**
     * 
     */
    public Component getComponent() {
        return component;
    }
    /**
     * 
     */
    public void setComponent(Component value) {
        component = value;
    }

    /*
    public Long getComponentId() {
        return componentId;
    }
    public void setComponentId(Long value) {
        componentId = value;
    }
    */

    /**
     * 
     */
    public GroupName getGroupName() {
        return groupName;
    }
    /**
     * 
     */
    public void setGroupName(GroupName value) {
        groupName = value;
    }

    /**
     * 
     */
    public String getSerialNumber() {
        return serialNumber;
    }
    /**
     * 
     */
    public void setSerialNumber(String value) {
        serialNumber = value;
    }

    /**
     * 
     */
    public boolean getVerified() {
        return verified;
    }
    /**
     * 
     */
    public void setVerified(boolean value) {
        verified = value;
    }


    public String toString() {
        return getId().toString();
    }

    /*
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof APSComponent) ) return false;
        final APSComponent castOther = (APSComponent)other;
        
        return this.getComponent().equals(castOther.getComponent());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponent());
        return result;
    }

    /**
     * Performs deep copy of this component, but not the parent and child
     * relationships. Does not preserve object id fields in all cases.
     */
    public Object clone() throws CloneNotSupportedException {

        if (getId() == null)
            throw new CloneNotSupportedException();
        
        APSComponent apsComponentCopy = new APSComponent();

        // copy scalar data
        apsComponentCopy.setSerialNumber(getSerialNumber());
        apsComponentCopy.setGroupName(getGroupName());
        apsComponentCopy.setVerified(getVerified());

        return apsComponentCopy;
    }

}
