/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/portal/constants/DesignElementType.java $
 *   $Date: 2015-04-17 12:25:03 -0500 (Fri, 17 Apr 2015) $
 *   $Revision: 594 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.portal.constants;

/**
 * Design element type enum.
 */
public enum DesignElementType {
    COMPONENT("component"),
    DESIGN("design");
    
    private final String type;
    private DesignElementType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
