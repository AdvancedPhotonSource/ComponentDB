/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt.metadata;

import gov.anl.aps.cdb.mqtt.CdbMqttTopic;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;

/** Pending context for a machine trash or restore event. */
public final class MachineTrashChange {

    private final CdbMqttTopic topic;
    private final ItemDomainMachineDesign requestedHierarchyRoot;
    private final int affectedMachineCount;

    public MachineTrashChange(CdbMqttTopic topic,
            ItemDomainMachineDesign requestedRoot, int affectedCount) {
        this.topic = topic;
        this.requestedHierarchyRoot = requestedRoot;
        this.affectedMachineCount = affectedCount;
    }

    public CdbMqttTopic getTopic() { return topic; }
    public ItemDomainMachineDesign getRequestedHierarchyRoot() { return requestedHierarchyRoot; }
    public int getAffectedMachineCount() { return affectedMachineCount; }
}
