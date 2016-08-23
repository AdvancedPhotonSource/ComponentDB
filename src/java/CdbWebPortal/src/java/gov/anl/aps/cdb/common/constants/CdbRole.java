/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/common/constants/CdbRole.java $
 *   $Date: 2015-04-17 12:25:03 -0500 (Fri, 17 Apr 2015) $
 *   $Revision: 594 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.common.constants;

/**
 * CDB role enum.
 */
public enum CdbRole {

    USER("user"),
    ADMIN("admin");

    private final String type;

    private CdbRole(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static CdbRole fromString(String type) {
        CdbRole role = null;
        switch (type) {
            case "user":
                role = USER;
                break;
            case "admin":
                role = ADMIN;
                break;
        }
        return role;
    }
}
