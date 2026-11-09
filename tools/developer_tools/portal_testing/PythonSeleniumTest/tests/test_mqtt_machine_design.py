#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.

MQTT verification for machine-design events fired from the portal UI.

Marked ``mqtt`` so the parallel suite deselects it (``-m "not mqtt"``); run it
serially via sbin/cdb_mqtt_test.sh. Skipped when CDB_TEST_MQTT=false, when
paho-mqtt is missing, or when the broker cannot be reached.
"""

import unittest
import uuid

import pytest

from cdbSeleniumModules.machineDesign import MachineDesign
from tests.mqtt_base import MqttPortalTestCase, described_as, mqtt_test_support as support

MACHINE_DOMAIN = 'Machine Design'
MACHINE_ENTITY_TYPE = 'ItemDomainMachineDesign'
PROJECT = 'APS-U'
CATALOG_NAME = 'Test Item Assembly'  # unique catalog item (id 2) in the test database
AFFECTED_COUNT = 2  # parent + one child


@pytest.mark.mqtt
class MachineDesignMqttTest(MqttPortalTestCase):

    def setUp(self):
        super().setUp()
        self.machine_design = MachineDesign(self.driver)

    def create_hierarchy(self, label):
        suffix = uuid.uuid4().hex[:10]
        parent_name = 'MQTT %s parent %s' % (label, suffix)
        child_name = 'MQTT %s child %s' % (label, suffix)
        self.machine_design.navigate_to_machine_design()
        self.machine_design.add_machine_design_with_child(
            parent_name, child_name, 'Created by MQTT Selenium test', project=PROJECT)
        return suffix, parent_name, child_name

    def trash_quietly(self, parent_name):
        # Leave disposable hierarchies in trash; never mask the original failure.
        try:
            self.machine_design.navigate_to_machine_design()
            self.machine_design.move_top_level_machine_to_trash(parent_name)
        except Exception as ex:
            print('Cleanup failed; move "%s" to trash manually: %s' % (parent_name, ex))

    def assert_trash_event(self, event, operation, machine_name, root_name):
        self.assertEqual(event['operation'], operation)
        self.assertTrue(described_as('affectedMachine.description', machine_name)(event))
        self.assertEqual(event['affectedMachine']['domain'], MACHINE_DOMAIN)
        self.assertTrue(described_as('requestedHierarchyRoot.description', root_name)(event),
                        msg='Unexpected hierarchy root: %s' % event.get('requestedHierarchyRoot'))
        self.assertEqual(event['affectedMachineCount'], AFFECTED_COUNT)
        self.assert_actor(event)

    def test_machine_trash_and_restore(self):
        self.mqtt.drain()
        _, parent_name, child_name = self.create_hierarchy('trash')

        # Creating through the UI fires the generic add event.
        added = self.wait_for_ui_event(
            support.TOPIC_ENTITY_ADD,
            support.all_of(lambda p: p.get('entityType') == MACHINE_ENTITY_TYPE,
                           described_as('entityDescription', parent_name)),
            'add of %s' % parent_name)
        self.assertEqual(added['operation'], 'add')
        self.assert_actor(added)

        active = True
        try:
            self.mqtt.drain()
            self.machine_design.move_top_level_machine_to_trash(parent_name)
            active = False
            for name in (parent_name, child_name):
                event = self.wait_for_ui_event(
                    support.TOPIC_MD_TRASH,
                    described_as('affectedMachine.description', name),
                    'trash of %s' % name)
                self.assert_trash_event(event, 'trash', name, parent_name)

            self.mqtt.drain()
            self.machine_design.navigate_to_deleted_machine_design()
            self.machine_design.restore_deleted_machine(parent_name)
            active = True
            for name in (parent_name, child_name):
                event = self.wait_for_ui_event(
                    support.TOPIC_MD_RESTORE,
                    described_as('affectedMachine.description', name),
                    'restore of %s' % name)
                self.assert_trash_event(event, 'restore', name, parent_name)
        finally:
            if active:
                self.trash_quietly(parent_name)

    def test_machine_assign_catalog(self):
        suffix, parent_name, child_name = self.create_hierarchy('assign')
        try:
            self.mqtt.drain()
            self.machine_design.assign_catalog_to_child_machine(
                parent_name, child_name, CATALOG_NAME, filter_text=suffix)

            event = self.wait_for_ui_event(
                support.TOPIC_MD_ASSIGNED_ITEM,
                described_as('machine.description', child_name),
                'assignment on %s' % child_name)
            self.assertEqual(event['machine']['domain'], MACHINE_DOMAIN)
            self.assertIsNone(event.get('oldAssignedItem'))
            self.assertTrue(described_as('newAssignedItem.description', CATALOG_NAME)(event),
                            msg='Unexpected assigned item: %s' % event.get('newAssignedItem'))
            self.assertEqual(event['newAssignedItem']['domain'], 'Catalog')
            self.assert_actor(event)
        finally:
            self.trash_quietly(parent_name)


if __name__ == '__main__':
    unittest.main()
