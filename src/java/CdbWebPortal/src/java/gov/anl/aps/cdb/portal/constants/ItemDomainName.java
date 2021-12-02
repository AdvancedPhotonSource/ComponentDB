/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemDomainName {
    location("Location", ItemDomainName.LOCATION_ID),
    catalog("Catalog", ItemDomainName.CATALOG_ID),
    inventory("Inventory", ItemDomainName.INVENTORY_ID),
    maarc("MAARC", ItemDomainName.MAARC_ID),
    machineDesign("Machine Design", ItemDomainName.MACHINE_DESIGN_ID),
    cableCatalog("Cable Catalog", ItemDomainName.CABLE_CATALOG_ID),
    cableInventory("Cable Inventory", ItemDomainName.CABLE_INVENTORY_ID),
    cableDesign("Cable Design", ItemDomainName.CABLE_DESIGN_ID),
    
    // Deprecated Domain
    cable("Cable", ItemDomainName.CABLE_ID);

    public final static int LOCATION_ID = 1;
    public final static int CATALOG_ID = 2;
    public final static int INVENTORY_ID = 3;

    // Deprecated Domain
    public final static int CABLE_ID = 4;

    public final static int MAARC_ID = 5;
    public final static int MACHINE_DESIGN_ID = 6;
    public final static int CABLE_CATALOG_ID = 7;
    public final static int CABLE_INVENTORY_ID = 8;
    public final static int CABLE_DESIGN_ID = 9;

    private String value;
    private Integer id;

    private ItemDomainName(String value, Integer id) {
        this.value = value;
        this.id = id;
    }

    public final String getValue() {
        return value;
    }

    public final Integer getId() {
        return id;
    }
};
