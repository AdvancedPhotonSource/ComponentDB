/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/** Scalar snapshot of a completed machine trash or restore operation. */
public final class CdbMachineTrashEvent extends CdbMqttEvent {

    private final CdbMqttOperation operation;
    private final CdbItemSnapshot affectedMachine;
    private final CdbItemSnapshot requestedHierarchyRoot;
    private final int affectedMachineCount;

    public CdbMachineTrashEvent(CdbMqttTopic topic, ItemDomainMachineDesign machine,
            ItemDomainMachineDesign requestedRoot, int affectedCount, UserInfo user) {
        super(topic, user);
        if (topic != CdbMqttTopic.MACHINE_DESIGN_TRASH
                && topic != CdbMqttTopic.MACHINE_DESIGN_RESTORE) {
            throw new IllegalArgumentException("trash or restore topic is required");
        }
        if (machine == null || requestedRoot == null || affectedCount < 1) {
            throw new IllegalArgumentException("machine, root, and affected count are required");
        }
        operation = topic == CdbMqttTopic.MACHINE_DESIGN_TRASH
                ? CdbMqttOperation.TRASH : CdbMqttOperation.RESTORE;
        this.affectedMachine = new CdbItemSnapshot(machine);
        this.requestedHierarchyRoot = new CdbItemSnapshot(requestedRoot);
        this.affectedMachineCount = affectedCount;
    }

    public CdbMqttOperation getOperation() { return operation; }
    public CdbItemSnapshot getAffectedMachine() { return affectedMachine; }
    public CdbItemSnapshot getRequestedHierarchyRoot() { return requestedHierarchyRoot; }
    public int getAffectedMachineCount() { return affectedMachineCount; }
}
