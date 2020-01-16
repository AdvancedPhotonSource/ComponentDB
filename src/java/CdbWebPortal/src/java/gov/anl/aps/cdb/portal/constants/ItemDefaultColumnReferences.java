/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemDefaultColumnReferences {
    name("name"),
    itemIdentifier1("itemIdentifier1"),
    itemIdentifier2("itemIdentifier2"),
    itemProject("itemProject"),
    description("description"),
    qrId("qrId"),
    derivedFromItem("derivedFromItem"),
    ownerUser("ownerUser"),
    ownerGroup("ownerGroup"),
    groupWriteable("groupWriteable"),
    property("property"),
    
    //Inventory Specific
    inventoryStatus("status");

    private String value;

    private ItemDefaultColumnReferences(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }
}
