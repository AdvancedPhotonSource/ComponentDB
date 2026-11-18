/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;

/** Scalar item-element reference for event payloads. */
public final class CdbItemElementSnapshot {

    private final String id;
    private final String name;
    private final CdbItemSnapshot parentItem;
    private final CdbItemSnapshot containedItem;

    public CdbItemElementSnapshot(ItemElement element) {
        if (element == null) {
            throw new IllegalArgumentException("element is required");
        }
        Object elementId = element.getId();
        id = elementId == null ? null : elementId.toString();
        name = element.getName();
        parentItem = element.getParentItem() == null
                ? null : new CdbItemSnapshot(element.getParentItem());
        containedItem = element.getContainedItem() == null
                ? null : new CdbItemSnapshot(element.getContainedItem());
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public CdbItemSnapshot getParentItem() { return parentItem; }
    public CdbItemSnapshot getContainedItem() { return containedItem; }
}
