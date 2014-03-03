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
import java.util.List;
import java.util.ArrayList;

/**
 * IRMIS business object that represents an immutable component type name. These base
 * component types are commonly needed types such as room, rack, enclosure, as well
 * as the agreed-upon set of common EPICS device types such as VME boards, IP modules, etc.
 * This class doesn't actually contain the component type, but rather flags those that
 * are considered base.
 * This class is mapped via hibernate one-to-one with IRMIS base_component_type table.
 *
 * @see ComponentType
 */
public class BaseComponentType extends IRMISDataObject {

    private String componentTypeName;

    /**
     * Do-nothing constructor.
     */
    public BaseComponentType() {
    }


    /**
     * Get the name of the base component type.
     */
    public String getComponentTypeName() {
        return this.componentTypeName;
    }
    /**
     * Set the name of the base component type.
     */
    public void setComponentTypeName(String value) {
        this.componentTypeName = value;
    }


    public String toString() {
        return getComponentTypeName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof BaseComponentType) ) return false;
        final BaseComponentType castOther = (BaseComponentType)other;
        return this.getComponentTypeName().equals(castOther.getComponentTypeName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getComponentTypeName());
        return result;
    }

}
