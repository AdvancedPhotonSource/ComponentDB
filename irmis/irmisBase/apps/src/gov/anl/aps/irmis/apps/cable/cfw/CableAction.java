/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.cable.cfw;

// persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentPort;
import gov.anl.aps.irmis.persistence.component.Cable;

/**
 * Reflects a single change made to the cable object graph during
 * the time time the cable table is being edited. These are kept in
 * an undo stack in the cable model.
 */
public class CableAction {

    private int type;       // type of action, enumerated below as static int
    private Cable cable;  // cable being edited
    private String originalString;    // a string before modification
    private ComponentPort p1;
    private ComponentPort p2;    

    /**
     * Cable being added.
     */
    static final int ADD_CABLE = 1;

    /**
     * Cable changed from single ended to double (attached to 2 ports).
     */
    static final int SINGLE_TO_DOUBLE_ENDED = 2;

    /**
     * Cable changed from double ended to single (attached to just 1 port).
     */
    static final int DOUBLE_TO_SINGLE_ENDED = 3;

    /**
     * Cable virtual flag changed.
     */
    static final int UPDATE_VIRTUAL = 4;

    /**
     * Cable label changed.
     */
    static final int UPDATE_LABEL = 5;

    /**
     * Cable color changed.
     */
    static final int UPDATE_COLOR = 6;

    /**
     * Cable being removed.
     */
    static final int REMOVE_CABLE = 7;

    /**
     * Cable dest string changed.
     */
    static final int UPDATE_DEST = 8;


    /**
     * Do nothing constructor.
     */
    private CableAction() {
    }

    /**
     * Create a new event of the given type.
     */
    public CableAction(int type, Cable cable, String originalString) {
        this.type = type;
        this.cable = cable;
        this.originalString = originalString;
        this.p1 = null;
        this.p2 = null;
    }

    public CableAction(int type, Cable cable, ComponentPort p1, ComponentPort p2) {
        this.type = type;
        this.cable = cable;
        this.originalString = null;
        this.p1 = p1;
        this.p2 = p2;
    }

    public CableAction(int type, Cable cable, String originalString, ComponentPort p1, ComponentPort p2) {
        this.type = type;
        this.cable = cable;
        this.originalString = originalString;
        this.p1 = p1;
        this.p2 = p2;
    }

    public int getType() {
        return this.type;
    }

    public Cable getCable() {
        return this.cable;
    }

    public String getOriginalString() {
        return this.originalString;
    }

    public ComponentPort getComponentPort1() {
        return p1;
    }
    public ComponentPort getComponentPort2() {
        return p2;
    }
}
