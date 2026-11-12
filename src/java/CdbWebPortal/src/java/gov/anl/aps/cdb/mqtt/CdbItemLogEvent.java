/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Objects;

/**
 * Scalar snapshot of a completed item log-entry change. Follows CdbItemPropertyEvent
 * text conventions: add/delete carry text bounded at MAX_TEXT_LENGTH; updates carry a
 * bounded line diff (null when the text is unchanged) and omit full text.
 */
public final class CdbItemLogEvent extends CdbMqttEvent {

    public static final int MAX_TEXT_LENGTH = CdbItemPropertyEvent.MAX_TEXT_LENGTH;

    private final CdbMqttOperation operation;
    private final String itemId;
    private final String itemType;
    private final String itemDescription;
    private final CdbLogSnapshot oldLog;
    private final CdbLogSnapshot newLog;
    private final String textDiff;
    private final boolean diffTruncated;

    private CdbItemLogEvent(CdbMqttTopic topic, CdbMqttOperation operation, Item item,
            Log oldLog, Log newLog, UserInfo user) {
        super(topic, user);
        if (item == null || (oldLog == null && newLog == null)) {
            throw new IllegalArgumentException("item and a log entry are required");
        }
        this.operation = operation;
        Object id = item.getId();
        itemId = id == null ? null : id.toString();
        itemType = item.getClass().getSimpleName();
        itemDescription = item.getSystemLogString();

        String oldText = oldLog == null ? null : oldLog.getText();
        String newText = newLog == null ? null : newLog.getText();
        if (oldLog != null && newLog != null) {
            if (Objects.equals(oldText, newText)) {
                textDiff = null;
                diffTruncated = false;
            } else {
                String diff = CdbItemPropertyEvent.createLineDiff(oldText, newText);
                diffTruncated = diff.length() > MAX_TEXT_LENGTH;
                textDiff = CdbItemPropertyEvent.truncate(diff);
            }
            this.oldLog = new CdbLogSnapshot(oldLog, null, true);
            this.newLog = new CdbLogSnapshot(newLog, null, true);
        } else {
            textDiff = null;
            diffTruncated = CdbItemPropertyEvent.length(oldText) > MAX_TEXT_LENGTH
                    || CdbItemPropertyEvent.length(newText) > MAX_TEXT_LENGTH;
            this.oldLog = oldLog == null ? null
                    : new CdbLogSnapshot(oldLog, CdbItemPropertyEvent.truncate(oldText), false);
            this.newLog = newLog == null ? null
                    : new CdbLogSnapshot(newLog, CdbItemPropertyEvent.truncate(newText), false);
        }
    }

    public static CdbItemLogEvent added(Item item, Log log, UserInfo user) {
        return new CdbItemLogEvent(CdbMqttTopic.ITEM_LOG_ADD, CdbMqttOperation.ADD,
                item, null, log, user);
    }

    public static CdbItemLogEvent updated(Item item, Log oldLog, Log newLog, UserInfo user) {
        return new CdbItemLogEvent(CdbMqttTopic.ITEM_LOG_UPDATE, CdbMqttOperation.UPDATE,
                item, oldLog, newLog, user);
    }

    public static CdbItemLogEvent deleted(Item item, Log log, UserInfo user) {
        return new CdbItemLogEvent(CdbMqttTopic.ITEM_LOG_DELETE, CdbMqttOperation.DELETE,
                item, log, null, user);
    }

    public CdbMqttOperation getOperation() { return operation; }
    public String getItemId() { return itemId; }
    public String getItemType() { return itemType; }
    public String getItemDescription() { return itemDescription; }
    public CdbLogSnapshot getOldLog() { return oldLog; }
    public CdbLogSnapshot getNewLog() { return newLog; }
    public String getTextDiff() { return textDiff; }
    public boolean isDiffTruncated() { return diffTruncated; }
}
