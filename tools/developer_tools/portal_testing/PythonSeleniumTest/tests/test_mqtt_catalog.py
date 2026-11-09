#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.

MQTT verification for generic entity and property events fired from the portal UI.

Marked ``mqtt`` so the parallel suite deselects it (``-m "not mqtt"``); run it
serially via sbin/cdb_mqtt_test.sh. Skipped when CDB_TEST_MQTT=false, when
paho-mqtt is missing, or when the broker cannot be reached.
"""

import unittest
import uuid

import pytest

from cdbSeleniumModules.catalog import Catalog
from tests.mqtt_base import MqttPortalTestCase, described_as, mqtt_test_support as support

CATALOG_ENTITY_TYPE = 'ItemDomainCatalog'


@pytest.mark.mqtt
class CatalogMqttTest(MqttPortalTestCase):

    def setUp(self):
        super().setUp()
        self.catalog = Catalog(self.driver)

    def test_catalog_add_property_delete(self):
        suffix = uuid.uuid4().hex[:10]
        name = 'MQTT catalog %s' % suffix
        property_value = 'mqtt-%s' % suffix

        self.mqtt.drain()
        self.catalog.start_create_sample_catalog_item(
            name=name, model='MQTT %s' % suffix, alternate_name='MQTT')
        self.catalog.finish_create_sample_catalog_item()

        added = self.wait_for_ui_event(
            support.TOPIC_ENTITY_ADD,
            support.all_of(lambda p: p.get('entityType') == CATALOG_ENTITY_TYPE,
                           described_as('entityDescription', name)),
            'add of %s' % name)
        self.assertEqual(added['operation'], 'add')
        self.assert_actor(added)
        item_id = added['entityId']

        deleted = False
        try:
            self.mqtt.drain()
            self.catalog.add_property_to_catalog_item(self, prop_value_text=property_value)
            prop = self.wait_for_ui_event(
                support.TOPIC_PROPERTY_ADD,
                support.all_of(support.id_at('itemId', item_id),
                               lambda p: support.get_path(p, 'newProperty.value') == property_value),
                'property %s on item %s' % (property_value, item_id))
            self.assertEqual(prop['operation'], 'add')
            self.assertEqual(prop['itemType'], CATALOG_ENTITY_TYPE)
            self.assertIsNone(prop.get('oldProperty'))
            self.assert_actor(prop)

            self.mqtt.drain()
            self.catalog.delete_current_item()
            deleted = True
            removed = self.wait_for_ui_event(
                support.TOPIC_ENTITY_DELETE,
                support.all_of(support.id_at('entityId', item_id),
                               lambda p: p.get('entityType') == CATALOG_ENTITY_TYPE),
                'delete of item %s' % item_id)
            self.assertEqual(removed['operation'], 'delete')
            self.assert_actor(removed)
        finally:
            if not deleted:
                print('Cleanup: delete catalog item "%s" (id %s) manually' % (name, item_id))


if __name__ == '__main__':
    unittest.main()
