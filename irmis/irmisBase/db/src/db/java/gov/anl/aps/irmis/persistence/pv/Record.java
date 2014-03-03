/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.pv;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.Iterator;

/**
 * IRMIS business object that represents a single process variable from an ioc boot.
 * This class is mapped via hibernate almost one-to-one with IRMIS rec table.
 */
public class Record extends IRMISDataObject {

    private String recordName;
    private SortedSet fields = new TreeSet();
    private RecordType recordType;
    private IOCBoot iocBoot;

    /**
     * Constructor
     */
    public Record() {
    }

    /**
     * Get the set of process variable fields associated with this record.
     */
    public SortedSet getFields() {
        return this.fields;
    }
    /**
     * Set the set of process variable fields associated with this record.
     */
    public void setFields(SortedSet value) {
        this.fields = value;
    }
    /**
     * Add one Field to the set of associated fields. Establishes bidirectional
     * identity (ie. field knows which Record it belongs to as well).
     */
    public void addField(Field field) {
        field.setRecord(this);
        fields.add(field);
    }    

    /**
     * Find and return the field whose type corresponds to given fieldName
     * (ie. find "DESC" or "HOPR" field). Case of fieldName is ignored.
     */
    public Field findField(String fieldName) {
        Iterator it = getFields().iterator();
        Field f = null;
        while (it.hasNext()) {
            f = (Field)it.next();
            if (f.getFieldType().getFieldType().equalsIgnoreCase(fieldName))
                break;
            else
                f = null;
        }
        return f;
    }
    
    /**
     * Get the RecordType from which this record instance derives. Ie. what dbd
     * file is behind the underlying definition of this record's type.
     */
    public RecordType getRecordType() {
        return this.recordType;
    }
    /**
     * Set the RecordType from which this record instance derives. Ie. what dbd
     * file is behind the underlying definition of this record's type.
     */
    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    /**
     * Get the parent IOCBoot associated with this record.
     */
    public IOCBoot getIocBoot() {
        return this.iocBoot;
    }
    /**
     * Set the parent IOCBoot associated with this record.
     */
    public void setIocBoot(IOCBoot value) {
        this.iocBoot = value;
    }

    /**
     * Get the actual process variable name.
     */
    public String getRecordName() {
        return this.recordName;
    }
    /**
     * Set the actual process variable name.
     */
    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String toString() {
        return getId().toString() + ":" + getRecordName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof Record) ) return false;
        final Record castOther = (Record) other;
        return this.getIocBoot().getId()==castOther.getIocBoot().getId() &&
            this.getRecordName()==castOther.getRecordName();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result,getIocBoot().getId());
        result = HashCodeUtil.hash(result,getRecordName());
        return result;
    }

}
