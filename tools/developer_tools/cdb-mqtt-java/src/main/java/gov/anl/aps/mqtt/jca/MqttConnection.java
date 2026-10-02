/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt.jca;

public interface MqttConnection extends AutoCloseable {
    void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception;
    @Override void close() throws Exception;
}
