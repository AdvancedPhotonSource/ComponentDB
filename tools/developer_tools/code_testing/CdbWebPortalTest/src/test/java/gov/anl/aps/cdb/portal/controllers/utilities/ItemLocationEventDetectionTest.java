/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.mqtt.CdbItemLocationEvent;
import gov.anl.aps.cdb.mqtt.metadata.ItemLocationChange;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemLocationEventDetectionTest {

    @Test
    public void convertsPendingLocationChangeToEvent() throws Exception {
        ItemDomainInventory affected = inventory(1, "Device");
        ItemDomainLocation oldLocation = location(2, "Old");
        ItemDomainLocation newLocation = location(3, "New");
        affected.setPendingLocationChange(new ItemLocationChange(
                oldLocation, "old details", newLocation, "new details"));

        List<MqttEvent> events = new TestUtility().events(affected);

        assertEquals(1, events.size());
        CdbItemLocationEvent event = (CdbItemLocationEvent) events.get(0);
        assertEquals("2", event.getOldLocation().getId());
        assertEquals("3", event.getNewLocation().getId());
    }

    @Test
    public void itemWithoutPendingChangeProducesNoLocationEvent() throws Exception {
        assertTrue(new TestUtility().events(inventory(1, "Device")).isEmpty());
    }

    private static ItemDomainInventory inventory(int id, String name) {
        ItemDomainInventory item = new ItemDomainInventory();
        initialize(item, id, name);
        return item;
    }

    private static ItemDomainLocation location(int id, String name) {
        ItemDomainLocation item = new ItemDomainLocation();
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

    private static final class TestUtility extends ItemDomainInventoryControllerUtility {
        List<MqttEvent> events(ItemDomainInventory item) throws Exception {
            return prepareSpecializedUpdateEvents(item, null);
        }
    }
}
