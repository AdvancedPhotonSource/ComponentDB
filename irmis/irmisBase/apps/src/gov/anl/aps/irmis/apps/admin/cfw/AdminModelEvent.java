/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.admin.cfw;

/**
 * Provides notification that a particular aspect of the <code>AdminModel</code>
 * has changed.
 */
public class AdminModelEvent {

    private int type;
    private Object payload;

    /**
     * Model now contains a new list of IRMIS users (persons).
     * 
     */
    static final int NEW_PERSONS = 1;

    /**
     * Model now contains a new or updated person.
     * 
     */
    static final int PERSON_SAVED = 2;

    /**
     * Model now contains a new list of component types.
     * 
     */
    static final int NEW_COMPONENT_TYPES = 3;

    /**
     * Do nothing constructor.
     */
    private AdminModelEvent() {
    }

    /**
     * Create a new event of the given type.
     */
    public AdminModelEvent(int type) {
        this.type = type;
        this.payload = null;
    }

    /**
     * Create a new event of the given type.
     */
    public AdminModelEvent(int type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public int getType() {
        return this.type;
    }

    public Object getPayload() {
        return this.payload;
    }

}
