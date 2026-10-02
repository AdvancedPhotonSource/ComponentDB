/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt.jca;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.mqtt.MqttEvent;
import gov.anl.aps.mqtt.MqttPublisher;
import gov.anl.aps.mqtt.exception.MqttException;
import gov.anl.aps.mqtt.exception.MqttPublishException;
import gov.anl.aps.mqtt.exception.MqttSerializationException;
import java.nio.charset.StandardCharsets;

public class JcaMqttPublisher implements MqttPublisher {
    private final MqttConnectionProvider connections;
    private final ObjectMapper mapper;
    private final int qos;
    private final boolean retained;

    public JcaMqttPublisher(MqttConnectionProvider connections, ObjectMapper mapper) { this(connections, mapper, 0, false); }
    public JcaMqttPublisher(MqttConnectionProvider connections, ObjectMapper mapper, int qos, boolean retained) {
        if (connections == null || mapper == null) throw new IllegalArgumentException("connections and mapper are required");
        if (qos < 0 || qos > 2) throw new IllegalArgumentException("qos must be between 0 and 2");
        this.connections = connections; this.mapper = mapper; this.qos = qos; this.retained = retained;
    }

    @Override public void publish(MqttEvent event) throws MqttException {
        if (event == null || event.getTopic() == null || event.getTopic().trim().isEmpty()) throw new IllegalArgumentException("event topic is required");
        final byte[] payload;
        try { payload = mapper.writeValueAsString(event).getBytes(StandardCharsets.UTF_8); }
        catch (JsonProcessingException ex) { throw new MqttSerializationException("Failed to serialize MQTT event", ex); }
        try (MqttConnection connection = connections.getConnection()) {
            connection.publish(event.getTopic(), payload, qos, retained);
        } catch (MqttException ex) { throw ex; }
        catch (Exception ex) { throw new MqttPublishException("Failed to publish MQTT event to " + event.getTopic(), ex); }
    }
}
