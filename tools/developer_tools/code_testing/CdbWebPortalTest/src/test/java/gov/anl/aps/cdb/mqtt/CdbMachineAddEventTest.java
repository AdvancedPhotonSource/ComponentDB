/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.mqtt.metadata.MachineAddChange;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbMachineAddEventTest {

    @Test
    public void serializesMachineParentAndActorWithoutTopicProperty() throws Exception {
        UserInfo user = new UserInfo();
        user.setUsername("operator");
        ItemDomainMachineDesign added = machine(1, "Added");
        added.setEntityTypeListFromApi(Collections.singletonList(new EntityType(9, "ioc")));
        CdbMachineAddEvent event = new CdbMachineAddEvent(added, machine(2, "Parent"), user);

        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("cdb/machine-design/add", event.getTopic());
        assertEquals("1", event.getMachine().getId());
        assertEquals("machineDesign", event.getMachine().getDomain());
        assertEquals("ioc", event.getMachine().getEntityTypes().get(0).getName());
        assertEquals("2", event.getParent().getId());
        assertEquals("operator", event.getTriggeredByUsername());
        assertFalse(json.contains("\"topic\""));
        assertFalse(json.contains("oldParent"));
        assertFalse(json.contains("newParent"));
        assertTrue(json.contains("\"parent\""));
        assertTrue(json.contains("\"eventTimestamp\""));
    }

    @Test
    public void rootMachineHasNullParent() throws Exception {
        CdbMachineAddEvent event = new CdbMachineAddEvent(machine(1, "Root"), null, null);

        assertNull(event.getParent());
        assertNull(event.getTriggeredByUsername());
        assertTrue(new ObjectMapper().writeValueAsString(event).contains("\"parent\":null"));
    }

    @Test
    public void omitsTemplateFieldsForNonTemplateMachine() throws Exception {
        String json = new ObjectMapper().writeValueAsString(
                new CdbMachineAddEvent(machine(1, "Plain"), null, null));

        assertFalse(json.contains("sourceTemplate"));
        assertFalse(json.contains("templateAppliedToMachine"));
        assertFalse(json.contains("templateCreatedMachineCount"));
    }

    @Test
    public void serializesTemplateContext() throws Exception {
        MachineAddChange change = new MachineAddChange(machine(3, "Child"), machine(2, "Instance"),
                machine(10, "Child template"), machine(2, "Instance"), 4);
        CdbMachineAddEvent event = new CdbMachineAddEvent(change, null);

        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("10", event.getSourceTemplate().getId());
        assertEquals("2", event.getTemplateAppliedToMachine().getId());
        assertEquals(Integer.valueOf(4), event.getTemplateCreatedMachineCount());
        assertTrue(json.contains("\"sourceTemplate\":{"));
        assertTrue(json.contains("\"templateAppliedToMachine\":{"));
        assertTrue(json.contains("\"templateCreatedMachineCount\":4"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresMachine() {
        new CdbMachineAddEvent(null, null, null);
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
