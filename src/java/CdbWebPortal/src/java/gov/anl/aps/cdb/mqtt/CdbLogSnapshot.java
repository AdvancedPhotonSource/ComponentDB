/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.annotation.JsonFormat;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Date;

/** Scalar snapshot of an item log entry; attachments are never included. */
public final class CdbLogSnapshot {

    private final String logId;
    private final String text;
    private final boolean textOmitted;
    private final Date enteredOnDateTime;
    private final Date effectiveFromDateTime;
    private final Date effectiveToDateTime;
    private final String enteredByUsername;
    private final String logTopicId;
    private final String logTopicName;

    public CdbLogSnapshot(Log log, String text, boolean textOmitted) {
        Object id = log.getId();
        logId = id == null ? null : id.toString();
        this.text = text;
        this.textOmitted = textOmitted;
        enteredOnDateTime = copy(log.getEnteredOnDateTime());
        effectiveFromDateTime = copy(log.getEffectiveFromDateTime());
        effectiveToDateTime = copy(log.getEffectiveToDateTime());
        UserInfo enteredBy = log.getEnteredByUser();
        enteredByUsername = enteredBy == null ? null : enteredBy.getUsername();
        LogTopic topic = log.getLogTopic();
        logTopicId = topic == null || topic.getId() == null ? null : topic.getId().toString();
        logTopicName = topic == null ? null : topic.getName();
    }

    private static Date copy(Date value) {
        return value == null ? null : new Date(value.getTime());
    }

    public String getLogId() { return logId; }
    public String getText() { return text; }
    public boolean isTextOmitted() { return textOmitted; }
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEnteredOnDateTime() { return copy(enteredOnDateTime); }
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEffectiveFromDateTime() { return copy(effectiveFromDateTime); }
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEffectiveToDateTime() { return copy(effectiveToDateTime); }
    public String getEnteredByUsername() { return enteredByUsername; }
    public String getLogTopicId() { return logTopicId; }
    public String getLogTopicName() { return logTopicName; }
}
