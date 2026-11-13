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
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
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

    @Test
    public void nonTemplateNodesHaveNoTemplateContext() {
        CdbMachineAddEvent event = (CdbMachineAddEvent) persist(machine(null, "Plain")).get(0);

        assertNull(event.getSourceTemplate());
        assertNull(event.getTemplateAppliedToMachine());
        assertNull(event.getTemplateCreatedMachineCount());
    }

    @Test
    public void templateInstantiatedUnderExistingParentSharesNewTopNode() {
        ItemDomainMachineDesign parent = saved(machine(null, "Parent"));
        ItemDomainMachineDesign templateTop = saved(machine(null, "Template top"));
        ItemDomainMachineDesign templateChild = saved(machine(null, "Template child"));
        ItemDomainMachineDesign top = fromTemplate(machine(null, "Instance"), templateTop, false);
        ItemDomainMachineDesign a = fromTemplate(machine(null, "A"), templateChild, false);
        ItemDomainMachineDesign b = fromTemplate(machine(null, "B"), templateChild, false);
        child(parent, top);
        child(top, a);
        child(a, b);

        List<MqttEvent> events = persist(parent);

        assertEquals(3, events.size());
        assertTemplate(events.get(0), templateTop, top, 3);
        assertTemplate(events.get(1), templateChild, top, 3);
        assertTemplate(events.get(2), templateChild, top, 3);
        assertAdd(events.get(0), top, parent);
    }

    @Test
    public void templateAssignedToExistingMachineReportsThatMachine() {
        ItemDomainMachineDesign template = saved(machine(null, "Template"));
        ItemDomainMachineDesign templateChild = saved(machine(null, "Template child"));
        ItemDomainMachineDesign existing = fromTemplate(saved(machine(null, "Existing")), template, false);
        ItemDomainMachineDesign a = fromTemplate(machine(null, "A"), templateChild, false);
        ItemDomainMachineDesign b = fromTemplate(machine(null, "B"), templateChild, false);
        child(existing, a);
        child(existing, b);

        List<MqttEvent> events = persist(existing);

        assertEquals(2, events.size());
        assertTemplate(events.get(0), templateChild, existing, 2);
        assertTemplate(events.get(1), templateChild, existing, 2);
    }

    @Test
    public void newTemplateUnderOlderTemplateInstanceStartsNewApplication() {
        ItemDomainMachineDesign oldTemplate = saved(machine(null, "Old template"));
        ItemDomainMachineDesign newTemplate = saved(machine(null, "New template"));
        ItemDomainMachineDesign olderInstance = fromTemplate(saved(machine(null, "Older")), oldTemplate, true);
        ItemDomainMachineDesign added = fromTemplate(machine(null, "Added"), newTemplate, false);
        child(olderInstance, added);

        List<MqttEvent> events = persist(olderInstance);

        assertEquals(1, events.size());
        assertTemplate(events.get(0), newTemplate, added, 1);
    }

    @Test
    public void templateUnderNewPlaceholderAppliesToTemplateNodeOnly() {
        ItemDomainMachineDesign template = saved(machine(null, "Template"));
        ItemDomainMachineDesign placeholder = machine(null, "Placeholder");
        ItemDomainMachineDesign instance = fromTemplate(machine(null, "Instance"), template, false);
        child(placeholder, instance);

        List<MqttEvent> events = persist(placeholder);

        assertEquals(2, events.size());
        assertNull(((CdbMachineAddEvent) events.get(0)).getTemplateAppliedToMachine());
        assertTemplate(events.get(1), template, instance, 1);
    }

    @Test
    public void separateApplicationsInOneListSaveAreCountedSeparately() {
        ItemDomainMachineDesign template = saved(machine(null, "Template"));
        ItemDomainMachineDesign first = fromTemplate(machine(null, "First"), template, false);
        ItemDomainMachineDesign second = fromTemplate(machine(null, "Second"), template, false);
        child(first, fromTemplate(machine(null, "First child"), template, false));

        List<MqttEvent> events = persist(first, second);

        assertEquals(3, events.size());
        assertTemplate(events.get(0), template, first, 2);
        assertTemplate(events.get(1), template, first, 2);
        assertTemplate(events.get(2), template, second, 1);
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

    private static void assertTemplate(MqttEvent value, ItemDomainMachineDesign template,
            ItemDomainMachineDesign appliedTo, int count) {
        CdbMachineAddEvent event = (CdbMachineAddEvent) value;
        assertEquals(template.getId().toString(), event.getSourceTemplate().getId());
        assertEquals(appliedTo.getId().toString(), event.getTemplateAppliedToMachine().getId());
        assertEquals(Integer.valueOf(count), event.getTemplateCreatedMachineCount());
    }

    // Mirrors ItemControllerUtility.addCreatedFromTemplateRelationshipToItem.
    private static ItemDomainMachineDesign fromTemplate(ItemDomainMachineDesign machine,
            ItemDomainMachineDesign template, boolean relationshipSaved) {
        ItemElementRelationship relationship = new ItemElementRelationship();
        relationship.setRelationshipType(new RelationshipType(1,
                ItemElementRelationshipTypeNames.template.getValue()));
        relationship.setFirstItemElement(machine.getSelfElement());
        relationship.setSecondItemElement(template.getSelfElement());
        if (relationshipSaved) {
            relationship.setId(900);
        }
        machine.setItemElementRelationshipList(new ArrayList<>(Collections.singletonList(relationship)));
        return machine;
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
