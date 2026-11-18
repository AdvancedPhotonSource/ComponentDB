/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.EntityType;

/** Scalar entity-type reference for event payloads. */
public final class CdbEntityTypeSnapshot {

    private final Integer id;
    private final String name;

    public CdbEntityTypeSnapshot(EntityType entityType) {
        if (entityType == null) {
            throw new IllegalArgumentException("entity type is required");
        }
        id = entityType.getId();
        name = entityType.getName();
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
}
