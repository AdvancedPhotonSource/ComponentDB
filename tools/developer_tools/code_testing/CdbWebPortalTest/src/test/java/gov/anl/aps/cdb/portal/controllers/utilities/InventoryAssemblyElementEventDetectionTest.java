/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.mqtt.CdbInventoryAssemblyElementEvent;
import gov.anl.aps.cdb.mqtt.metadata.InventoryAssemblyElementChange;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class InventoryAssemblyElementEventDetectionTest {

    @Test
    public void convertsAssignReplaceAndClearMetadataToEvents() throws Exception {
        ItemDomainInventory assembly = inventory(1, "Assembly");
        ItemElement element = element(10, assembly);
        ItemDomainInventory first = inventory(2, "First");
        ItemDomainInventory second = inventory(3, "Second");
        assembly.addPendingAssemblyElementChange(
                new InventoryAssemblyElementChange(element, null, first));
        assembly.addPendingAssemblyElementChange(
                new InventoryAssemblyElementChange(element, first, second));
        assembly.addPendingAssemblyElementChange(
                new InventoryAssemblyElementChange(element, second, null));

        List<MqttEvent> events = new TestUtility().events(assembly);

        assertEquals(3, events.size());
        CdbInventoryAssemblyElementEvent assign
                = (CdbInventoryAssemblyElementEvent) events.get(0);
        assertNull(assign.getOldAssignedInventoryItem());
        assertEquals("2", assign.getNewAssignedInventoryItem().getId());
        CdbInventoryAssemblyElementEvent clear
                = (CdbInventoryAssemblyElementEvent) events.get(2);
        assertEquals("3", clear.getOldAssignedInventoryItem().getId());
        assertNull(clear.getNewAssignedInventoryItem());
    }

    @Test
    public void persistedElementComparisonDetectsAssignmentAndSuppressesNoOp() throws Exception {
        ItemDomainInventory original = inventory(1, "Assembly");
        ItemElement originalElement = element(10, original);
        original.getFullItemElementList().add(originalElement);
        ItemDomainInventory edited = inventory(1, "Assembly");
        ItemElement editedElement = element(10, edited);
        ItemDomainInventory assigned = inventory(2, "Assigned");
        editedElement.setContainedItem(assigned);
        edited.getFullItemElementList().add(editedElement);
        TestUtility utility = new TestUtility();

        utility.detect(original, edited);

        List<MqttEvent> events = utility.events(edited);
        assertEquals(1, events.size());
        CdbInventoryAssemblyElementEvent event
                = (CdbInventoryAssemblyElementEvent) events.get(0);
        assertNull(event.getOldAssignedInventoryItem());
        assertEquals("2", event.getNewAssignedInventoryItem().getId());
    }

    @Test
    public void includesChangesFromNewlyGeneratedChildAssembly() throws Exception {
        ItemDomainInventory root = inventory(1, "Root");
        ItemDomainInventory childAssembly = inventory(2, "Child Assembly");
        ItemDomainInventory grandchild = inventory(3, "Grandchild");
        ItemElement rootElement = element(10, root);
        ItemElement childElement = element(11, childAssembly);
        root.addPendingAssemblyElementChange(
                new InventoryAssemblyElementChange(rootElement, null, childAssembly));
        childAssembly.addPendingAssemblyElementChange(
                new InventoryAssemblyElementChange(childElement, null, grandchild));

        List<MqttEvent> events = new TestUtility().events(root);

        assertEquals(2, events.size());
        assertEquals("1", ((CdbInventoryAssemblyElementEvent) events.get(0))
                .getInventoryAssembly().getId());
        assertEquals("2", ((CdbInventoryAssemblyElementEvent) events.get(1))
                .getInventoryAssembly().getId());
    }

    @Test
    public void unchangedAssemblyProducesNoEvent() throws Exception {
        assertTrue(new TestUtility().events(inventory(1, "Assembly")).isEmpty());
    }

    private static ItemElement element(int id, Item parent) {
        ItemElement element = new ItemElement();
        element.setId(id);
        element.setName("E" + id);
        element.setParentItem(parent);
        return element;
    }

    private static ItemDomainInventory inventory(int id, String name) {
        ItemDomainInventory item = new ItemDomainInventory();
        item.setId(id);
        item.setName(name);
        item.setEntityTypeListFromApi(new ArrayList<>());
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        return item;
    }

    private static final class TestUtility extends ItemDomainInventoryControllerUtility {
        void detect(ItemDomainInventory original, ItemDomainInventory edited) {
            prepareInventoryAssemblyElementChanges(original, edited,
                    original.getFullItemElementList(), edited.getFullItemElementList());
        }

        List<MqttEvent> events(ItemDomainInventory item) throws Exception {
            return prepareSpecializedUpdateEvents(item, null);
        }
    }
}
