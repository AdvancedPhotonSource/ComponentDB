/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.base.mqtt.exception;

public class MqttSerializationException extends MqttException {
    public MqttSerializationException(String message, Throwable cause) { super(message, cause); }
}
