/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
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
