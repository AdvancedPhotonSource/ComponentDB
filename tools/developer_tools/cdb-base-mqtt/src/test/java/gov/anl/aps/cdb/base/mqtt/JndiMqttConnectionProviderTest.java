/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.base.mqtt;

import gov.anl.aps.cdb.base.mqtt.exception.MqttNotConfiguredException;
import gov.anl.aps.cdb.base.mqtt.jca.JndiMqttConnectionProvider;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JndiMqttConnectionProviderTest {
    @Test void missingJndiResourceIsReportedAsNotConfigured() {
        assertThrows(MqttNotConfiguredException.class, () -> new JndiMqttConnectionProvider("java:comp/env/missing-mqtt").getConnection());
    }
}
