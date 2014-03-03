/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package CFWDemo;

/**
 * Provides notification that a particular aspect of the <code>DemoModel</code>
 * has changed. We need this, because the DemoModel could possibly contain lots
 * of different data used for different purposes. When something happens to change
 * that data (ie. a SQL query completes), we want to notify the listeners and
 * be able to tell them what specifically has changed.
 *
 * Create a new specific event with:
 * <code>new DemoModelEvent(DemoModelEvent.NEW_IOC_LIST)</code>
 */
public class DemoModelEvent {

    /**
     * Model now contains a new ioc list read from database.
     */
    static final int NEW_IOC_LIST = 1;

    // add your additional enumerated event codes here...
    // static final int NEW_WHATEVER = 2; 

    private int type;

    /**
     * Do nothing constructor.
     */
    private DemoModelEvent() {
    }

    /**
     * Create a new event of the given type.
     */
    public DemoModelEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}
