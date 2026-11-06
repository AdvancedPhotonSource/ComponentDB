/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.mqtt.CdbItemPropertyEvent;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemPropertyEventDetectionTest {

    @Test
    public void classifiesAddUpdateAndDeleteByStableId() throws Exception {
        ItemDomainCatalog original = item(1,
                property(10, "unchanged", "a"),
                property(11, "updated", "old"),
                property(12, "deleted", "gone"));
        ItemDomainCatalog edited = item(1,
                property(10, "unchanged", "a"),
                property(11, "updated", "new"),
                property(null, "added", "here"));
        TestUtility utility = new TestUtility(original);

        List<MqttEvent> events = utility.detect(edited);

        assertEquals(3, events.size());
        assertEquals("cdb/item/property/update", events.get(0).getTopic());
        assertEquals("cdb/item/property/add", events.get(1).getTopic());
        assertEquals("cdb/item/property/delete", events.get(2).getTopic());
        CdbItemPropertyEvent update = (CdbItemPropertyEvent) events.get(0);
        assertEquals("old", update.getOldProperty().getValue());
        assertEquals("new", update.getNewProperty().getValue());
    }

    @Test
    public void identicalPropertiesProduceNoEvents() throws Exception {
        ItemDomainCatalog original = item(1, property(10, "status", "Installed"));
        ItemDomainCatalog edited = item(1, property(10, "status", "Installed"));

        assertTrue(new TestUtility(original).detect(edited).isEmpty());
    }

    @Test
    public void removalLifecycleProducesDeleteEvent() throws Exception {
        ItemDomainCatalog original = item(1, property(10, "removed", "old"));
        ItemDomainCatalog edited = item(1);
        TestUtility utility = new TestUtility(original);

        utility.detectRemoval(edited);
        List<MqttEvent> events = utility.detectPending(edited);

        assertEquals(1, events.size());
        CdbItemPropertyEvent event = (CdbItemPropertyEvent) events.get(0);
        assertEquals("cdb/item/property/delete", event.getTopic());
        assertEquals("old", event.getOldProperty().getValue());
        assertNull(event.getNewProperty());
    }

    @Test
    public void statusPropertyUsesOrdinaryPropertyUpdateEvent() throws Exception {
        ItemDomainCatalog original = item(1, property(10, "inventory_status", "Available"));
        ItemDomainCatalog edited = item(1, property(10, "inventory_status", "Installed"));

        CdbItemPropertyEvent event = (CdbItemPropertyEvent)
                new TestUtility(original).detect(edited).get(0);

        assertEquals("inventory_status", event.getNewProperty().getPropertyTypeName());
        assertEquals("Available", event.getOldProperty().getValue());
        assertEquals("Installed", event.getNewProperty().getValue());
    }

    private static PropertyValue property(Integer id, String typeName, String valueText) {
        PropertyValue value = new PropertyValue();
        value.setId(id);
        PropertyType propertyType = new PropertyType(100 + (id == null ? 0 : id), typeName);
        propertyType.setPropertyTypeMetadataList(new ArrayList<>());
        value.setPropertyType(propertyType);
        value.setValue(valueText);
        value.setPropertyMetadataList(new ArrayList<>());
        return value;
    }

    private static ItemDomainCatalog item(int id, PropertyValue... values) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        item.setId(id);
        item.setName("Item");
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        self.setPropertyValueList(new ArrayList<>(Arrays.asList(values)));
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        return item;
    }

    private static final class TestUtility extends ItemDomainCatalogControllerUtility {
        private final ItemDomainCatalog original;

        TestUtility(ItemDomainCatalog original) {
            this.original = original;
        }

        @Override
        public ItemDomainCatalog findById(Integer id) {
            return original;
        }

        void detectRemoval(ItemDomainCatalog edited) throws Exception {
            prepareEntityUpdateOnRemoval(edited);
        }

        List<MqttEvent> detectPending(ItemDomainCatalog edited) throws Exception {
            return prepareSpecializedUpdateEvents(edited, new UserInfo());
        }

        List<MqttEvent> detect(ItemDomainCatalog edited) throws Exception {
            EntityInfo entityInfo = new EntityInfo();
            entityInfo.setLastModifiedByUser(new UserInfo());
            entityInfo.setLastModifiedOnDateTime(new Date());
            for (PropertyValue value : original.getPropertyValueList()) {
                value.setPropertyValueHistoryList(new ArrayList<>());
            }
            for (PropertyValue value : edited.getPropertyValueList()) {
                value.setPropertyValueHistoryList(new ArrayList<>());
            }
            edited.setPendingPropertyValueChanges(
                    gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility
                            .preparePropertyValueHistory(original.getPropertyValueList(),
                                    edited.getPropertyValueList(), entityInfo));
            return prepareSpecializedUpdateEvents(edited, new UserInfo());
        }
    }
}
