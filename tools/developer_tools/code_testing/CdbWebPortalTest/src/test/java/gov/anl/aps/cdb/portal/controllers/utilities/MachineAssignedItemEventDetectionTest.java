/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.mqtt.CdbMachineAssignedItemEvent;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class MachineAssignedItemEventDetectionTest {

    @Test
    public void detectsAssignReplaceAndUnassign() throws Exception {
        ItemDomainCatalog first = catalog(10, "First");
        ItemDomainCatalog second = catalog(11, "Second");
        assertTransition(null, first);
        assertTransition(first, second);
        assertTransition(first, null);
    }

    @Test
    public void detectsInstalledStateOnlyChange() throws Exception {
        ItemDomainCatalog assigned = catalog(10, "Assigned");
        ItemDomainMachineDesign original = machine(1, assigned, true);
        ItemDomainMachineDesign edited = machine(1, assigned, false);
        TestUtility utility = new TestUtility();

        utility.detect(original, edited);
        CdbMachineAssignedItemEvent event = event(utility.events(edited));

        assertTrue(event.isOldInstalled());
        assertFalse(event.isNewInstalled());
    }

    @Test
    public void suppressesNoOp() throws Exception {
        ItemDomainCatalog assigned = catalog(10, "Assigned");
        ItemDomainMachineDesign original = machine(1, assigned, true);
        ItemDomainMachineDesign edited = machine(1, assigned, true);
        TestUtility utility = new TestUtility();

        utility.detect(original, edited);

        assertTrue(utility.events(edited).isEmpty());
    }

    private static void assertTransition(Item oldItem, Item newItem) throws Exception {
        ItemDomainMachineDesign original = machine(1, oldItem, true);
        ItemDomainMachineDesign edited = machine(1, newItem, true);
        TestUtility utility = new TestUtility();
        utility.detect(original, edited);
        CdbMachineAssignedItemEvent event = event(utility.events(edited));
        assertEquals(oldItem == null ? null : oldItem.getId().toString(),
                event.getOldAssignedItem() == null ? null : event.getOldAssignedItem().getId());
        assertEquals(newItem == null ? null : newItem.getId().toString(),
                event.getNewAssignedItem() == null ? null : event.getNewAssignedItem().getId());
    }

    private static CdbMachineAssignedItemEvent event(List<MqttEvent> events) {
        assertEquals(1, events.size());
        return (CdbMachineAssignedItemEvent) events.get(0);
    }

    private static ItemDomainMachineDesign machine(int id, Item assigned, boolean installed) {
        ItemDomainMachineDesign machine = new ItemDomainMachineDesign();
        initialize(machine, id, "Machine");
        machine.setAssignedItem(assigned);
        machine.setIsHoused(installed);
        return machine;
    }

    private static ItemDomainCatalog catalog(int id, String name) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        initialize(item, id, name);
        return item;
    }

    private static void initialize(Item item, int id, String name) {
        item.setId(id);
        item.setName(name);
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
    }

    private static final class TestUtility extends ItemDomainMachineDesignControllerUtility {
        void detect(ItemDomainMachineDesign original, ItemDomainMachineDesign edited) {
            prepareMachineAssignedItemChange(original, edited);
        }

        List<MqttEvent> events(ItemDomainMachineDesign edited) throws Exception {
            return prepareSpecializedUpdateEvents(edited, null);
        }
    }
}
