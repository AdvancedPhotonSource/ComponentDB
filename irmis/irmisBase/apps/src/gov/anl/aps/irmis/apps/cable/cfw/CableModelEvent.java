/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.cable.cfw;

/**
 * Provides notification that a particular aspect of the <code>CableModel</code>
 * has changed.
 */
public class CableModelEvent {

    private int type;
    private Object payload;

    /**
     * Model now contains the set of cables attached to selected port.
     * Payload is a List of <code>Cable</code> objects, or null if none.
     */
    static final int NEW_CABLES = 2;

    /**
     * Model now contains the cable connecting ports of component A and B.
     * Payload is a <code>Cable</code> object, or null if none.
     */
    static final int NEW_CABLE = 3;

    /**
     * Model now contains the component and port for Component B slot
     * as determined by the selected cable.
     * Payload is a <code>ComponentPort</code> object, or null if none.
     */
    static final int NEW_COMPONENT_B_AND_PORT = 4;

    /**
     * Model now contains a newly create cable between selected ports.
     * Payload is a <code>Cable</code> object.
     */
    static final int CABLE_ADDED = 5;

    /**
     * The model's selected cable is now null.
     * Payload is the <code>Cable</code> object that was deleted.
     */
    static final int CABLE_REMOVED = 6;

    /**
     * The document is requesting a complete redraw. The model has
     * whatever state needs to be redrawn.
     */
    static final int REDRAW = 7;

    /**
     * Model now contains a newly updated cable: either label, color,
     * or single-endedness has changed.
     * Payload is a <code>Cable</code> object.
     */
    static final int CABLE_UPDATED = 8;

    /**
     * The housing find mechanism has been configured for use. Payload of this
     * event contains a <code>ConfigureFindModel</code> object.
     */
    static final int HOUSING_FIND_CONFIG = 9;

    /**
     * The control find mechanism has been configured for use. Payload of this
     * event contains a <code>ConfigureFindModel</code> object.
     */
    static final int CONTROL_FIND_CONFIG = 10;

    /**
     * The power find mechanism has been configured for use. Payload of this
     * event contains a <code>ConfigureFindModel</code> object.
     */
    static final int POWER_FIND_CONFIG = 11;

    /**
     * All child components of a given component have been loaded from the db,
     * so notify the cable interface that they can be displayed (expanded)
     * in the tree. Payload of this event contains a <code>Component</code> object
     * corresponding to parent of children.
     */    
    static final int EXPAND_CHILDREN = 13;

    /**
     * The database commit of modified cables is complete.
     */
    static final int SAVE_COMPLETE = 14;


    /**
     * Do nothing constructor.
     */
    private CableModelEvent() {
    }

    /**
     * Create a new event of the given type.
     */
    public CableModelEvent(int type) {
        this.type = type;
        this.payload = null;
    }

    /**
     * Create a new event of the given type.
     */
    public CableModelEvent(int type, Object payload) {
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
