/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum PortalStyles {
    favoritesOn("favoriteOn"),
    favoritesOff("favoriteOff"),
    rowStyleErrorInEntity("errorItemInRow"),
    rowStyleNewEntity("newItemInRow"),
    itemConnectorIcon("ui-icon-item-connector"),
    machineDesingTemplateIcon("ui-icon-machine-design-template"),
    machineDesignControlIcon("fa fa-code"),
    machineDesignPowerIcon("fa fa-bolt"),
    machineDesignIOCIcon("ui-icon-control"),
    machineDesignIcon("ui-icon-machine-design"),
    catalogIcon("ui-icon-catalog"),
    inventoryIcon("ui-icon-inventory"),
    maarcIcon("ui-icon-maarc"),
    cableDesignIcon("ui-icon-cable-design");

    private String value;

    private PortalStyles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
};
