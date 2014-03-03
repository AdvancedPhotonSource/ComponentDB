/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw;

// persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;
import gov.anl.aps.irmis.persistence.login.Person;

/**
 * Reflects a single change made to the component object graph during
 * the time time a component is being configured. These are kept in
 * an undo stack in the component model.
 */
public class ConfigureAction {

    private int type;       // type of action, enumerated below as static int
    private Component ctc;  // component being configured
    private ComponentRelationship cr; // parent rel removed (if applicable)
    private int index;      // index of addition/removal (if applicable)
    private int hierarchy;  // hierarchy where action applied (from ComponentRelationshipType)
    private Person verifiedPerson; // user who verified ctc in given hierarchy
    private String originalString;    // a string before modification
    private ComponentType ct; // original component type before replacement

    /**
     * Component to configure has been added as a child.
     */
    static final int ADD_CHILD = 1;

    /**
     * Component to configure has been deleted at given index position.
     */
    static final int DELETE_CHILD = 2;

    /**
     * Component to configure has been demoted, and a new parent inserted in place of it.
     */
    static final int INSERT_PARENT = 3;

    /**
     * Component to configure has been relocated up or down with respect to other children.
     */
    static final int RELOCATE_CHILD = 4;

    /**
     * Component to configure has had its parent relationship's logical description modified.
     * Original value stored in originalString.
     */
    static final int MODIFY_LOGICAL_DESCRIPTION = 5;

    /**
     * Component to configure has had its component name modified.
     * Original value stored in originalString.
     */
    static final int MODIFY_COMPONENT_NAME = 6;

    /**
     * Component to configure has been relocated under a new parent component. 
     */
    static final int MODIFY_PARENT = 7;

    /**
     * Component to configure has been given a new component type.
     */
    static final int REPLACE_COMPONENT = 8;

    /**
     * Component to configure has had its serial number modified.
     * Original value stored in originalString.
     */
    static final int MODIFY_SERIAL_NUMBER = 9;

    /**
     * Component to configure has had its group ownership modified.
     * Original value stored in originalString.
     */
    static final int MODIFY_GROUP_NAME = 10;

    /**
     * Component to configure has had its verified flag modified.
     * Original value stored in originalString.
     */
    static final int MODIFY_VERIFIED_FLAG = 11;

    /**
     * Component has been verified within a particular hierarchy.
     */
    static final int COMPONENT_VERIFIED = 12;

    /**
     * Component has been un-verified within a particular hierarchy.
     */
    static final int COMPONENT_UNVERIFIED = 13;

    /**
     * Component to configure has had it's image URI modified.
     */
    static final int MODIFY_COMPONENT_IMAGE_URI = 14;

    /**
     * Do nothing constructor.
     */
    private ConfigureAction() {
    }

    /**
     * Create a new event of the given type.
     */
    public ConfigureAction(int type, Component ctc, ComponentRelationship cr,
                           int index, int hierarchy, String originalString, 
                           ComponentType ct, Person verifiedPerson) {
        this.type = type;
        this.ctc = ctc;
        this.cr = cr;
        this.index = index;
        this.hierarchy = hierarchy;
        this.originalString = originalString;
        this.ct = ct;
        this.verifiedPerson = verifiedPerson;
    }

    /**
     * Create a new event of the given type.
     */
    public ConfigureAction(int type, Component ctc, ComponentRelationship cr,
                           int index, int hierarchy, String originalString) {
        this(type, ctc, cr, index, hierarchy, originalString, null, null);
    }

    public ConfigureAction(int type, Component ctc, ComponentRelationship cr,
                           int index, int hierarchy) {
        this(type, ctc, cr, index, hierarchy, null);
    }

    public int getType() {
        return this.type;
    }

    public Component getComponentToConfigure() {
        return this.ctc;
    }

    public ComponentRelationship getParentRelationship() {
        return this.cr;
    }

    public int getIndex() {
        return this.index;
    }

    public int getHierarchy() {
        return this.hierarchy;
    }

    public String getOriginalString() {
        return this.originalString;
    }

    public ComponentType getComponentType() {
        return this.ct;
    }

    public Person getVerifiedPerson() {
        return this.verifiedPerson;
    }

}
