/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.Attachment;

/**
 * Scalar attachment metadata; file contents are never included. Identified by its unique
 * stored name, which exists before the attachment row is saved (unlike its DB ID).
 */
public final class CdbAttachmentSnapshot {

    private final String name;
    private final String originalFilename;

    public CdbAttachmentSnapshot(Attachment attachment) {
        name = attachment.getName();
        originalFilename = attachment.getOriginalFilename();
    }

    public String getName() { return name; }
    public String getOriginalFilename() { return originalFilename; }
}
