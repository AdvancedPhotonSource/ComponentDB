/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * IRMIS business object that represents the type of a record such as ai or mbbo.
 * This class is mapped via hibernate almost one-to-one with IRMIS rec_type table.
 */
public class RecordType extends IRMISDataObject implements Comparable {

    private String recordType;
    // making this a sorted set to preserve ordering given in dbd files
    private SortedSet fieldTypes = new TreeSet();
    private IOCResource iocResource;
    private IOCBoot iocBoot;

    /**
     * Constructor
     */
    public RecordType() {
    }

    /**
     * Get the set of field types defined for this particular record type. Ie.
     * what are the fields (DESC, HOPR, INPA) defined for this record type (ai)?
     */
    public SortedSet getFieldTypes() {
        return this.fieldTypes;
    }
    /**
     * Get the set of field types defined for this particular record type. Ie.
     * what are the fields (DESC, HOPR, INPA) defined for this record type (ai)?
     */
    public void setFieldTypes(SortedSet value) {
        this.fieldTypes = value;
    }

    /**
     * Add one FieldType to the set of associated field types. Establishes bidirectional
     * identity (ie. field type knows which RecordType it belongs to as well).
     */
    public void addFieldType(FieldType fieldType) {
        fieldType.setRecordType(this);
        fieldTypes.add(fieldType);
    }    

    /**
     * Get the parent IOCBoot associated with this record type.
     */
    public IOCBoot getIocBoot() {
        return this.iocBoot;
    }
    /**
     * Set the parent IOCBoot associated with this record type.
     */
    public void setIocBoot(IOCBoot value) {
        this.iocBoot = value;
    }

    /**
     * Get the IOCResource which is responsible for the definition of this record
     * type. This implies that the resource will be a dbd file.
     */
    public IOCResource getIocResource() {
        return this.iocResource;
    }
    /**
     * Set the IOCResource which is responsible for the definition of this record
     * type. This implies that the resource will be a dbd file.
     */
    public void setIocResource(IOCResource value) {
        this.iocResource = value;
    }

    /**
     * Get the string representing the record type. Examples: ai, mbbo, calc.
     */
    public String getRecordType() {
        return this.recordType;
    }

    /**
     * Set the string representing the record type. Examples: ai, mbbo, calc.
     */
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String toString() {
        return getId().toString();
    }

    /**
     * Required to implement Comparable interface. Orders record types by the
     * getRecordType string. Not generally recommended
     * that compareTo uses different comparators than equals method, but
     * should be ok with our expected usage.
     */
    public int compareTo(Object o) {
        RecordType otherRecordType = (RecordType)o;
        return this.getRecordType().compareTo(otherRecordType.getRecordType());
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof RecordType) ) return false;
        final RecordType castOther = (RecordType) other;
        return this.getIocBoot().getId()==castOther.getIocBoot().getId() &&
            this.getRecordType()==castOther.getRecordType();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result,getIocBoot().getId());
        result = HashCodeUtil.hash(result,getRecordType());
        return result;
    }

}
