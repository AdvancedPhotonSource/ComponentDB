/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.annotation.JsonValue;

/** Operation represented by a CDB MQTT event. */
public enum CdbMqttOperation {
    ADD("add"),
    UPDATE("update"),
    DELETE("delete"),
    TRASH("trash"),
    RESTORE("restore");

    private final String value;

    CdbMqttOperation(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
