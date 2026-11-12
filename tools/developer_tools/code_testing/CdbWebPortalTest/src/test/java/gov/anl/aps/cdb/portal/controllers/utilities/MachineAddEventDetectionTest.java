/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.mqtt.CdbMachineAddEvent;
import gov.anl.aps.cdb.mqtt.metadata.MachineAddChange;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class MachineAddEventDetectionTest {

    private final TestUtility utility = new TestUtility();
    private int nextId = 100;

    @Test
    public void rootCreationPublishesNullParentAfterIdAssigned() {
        ItemDomainMachineDesign root = machine(null, "Root");

        List<MqttEvent> events = persist(root);

        assertEquals(1, events.size());
        CdbMachineAddEvent event = (CdbMachineAddEvent) events.get(0);
        assertEquals(root.getId().toString(), event.getMachine().getId());
        assertNull(event.getParent());
        assertEquals("operator", event.getTriggeredByUsername());
    }

    @Test
    public void nestedNewNodesOnParentEditPublishOnePerNodeWithDirectParent() {
        ItemDomainMachineDesign parent = saved(machine(null, "Parent"));
        ItemDomainMachineDesign child = machine(null, "Child");
        ItemDomainMachineDesign grandchild = machine(null, "Grandchild");
        child(parent, child);
        child(child, grandchild);

        List<MqttEvent> events = persist(parent);

        assertEquals(2, events.size());
        assertAdd(events.get(0), child, parent);
        assertAdd(events.get(1), grandchild, child);
    }

    @Test
    public void templateStyleHierarchyUnderNewRootPublishesEveryNode() {
        ItemDomainMachineDesign existingParent = saved(machine(null, "Existing"));
        ItemDomainMachineDesign root = machine(null, "Instance");
        ItemDomainMachineDesign a = machine(null, "A");
        ItemDomainMachineDesign b = machine(null, "B");
        child(existingParent, root);
        child(root, a);
        child(root, b);

        List<MqttEvent> events = persist(root);

        assertEquals(3, events.size());
        assertAdd(events.get(0), root, existingParent);
        assertAdd(events.get(1), a, root);
        assertAdd(events.get(2), b, root);
    }

    @Test
    public void idBearingNodeNotYetInDbIsTreatedAsNew() {
        ItemDomainMachineDesign parent = saved(machine(null, "Parent"));
        ItemDomainMachineDesign child = machine(55, "Preassigned");
        child(parent, child);

        List<MqttEvent> events = persist(parent);

        assertEquals(1, events.size());
        assertAdd(events.get(0), child, parent);
    }

    @Test
    public void movingExistingNodeDoesNotPublishAdd() {
        ItemDomainMachineDesign newParent = saved(machine(null, "NewParent"));
        ItemDomainMachineDesign moved = saved(machine(null, "Moved"));
        ItemDomainMachineDesign movedChild = saved(machine(null, "MovedChild"));
        child(moved, movedChild);
        child(newParent, moved);

        assertTrue(persist(newParent).isEmpty());
    }

    @Test
    public void nodeReachableFromSeveralListEntriesIsPublishedOnce() {
        ItemDomainMachineDesign parent = saved(machine(null, "Parent"));
        ItemDomainMachineDesign child = machine(null, "Child");
        child(parent, child);

        List<MqttEvent> events = persist(parent, child);

        assertEquals(1, events.size());
        assertAdd(events.get(0), child, parent);
    }

    @Test
    public void newNodesInItemsToUpdateCascadeArePublished() {
        ItemDomainMachineDesign root = saved(machine(null, "Root"));
        ItemDomainMachineDesign other = saved(machine(null, "Other"));
        ItemDomainMachineDesign added = machine(null, "Added");
        child(other, added);
        root.addItemToUpdate(other);

        List<MqttEvent> events = persist(root);

        assertEquals(1, events.size());
        assertAdd(events.get(0), added, other);
    }

    @Test
    public void failedPersistenceLeavesNoEventsAndPendingStateIsCleared() {
        ItemDomainMachineDesign root = machine(null, "Root");
        utility.prepareSpecializedCreateEvents(Collections.singletonList(root), user());
        // Simulated failure: no ID assigned.

        List<MqttEvent> events = utility.createSpecializedCreateEvents(
                Collections.singletonList(root), user());

        assertTrue(events.isEmpty());
        assertTrue(root.getPendingMachineAddChanges().isEmpty());
    }

    private List<MqttEvent> persist(ItemDomainMachineDesign... roots) {
        List<ItemDomainMachineDesign> entities = Arrays.asList(roots);
        utility.prepareSpecializedCreateEvents(entities, user());
        for (ItemDomainMachineDesign root : entities) {
            for (MachineAddChange change : root.getPendingMachineAddChanges()) {
                ItemDomainMachineDesign machine = change.getMachine();
                if (machine.getId() == null) {
                    machine.setId(nextId++);
                }
                utility.persistedIds.add(machine.getId());
            }
        }
        return utility.createSpecializedCreateEvents(entities, user());
    }

    private ItemDomainMachineDesign saved(ItemDomainMachineDesign machine) {
        machine.setId(nextId++);
        utility.persistedIds.add(machine.getId());
        return machine;
    }

    private static void assertAdd(MqttEvent value, ItemDomainMachineDesign machine,
            ItemDomainMachineDesign parent) {
        CdbMachineAddEvent event = (CdbMachineAddEvent) value;
        assertEquals(machine.getId().toString(), event.getMachine().getId());
        assertEquals(parent.getId().toString(), event.getParent().getId());
    }

    private static UserInfo user() {
        UserInfo user = new UserInfo();
        user.setUsername("operator");
        return user;
    }

    private static ItemDomainMachineDesign machine(Integer id, String name) {
        ItemDomainMachineDesign item = new ItemDomainMachineDesign();
        item.setId(id);
        item.setName(name);
        item.setEntityTypeListFromApi(new ArrayList<>());
        item.setItemElementMemberList(new ArrayList<>());
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        item.setEntityInfo(new EntityInfo());
        return item;
    }

    private static void child(ItemDomainMachineDesign parent, ItemDomainMachineDesign contained) {
        ItemElement element = new ItemElement();
        element.setParentItem(parent);
        element.setContainedItem(contained);
        element.setName("E" + parent.getFullItemElementList().size());
        parent.getFullItemElementList().add(element);
        contained.getItemElementMemberList().add(element);
        parent.resetItemElementVars();
    }

    private static final class TestUtility extends ItemDomainMachineDesignControllerUtility {
        private final Set<Integer> persistedIds = new HashSet<>();

        @Override
        protected boolean isMachinePersisted(ItemDomainMachineDesign machine) {
            return machine.getId() != null && persistedIds.contains(machine.getId());
        }
    }
}
