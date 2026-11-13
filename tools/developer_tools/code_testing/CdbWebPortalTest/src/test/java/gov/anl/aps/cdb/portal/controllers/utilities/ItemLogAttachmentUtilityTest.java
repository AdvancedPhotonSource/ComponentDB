/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemLogAttachmentUtilityTest {

    @Test
    public void appendsAttachmentAndSavesThroughItemUpdate() throws Exception {
        Log log = log(10);
        ItemDomainCatalog item = item(log);
        Attachment attachment = new Attachment(5);
        TestUtility utility = new TestUtility();

        assertSame(item, utility.addLogAttachment(item, log, attachment, new UserInfo()));

        assertEquals(Collections.singletonList(attachment), log.getAttachmentList());
        assertSame(item, utility.updated);
    }

    @Test
    public void rejectsLogFromAnotherItemWithoutSaving() {
        TestUtility utility = new TestUtility();
        Log foreign = log(99);

        try {
            utility.addLogAttachment(item(log(10)), foreign, new Attachment(5), new UserInfo());
            fail("Expected CdbException");
        } catch (CdbException expected) {
            assertNull(utility.updated);
            assertNull(foreign.getAttachmentList());
        }
    }

    private static Log log(int id) {
        Log log = new Log();
        log.setId(id);
        return log;
    }

    private static ItemDomainCatalog item(Log... logs) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        item.setId(1);
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        self.setLogList(new ArrayList<>(Arrays.asList(logs)));
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        return item;
    }

    private static final class TestUtility extends ItemDomainCatalogControllerUtility {
        private ItemDomainCatalog updated;

        @Override
        public ItemDomainCatalog update(ItemDomainCatalog item, UserInfo user) {
            updated = item;
            return item;
        }
    }
}
