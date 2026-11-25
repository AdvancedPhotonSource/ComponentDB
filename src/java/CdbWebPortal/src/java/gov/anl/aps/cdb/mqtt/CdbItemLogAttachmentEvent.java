/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Attachment newly associated with an item log entry. */
public final class CdbItemLogAttachmentEvent extends CdbMqttEvent {

    private final CdbMqttOperation operation = CdbMqttOperation.ADD;
    private final String itemId;
    private final String itemType;
    private final String itemDescription;
    private final String logId;
    private final CdbAttachmentSnapshot attachment;

    public CdbItemLogAttachmentEvent(Item item, Log log, Attachment attachment, UserInfo user) {
        super(CdbMqttTopic.ITEM_LOG_ATTACHMENT_ADD, user);
        if (item == null || log == null || attachment == null) {
            throw new IllegalArgumentException("item, log and attachment are required");
        }
        Object id = item.getId();
        itemId = id == null ? null : id.toString();
        itemType = item.getClass().getSimpleName();
        itemDescription = item.getSystemLogString();
        logId = log.getId() == null ? null : log.getId().toString();
        this.attachment = new CdbAttachmentSnapshot(attachment);
    }

    public CdbMqttOperation getOperation() { return operation; }
    public String getItemId() { return itemId; }
    public String getItemType() { return itemType; }
    public String getItemDescription() { return itemDescription; }
    public String getLogId() { return logId; }
    public CdbAttachmentSnapshot getAttachment() { return attachment; }
}
