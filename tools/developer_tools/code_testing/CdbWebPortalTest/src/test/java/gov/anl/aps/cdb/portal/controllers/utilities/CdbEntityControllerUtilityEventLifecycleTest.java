/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.base.mqtt.MqttEvent;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.mqtt.CdbEntityEvent;
import gov.anl.aps.cdb.portal.constants.SystemLogLevel;
import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.Test;
import static org.junit.Assert.*;

public class CdbEntityControllerUtilityEventLifecycleTest {

    @Test
    public void publishesGenericThenSpecializedEventAfterSuccessfulUpdate() throws Exception {
        TestUtility utility = new TestUtility(new TestFacade());
        TestEntity entity = new TestEntity(1);
        utility.specializedEvents = Collections.singletonList(new TestEvent("specialized"));

        utility.update(entity, null);

        assertEquals(2, utility.publishedEvents.size());
        assertTrue(utility.publishedEvents.get(0) instanceof CdbEntityEvent);
        assertEquals("specialized", utility.publishedEvents.get(1).getTopic());
        assertTrue(utility.facade.editCalled);
    }

    @Test
    public void publishesCollectedEventsForListOnlyAfterSuccessfulEdit() throws Exception {
        TestUtility utility = new TestUtility(new TestFacade());
        utility.createEventPerEntity = true;

        utility.updateList(Arrays.asList(new TestEntity(1), new TestEntity(2)), null);

        assertEquals(4, utility.publishedEvents.size());
        assertTrue(utility.publishedEvents.get(0) instanceof CdbEntityEvent);
        assertTrue(utility.publishedEvents.get(1) instanceof CdbEntityEvent);
        assertEquals("specialized/1", utility.publishedEvents.get(2).getTopic());
        assertEquals("specialized/2", utility.publishedEvents.get(3).getTopic());
    }

    @Test
    public void doesNotPublishAnyEventWhenPersistenceFails() throws Exception {
        TestFacade facade = new TestFacade();
        facade.failEdit = true;
        TestUtility utility = new TestUtility(facade);
        utility.specializedEvents = Collections.singletonList(new TestEvent("specialized"));

        try {
            utility.update(new TestEntity(1), null);
            fail("Expected persistence failure");
        } catch (IllegalStateException expected) {
            assertEquals("edit failed", expected.getMessage());
        }

        assertTrue(utility.publishedEvents.isEmpty());
    }

    @Test
    public void removalUpdatePublishesSpecializedEventOnlyAfterSuccessfulEdit() throws Exception {
        TestUtility utility = new TestUtility(new TestFacade());
        utility.specializedEvents = Collections.singletonList(new TestEvent("specialized/remove"));

        utility.updateOnRemoval(new TestEntity(1), null);

        assertEquals(2, utility.publishedEvents.size());
        assertTrue(utility.publishedEvents.get(0) instanceof CdbEntityEvent);
        assertEquals("specialized/remove", utility.publishedEvents.get(1).getTopic());
    }

    @Test
    public void updateWithoutSpecializedEventsStillPublishesGenericEvent() throws Exception {
        TestUtility utility = new TestUtility(new TestFacade());

        utility.update(new TestEntity(1), null);

        assertEquals(1, utility.publishedEvents.size());
        assertTrue(utility.publishedEvents.get(0) instanceof CdbEntityEvent);
    }

    private static final class TestEvent extends MqttEvent {
        private final String topic;

        TestEvent(String topic) {
            this.topic = topic;
        }

        @Override
        public String getTopic() {
            return topic;
        }
    }

    private static final class TestEntity extends CdbEntity {
        private final Integer id;

        TestEntity(Integer id) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }

    private static final class TestFacade extends CdbEntityFacade<TestEntity> {
        private boolean editCalled;
        private boolean failEdit;

        TestFacade() {
            super(TestEntity.class);
        }

        @Override
        protected EntityManager getEntityManager() {
            throw new UnsupportedOperationException("Not used by this test");
        }

        @Override
        public TestEntity edit(TestEntity entity) {
            editCalled = true;
            if (failEdit) {
                throw new IllegalStateException("edit failed");
            }
            return entity;
        }

        @Override
        public void edit(List<TestEntity> entities) {
            editCalled = true;
            if (failEdit) {
                throw new IllegalStateException("edit failed");
            }
        }
    }

    private static final class TestUtility
            extends CdbEntityControllerUtility<TestEntity, TestFacade> {
        private final TestFacade facade;
        private final List<MqttEvent> publishedEvents = new ArrayList<>();
        private List<MqttEvent> specializedEvents = Collections.emptyList();
        private boolean createEventPerEntity;

        TestUtility(TestFacade facade) {
            this.facade = facade;
        }

        @Override
        protected TestFacade getEntityDbFacade() {
            return facade;
        }

        @Override
        public TestEntity createEntityInstance(UserInfo sessionUser) {
            return new TestEntity(null);
        }

        @Override
        public String getEntityTypeName() {
            return "testEntity";
        }

        @Override
        protected List<MqttEvent> prepareSpecializedUpdateEvents(
                TestEntity entity, UserInfo userInfo) {
            if (createEventPerEntity) {
                return Collections.singletonList(new TestEvent("specialized/" + entity.getId()));
            }
            return specializedEvents;
        }

        @Override
        protected void publishMqttEvent(MqttEvent event) {
            publishedEvents.add(event);
        }

        @Override
        protected void addCdbEntitySystemLog(
                SystemLogLevel logLevel, String message, UserInfo sessionUser) throws CdbException {
        }

        @Override
        protected void addCdbEntityWarningSystemLog(String warningMessage,
                Exception exception, CdbEntity entity, UserInfo sessionUser) throws CdbException {
        }
    }
}
