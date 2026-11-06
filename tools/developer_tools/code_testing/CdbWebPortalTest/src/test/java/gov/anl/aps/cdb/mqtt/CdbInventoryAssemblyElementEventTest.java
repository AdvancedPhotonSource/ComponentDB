/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbInventoryAssemblyElementEventTest {

    @Test
    public void serializesAssemblySlotAndAssignmentTransition() throws Exception {
        ItemDomainInventory assembly = inventory(1, "Assembly");
        ItemDomainInventory oldItem = inventory(2, "Old");
        ItemDomainInventory newItem = inventory(3, "New");
        ItemDomainCatalog catalogAssembly = catalog(4, "Catalog Assembly");
        ItemDomainCatalog catalogPart = catalog(5, "Catalog Part");
        ItemElement catalogElement = element(6, catalogAssembly, catalogPart);
        ItemElement assemblyElement = element(7, assembly, newItem);
        assemblyElement.setDerivedFromItemElement(catalogElement);

        CdbInventoryAssemblyElementEvent event = new CdbInventoryAssemblyElementEvent(
                assembly, assemblyElement, oldItem, newItem, null);
        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("cdb/inventory/assembly-element/update", event.getTopic());
        assertEquals("1", event.getInventoryAssembly().getId());
        assertEquals("7", event.getAssemblyElement().getId());
        assertEquals("6", event.getCatalogElement().getId());
        assertEquals("2", event.getOldAssignedInventoryItem().getId());
        assertEquals("3", event.getNewAssignedInventoryItem().getId());
        assertFalse(json.contains("\"topic\""));
    }

    private static ItemElement element(int id, Item parent, Item contained) {
        ItemElement element = new ItemElement();
        element.setId(id);
        element.setName("E" + id);
        element.setParentItem(parent);
        element.setContainedItem(contained);
        return element;
    }

    private static ItemDomainInventory inventory(int id, String name) {
        ItemDomainInventory item = new ItemDomainInventory();
        initialize(item, id, name, 2, "inventory");
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
