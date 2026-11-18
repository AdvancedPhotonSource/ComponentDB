/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.base.mqtt.jca;

import gov.anl.aps.cdb.base.mqtt.exception.MqttNotConfiguredException;

public interface MqttConnectionProvider {
    MqttConnection getConnection() throws MqttNotConfiguredException;
}
