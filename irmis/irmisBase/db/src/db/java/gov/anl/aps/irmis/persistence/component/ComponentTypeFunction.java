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
 * This class is mapped via hibernate one-to-one with IRMIS component_type_function table.
 */
public class ComponentTypeFunction extends IRMISDataObject implements Cloneable {

    private Function function;
    private ComponentType componentType;

    /**
     * Do-nothing constructor.
     */
    public ComponentTypeFunction() {
    }

    /**
     * 
     */
    public Function getFunction() {
        return this.function;
    }
    /**
     * 
     */
    public void setFunction(Function value) {
        this.function = value;
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


    public String toString() {
        return getFunction().getFunctionName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof ComponentTypeFunction) ) return false;
        final ComponentTypeFunction castOther = (ComponentTypeFunction)other;
        return this.getFunction().equals(castOther.getFunction());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getFunction().getFunctionName());
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
        
        ComponentTypeFunction ctfCopy = new ComponentTypeFunction();

        // copy attributes of this class    
        ctfCopy.setFunction(getFunction());
        return ctfCopy;
    }

}
