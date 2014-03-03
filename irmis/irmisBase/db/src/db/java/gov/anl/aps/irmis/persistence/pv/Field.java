/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;

/**
 * IRMIS business object that represents a process variable field such as HOPR.
 * This class is mapped via hibernate almost one-to-one with IRMIS fld table.
 */
public class Field extends IRMISDataObject implements Comparable {

    /**
     * Indicates that we don't know field state yet. 
     */
    public static final int UNKNOWN = 0;      

    /**
     * Field defined only in dbd, not referenced in db
     */
    public static final int DEFAULT = 1;

    /**
     * Field defined in dbd, but same value given in db
     */
    public static final int OVERWRITTEN = 2;

    /**
     * Field defined in dbd, but given new value in db
     */
    public static final int USER_DEFINED = 3;  

    private String fieldValue;
    private Record record;
    private FieldType fieldType;
    private IOCResource iocResource;
    private int fieldState = UNKNOWN;

    /**
     * Constructor
     */
    public Field() {
    }

    /**
     * Get IOCResource responsible for the definition of this process variable field.
     *
     * @return IOCResource
     */
    public IOCResource getIocResource() {
        return this.iocResource;
    }
    /**
     * Set IOCResource responsible for the definition of this process variable field.
     *
     * @param value given IOCResource
     */
    public void setIocResource(IOCResource value) {
        this.iocResource = value;
    }

    /**
     * Get field type (ex. HOPR or DESC)
     *
     * @return FieldType
     */
    public FieldType getFieldType() {
        return this.fieldType;
    }
    /**
     * Set field type (ex. HOPR or DESC)
     *
     * @param value FieldType
     */
    public void setFieldType(FieldType value) {
        this.fieldType = value;
    }

    /**
     * Get Record associated with this field.
     *
     * @return Record
     */
    public Record getRecord() {
        return this.record;
    }
    /**
     * Set Record associated with this field.
     *
     * @param value Record
     */    
    public void setRecord(Record value) {
        this.record = value;
    }

    /**
     * Get field value for this field. This is the string value as declared
     * in the db file which defines this field.
     *
     * @return String
     */
    public String getFieldValue() {
        return this.fieldValue;
    }
    /**
     * Set field value. This is the string value as declared
     * in the db file which defines this field.
     *
     * @param value String
     */
    public void setFieldValue(String value) {
        this.fieldValue = value;
    }

    /**
     * Get field state (one of UNKNOWN, DEFAULT, OVERWRITTEN, or USER-DEFINED enum).
     */
    public int getFieldState() {
        return this.fieldState;
    }
    /** 
     * Set field state (one of UNKNOWN, DEFAULT, OVERWRITTEN, or USER-DEFINED enum).
     */
    public void setFieldState(int state) {
        this.fieldState = state;
    }

    public String toString() {
        return getId().toString();
    }

    /**
     * Required to implement Comparable interface. Orders fields by the
     * field name (ie. fieldType of FieldType). Not generally recommended
     * that compareTo uses different comparators than equals method, but
     * should be ok with our expected usage.
     */
    public int compareTo(Object o) {
        Field otherField = (Field)o;
        return this.getFieldType().getFieldType().compareTo(otherField.getFieldType().getFieldType());
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof Field) ) return false;
        final Field castOther = (Field) other;
        return this.getRecord().getId()==castOther.getRecord().getId() &&
            this.getFieldType().getId()==castOther.getFieldType().getId();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result,getRecord().getId());
        result = HashCodeUtil.hash(result,getFieldType().getId());
        return result;
    }

}
