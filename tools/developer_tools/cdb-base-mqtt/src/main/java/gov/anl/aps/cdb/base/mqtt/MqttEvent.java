/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.base.mqtt;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

/** Base for an application-owned payload and its destination topic. */
public abstract class MqttEvent {
    private final Date eventTimestamp;

    protected MqttEvent() {
        eventTimestamp = new Date();
    }

    @JsonIgnore
    public abstract String getTopic();

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEventTimestamp() {
        return new Date(eventTimestamp.getTime());
    }
}
