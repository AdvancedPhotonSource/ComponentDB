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
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbItemLogAttachmentEventTest {

    @Test
    public void serializesLogIdAndAttachmentNamesOnly() throws Exception {
        Attachment attachment = new Attachment(5, "attachment.123.pdf");
        attachment.setOriginalFilename("report.pdf");
        attachment.setTag("internal-tag");
        Log log = new Log();
        log.setId(7);
        UserInfo user = new UserInfo();
        user.setUsername("operator");

        CdbItemLogAttachmentEvent event = new CdbItemLogAttachmentEvent(item(), log, attachment, user);
        String json = new ObjectMapper().writeValueAsString(event);

        assertEquals("cdb/item/log/attachment/add", event.getTopic());
        assertEquals(CdbMqttOperation.ADD, event.getOperation());
        assertEquals("1", event.getItemId());
        assertEquals("ItemDomainCatalog", event.getItemType());
        assertEquals("7", event.getLogId());
        assertEquals("attachment.123.pdf", event.getAttachment().getName());
        assertEquals("report.pdf", event.getAttachment().getOriginalFilename());
        assertEquals("operator", event.getTriggeredByUsername());
        assertTrue(json.contains("\"operation\":\"add\""));
        assertTrue(json.contains("\"eventTimestamp\""));
        assertFalse(json.contains("\"topic\""));
        // DB id, tag and paths stay server-side.
        assertFalse(json.contains("attachmentId"));
        assertFalse(json.contains("internal-tag"));
        assertFalse(json.contains("filePath"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresAttachment() {
        Log log = new Log();
        log.setId(7);
        new CdbItemLogAttachmentEvent(item(), log, null, null);
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
}
