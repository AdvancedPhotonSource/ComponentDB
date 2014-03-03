/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenthistory.echo2;

/**
 * Provides notification that a particular aspect of the <code>ComponentHistoryModel</code>
 * has changed.
 */
public class ComponentHistoryModelEvent {

    /**
     * Model now contains initial data required to begin appliation.
     */
    static final int RELOAD_COMPLETE = 1;    

    /**
     * Model now contains list of component instances.
     */
    static final int INSTANCE_SEARCH_COMPLETE = 2;    

    /**
     * Model now contains list of component history.
     */
    static final int HISTORY_SEARCH_COMPLETE = 3;    

    /**
     * The login/logout attempt is complete, so user interface may be updated now.
     */
    static final int LOGIN_COMPLETE = 4;    

    /**
     * A new location ComponentInstanceState was added to the current component instance.
     */
    static final int NEW_LOCATION_HISTORY = 5;    

    /**
     * A new operation ComponentInstanceState was added to the current component instance.
     */
    static final int NEW_OPERATION_HISTORY = 6;    

    /**
     * A list of all components in housing into which the selected component instance
     * could be placed has been determined.
     */
    static final int OPEN_INSTALL_SLOTS_FOUND = 7;    

    /**
     * A new calibration ComponentInstanceState was added to the current component instance.
     */
    static final int NEW_CALIBRATION_HISTORY = 8;    

    /**
     * A new nrtl ComponentInstanceState was added to the current component instance.
     */
    static final int NEW_NRTL_HISTORY = 9;    


    private int type;

    /**
     * Do nothing constructor.
     */
    private ComponentHistoryModelEvent() {
    }

    /**
     * Create a new event of the given type.
     */
    public ComponentHistoryModelEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}
