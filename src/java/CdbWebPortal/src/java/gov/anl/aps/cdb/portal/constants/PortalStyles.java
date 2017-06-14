/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum PortalStyles {
    favoritesOn("favoriteOn"),
    favoritesOff("favoriteOff"),
    
    rowStyleErrorInEntity("errorItemInRow"), 
    rowStyleNewEntity("newItemInRow"); 

    private String value;

    private PortalStyles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
};
