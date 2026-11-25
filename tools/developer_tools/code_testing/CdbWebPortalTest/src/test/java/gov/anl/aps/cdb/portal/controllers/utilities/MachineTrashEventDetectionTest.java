/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.mqtt.CdbMachineTrashEvent;
import gov.anl.aps.cdb.mqtt.CdbMqttTopic;
import gov.anl.aps.cdb.mqtt.metadata.MachineTrashChange;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class MachineTrashEventDetectionTest {

    @Test
    public void createsOneEventPerAffectedMachine() throws Exception {
        ItemDomainMachineDesign root = machine(1, "Root");
        ItemDomainMachineDesign child = machine(2, "Child");
        root.setPendingTrashChange(new MachineTrashChange(
                CdbMqttTopic.MACHINE_DESIGN_TRASH, root, 2));
        child.setPendingTrashChange(new MachineTrashChange(
                CdbMqttTopic.MACHINE_DESIGN_TRASH, root, 2));
        TestUtility utility = new TestUtility();

        List<MqttEvent> rootEvents = utility.events(root);
        List<MqttEvent> childEvents = utility.events(child);

        assertEquals(1, rootEvents.size());
        assertEquals(1, childEvents.size());
        CdbMachineTrashEvent childEvent = (CdbMachineTrashEvent) childEvents.get(0);
        assertEquals("1", childEvent.getRequestedHierarchyRoot().getId());
        assertEquals(2, childEvent.getAffectedMachineCount());
    }

    @Test
    public void itemWithoutPendingTransitionProducesNoEvent() throws Exception {
        assertTrue(new TestUtility().events(machine(1, "Root")).isEmpty());
    }

    private static ItemDomainMachineDesign machine(int id, String name) {
        ItemDomainMachineDesign item = new ItemDomainMachineDesign();
        item.setId(id);
        item.setName(name);
        item.setEntityTypeListFromApi(new ArrayList<>());
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        return item;
    }

    private static final class TestUtility extends ItemDomainMachineDesignControllerUtility {
        List<MqttEvent> events(ItemDomainMachineDesign item) throws Exception {
            return prepareSpecializedUpdateEvents(item, null);
        }
    }
}
