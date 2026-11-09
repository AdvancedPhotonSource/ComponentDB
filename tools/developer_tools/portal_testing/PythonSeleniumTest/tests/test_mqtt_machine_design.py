#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.

MQTT verification for the UI-only machine trash/restore events.

Marked ``mqtt`` so the parallel suite deselects it (``-m "not mqtt"``); run it
serially via sbin/cdb_mqtt_test.sh. Skipped when CDB_TEST_MQTT=false, when
paho-mqtt is missing, or when the broker cannot be reached.
"""

import os
import sys
import unittest
import uuid

import pytest

from cdbSeleniumModules.machineDesign import MachineDesign
from tests.base import CdbPortalTestCase

# Shared subscriber helper lives with the API tests.
_SUPPORT_DIR = os.path.abspath(os.path.join(
    os.path.dirname(__file__), '..', '..', '..', 'python-client', 'test'))
if os.path.isdir(_SUPPORT_DIR) and _SUPPORT_DIR not in sys.path:
    sys.path.append(_SUPPORT_DIR)

try:
    from mqtt_test_support import (
        TOPIC_MD_RESTORE,
        TOPIC_MD_TRASH,
        get_path,
        start_collector_or_skip,
    )
except ImportError:
    start_collector_or_skip = None

ADMIN_USERNAME = 'cdb'
MACHINE_DOMAIN = 'Machine Design'
PROJECT = 'APS-U'
UI_EVENT_TIMEOUT = 30
AFFECTED_COUNT = 2  # parent + one child


def described_as(path, name):
    """Match item snapshots by name; descriptions look like '<name> [Item Id: N]'."""
    prefix = '%s [Item Id:' % name
    return lambda payload: str(get_path(payload, path) or '').startswith(prefix)


@pytest.mark.mqtt
class MachineDesignMqttTest(CdbPortalTestCase):

    @classmethod
    def setUpClass(cls):
        super().setUpClass()
        if start_collector_or_skip is None:
            raise unittest.SkipTest(
                'mqtt_test_support not importable; expected it in %s' % _SUPPORT_DIR)
        # Connect before the browser starts so an unavailable broker skips cheaply.
        cls.mqtt = start_collector_or_skip()

    @classmethod
    def tearDownClass(cls):
        cls.mqtt.stop()
        super().tearDownClass()

    def setUp(self):
        super().setUp()
        self.machine_design = MachineDesign(self.driver)

    def wait_for_trash_event(self, topic, machine_name):
        return self.mqtt.wait_for(
            topic, described_as('affectedMachine.description', machine_name),
            timeout=max(UI_EVENT_TIMEOUT, self.mqtt.settings.timeout),
            description='affected machine %s' % machine_name)

    def assert_trash_event(self, event, operation, machine_name, root_name):
        self.assertEqual(event['operation'], operation)
        self.assertTrue(described_as('affectedMachine.description', machine_name)(event))
        self.assertEqual(event['affectedMachine']['domain'], MACHINE_DOMAIN)
        self.assertTrue(described_as('requestedHierarchyRoot.description', root_name)(event),
                        msg='Unexpected hierarchy root: %s' % event.get('requestedHierarchyRoot'))
        self.assertEqual(event['affectedMachineCount'], AFFECTED_COUNT)
        self.assertEqual(event.get('triggeredByUsername'), ADMIN_USERNAME)
        self.assertIn('eventTimestamp', event)
        self.assertNotIn('topic', event)

    def test_machine_trash_and_restore(self):
        suffix = uuid.uuid4().hex[:10]
        parent_name = 'MQTT trash parent %s' % suffix
        child_name = 'MQTT trash child %s' % suffix

        self.machine_design.navigate_to_machine_design()
        self.machine_design.add_machine_design_with_child(
            parent_name, child_name, 'Created by MQTT Selenium test', project=PROJECT)

        active = True
        try:
            self.mqtt.drain()
            self.machine_design.move_top_level_machine_to_trash(parent_name)
            active = False
            for name in (parent_name, child_name):
                event = self.wait_for_trash_event(TOPIC_MD_TRASH, name)
                self.assert_trash_event(event, 'trash', name, parent_name)

            self.mqtt.drain()
            self.machine_design.navigate_to_deleted_machine_design()
            self.machine_design.restore_deleted_machine(parent_name)
            active = True
            for name in (parent_name, child_name):
                event = self.wait_for_trash_event(TOPIC_MD_RESTORE, name)
                self.assert_trash_event(event, 'restore', name, parent_name)
        finally:
            # Leave the disposable hierarchy in trash, never active.
            if active:
                try:
                    self.machine_design.navigate_to_machine_design()
                    self.machine_design.move_top_level_machine_to_trash(parent_name)
                except Exception as ex:
                    # Do not mask the original failure; report the leftover instead.
                    print('Cleanup failed; move "%s" to trash manually: %s' % (parent_name, ex))


if __name__ == '__main__':
    unittest.main()
