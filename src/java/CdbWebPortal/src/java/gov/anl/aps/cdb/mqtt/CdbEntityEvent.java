/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Scalar snapshot of a completed entity operation. */
public class CdbEntityEvent extends CdbMqttEvent {

    private final CdbMqttOperation operation;
    private final String entityId;
    private final String entityType;
    private final String entityDescription;

    public CdbEntityEvent(CdbMqttTopic topic, CdbEntity entity, UserInfo triggeredByUser) {
        super(topic, triggeredByUser);
        if (entity == null) {
            throw new IllegalArgumentException("entity is required");
        }
        this.operation = CdbMqttOperation.valueOf(topic.name());
        Object id = entity.getId();
        this.entityId = id == null ? null : id.toString();
        this.entityType = entity.getClass().getSimpleName();
        this.entityDescription = entity.getSystemLogString();
    }

    public CdbMqttOperation getOperation() {
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
}
