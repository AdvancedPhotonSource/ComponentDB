/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw;

/**
 * Provides notification that a particular aspect of the <code>ComponentModel</code>
 * has changed.
 */
public class ComponentModelEvent {

    private int type;
    private Object payload;

    /**
     * Model now contains a new list of filtered component types.
     */
    static final int NEW_FILTERED_TYPES = 2;

    /**
     * The database commit of model's component to configure is complete.
     */
    static final int COMMIT_COMPLETE = 3;

    /**
     * A child has been removed from one of the hierarchies. Payload
     * of this event contains a <code>ConfigureAction</code> indicating which.
     */
    static final int CHILD_REMOVED = 4;

    /**
     * A child has been added to one of the hierarchies. Payload
     * of this event contains a <code>ConfigureAction</code> indicating where.
     */
    static final int CHILD_ADDED = 5;

    /**
     * A child has been moved up or down with respect to other children. Payload
     * of this event contains a <code>ConfigureAction</code> indicating up or down.
     */
    static final int CHILD_RELOCATED = 6;

    /**
     * The logical description of the parent relationship of component to configure
     * has been modified. Payload of this event contains a <code>ConfigureAction</code>
     * indicating which component, hierarchy, and component rel with modified desc.
     */
    static final int MODIFY_LOGICAL_DESCRIPTION = 7;

    /**
     * Component selection has been done programatically (instead of mouse click).
     * Payload of this event contains a <code>Component</code> object, indicating
     * indicating which component to select in the housing hierarchy.
     */
    static final int SELECT_COMPONENT_IN_HOUSING = 8;

    /**
     * Requests redraw of everything based on whatever is currently in model.
     */
    static final int REDRAW = 9;

    /**
     * The housing find mechanism has been configured for use. Payload of this
     * event contains a <code>ConfigureFindModel</code> object.
     */
    static final int HOUSING_FIND_CONFIG = 10;

    /**
     * The control find mechanism has been configured for use. Payload of this
     * event contains a <code>ConfigureFindModel</code> object.
     */
    static final int CONTROL_FIND_CONFIG = 11;

    /**
     * The power find mechanism has been configured for use. Payload of this
     * event contains a <code>ConfigureFindModel</code> object.
     */
    static final int POWER_FIND_CONFIG = 12;

    /**
     * All child components of a given component have been loaded from the db,
     * so notify the component interface that they can be displayed (expanded)
     * in the tree. Payload of this event contains a <code>Component</code> object
     * corresponding to parent of children.
     */
    static final int EXPAND_CHILDREN = 13;

    /**
     * The component name of the component to configure has been modified.
     * Payload of this event contains a <code>ConfigureAction</code>
     * indicating which component and the original component name.
     */
    static final int MODIFY_COMPONENT_NAME = 14;

    /**
     * A list of potential replacement component types is now available in
     * the model. These are possible replacements for current selectedComponent.
     */
    static final int REPLACEMENT_LIST = 15;


    /**
     * Do nothing constructor.
     */
    private ComponentModelEvent() {
    }

    /**
     * Create a new event of the given type.
     */
    public ComponentModelEvent(int type) {
        this.type = type;
        this.payload = null;
    }

    /**
     * Create a new event of the given type.
     */
    public ComponentModelEvent(int type, Object payload) {
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
