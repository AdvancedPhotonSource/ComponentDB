/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt.exception;

public class MqttPublishException extends MqttException {
    public MqttPublishException(String message, Throwable cause) { super(message, cause); }
}
