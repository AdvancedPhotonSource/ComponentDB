/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.mqtt.CdbItemLogEvent;
import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemLogEventDetectionTest {

    @Test
    public void classifiesUpdateAndDeleteByStableId() throws Exception {
        ItemDomainCatalog original = item(log(10, "unchanged"), log(11, "old"), log(12, "gone"));
        ItemDomainCatalog edited = item(log(10, "unchanged"), log(11, "new"));

        List<MqttEvent> events = new TestUtility(original).detect(edited);

        assertEquals(2, events.size());
        CdbItemLogEvent update = (CdbItemLogEvent) events.get(0);
        assertEquals("cdb/item/log/update", update.getTopic());
        assertEquals("11", update.getNewLog().getLogId());
        assertEquals("-old\n+new\n", update.getTextDiff());
        CdbItemLogEvent delete = (CdbItemLogEvent) events.get(1);
        assertEquals("cdb/item/log/delete", delete.getTopic());
        assertEquals("12", delete.getOldLog().getLogId());
        assertEquals("gone", delete.getOldLog().getText());
    }

    @Test
    public void unchangedAttachmentOnlyAndSameInstantEditsProduceNoEvents() throws Exception {
        Log originalLog = log(10, "text");
        originalLog.setEffectiveFromDateTime(new Timestamp(1000));
        Log editedLog = log(10, "text");
        editedLog.setAttachmentList(new ArrayList<>(Collections.singletonList(new Attachment(5))));

        assertTrue(new TestUtility(item(originalLog)).detect(item(editedLog)).isEmpty());
    }

    @Test
    public void effectiveDateAndTopicChangesAreUpdates() throws Exception {
        Log dateEdit = log(10, "text");
        dateEdit.setEffectiveFromDateTime(new Date(9000));
        Log topicEdit = log(11, "text");
        topicEdit.setLogTopic(new LogTopic(2, "Repair"));
        ItemDomainCatalog original = item(log(10, "text"), log(11, "text"));

        List<MqttEvent> events = new TestUtility(original).detect(item(dateEdit, topicEdit));

        assertEquals(2, events.size());
        assertNull(((CdbItemLogEvent) events.get(0)).getTextDiff());
        assertEquals("Repair", ((CdbItemLogEvent) events.get(1)).getNewLog().getLogTopicName());
    }

    @Test
    public void newLogsAreNotUpdateEvents() throws Exception {
        ItemDomainCatalog original = item(log(10, "existing"));
        ItemDomainCatalog edited = item(log(null, "new"), log(10, "existing"));

        assertTrue(new TestUtility(original).detect(edited).isEmpty());
    }

    @Test
    public void removalLifecycleProducesDeleteEvent() throws Exception {
        TestUtility utility = new TestUtility(item(log(10, "removed")));
        ItemDomainCatalog edited = item();

        utility.detectRemoval(edited);
        List<MqttEvent> events = utility.detectPending(edited);

        assertEquals(1, events.size());
        assertEquals("cdb/item/log/delete", events.get(0).getTopic());
    }

    @Test
    public void createPathUsesIdsAssignedInPlace() {
        Log added = log(null, "on create");
        ItemDomainCatalog created = item(added);
        TestUtility utility = new TestUtility(null);

        utility.prepareSpecializedCreateEvents(Collections.singletonList(created), user());
        added.setId(70);
        List<MqttEvent> events = utility.createSpecializedCreateEvents(
                Collections.singletonList(created), user());

        assertEquals(1, events.size());
        CdbItemLogEvent event = (CdbItemLogEvent) events.get(0);
        assertEquals("cdb/item/log/add", event.getTopic());
        assertEquals("70", event.getNewLog().getLogId());
        assertEquals("on create", event.getNewLog().getText());
    }

    @Test
    public void updatePathResolvesMergedIdsFromPersistedItem() {
        ItemDomainCatalog edited = item(log(null, "second"), log(null, "first"), log(10, "existing"));
        TestUtility utility = new TestUtility(null);
        utility.prepareSpecializedCreateEvents(Collections.singletonList(edited), user());
        // Merge assigns IDs only to managed copies; the utility reads them back.
        utility.original = item(log(71, "first"), log(72, "second"), log(10, "existing"));

        List<MqttEvent> events = utility.createSpecializedCreateEvents(
                Collections.singletonList(edited), user());

        assertEquals(2, events.size());
        assertEquals("72", ((CdbItemLogEvent) events.get(0)).getNewLog().getLogId());
        assertEquals("71", ((CdbItemLogEvent) events.get(1)).getNewLog().getLogId());
        assertTrue(edited.getPendingAddedLogs().isEmpty());
    }

    @Test
    public void unpersistedNewLogPublishesNothing() {
        ItemDomainCatalog edited = item(log(null, "never saved"), log(10, "existing"));
        TestUtility utility = new TestUtility(null);
        utility.prepareSpecializedCreateEvents(Collections.singletonList(edited), user());
        utility.original = item(log(10, "existing"));

        assertTrue(utility.createSpecializedCreateEvents(
                Collections.singletonList(edited), user()).isEmpty());
    }

    private static Log log(Integer id, String text) {
        Log log = new Log();
        log.setId(id);
        log.setText(text);
        log.setEnteredByUser(user());
        log.setEnteredOnDateTime(new Date(1000));
        log.setEffectiveFromDateTime(new Date(1000));
        return log;
    }

    private static ItemDomainCatalog item(Log... logs) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        item.setId(1);
        item.setName("Item");
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        self.setPropertyValueList(new ArrayList<>());
        self.setLogList(new ArrayList<>(Arrays.asList(logs)));
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        return item;
    }

    private static UserInfo user() {
        UserInfo user = new UserInfo();
        user.setUsername("operator");
        return user;
    }

    private static final class TestUtility extends ItemDomainCatalogControllerUtility {
        private ItemDomainCatalog original;

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
            return prepareSpecializedUpdateEvents(edited, user());
        }

        List<MqttEvent> detect(ItemDomainCatalog edited) throws Exception {
            edited.setPendingLogChanges(LogUtility.prepareLogChanges(
                    original.getLogList(), edited.getLogList()));
            return prepareSpecializedUpdateEvents(edited, user());
        }
    }
}
