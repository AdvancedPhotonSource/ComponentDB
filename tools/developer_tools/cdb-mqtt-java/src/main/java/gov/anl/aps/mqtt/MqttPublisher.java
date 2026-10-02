/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt;

import gov.anl.aps.mqtt.exception.MqttException;

public interface MqttPublisher {
    void publish(MqttEvent event) throws MqttException;
}
