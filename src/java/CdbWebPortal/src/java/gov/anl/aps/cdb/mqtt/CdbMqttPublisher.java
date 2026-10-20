/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.base.mqtt.MqttPublisher;
import gov.anl.aps.cdb.base.mqtt.exception.MqttException;
import gov.anl.aps.cdb.base.mqtt.exception.MqttNotConfiguredException;
import gov.anl.aps.cdb.base.mqtt.exception.MqttPublishException;
import gov.anl.aps.cdb.base.mqtt.jca.JcaMqttPublisher;
import gov.anl.aps.cdb.base.mqtt.jca.JndiMqttConnectionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Application adapter that makes MQTT publishing best effort. */
public final class CdbMqttPublisher {

    public static final String JNDI_RESOURCE_NAME = "cdb/MQTT/resource";
    private static final Logger LOGGER = LogManager.getLogger(CdbMqttPublisher.class.getName());
    private static final MqttPublisher PUBLISHER = new JcaMqttPublisher(
            new JndiMqttConnectionProvider(JNDI_RESOURCE_NAME), new ObjectMapper());

    private CdbMqttPublisher() {
    }

    public static void publish(MqttEvent event) {
        try {
            PUBLISHER.publish(event);
        } catch (MqttNotConfiguredException ex) {
            LOGGER.warn("MQTT is unavailable; skipped event for " + event.getTopic()
                    + ". " + ex.getMessage());
        } catch (MqttPublishException ex) {
            LOGGER.warn("MQTT broker is unavailable or disconnected; skipped event for "
                    + event.getTopic() + ". " + getRootCauseMessage(ex));
        } catch (MqttException | RuntimeException ex) {
            LOGGER.error("Failed to prepare MQTT event for " + event.getTopic(), ex);
        }
    }

    private static String getRootCauseMessage(Throwable error) {
        Throwable cause = error;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
    }
}
