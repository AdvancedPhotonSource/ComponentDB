/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.base.mqtt.MqttEvent;

/** Scalar snapshot of a completed entity operation. */
public class CdbEntityEvent extends MqttEvent {

    private final String topic;
    private final String operation;
    private final String entityId;
    private final String entityType;
    private final String entityDescription;
    private final String triggeredByUsername;

    public CdbEntityEvent(CdbMqttTopic topic, CdbEntity entity, UserInfo triggeredByUser) {
        if (topic == null || entity == null) {
            throw new IllegalArgumentException("topic and entity are required");
        }
        this.topic = topic.getValue();
        this.operation = topic.name().toLowerCase();
        Object id = entity.getId();
        this.entityId = id == null ? null : id.toString();
        this.entityType = entity.getClass().getSimpleName();
        this.entityDescription = entity.getSystemLogString();
        this.triggeredByUsername = triggeredByUser == null ? null : triggeredByUser.getUsername();
    }

    @Override
    public String getTopic() {
        return topic;
    }

    public String getOperation() {
        return operation;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityDescription() {
        return entityDescription;
    }

    public String getTriggeredByUsername() {
        return triggeredByUsername;
    }
}
