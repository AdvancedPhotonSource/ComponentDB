/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** An application-owned payload and its destination topic. */
public interface MqttEvent {
    @JsonIgnore
    String getTopic();
}
