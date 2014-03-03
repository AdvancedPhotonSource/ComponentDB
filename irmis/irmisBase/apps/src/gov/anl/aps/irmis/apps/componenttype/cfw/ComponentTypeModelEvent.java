/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw;

/**
 * Provides notification that a particular aspect of the <code>ComponentTypeModel</code>
 * has changed.
 */
public class ComponentTypeModelEvent {

    /**
     * Model now contains a new list of component type information.
     */
    static final int NEW_COMPONENT_TYPE_LIST = 1;    

    /**
     * Model now contains a fully instantiated component type object.
     */
    static final int NEW_ATTR_LIST = 2;    

    /**
     * Model now contains a new component type selection.
     */
    static final int NEW_COMPONENT_TYPE_SELECTION = 3;    

    /**
     * Model contains a new component type.
     */
    static final int NEW_COMPONENT_TYPE = 4;    

    private int type;

    /**
     * Do nothing constructor.
     */
    private ComponentTypeModelEvent() {
    }

    /**
     * Create a new event of the given type.
     */
    public ComponentTypeModelEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}
