/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbItemLogEventTest {

    @Test
    public void serializesAddedLogWithItemContextAndNoAttachments() throws Exception {
        Log log = log(7, "First entry");
        log.setLogTopic(new LogTopic(3, "Maintenance"));
        Attachment attachment = new Attachment();
        attachment.setOriginalFilename("secret.pdf");
        log.setAttachmentList(new ArrayList<>(Collections.singletonList(attachment)));

        CdbItemLogEvent event = CdbItemLogEvent.added(item(), log, user());
        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("cdb/item/log/add", event.getTopic());
        assertEquals(CdbMqttOperation.ADD, event.getOperation());
        assertEquals("1", event.getItemId());
        assertEquals("ItemDomainCatalog", event.getItemType());
        assertNull(event.getOldLog());
        assertEquals("7", event.getNewLog().getLogId());
        assertEquals("First entry", event.getNewLog().getText());
        assertFalse(event.getNewLog().isTextOmitted());
        assertEquals("author", event.getNewLog().getEnteredByUsername());
        assertEquals("3", event.getNewLog().getLogTopicId());
        assertEquals("Maintenance", event.getNewLog().getLogTopicName());
        assertNull(event.getTextDiff());
        assertFalse(event.isDiffTruncated());
        assertEquals("operator", event.getTriggeredByUsername());
        assertTrue(json.contains("\"operation\":\"add\""));
        assertTrue(json.contains("\"enteredOnDateTime\":\""));
        assertFalse(json.contains("\"topic\""));
        assertFalse(json.contains("attachment"));
        assertFalse(json.contains("secret.pdf"));
    }

    @Test
    public void updateCarriesLineDiffAndOmitsFullText() {
        Log oldLog = log(7, "line one\nline two");
        Log newLog = log(7, "line one\nline 2");

        CdbItemLogEvent event = CdbItemLogEvent.updated(item(), oldLog, newLog, user());

        assertEquals("cdb/item/log/update", event.getTopic());
        assertEquals("-line two\n+line 2\n", event.getTextDiff());
        assertTrue(event.getOldLog().isTextOmitted());
        assertNull(event.getOldLog().getText());
        assertNull(event.getNewLog().getText());
        assertFalse(event.isDiffTruncated());
    }

    @Test
    public void updateWithUnchangedTextHasNullDiff() {
        Log oldLog = log(7, "same");
        Log newLog = log(7, "same");
        newLog.setEffectiveFromDateTime(new Date(5000));

        CdbItemLogEvent event = CdbItemLogEvent.updated(item(), oldLog, newLog, user());

        assertNull(event.getTextDiff());
        assertEquals(5000, event.getNewLog().getEffectiveFromDateTime().getTime());
    }

    @Test
    public void deleteCarriesOldLogOnly() {
        CdbItemLogEvent event = CdbItemLogEvent.deleted(item(), log(7, "gone"), user());

        assertEquals("cdb/item/log/delete", event.getTopic());
        assertEquals(CdbMqttOperation.DELETE, event.getOperation());
        assertEquals("gone", event.getOldLog().getText());
        assertNull(event.getNewLog());
    }

    @Test
    public void longTextIsBoundedAndFlagged() {
        String text = new String(new char[CdbItemLogEvent.MAX_TEXT_LENGTH + 10]).replace('\0', 'x');

        CdbItemLogEvent added = CdbItemLogEvent.added(item(), log(7, text), user());
        CdbItemLogEvent updated = CdbItemLogEvent.updated(item(), log(7, "a"), log(7, text), user());

        assertEquals(CdbItemLogEvent.MAX_TEXT_LENGTH, added.getNewLog().getText().length());
        assertTrue(added.isDiffTruncated());
        assertEquals(CdbItemLogEvent.MAX_TEXT_LENGTH, updated.getTextDiff().length());
        assertTrue(updated.isDiffTruncated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresItem() {
        CdbItemLogEvent.added(null, log(7, "x"), null);
    }

    private static Log log(Integer id, String text) {
        Log log = new Log();
        log.setId(id);
        log.setText(text);
        UserInfo author = new UserInfo();
        author.setUsername("author");
        log.setEnteredByUser(author);
        log.setEnteredOnDateTime(new Date(1000));
        log.setEffectiveFromDateTime(new Date(1000));
        return log;
    }

    private static ItemDomainCatalog item() {
        ItemDomainCatalog item = new ItemDomainCatalog();
        item.setId(1);
        item.setName("Item");
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        return item;
    }

    private static UserInfo user() {
        UserInfo user = new UserInfo();
        user.setUsername("operator");
        return user;
    }
}
