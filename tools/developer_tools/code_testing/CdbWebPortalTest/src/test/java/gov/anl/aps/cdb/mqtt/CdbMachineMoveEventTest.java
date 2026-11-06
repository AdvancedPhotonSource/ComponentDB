/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbMachineMoveEventTest {

    @Test
    public void serializesMoveContextAndActorWithoutTopicProperty() throws Exception {
        UserInfo user = new UserInfo();
        user.setUsername("operator");
        ItemDomainMachineDesign moved = machine(1, "Moved");
        moved.setEntityTypeListFromApi(Collections.singletonList(new EntityType(9, "ioc")));
        CdbMachineMoveEvent event = new CdbMachineMoveEvent(
                moved, machine(2, "Old"), machine(3, "New"), user);

        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("cdb/machine-design/move", event.getTopic());
        assertEquals("1", event.getMachine().getId());
        assertEquals("2", event.getOldParent().getId());
        assertEquals("3", event.getNewParent().getId());
        assertEquals("machineDesign", event.getMachine().getDomain());
        assertEquals(Integer.valueOf(9), event.getMachine().getEntityTypes().get(0).getId());
        assertEquals("ioc", event.getMachine().getEntityTypes().get(0).getName());
        assertEquals("operator", event.getTriggeredByUsername());
        assertFalse(json.contains("\"topic\""));
        assertTrue(json.contains("\"eventTimestamp\""));
    }

    @Test
    public void supportsMoveFromTopLevel() {
        CdbMachineMoveEvent event = new CdbMachineMoveEvent(
                machine(1, "Moved"), null, machine(3, "New"), null);
        assertNull(event.getOldParent());
        assertNull(event.getTriggeredByUsername());
    }

    private static ItemDomainMachineDesign machine(int id, String name) {
        ItemDomainMachineDesign item = new ItemDomainMachineDesign();
        item.setId(id);
        item.setName(name);
        item.setDomain(new Domain(4, "machineDesign"));
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        return item;
    }
}
