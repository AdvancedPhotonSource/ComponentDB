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
 * IRMIS business object that represents a single ioc boot and contains (possibly)
 * all the records and fields that are part of the ioc's presence.
 * This class is mapped via hibernate almost one-to-one with IRMIS ioc_boot table.
 */
public class IOCBoot extends IRMISDataObject {

    private IOC ioc;
    private Set iocResources = new HashSet();
    private Set recordTypes = new HashSet();
    private Set records = new HashSet();
    private String sysBootLine;
    private Date iocBootDate;
    private boolean currentLoad;
    private boolean currentBoot;
    private Date modifiedDate;
    private String modifiedBy;
    private boolean participates = true;

    /**
     * Do-nothing constructor.
     */
    public IOCBoot() {
    }

    /**
     * Get IOC associated with this instance of an ioc boot.
     */
    public IOC getIoc() {
        return this.ioc;
    }
    /**
     * Set IOC associated with this instance of an ioc boot.
     */
    public void setIoc(IOC value) {
        this.ioc = value;
    }

    /**
     * Get the complete set of IOCResource associated with this ioc boot. 
     * This set represents the db, dbd, and template files that are part
     * of the startup command boot sequence.
     *
     * @see IOCResource
     */
    public Set getIocResources() {
        return this.iocResources;
    }
    /**
     * Set the complete set of IOCResource associated with this ioc boot. 
     * This set represents the db, dbd, and template files that are part
     * of the startup command boot sequence.
     *
     * @see IOCResource
     */
    public void setIocResources(Set value) {
        this.iocResources = value;
    }
    /**
     * Add one IOCResource to set of known resources. Establishes bidirectional
     * identity (ie. resource knows which IOCBoot it belongs to as well).
     */
    public void addIocResource(IOCResource iocResource) {
        iocResource.setIocBoot(this);
        iocResources.add(iocResource);
    }

    /**
     * Get the complete set of RecordType associated with this ioc boot. This
     * is the set of record types as defined in the dbd file used for this boot.
     */
    public Set getRecordTypes() {
        return this.recordTypes;
    }
    /**
     * Set the complete set of RecordType associated with this ioc boot. This
     * is the set of record types as defined in the dbd file used for this boot.
     */
    public void setRecordTypes(Set value) {
        this.recordTypes = value;
    }
    /**
     * Add one RecordType to set of known record types. Establishes bidirectional
     * identity (ie. record type knows which IOCBoot it belongs to as well).
     */
    public void addRecordType(RecordType recordType) {
        recordType.setIocBoot(this);
        recordTypes.add(recordType);
    }

    /**
     * Get the complete set of Record associated with this ioc boot. This is the
     * complete set of records (pv's) defined in all the db files of this boot.
     */
    public Set getRecords() {
        return this.records;
    }
    /**
     * Set the complete set of Record associated with this ioc boot. This is the
     * complete set of records (pv's) defined in all the db files of this boot.
     */
    public void setRecords(Set value) {
        this.records = value;
    }
    /**
     * Add one Record to the set of known records. Establishes bidirectional
     * identity (ie. record knows which IOCBoot it belongs to as well).
     */
    public void addRecord(Record record) {
        record.setIocBoot(this);
        records.add(record);
    }

    /**
     * Get the full file system path of the startup command file scanned as
     * part of this ioc boot.
     */
    public String getSysBootLine() {
        return this.sysBootLine;
    }
    /**
     * Set the full file system path of the startup command file scanned as
     * part of this ioc boot.
     */
    public void setSysBootLine(String value) {
        this.sysBootLine = value;
    }

    /**
     * Get the date when this ioc was last rebooted. The accuracy of this value
     * depends on how the pv crawler script determines this, and so is site
     * dependent.
     */
    public Date getIocBootDate() {
        return this.iocBootDate;
    }
    /**
     * Set the date when this ioc was last rebooted. The accuracy of this value
     * depends on how the pv crawler script determines this, and so is site
     * dependent.
     */
    public void setIocBootDate(Date value) {
        this.iocBootDate = value;
    }

    /**
     * Indicates whether this ioc boot contains the most recent set of resources,
     * records, and fields for the given ioc. This is necessary since an ioc boot
     * can fail, or get repeatedly rebooted without any changes to its resources.
     */
    public boolean getCurrentLoad() {
        return this.currentLoad;
    }
    public void setCurrentLoad(boolean value) {
        this.currentLoad = value;
    }

    /**
     * Indicates whether this ioc boot is the most recent ocurrence.
     */
    public boolean getCurrentBoot() {
        return this.currentBoot;
    }
    public void setCurrentBoot(boolean value) {
        this.currentBoot = value;
    }

    /**
     * Direct reflection of last modification of underlying database row.
     */
    public Date getModifiedDate() {
        return this.modifiedDate;
    }
    public void setModifiedDate(Date value) {
        this.modifiedDate = value;
    }

    /**
     * Represent user name or application name which last modified (created)
     * underlying database row.
     */
    public String getModifiedBy() {
        return this.modifiedBy;
    }
    public void setModifiedBy(String value) {
        this.modifiedBy = value;
    }

    /**
     * Flag indicating that this IOCBoot instance is part of the history
     * of boots related to a single Record. This is not established from
     * the database, and is not guaranteed to be set.
     */
    public boolean getParticipates() {
	return this.participates;
    }

    public void setParticipates(boolean participates) {
        this.participates = participates;
    }


    public String toString() {
        return getId().toString();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof IOCBoot) ) return false;
        final IOCBoot castOther = (IOCBoot) other;
        return this.getId() == castOther.getId();
        //return this.getIoc().getIocName().equals(castOther.getIoc().getIocName()) &&
        //this.getIocBootDate() == castOther.getIocBootDate();
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getId());
        //result = HashCodeUtil.hash(result, getIoc().getId());
        //result = HashCodeUtil.hash(result, getIocBootDate());
        return result;
    }

}
