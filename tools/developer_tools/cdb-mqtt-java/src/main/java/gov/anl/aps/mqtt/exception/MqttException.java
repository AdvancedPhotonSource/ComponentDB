/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt.exception;

public abstract class MqttException extends Exception {
    protected MqttException(String message) { super(message); }
    protected MqttException(String message, Throwable cause) { super(message, cause); }
}
