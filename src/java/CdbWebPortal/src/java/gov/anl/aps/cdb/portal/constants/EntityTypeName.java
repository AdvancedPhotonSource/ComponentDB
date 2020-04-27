/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum EntityTypeName {
    template("Template"),
    inventory("Inventory");

    private String value;

    private EntityTypeName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
};
