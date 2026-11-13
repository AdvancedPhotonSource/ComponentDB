/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.Attachment;

/** Scalar attachment metadata; file contents and storage paths are never included. */
public final class CdbAttachmentSnapshot {

    private final String attachmentId;
    private final String originalFilename;

    public CdbAttachmentSnapshot(Attachment attachment) {
        Object id = attachment.getId();
        attachmentId = id == null ? null : id.toString();
        originalFilename = attachment.getOriginalFilename();
    }

    public String getAttachmentId() { return attachmentId; }
    public String getOriginalFilename() { return originalFilename; }
}
