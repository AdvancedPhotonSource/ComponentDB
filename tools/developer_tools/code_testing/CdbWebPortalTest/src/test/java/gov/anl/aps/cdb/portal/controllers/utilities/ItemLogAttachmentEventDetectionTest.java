/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.mqtt.CdbItemLogAttachmentEvent;
import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemLogAttachmentEventDetectionTest {

    @Test
    public void unsavedAttachmentOnExistingLogIsAnUpdateEventBuiltBeforeSave() throws Exception {
        ItemDomainCatalog original = item(log(10, "entry"));
        ItemDomainCatalog edited = item(log(10, "entry", attachment(null, "attachment.b.png", "b.png")));

        List<MqttEvent> events = new TestUtility(original).updateEvents(edited);

        assertEquals(1, events.size());
        assertAttachment(events.get(0), "10", "attachment.b.png", "b.png");
    }

    @Test
    public void preSavedRestAttachmentIsDetectedByUnreferencedId() throws Exception {
        ItemDomainCatalog original = item(log(10, "entry", attachment(40, "attachment.old.pdf", "old.pdf")));
        ItemDomainCatalog edited = item(log(10, "entry",
                attachment(40, "attachment.old.pdf", "old.pdf"),
                attachment(50, "attachment.a.pdf", "a.pdf")));

        List<MqttEvent> events = new TestUtility(original).updateEvents(edited);

        assertEquals(1, events.size());
        assertAttachment(events.get(0), "10", "attachment.a.pdf", "a.pdf");
    }

    @Test
    public void unchangedAttachmentsProduceNoEvents() throws Exception {
        ItemDomainCatalog original = item(log(10, "entry", attachment(40, "attachment.old.pdf", "old.pdf")));
        ItemDomainCatalog edited = item(log(10, "entry", attachment(40, "attachment.old.pdf", "old.pdf")));

        assertTrue(new TestUtility(original).updateEvents(edited).isEmpty());
    }

    @Test
    public void attachmentOnlyEditIsNotALogUpdate() throws Exception {
        ItemDomainCatalog original = item(log(10, "entry"));
        ItemDomainCatalog edited = item(log(10, "entry", attachment(null, "attachment.a.pdf", "a.pdf")));

        List<MqttEvent> events = new TestUtility(original).updateEvents(edited);

        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof CdbItemLogAttachmentEvent);
    }

    @Test
    public void sameAttachmentListedTwiceIsPublishedOnce() throws Exception {
        Attachment attachment = attachment(null, "attachment.d.pdf", "d.pdf");
        ItemDomainCatalog original = item(log(10, "entry"));
        ItemDomainCatalog edited = item(log(10, "entry", attachment, attachment));

        assertEquals(1, new TestUtility(original).updateEvents(edited).size());
    }

    @Test
    public void newLogAttachmentsFollowTheLogAddWithItsResolvedId() {
        Attachment first = attachment(null, "attachment.1.txt", "one.txt");
        Attachment second = attachment(null, "attachment.2.txt", "two.txt");
        ItemDomainCatalog edited = item(log(null, "new entry", first, second, first), log(10, "entry"));
        TestUtility utility = new TestUtility(null);
        List<ItemDomainCatalog> entities = Collections.singletonList(edited);

        utility.prepareSpecializedCreateEvents(entities, user());
        utility.original = item(log(70, "new entry"), log(10, "entry"));
        List<MqttEvent> events = utility.createSpecializedCreateEvents(entities, user());

        assertEquals(3, events.size());
        assertEquals("cdb/item/log/add", events.get(0).getTopic());
        assertAttachment(events.get(1), "70", "attachment.1.txt", "one.txt");
        assertAttachment(events.get(2), "70", "attachment.2.txt", "two.txt");
    }

    @Test
    public void unpersistedNewLogPublishesNoAttachmentEvents() {
        ItemDomainCatalog edited = item(log(null, "never saved", attachment(null, "attachment.x.pdf", "x.pdf")));
        TestUtility utility = new TestUtility(null);
        List<ItemDomainCatalog> entities = Collections.singletonList(edited);

        utility.prepareSpecializedCreateEvents(entities, user());
        utility.original = item();

        assertTrue(utility.createSpecializedCreateEvents(entities, user()).isEmpty());
    }

    private static void assertAttachment(MqttEvent value, String logId, String name,
            String originalFilename) {
        CdbItemLogAttachmentEvent event = (CdbItemLogAttachmentEvent) value;
        assertEquals("cdb/item/log/attachment/add", event.getTopic());
        assertEquals(logId, event.getLogId());
        assertEquals(name, event.getAttachment().getName());
        assertEquals(originalFilename, event.getAttachment().getOriginalFilename());
        assertEquals("operator", event.getTriggeredByUsername());
    }

    private static Attachment attachment(Integer id, String name, String originalFilename) {
        Attachment attachment = new Attachment(id, name);
        attachment.setOriginalFilename(originalFilename);
        return attachment;
    }

    private static Log log(Integer id, String text, Attachment... attachments) {
        Log log = new Log();
        log.setId(id);
        log.setText(text);
        log.setEnteredByUser(user());
        log.setEnteredOnDateTime(new Date(1000));
        log.setAttachmentList(new ArrayList<>(Arrays.asList(attachments)));
        return log;
    }

    private static ItemDomainCatalog item(Log... logs) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        item.setId(1);
        item.setName("Item");
        ItemElement self = new ItemElement();
        self.setParentItem(item);
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

        // Mirrors update(): diffs from prepareEntityUpdate, then specialized update events.
        List<MqttEvent> updateEvents(ItemDomainCatalog edited) throws Exception {
            edited.setPendingLogChanges(LogUtility.prepareLogChanges(
                    original.getLogList(), edited.getLogList()));
            edited.setPendingLogAttachmentChanges(LogUtility.findAddedAttachments(
                    original.getLogList(), edited.getLogList()));
            return prepareSpecializedUpdateEvents(edited, user());
        }
    }
}
