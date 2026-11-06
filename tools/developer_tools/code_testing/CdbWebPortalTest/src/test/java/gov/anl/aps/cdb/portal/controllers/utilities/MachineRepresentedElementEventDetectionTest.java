/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.mqtt.CdbMachineRepresentedElementEvent;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class MachineRepresentedElementEventDetectionTest {

    @Test
    public void detectsSetReplaceAndClear() throws Exception {
        ItemElement first = element(10, "E1");
        ItemElement second = element(11, "E2");
        assertTransition(null, first);
        assertTransition(first, second);
        assertTransition(first, null);
    }

    @Test
    public void includesPropagatedInstanceEventWithTemplateContext() throws Exception {
        ItemElement oldElement = element(10, "E1");
        ItemElement newElement = element(11, "E2");
        ItemDomainMachineDesign template = machine(1, newElement);
        ItemDomainMachineDesign instance = machine(2, newElement);
        instance.setPendingRepresentedElementChange(oldElement, template);
        template.addItemToUpdate(instance);
        TestUtility utility = new TestUtility();

        List<MqttEvent> events = utility.events(template);

        assertEquals(1, events.size());
        CdbMachineRepresentedElementEvent event
                = (CdbMachineRepresentedElementEvent) events.get(0);
        assertEquals("2", event.getMachine().getId());
        assertEquals("1", event.getSourceTemplate().getId());
        assertEquals("10", event.getOldRepresentedElement().getId());
        assertEquals("11", event.getNewRepresentedElement().getId());
    }

    @Test
    public void suppressesNoOp() throws Exception {
        ItemElement represented = element(10, "E1");
        ItemDomainMachineDesign original = machine(1, represented);
        ItemDomainMachineDesign edited = machine(1, represented);
        TestUtility utility = new TestUtility();

        utility.detect(original, edited);

        assertTrue(utility.events(edited).isEmpty());
    }

    private static void assertTransition(ItemElement oldElement, ItemElement newElement)
            throws Exception {
        ItemDomainMachineDesign original = machine(1, oldElement);
        ItemDomainMachineDesign edited = machine(1, newElement);
        TestUtility utility = new TestUtility();
        utility.detect(original, edited);
        List<MqttEvent> events = utility.events(edited);
        assertEquals(1, events.size());
        CdbMachineRepresentedElementEvent event
                = (CdbMachineRepresentedElementEvent) events.get(0);
        assertEquals(oldElement == null ? null : oldElement.getId().toString(),
                event.getOldRepresentedElement() == null
                        ? null : event.getOldRepresentedElement().getId());
        assertEquals(newElement == null ? null : newElement.getId().toString(),
                event.getNewRepresentedElement() == null
                        ? null : event.getNewRepresentedElement().getId());
    }

    private static ItemElement element(int id, String name) {
        ItemDomainCatalog assembly = catalog(100 + id, "Assembly");
        ItemDomainCatalog part = catalog(200 + id, "Part");
        ItemElement element = new ItemElement();
        element.setId(id);
        element.setName(name);
        element.setParentItem(assembly);
        element.setContainedItem(part);
        return element;
    }

    private static ItemDomainMachineDesign machine(int id, ItemElement represented) {
        ItemDomainMachineDesign item = new ItemDomainMachineDesign();
        initialize(item, id, "Machine");
        item.setRepresentsCatalogElement(represented);
        return item;
    }

    private static ItemDomainCatalog catalog(int id, String name) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        initialize(item, id, name);
        return item;
    }

    private static void initialize(Item item, int id, String name) {
        item.setId(id);
        item.setName(name);
        item.setEntityTypeListFromApi(new ArrayList<>());
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
    }

    private static final class TestUtility extends ItemDomainMachineDesignControllerUtility {
        void detect(ItemDomainMachineDesign original, ItemDomainMachineDesign edited) {
            prepareMachineRepresentedElementChange(original, edited);
        }

        List<MqttEvent> events(ItemDomainMachineDesign edited) throws Exception {
            return prepareSpecializedUpdateEvents(edited, null);
        }
    }
}
