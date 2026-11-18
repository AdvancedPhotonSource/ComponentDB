/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbItemLocationEventTest {

    @Test
    public void serializesLocationAndDetailsTransition() throws Exception {
        ItemDomainInventory affected = inventory(1, "Device");
        ItemDomainLocation oldLocation = location(2, "Old");
        ItemDomainLocation newLocation = location(3, "New");
        CdbItemLocationEvent event = new CdbItemLocationEvent(affected,
                oldLocation, newLocation, "rack 1", "rack 2", null);

        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("cdb/item/location/update", event.getTopic());
        assertEquals("1", event.getItem().getId());
        assertEquals("2", event.getOldLocation().getId());
        assertEquals("3", event.getNewLocation().getId());
        assertEquals("rack 1", event.getOldLocationDetails());
        assertEquals("rack 2", event.getNewLocationDetails());
        assertFalse(json.contains("\"topic\""));
    }

    @Test
    public void supportsSetAndClearTransitions() {
        ItemDomainInventory affected = inventory(1, "Device");
        ItemDomainLocation location = location(2, "Location");
        assertNull(new CdbItemLocationEvent(affected, null, location,
                null, null, null).getOldLocation());
        assertNull(new CdbItemLocationEvent(affected, location, null,
                null, null, null).getNewLocation());
    }

    private static ItemDomainInventory inventory(int id, String name) {
        ItemDomainInventory item = new ItemDomainInventory();
        initialize(item, id, name, 2, "inventory");
        return item;
    }

    private static ItemDomainLocation location(int id, String name) {
        ItemDomainLocation item = new ItemDomainLocation();
        initialize(item, id, name, 3, "location");
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
