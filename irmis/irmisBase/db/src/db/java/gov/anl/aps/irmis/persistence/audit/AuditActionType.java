/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence.audit;

import gov.anl.aps.irmis.persistence.IRMISDataObject;
import gov.anl.aps.irmis.persistence.HashCodeUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * IRMIS business object that represents a single audit action type.
 * This class is mapped via hibernate almost one-to-one with IRMIS audit_action_type table.
 */
public class AuditActionType extends IRMISDataObject {

    public static final int ADD_COMPONENT = 1;
    public static final int MODIFY_LOGICAL_ORDER = 2;
    public static final int MODIFY_LOGICAL_DESCRIPTION = 3;
    public static final int DELETE_COMPONENT = 4;
    public static final int ADD_COMPONENT_TYPE = 5;
    public static final int EDIT_COMPONENT_TYPE = 6;
    public static final int LOGIN_SUCCESSFUL = 7;
    public static final int LOGIN_FAILED = 8;
    public static final int LOGOUT = 9;
    public static final int ADD_CABLE = 10;
    public static final int REMOVE_CABLE = 11;
    public static final int EDIT_CABLE = 12;
    public static final int MODIFY_COMPONENT_NAME = 13;
    public static final int MODIFY_PARENT = 14;
    public static final int MODIFY_COMPONENT_TYPE = 15;
    public static final int MODIFY_SERIAL_NUMBER = 16;
    public static final int MODIFY_GROUP_NAME = 17;
    public static final int MODIFY_VERIFIED_FLAG = 18;
    public static final int MODIFY_COMPONENT_IMAGE_URI = 19;

    private String actionName;

    /**
     * Constructor
     */
    public AuditActionType() {
    }

    /**
     * Get the name of this audit action.
     */
    public String getActionName() {
        return this.actionName;
    }
    /**
     * Set the name of this audit action.
     */
    public void setActionName(String value) {
        this.actionName = value;
    }

    public String toString() {
        return getActionName();
    }

    /**
     * Custom equals which compares "business key" equality.
     */
    public boolean equals(Object other) {
        if (this==other) return true;
        if ( !(other instanceof AuditActionType) ) return false;
        final AuditActionType castOther = (AuditActionType) other;
        return this.getActionName().equals(castOther.getActionName());
    }

    /**
     * Custom hashCode computed on "business key" fields.
     */
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getActionName());
        return result;
    }

}
