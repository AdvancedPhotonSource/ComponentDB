/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbMachineRepresentedElementEventTest {

    @Test
    public void serializesElementAndCatalogContext() throws Exception {
        ItemDomainMachineDesign machine = machine(1, "Machine");
        ItemDomainCatalog assembly = catalog(2, "Assembly");
        ItemDomainCatalog part = catalog(3, "Part");
        ItemElement element = element(4, "E1", assembly, part);

        CdbMachineRepresentedElementEvent event
                = new CdbMachineRepresentedElementEvent(machine, null, element, null);
        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("cdb/machine-design/represented-element/update", event.getTopic());
        assertNull(event.getOldRepresentedElement());
        assertEquals("4", event.getNewRepresentedElement().getId());
        assertEquals("2", event.getNewRepresentedElement().getParentItem().getId());
        assertEquals("3", event.getNewRepresentedElement().getContainedItem().getId());
        assertNull(event.getSourceTemplate());
        assertFalse(json.contains("\"topic\""));
    }

    @Test
    public void includesTemplateContextForPropagatedChange() {
        CdbMachineRepresentedElementEvent event = new CdbMachineRepresentedElementEvent(
                machine(1, "Instance"), null, null, machine(2, "Template"), null);
        assertEquals("2", event.getSourceTemplate().getId());
    }

    private static ItemElement element(int id, String name, Item parent, Item contained) {
        ItemElement element = new ItemElement();
        element.setId(id);
        element.setName(name);
        element.setParentItem(parent);
        element.setContainedItem(contained);
        return element;
    }

    private static ItemDomainMachineDesign machine(int id, String name) {
        ItemDomainMachineDesign item = new ItemDomainMachineDesign();
        initialize(item, id, name, 4, "machineDesign");
        return item;
    }

    private static ItemDomainCatalog catalog(int id, String name) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        initialize(item, id, name, 1, "catalog");
        return item;
    }

    private static void initialize(Item item, int id, String name, int domainId, String domain) {
        item.setId(id);
        item.setName(name);
        item.setDomain(new Domain(domainId, domain));
        item.setEntityTypeListFromApi(new ArrayList<>());
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
    }
}
