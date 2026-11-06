/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Scalar item reference for event payloads. */
public final class CdbItemSnapshot {

    private final String id;
    private final String domain;
    private final Integer domainId;
    private final String description;
    private final List<CdbEntityTypeSnapshot> entityTypes;

    public CdbItemSnapshot(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item is required");
        }
        Object itemId = item.getId();
        id = itemId == null ? null : itemId.toString();
        domain = item.getDomain() == null ? null : item.getDomain().getName();
        domainId = item.getDomain() == null ? null : item.getDomain().getId();
        description = item.getSystemLogString();
        List<CdbEntityTypeSnapshot> snapshots = new ArrayList<>();
        if (item.getEntityTypeList() != null) {
            List<EntityType> entityTypes = new ArrayList<>(item.getEntityTypeList());
            entityTypes.sort(Comparator.comparing(EntityType::getId,
                    Comparator.nullsLast(Comparator.naturalOrder())));
            for (EntityType entityType : entityTypes) {
                snapshots.add(new CdbEntityTypeSnapshot(entityType));
            }
        }
        entityTypes = Collections.unmodifiableList(snapshots);
    }

    public String getId() { return id; }
    public String getDomain() { return domain; }
    @JsonProperty("domain_id")
    public Integer getDomainId() { return domainId; }
    public String getDescription() { return description; }
    public List<CdbEntityTypeSnapshot> getEntityTypes() { return entityTypes; }
}
