/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

public enum CdbMqttTopic {
    ADD("cdb/entity/add"),
    UPDATE("cdb/entity/update"),
    DELETE("cdb/entity/delete"),
    ITEM_PROPERTY_ADD("cdb/item/property/add"),
    ITEM_PROPERTY_UPDATE("cdb/item/property/update"),
    ITEM_PROPERTY_DELETE("cdb/item/property/delete"),
    MACHINE_DESIGN_ASSIGNED_ITEM_UPDATE("cdb/machine-design/assigned-item/update"),
    MACHINE_DESIGN_MOVE("cdb/machine-design/move");

    private final String value;

    CdbMqttTopic(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
