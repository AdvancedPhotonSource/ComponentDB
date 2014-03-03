/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.ioc.cfw;

/**
 * Provides notification that a particular aspect of the <code>IOCModel</code>
 * has changed.
 */
public class IOCModelEvent {

    /**
     * Model now contains a new list of ioc information.
     */
    static final int NEW_IOC_LIST = 1;    

    /**
     * Model now contains list of ioc attributes
     */
    static final int NEW_IOC_ATTR_LIST = 2;    

    /**
     * Model now contains a new selectedIocName.
     */
    static final int NEW_IOC_SELECTION = 3;

    /**
     * New IOC has been saved, meaning model now contains modified IOC object.
     */
    static final int IOC_SAVED = 4;

    /**
     * An IOC attribute has been deleted, so attribute list is modified.
     */
    static final int IOC_ATTRIBUTE_DELETED = 5;


    private int type;

    /**
     * Do nothing constructor.
     */
    private IOCModelEvent() {
    }

    /**
     * Create a new event of the given type.
     */
    public IOCModelEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}
