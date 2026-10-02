/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.mqtt.exception.MqttNotConfiguredException;
import gov.anl.aps.mqtt.exception.MqttPublishException;
import gov.anl.aps.mqtt.exception.MqttSerializationException;
import gov.anl.aps.mqtt.jca.JcaMqttPublisher;
import gov.anl.aps.mqtt.jca.MqttConnection;
import gov.anl.aps.mqtt.jca.MqttConnectionProvider;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JcaMqttPublisherTest {
    @Test void publishesConfiguredArgumentsAndCloses() throws Exception {
        FakeConnection connection = new FakeConnection();
        JcaMqttPublisher publisher = new JcaMqttPublisher(() -> connection, new ObjectMapper(), 1, true);
        publisher.publish(new Event("value"));
        assertEquals("cdb/add", connection.topic);
        assertTrue(new String(connection.payload, StandardCharsets.UTF_8).contains("\"value\":\"value\""));
        assertEquals(1, connection.qos); assertTrue(connection.retained); assertTrue(connection.closed);
    }
    @Test void reportsMissingConfiguration() {
        MqttConnectionProvider provider = () -> { throw new MqttNotConfiguredException("missing"); };
        assertThrows(MqttNotConfiguredException.class, () -> new JcaMqttPublisher(provider, new ObjectMapper()).publish(new Event("x")));
    }
    @Test void reportsSerializationFailureWithoutOpeningConnection() {
        boolean[] opened = {false};
        ObjectMapper mapper = new ObjectMapper() { @Override public String writeValueAsString(Object value) throws com.fasterxml.jackson.core.JsonProcessingException { throw new com.fasterxml.jackson.core.JsonProcessingException("bad") {}; } };
        assertThrows(MqttSerializationException.class, () -> new JcaMqttPublisher(() -> { opened[0]=true; return new FakeConnection(); }, mapper).publish(new Event("x")));
        assertFalse(opened[0]);
    }
    @Test void closesAfterPublishFailure() {
        FakeConnection connection = new FakeConnection(); connection.failPublish = true;
        assertThrows(MqttPublishException.class, () -> new JcaMqttPublisher(() -> connection, new ObjectMapper()).publish(new Event("x")));
        assertTrue(connection.closed);
    }
    @Test void rejectsInvalidQos() { assertThrows(IllegalArgumentException.class, () -> new JcaMqttPublisher(() -> null, new ObjectMapper(), 3, false)); }

    static final class Event implements MqttEvent {
        private final String value; Event(String value){this.value=value;}
        @Override public String getTopic(){return "cdb/add";} public String getValue(){return value;}
    }
    static final class FakeConnection implements MqttConnection {
        String topic; byte[] payload; int qos; boolean retained; boolean closed; boolean failPublish;
        @Override public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception { this.topic=topic;this.payload=payload;this.qos=qos;this.retained=retained;if(failPublish)throw new Exception("broker"); }
        @Override public void close(){closed=true;}
    }
}
