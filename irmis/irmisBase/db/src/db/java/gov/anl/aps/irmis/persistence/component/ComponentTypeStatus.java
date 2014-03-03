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

/**
 * IRMIS business object that represents 
 * This class is mapped via hibernate one-to-one with IRMIS component_type_status table.
 */
public class ComponentTypeStatus extends IRMISDataObject implements Cloneable {

    private ComponentType componentType;
    private int spareQuantity;
    private int stockQuantity;
    private String spareLocation;
    private boolean instantiated;
    private String nrtlStatus;
    private String nrtlAgency;

    /**
     * Do-nothing constructor.
     */
    public ComponentTypeStatus() {
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
    public int getSpareQuantity() {
        return this.spareQuantity;
    }
    /**
     * 
     */
    public void setSpareQuantity(int value) {
        this.spareQuantity = value;
    }

    /**
     * 
     */
    public int getStockQuantity() {
        return this.stockQuantity;
    }
    /**
     * 
     */
    public void setStockQuantity(int value) {
        this.stockQuantity = value;
    }

    public String getSpareLocation() {
        return this.spareLocation;
    }
    public void setSpareLocation(String value) {
        this.spareLocation = value;
    }

    public boolean getInstantiated() {
        return this.instantiated;
    }
    public void setInstantiated(boolean value) {
        this.instantiated = value;
    }

    public String getNrtlStatus() {
        return nrtlStatus;
    }

    public void setNrtlStatus(String value) {
        nrtlStatus = value;
    }

    public String getNrtlAgency() {
        return nrtlAgency;
    }

    public void setNrtlAgency(String value) {
        nrtlAgency = value;
    }


    public String toString() {
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentTypeStatus) ) return false;
        final ComponentTypeStatus castOther = (ComponentTypeStatus)other;
        return this.getId() == castOther.getId();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getId());
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
        
        ComponentTypeStatus ctsCopy = new ComponentTypeStatus();

        // copy attributes of this class
        ctsCopy.setSpareQuantity(getSpareQuantity());
        ctsCopy.setStockQuantity(getStockQuantity());
        ctsCopy.setSpareLocation(getSpareLocation());
        ctsCopy.setInstantiated(false);

        return ctsCopy;
    }

}
