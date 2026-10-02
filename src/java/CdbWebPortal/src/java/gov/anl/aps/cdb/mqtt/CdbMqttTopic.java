/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

public enum CdbMqttTopic {
    ADD("cdb/entity/add"),
    UPDATE("cdb/entity/update"),
    DELETE("cdb/entity/delete");

    private final String value;

    CdbMqttTopic(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
