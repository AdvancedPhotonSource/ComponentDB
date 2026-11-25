/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.mqtt.CdbItemLogAttachmentEvent;
import gov.anl.aps.cdb.mqtt.CdbItemLogEvent;
import gov.anl.aps.cdb.mqtt.CdbMachineAddEvent;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * One save mixing several changes publishes, after the generic entity event: specialized
 * update events (log update/delete, attachments on existing logs), then specialized create
 * events (machine adds, then each new log followed by its attachments), each exactly once.
 */
public class CombinedEventOrderingTest {

    @Test
    public void logEditsAndAttachmentUploadsInOneSave() throws Exception {
        Attachment existingLogUpload = attachment("attachment.e.pdf", "e.pdf");
        Attachment newLogUpload = attachment("attachment.n.pdf", "n.pdf");
        ItemDomainCatalog original = catalog(log(10, "edit me"), log(11, "attach here"), log(12, "delete me"));
        ItemDomainCatalog edited = catalog(log(null, "brand new", newLogUpload),
                log(10, "edited"), log(11, "attach here", existingLogUpload));
        CatalogUtility utility = new CatalogUtility(original);

        List<MqttEvent> events = utility.save(edited,
                catalog(log(70, "brand new"), log(10, "edited"), log(11, "attach here")));

        assertTopics(events,
                "cdb/item/log/update",
                "cdb/item/log/delete",
                "cdb/item/log/attachment/add",
                "cdb/item/log/add",
                "cdb/item/log/attachment/add");
        assertEquals("11", ((CdbItemLogAttachmentEvent) events.get(2)).getLogId());
        assertEquals("70", ((CdbItemLogEvent) events.get(3)).getNewLog().getLogId());
        assertEquals("70", ((CdbItemLogAttachmentEvent) events.get(4)).getLogId());
        assertNoDuplicates(events);

        // The save assigned attachment IDs; saving again with nothing new publishes nothing more.
        existingLogUpload.setId(500);
        newLogUpload.setId(501);
        utility.current = catalog(log(70, "brand new", newLogUpload), log(10, "edited"),
                log(11, "attach here", existingLogUpload));
        assertTrue(utility.save(catalog(log(70, "brand new", newLogUpload), log(10, "edited"),
                log(11, "attach here", existingLogUpload)), null).isEmpty());
    }

    @Test
    public void templateInstantiationWithParentLogInOneSave() {
        ItemDomainMachineDesign template = machine(800, "Template");
        ItemDomainMachineDesign templateChild = machine(801, "Template child");
        ItemDomainMachineDesign parent = machine(1, "Parent");
        parent.getSelfElement().setLogList(new ArrayList<>(Collections.singletonList(log(null, "parent note"))));
        ItemDomainMachineDesign top = fromTemplate(machine(null, "Instance"), template);
        ItemDomainMachineDesign child = fromTemplate(machine(null, "Instance child"), templateChild);
        child(parent, top);
        child(top, child);
        MachineUtility utility = new MachineUtility();
        utility.persistedIds.addAll(Arrays.asList(1, 800, 801));
        List<ItemDomainMachineDesign> entities = Collections.singletonList(parent);

        utility.prepareSpecializedCreateEvents(entities, user());
        top.setId(100);
        child.setId(101);
        ItemDomainMachineDesign persistedParent = machine(1, "Parent");
        persistedParent.getSelfElement().setLogList(new ArrayList<>(
                Collections.singletonList(log(90, "parent note"))));
        utility.persistedParent = persistedParent;
        List<MqttEvent> events = utility.createSpecializedCreateEvents(entities, user());

        assertTopics(events, "cdb/machine-design/add", "cdb/machine-design/add", "cdb/item/log/add");
        CdbMachineAddEvent first = (CdbMachineAddEvent) events.get(0);
        CdbMachineAddEvent second = (CdbMachineAddEvent) events.get(1);
        assertEquals("100", first.getMachine().getId());
        assertEquals("101", second.getMachine().getId());
        assertEquals(Integer.valueOf(2), second.getTemplateCreatedMachineCount());
        assertEquals("90", ((CdbItemLogEvent) events.get(2)).getNewLog().getLogId());
        assertNoDuplicates(events);
    }

    private static void assertTopics(List<MqttEvent> events, String... topics) {
        List<String> actual = new ArrayList<>();
        for (MqttEvent event : events) {
            actual.add(event.getTopic());
        }
        assertEquals(Arrays.asList(topics), actual);
    }

    // Duplicate = same topic and same subject (machine, log, or log + attachment).
    private static void assertNoDuplicates(List<MqttEvent> events) {
        Set<String> keys = new HashSet<>();
        for (MqttEvent event : events) {
            String subject;
            if (event instanceof CdbMachineAddEvent) {
                subject = ((CdbMachineAddEvent) event).getMachine().getId();
            } else if (event instanceof CdbItemLogAttachmentEvent) {
                CdbItemLogAttachmentEvent attachment = (CdbItemLogAttachmentEvent) event;
                subject = attachment.getLogId() + "/" + attachment.getAttachment().getName();
            } else {
                CdbItemLogEvent log = (CdbItemLogEvent) event;
                subject = (log.getNewLog() != null ? log.getNewLog() : log.getOldLog()).getLogId();
            }
            assertTrue("Duplicate " + event.getTopic() + " for " + subject,
                    keys.add(event.getTopic() + ":" + subject));
        }
    }

    private static Attachment attachment(String name, String originalFilename) {
        Attachment attachment = new Attachment(null, name);
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

    private static ItemDomainCatalog catalog(Log... logs) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        item.setId(1);
        item.setName("Item");
        selfElement(item).setLogList(new ArrayList<>(Arrays.asList(logs)));
        return item;
    }

    private static ItemDomainMachineDesign machine(Integer id, String name) {
        ItemDomainMachineDesign item = new ItemDomainMachineDesign();
        item.setId(id);
        item.setName(name);
        item.setEntityTypeListFromApi(new ArrayList<>());
        item.setItemElementMemberList(new ArrayList<>());
        selfElement(item);
        item.setEntityInfo(new EntityInfo());
        return item;
    }

    private static ItemElement selfElement(Item item) {
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        return self;
    }

    private static void child(ItemDomainMachineDesign parent, ItemDomainMachineDesign contained) {
        ItemElement element = new ItemElement();
        element.setParentItem(parent);
        element.setContainedItem(contained);
        element.setName("E" + parent.getFullItemElementList().size());
        parent.getFullItemElementList().add(element);
        contained.getItemElementMemberList().add(element);
        parent.resetItemElementVars();
    }

    private static ItemDomainMachineDesign fromTemplate(ItemDomainMachineDesign machine,
            ItemDomainMachineDesign template) {
        ItemElementRelationship relationship = new ItemElementRelationship();
        relationship.setRelationshipType(new RelationshipType(1,
                ItemElementRelationshipTypeNames.template.getValue()));
        relationship.setFirstItemElement(machine.getSelfElement());
        relationship.setSecondItemElement(template.getSelfElement());
        machine.setItemElementRelationshipList(new ArrayList<>(Collections.singletonList(relationship)));
        return machine;
    }

    private static UserInfo user() {
        UserInfo user = new UserInfo();
        user.setUsername("operator");
        return user;
    }

    private static final class CatalogUtility extends ItemDomainCatalogControllerUtility {
        private ItemDomainCatalog current;

        CatalogUtility(ItemDomainCatalog original) {
            current = original;
        }

        @Override
        public ItemDomainCatalog findById(Integer id) {
            return current;
        }

        // Mirrors CdbEntityControllerUtility.update ordering around a successful edit.
        List<MqttEvent> save(ItemDomainCatalog edited, ItemDomainCatalog afterSave) throws Exception {
            edited.setPendingLogChanges(LogUtility.prepareLogChanges(
                    current.getLogList(), edited.getLogList()));
            edited.setPendingLogAttachmentChanges(LogUtility.findAddedAttachments(
                    current.getLogList(), edited.getLogList()));
            List<MqttEvent> events = new ArrayList<>(prepareSpecializedUpdateEvents(edited, user()));
            List<ItemDomainCatalog> entities = Collections.singletonList(edited);
            prepareSpecializedCreateEvents(entities, user());
            if (afterSave != null) {
                current = afterSave;
            }
            events.addAll(createSpecializedCreateEvents(entities, user()));
            return events;
        }
    }

    private static final class MachineUtility extends ItemDomainMachineDesignControllerUtility {
        private final Set<Integer> persistedIds = new HashSet<>();
        private ItemDomainMachineDesign persistedParent;

        @Override
        protected boolean isMachinePersisted(ItemDomainMachineDesign machine) {
            return machine.getId() != null && persistedIds.contains(machine.getId());
        }

        @Override
        public ItemDomainMachineDesign findById(Integer id) {
            return persistedParent;
        }
    }
}
