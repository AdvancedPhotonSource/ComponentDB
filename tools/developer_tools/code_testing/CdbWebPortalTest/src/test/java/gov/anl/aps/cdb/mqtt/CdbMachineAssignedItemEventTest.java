/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbMachineAssignedItemEventTest {

    @Test
    public void serializesAssignTransitionWithoutTopicProperty() throws Exception {
        ItemDomainMachineDesign machine = machine(1, "M1");
        Item assigned = catalog(2, "Catalog");
        UserInfo user = new UserInfo();
        user.setUsername("operator");

        CdbMachineAssignedItemEvent event = new CdbMachineAssignedItemEvent(
                machine, null, assigned, true, false, user);
        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("cdb/machine-design/assigned-item/update", event.getTopic());
        assertEquals("1", event.getMachine().getId());
        assertEquals("machineDesign", event.getMachine().getDomain());
        assertEquals(Integer.valueOf(1), event.getMachine().getDomainId());
        assertNull(event.getOldAssignedItem());
        assertEquals("2", event.getNewAssignedItem().getId());
        assertEquals("catalog", event.getNewAssignedItem().getDomain());
        assertTrue(json.contains("\"domain_id\":1"));
        assertTrue(event.isOldInstalled());
        assertFalse(event.isNewInstalled());
        assertEquals("operator", event.getTriggeredByUsername());
        assertFalse(json.contains("\"topic\""));
    }

    @Test
    public void supportsUnassignTransition() {
        CdbMachineAssignedItemEvent event = new CdbMachineAssignedItemEvent(
                machine(1, "M1"), catalog(2, "Catalog"), null,
                false, true, null);
        assertEquals("2", event.getOldAssignedItem().getId());
        assertNull(event.getNewAssignedItem());
    }

    private static ItemDomainMachineDesign machine(int id, String name) {
        ItemDomainMachineDesign item = new ItemDomainMachineDesign();
        initialize(item, id, name);
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
        item.setDomain(new Domain(1, item instanceof ItemDomainMachineDesign
                ? "machineDesign" : "catalog"));
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
    }
}
