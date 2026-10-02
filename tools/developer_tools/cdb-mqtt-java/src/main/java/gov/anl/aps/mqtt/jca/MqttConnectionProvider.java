/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt.jca;

import gov.anl.aps.mqtt.exception.MqttNotConfiguredException;

public interface MqttConnectionProvider {
    MqttConnection getConnection() throws MqttNotConfiguredException;
}
