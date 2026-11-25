/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.metadata;

import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.Log;

/** Pending attachment newly associated with an item log; IDs may be assigned on save. */
public final class LogAttachmentChange {

    private final Log log;
    private final Attachment attachment;

    public LogAttachmentChange(Log log, Attachment attachment) {
        this.log = log;
        this.attachment = attachment;
    }

    public Log getLog() { return log; }
    public Attachment getAttachment() { return attachment; }
}
