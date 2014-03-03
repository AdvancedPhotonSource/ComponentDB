/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * IRMIS business object that represents the underlying type information for
 * a given process variable field. This includes the actual name and DBF type
 * of the field, as well as the default value given in the dbd definition.
 * This class is mapped via hibernate almost one-to-one with IRMIS fld_type table.
 */
public class FieldType extends IRMISDataObject {

    private RecordType recordType;
    private String fieldType;
    private String dbdType;
    private String defaultFieldValue;

    /**
     * Constructor
     */
    public FieldType() {
    }

    /**
     * Get record type from which this field type comes. This is the essentially
     * asking "which dbd definition" did a given field's type come from.
     */
    public RecordType getRecordType() {
        return this.recordType;
    }
    /**
     * Set record type from which this field type comes. This is the essentially
     * asking "which dbd definition" did a given field's type come from.
     */
    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    /**
     * Get the string representing this field's "type". Ex. HOPR or DESC
     */
    public String getFieldType() {
        return this.fieldType;
    }
    /**
     * Set the string representing this field's "type". Ex. HOPR or DESC
     */
    public void setFieldType(String value) {
        this.fieldType = value;
    }
    
    /**
     * Get the underlying DBF_XXXX data type of this field type. Just a 
     * string in the set {"DBF_LONG","DBF_INLINK", etc...}
     */
    public String getDbdType() {
        return this.dbdType;
    }
    /**
     * Set the underlying DBF_XXXX data type of this field type.
     */
    public void setDbdType(String value) {
        this.dbdType = value;
    }

    /**
     * Get the default value this field type is given by its dbd definition.
     */
    public String getDefaultFieldValue() {
        return this.defaultFieldValue;
    }
    /**
     * Set the default value this field type is given by its dbd definition.
     */
    public void setDefaultFieldValue(String value) {
        this.defaultFieldValue = value;
    }

    public String toString() {
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof FieldType) ) return false;
        final FieldType castOther = (FieldType) other;
        return this.getRecordType().getId()==castOther.getRecordType().getId() &&
            this.getFieldType()==castOther.getFieldType();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result,getRecordType().getId());
        result = HashCodeUtil.hash(result,getFieldType());
        return result;
    }

}
