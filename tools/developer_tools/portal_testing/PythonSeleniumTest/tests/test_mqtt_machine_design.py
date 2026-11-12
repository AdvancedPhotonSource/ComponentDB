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
# Test-database template with one template child; both names use the {nn} variable.
TEMPLATE_NAME = 'Template_Test {nn}'
TEMPLATE_CHILD_NAME = 'child {nn}'


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

    def trash_quietly(self, name, menu_id=MachineDesign.MACHINE_DESIGN_CONTEXT_MENU_ID):
        # Leave disposable hierarchies in trash; never mask the original failure.
        try:
            self.machine_design.navigate_to_machine_design()
            self.machine_design.move_top_level_machine_to_trash(name, menu_id)
        except Exception as ex:
            print('Cleanup failed; move "%s" to trash manually: %s' % (name, ex))

    def assert_trash_event(self, event, operation, machine_name, root_name):
        self.assertEqual(event['operation'], operation)
        self.assertTrue(described_as('affectedMachine.description', machine_name)(event))
        self.assertEqual(event['affectedMachine']['domain'], MACHINE_DOMAIN)
        self.assertTrue(described_as('requestedHierarchyRoot.description', root_name)(event),
                        msg='Unexpected hierarchy root: %s' % event.get('requestedHierarchyRoot'))
        self.assertEqual(event['affectedMachineCount'], AFFECTED_COUNT)
        self.assert_actor(event)

    def test_machine_add_parent_and_child(self):
        self.mqtt.drain()
        _, parent_name, child_name = self.create_hierarchy('add')
        try:
            parent = self.wait_for_ui_event(
                support.TOPIC_MD_ADD,
                described_as('machine.description', parent_name),
                'machine add of %s' % parent_name)
            self.assertEqual(parent['machine']['domain'], MACHINE_DOMAIN)
            self.assertIn('parent', parent)
            self.assertIsNone(parent['parent'], msg='Top-level machine must have null parent')
            self.assertNotIn('oldParent', parent)
            self.assertNotIn('newParent', parent)
            self.assertNotIn('sourceTemplate', parent)
            self.assertNotIn('templateAppliedToMachine', parent)
            self.assertNotIn('templateCreatedMachineCount', parent)
            self.assert_actor(parent)

            child = self.wait_for_ui_event(
                support.TOPIC_MD_ADD,
                described_as('machine.description', child_name),
                'machine add of %s' % child_name)
            self.assertEqual(str(child['parent']['id']), str(parent['machine']['id']))
            self.assertNotEqual(str(child['machine']['id']), str(parent['machine']['id']))
            self.assert_actor(child)

            # Adding the child must not re-announce the existing parent.
            self.mqtt.assert_no_message(
                support.TOPIC_MD_ADD,
                described_as('machine.description', parent_name),
                within=2.0, description='parent %s re-announced' % parent_name)
        finally:
            self.trash_quietly(parent_name)

    def test_machine_add_from_template(self):
        suffix = uuid.uuid4().hex[:10]
        parent_name = 'MQTT template parent %s' % suffix
        top_name = TEMPLATE_NAME.replace('{nn}', suffix)
        child_name = TEMPLATE_CHILD_NAME.replace('{nn}', suffix)
        self.machine_design.navigate_to_machine_design()
        self.machine_design.add_machine_design(
            parent_name, 'Created by MQTT Selenium test', PROJECT)
        try:
            self.mqtt.drain()
            self.machine_design.add_child_from_template(parent_name, TEMPLATE_NAME, suffix)

            top = self.wait_for_ui_event(
                support.TOPIC_MD_ADD, described_as('machine.description', top_name),
                'machine add of %s' % top_name)
            self.assertTrue(described_as('parent.description', parent_name)(top),
                            msg='Unexpected parent: %s' % top.get('parent'))
            self.assertTrue(described_as('sourceTemplate.description', TEMPLATE_NAME)(top),
                            msg='Unexpected template: %s' % top.get('sourceTemplate'))
            # The new top node is the machine the template was applied to.
            self.assertEqual(str(top['templateAppliedToMachine']['id']), str(top['machine']['id']))
            self.assertEqual(top['templateCreatedMachineCount'], 2)
            self.assert_actor(top)

            child = self.wait_for_ui_event(
                support.TOPIC_MD_ADD, described_as('machine.description', child_name),
                'machine add of %s' % child_name)
            self.assertEqual(str(child['parent']['id']), str(top['machine']['id']))
            self.assertTrue(described_as('sourceTemplate.description', TEMPLATE_CHILD_NAME)(child),
                            msg='Unexpected template: %s' % child.get('sourceTemplate'))
            self.assertEqual(str(child['templateAppliedToMachine']['id']), str(top['machine']['id']))
            self.assertEqual(child['templateCreatedMachineCount'], 2)
            self.assert_actor(child)

            # The existing parent is neither announced nor treated as the template target.
            self.mqtt.assert_no_message(
                support.TOPIC_MD_ADD, described_as('machine.description', parent_name),
                within=2.0, description='existing parent %s announced' % parent_name)
        finally:
            self.trash_quietly(parent_name)

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

    def test_machine_assign_link_and_move(self):
        suffix, parent_name, child_name = self.create_hierarchy('chain')
        mover_name = 'MQTT chain mover %s' % suffix
        self.machine_design.add_machine_design(
            mover_name, 'Created by MQTT Selenium test', PROJECT)
        parent_has_catalog = False
        mover_moved = False
        try:
            # Assign a catalog assembly to the parent.
            self.mqtt.drain()
            self.machine_design.assign_catalog_to_machine(
                [parent_name], CATALOG_NAME, filter_text=suffix)
            parent_has_catalog = True
            event = self.wait_for_ui_event(
                support.TOPIC_MD_ASSIGNED_ITEM,
                described_as('machine.description', parent_name),
                'assignment on %s' % parent_name)
            self.assertEqual(event['machine']['domain'], MACHINE_DOMAIN)
            self.assertIsNone(event.get('oldAssignedItem'))
            self.assertTrue(described_as('newAssignedItem.description', CATALOG_NAME)(event),
                            msg='Unexpected assigned item: %s' % event.get('newAssignedItem'))
            self.assertEqual(event['newAssignedItem']['domain'], 'Catalog')
            self.assert_actor(event)

            # Link the child to one of that assembly's parts.
            self.mqtt.drain()
            element_name = self.machine_design.link_child_to_assembly_part(
                parent_name, child_name, filter_text=suffix)
            event = self.wait_for_ui_event(
                support.TOPIC_MD_REPRESENTED_ELEMENT,
                described_as('machine.description', child_name),
                'represented element on %s' % child_name)
            self.assertIsNone(event.get('oldRepresentedElement'))
            self.assertEqual(event['newRepresentedElement']['name'], element_name)
            self.assertTrue(
                described_as('newRepresentedElement.parentItem.description', CATALOG_NAME)(event),
                msg='Unexpected element parent: %s' % event['newRepresentedElement'].get('parentItem'))
            self.assert_actor(event)

            # Drag the top-level mover onto the parent.
            self.mqtt.drain()
            self.machine_design.drag_machine_onto(mover_name, parent_name, filter_text=suffix)
            mover_moved = True
            event = self.wait_for_ui_event(
                support.TOPIC_MD_MOVE,
                described_as('machine.description', mover_name),
                'move of %s' % mover_name)
            self.mqtt.assert_no_message(
                support.TOPIC_MD_ADD, described_as('machine.description', mover_name),
                within=2.0, description='move announced as new machine %s' % mover_name)
            self.assertIsNone(event.get('oldParent'))
            self.assertTrue(described_as('newParent.description', parent_name)(event),
                            msg='Unexpected new parent: %s' % event.get('newParent'))
            self.assert_actor(event)
        finally:
            menu_id = (MachineDesign.MACHINE_DESIGN_CATALOG_CONTEXT_MENU_ID if parent_has_catalog
                       else MachineDesign.MACHINE_DESIGN_CONTEXT_MENU_ID)
            self.trash_quietly(parent_name, menu_id)
            if not mover_moved:
                self.trash_quietly(mover_name)

if __name__ == '__main__':
    unittest.main()
