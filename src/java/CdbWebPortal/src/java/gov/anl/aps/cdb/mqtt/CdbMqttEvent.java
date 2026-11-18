/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Base for CDB MQTT events with common routing and actor context. */
public abstract class CdbMqttEvent extends MqttEvent {

    private final String topic;
    private final String triggeredByUsername;

    protected CdbMqttEvent(CdbMqttTopic topic, UserInfo triggeredByUser) {
        if (topic == null) {
            throw new IllegalArgumentException("topic is required");
        }
        this.topic = topic.getValue();
        triggeredByUsername = triggeredByUser == null ? null : triggeredByUser.getUsername();
    }

    @Override
    public final String getTopic() {
        return topic;
    }

    public final String getTriggeredByUsername() {
        return triggeredByUsername;
    }
}
