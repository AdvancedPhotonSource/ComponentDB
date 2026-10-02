/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt.exception;

public class MqttNotConfiguredException extends MqttException {
    public MqttNotConfiguredException(String message) { super(message); }
    public MqttNotConfiguredException(String message, Throwable cause) { super(message, cause); }
}
