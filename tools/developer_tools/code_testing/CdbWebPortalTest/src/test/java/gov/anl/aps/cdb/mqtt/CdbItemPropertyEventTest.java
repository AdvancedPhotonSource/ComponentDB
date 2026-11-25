/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.jsf.handlers.MarkdownPropertyTypeHandler;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbItemPropertyEventTest {

    @Test
    public void serializesScalarAddedPropertyWithoutTopic() throws Exception {
        ItemDomainCatalog item = item(10, "Magnet");
        UserInfo user = new UserInfo();
        user.setUsername("operator");
        PropertyValue value = property(20, 30, "serial", "A1");
        value.setUnits("unit");
        value.setDescription("description");
        PropertyMetadata metadata = new PropertyMetadata(1, "source", "manual");
        metadata.setPropertyValue(value);
        value.setPropertyMetadataList(Collections.singletonList(metadata));

        CdbItemPropertyEvent event = CdbItemPropertyEvent.added(item, value, user);
        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("cdb/item/property/add", event.getTopic());
        assertEquals(CdbMqttOperation.ADD, event.getOperation());
        assertEquals("10", event.getItemId());
        assertEquals("operator", event.getTriggeredByUsername());
        assertEquals("A1", event.getNewProperty().getValue());
        assertEquals("manual", event.getNewProperty().getMetadata().get("source"));
        assertNull(event.getOldProperty());
        assertFalse(json.contains("\"topic\""));
        assertTrue(json.contains("\"eventTimestamp\""));
    }

    @Test
    public void longTextUpdateContainsChangedLineDiffInsteadOfFullText() {
        ItemDomainCatalog item = item(10, "Magnet");
        PropertyValue oldValue = markdown(20, "same\nold\ntail");
        PropertyValue newValue = markdown(20, "same\nnew\ntail");

        CdbItemPropertyEvent event = CdbItemPropertyEvent.updated(
                item, oldValue, newValue, null);

        assertEquals("-old\n+new\n", event.getTextDiff());
        assertNull(event.getOldProperty().getText());
        assertNull(event.getNewProperty().getText());
        assertTrue(event.getOldProperty().isTextOmitted());
        assertTrue(event.getNewProperty().isTextOmitted());
        assertFalse(event.isDiffTruncated());
    }

    @Test
    public void capsLongTextPayloadsAndReportsTruncation() {
        PropertyValue value = markdown(20,
                String.join("", Collections.nCopies(CdbItemPropertyEvent.MAX_TEXT_LENGTH + 1, "x")));

        CdbItemPropertyEvent event = CdbItemPropertyEvent.added(item(10, "Magnet"), value, null);

        assertEquals(CdbItemPropertyEvent.MAX_TEXT_LENGTH,
                event.getNewProperty().getText().length());
        assertFalse(event.getNewProperty().isTextOmitted());
        assertTrue(event.isDiffTruncated());
    }

    @Test
    public void deleteCarriesOnlyOldProperty() {
        CdbItemPropertyEvent event = CdbItemPropertyEvent.deleted(
                item(10, "Magnet"), property(20, 30, "serial", "A1"), null);
        assertEquals("cdb/item/property/delete", event.getTopic());
        assertNotNull(event.getOldProperty());
        assertNull(event.getNewProperty());
    }

    private static PropertyValue markdown(int id, String text) {
        PropertyValue value = property(id, 30, "notes", null);
        value.setText(text);
        value.getPropertyType().setPropertyTypeHandler(
                new PropertyTypeHandler(1, MarkdownPropertyTypeHandler.HANDLER_NAME));
        return value;
    }

    private static PropertyValue property(int id, int typeId, String typeName, String valueText) {
        PropertyType type = new PropertyType(typeId, typeName);
        PropertyValue value = new PropertyValue();
        value.setId(id);
        value.setPropertyType(type);
        value.setValue(valueText);
        value.setPropertyMetadataList(new ArrayList<>());
        return value;
    }

    private static ItemDomainCatalog item(int id, String name) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        item.setId(id);
        item.setName(name);
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        return item;
    }
}
