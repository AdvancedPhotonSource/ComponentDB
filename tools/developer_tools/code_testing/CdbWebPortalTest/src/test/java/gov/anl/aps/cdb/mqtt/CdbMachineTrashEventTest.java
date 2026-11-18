/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbMachineTrashEventTest {

    @Test
    public void serializesHierarchyTrashContext() throws Exception {
        CdbMachineTrashEvent event = new CdbMachineTrashEvent(
                CdbMqttTopic.MACHINE_DESIGN_TRASH,
                machine(2, "Child"), machine(1, "Root"), 3, null);
        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("cdb/machine-design/trash", event.getTopic());
        assertEquals(CdbMqttOperation.TRASH, event.getOperation());
        assertEquals("2", event.getAffectedMachine().getId());
        assertEquals("1", event.getRequestedHierarchyRoot().getId());
        assertEquals(3, event.getAffectedMachineCount());
        assertFalse(json.contains("\"topic\""));
    }

    @Test
    public void supportsRestoreOperation() {
        CdbMachineTrashEvent event = new CdbMachineTrashEvent(
                CdbMqttTopic.MACHINE_DESIGN_RESTORE,
                machine(1, "Root"), machine(1, "Root"), 1, null);
        assertEquals(CdbMqttOperation.RESTORE, event.getOperation());
        assertEquals("cdb/machine-design/restore", event.getTopic());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnrelatedTopic() {
        new CdbMachineTrashEvent(CdbMqttTopic.UPDATE,
                machine(1, "Root"), machine(1, "Root"), 1, null);
    }

    private static ItemDomainMachineDesign machine(int id, String name) {
        ItemDomainMachineDesign item = new ItemDomainMachineDesign();
        item.setId(id);
        item.setName(name);
        item.setDomain(new Domain(4, "machineDesign"));
        item.setEntityTypeListFromApi(new ArrayList<>());
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        return item;
    }
}
