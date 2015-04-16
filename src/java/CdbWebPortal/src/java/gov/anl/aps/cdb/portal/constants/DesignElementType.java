/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: $
 *   $Date: $
 *   $Revision: $
 *   $Author: $
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
    
    /**
     * Convert enum to string.
     * 
     * @return design element type string 
     */
    @Override
    public String toString() {
        return type;
    }
}
