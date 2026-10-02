/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt;

import gov.anl.aps.mqtt.exception.MqttNotConfiguredException;
import gov.anl.aps.mqtt.jca.JndiMqttConnectionProvider;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JndiMqttConnectionProviderTest {
    @Test void missingJndiResourceIsReportedAsNotConfigured() {
        assertThrows(MqttNotConfiguredException.class, () -> new JndiMqttConnectionProvider("java:comp/env/missing-mqtt").getConnection());
    }
}
