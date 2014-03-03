/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.pv.cfw;

/**
 * Provides notification that a particular aspect of the <code>PVModel</code>
 * has changed.
 */
public class PVModelEvent {

    /**
     * Model now contains a new list of ioc boot information.
     */
    static final int NEW_IOC_BOOT_LIST = 1;

    /**
     * Model now contains a new set of record search results.
     */
    static final int NEW_RECORD_LIST = 2;

    /**
     * Model now contains a new set of field info for selected record.
     */
    static final int NEW_FIELD_LIST = 3;

    /**
     * Model now contains a new set of related pv link info for selected record.
     */
    static final int NEW_LINK_LIST = 4;

    /**
     * Model now contains new data as a result of clicking a pv link.
     */
    static final int NEW_RECORD = 5;

    /**
     * Model now contains a new set of record clients for selected record
     */
    static final int NEW_CLIENT_LIST = 6;

    /**
     * Model now contains a new set of ioc boot objects for selected record.
     */
    static final int NEW_RECORD_BOOT_HISTORY_LIST = 7;

    private int type;

    /**
     * Do nothing constructor.
     */
    private PVModelEvent() {
    }

    /**
     * Create a new event of the given type.
     */
    public PVModelEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}
