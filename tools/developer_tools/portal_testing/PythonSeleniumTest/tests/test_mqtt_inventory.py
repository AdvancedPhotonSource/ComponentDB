#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.

MQTT verification for inventory location and assembly events fired from the portal UI.

Uses standard test-database inventory item 56 and restores it afterwards. Marked
``mqtt`` so the parallel suite deselects it; run serially via sbin/cdb_mqtt_test.sh.
"""

import unittest
import uuid

import pytest

from cdbSeleniumModules.inventory import Inventory
from tests.mqtt_base import MqttPortalTestCase, mqtt_test_support as support

# Standard test-database fixtures (see python-client test/cdb_test_base.py).
INVENTORY_ID = 56
ELEMENT_NAME = 'E1'
ORIGINAL_ITEM_ID, ORIGINAL_ITEM_NAME = 45, 'Unit: 0005'
REPLACEMENT_ITEM_ID, REPLACEMENT_ITEM_NAME = 41, 'Unit: 0001'


def on_element(element_name):
    return lambda p: element_name in (support.get_path(p, 'assemblyElement.name'),
                                      support.get_path(p, 'catalogElement.name'))


@pytest.mark.mqtt
class InventoryMqttTest(MqttPortalTestCase):

    def setUp(self):
        super().setUp()
        self.inventory = Inventory(self.driver)
        self.inventory.open_inventory_by_id(INVENTORY_ID)

    def test_inventory_location_details(self):
        original = self.inventory.get_location_details()
        new_details = 'mqtt-%s' % uuid.uuid4().hex[:10]
        changed = False
        try:
            self.mqtt.drain()
            self.inventory.set_location_details_inline(new_details)
            changed = True
            event = self.wait_for_ui_event(
                support.TOPIC_ITEM_LOCATION,
                support.all_of(support.id_at('item.id', INVENTORY_ID),
                               lambda p: p.get('newLocationDetails') == new_details),
                'location details on item %s' % INVENTORY_ID)
            self.assertEqual(event['item']['domain'], 'Inventory')
            self.assertEqual(event.get('oldLocationDetails'), original or None)
            self.assert_actor(event)
        finally:
            if changed:
                try:
                    self.inventory.set_location_details_inline(original)
                except Exception as ex:
                    print('Cleanup failed; reset location details of item %s to "%s": %s'
                          % (INVENTORY_ID, original, ex))

    def wait_for_assembly_event(self, new_item_id):
        return self.wait_for_ui_event(
            support.TOPIC_INVENTORY_ASSEMBLY_ELEMENT,
            support.all_of(support.id_at('inventoryAssembly.id', INVENTORY_ID),
                           on_element(ELEMENT_NAME),
                           support.id_at('newAssignedInventoryItem.id', new_item_id)),
            'element %s on item %s -> %s' % (ELEMENT_NAME, INVENTORY_ID, new_item_id))

    def test_inventory_assembly_element(self):
        changed = False
        try:
            self.mqtt.drain()
            self.inventory.update_assembly_element(
                ELEMENT_NAME, REPLACEMENT_ITEM_ID, REPLACEMENT_ITEM_NAME)
            changed = True
            event = self.wait_for_assembly_event(REPLACEMENT_ITEM_ID)
            self.assertEqual(str(event['oldAssignedInventoryItem']['id']), str(ORIGINAL_ITEM_ID),
                             msg='Test database not at baseline for element %s' % ELEMENT_NAME)
            self.assert_actor(event)

            self.mqtt.drain()
            self.inventory.update_assembly_element(
                ELEMENT_NAME, ORIGINAL_ITEM_ID, ORIGINAL_ITEM_NAME)
            changed = False
            event = self.wait_for_assembly_event(ORIGINAL_ITEM_ID)
            self.assertEqual(str(event['oldAssignedInventoryItem']['id']), str(REPLACEMENT_ITEM_ID))
            self.assert_actor(event)
        finally:
            if changed:
                try:
                    self.inventory.update_assembly_element(
                        ELEMENT_NAME, ORIGINAL_ITEM_ID, ORIGINAL_ITEM_NAME)
                except Exception as ex:
                    print('Cleanup failed; reassign %s on item %s to item %s: %s'
                          % (ELEMENT_NAME, INVENTORY_ID, ORIGINAL_ITEM_ID, ex))


if __name__ == '__main__':
    unittest.main()
