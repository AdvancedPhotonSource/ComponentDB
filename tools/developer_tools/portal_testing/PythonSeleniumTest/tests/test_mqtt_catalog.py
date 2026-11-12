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

    def test_catalog_item_and_property_lifecycle(self):
        suffix = uuid.uuid4().hex[:10]
        name = 'MQTT catalog %s' % suffix
        property_value = 'mqtt-%s' % suffix
        updated_value = property_value + '-updated'

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

            # Saving the property updates the item, which also fires the generic update.
            updated_item = self.wait_for_ui_event(
                support.TOPIC_ENTITY_UPDATE,
                support.all_of(support.id_at('entityId', item_id),
                               lambda p: p.get('entityType') == CATALOG_ENTITY_TYPE),
                'update of item %s' % item_id)
            self.assertEqual(updated_item['operation'], 'update')
            self.assert_actor(updated_item)

            property_id = prop['newProperty'].get('propertyValueId')
            on_property = support.all_of(
                support.id_at('itemId', item_id),
                lambda p: support.get_path(p, 'oldProperty.value') in (property_value, updated_value))

            self.mqtt.drain()
            self.catalog.update_first_catalog_property(updated_value)
            prop = self.wait_for_ui_event(
                support.TOPIC_PROPERTY_UPDATE, on_property,
                'property update on item %s' % item_id)
            self.assertEqual(prop['operation'], 'update')
            self.assertEqual(prop['oldProperty']['value'], property_value)
            self.assertEqual(prop['newProperty']['value'], updated_value)
            if property_id is not None:
                self.assertEqual(str(prop['newProperty']['propertyValueId']), str(property_id))
            self.assert_actor(prop)

            self.mqtt.drain()
            self.catalog.delete_first_catalog_property()
            prop = self.wait_for_ui_event(
                support.TOPIC_PROPERTY_DELETE, on_property,
                'property delete on item %s' % item_id)
            self.assertEqual(prop['operation'], 'delete')
            self.assertEqual(prop['oldProperty']['value'], updated_value)
            self.assertIsNone(prop.get('newProperty'))
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


    def create_catalog_item(self, label):
        suffix = uuid.uuid4().hex[:10]
        name = 'MQTT catalog %s %s' % (label, suffix)
        self.mqtt.drain()
        self.catalog.start_create_sample_catalog_item(
            name=name, model='MQTT %s' % suffix, alternate_name='MQTT')
        self.catalog.finish_create_sample_catalog_item()
        added = self.wait_for_ui_event(
            support.TOPIC_ENTITY_ADD,
            support.all_of(lambda p: p.get('entityType') == CATALOG_ENTITY_TYPE,
                           described_as('entityDescription', name)),
            'add of %s' % name)
        return suffix, name, added['entityId']

    def test_catalog_item_log_lifecycle(self):
        suffix, name, item_id = self.create_catalog_item('log')
        log_text = 'MQTT log %s' % suffix
        updated_text = log_text + ' updated'
        deleted = False
        try:
            self.mqtt.drain()
            self.catalog.add_log_to_catalog_item(log_text)
            added = self.wait_for_ui_event(
                support.TOPIC_LOG_ADD,
                support.all_of(support.id_at('itemId', item_id),
                               lambda p: support.get_path(p, 'newLog.text') == log_text),
                'log add on item %s' % item_id)
            self.assertEqual(added['operation'], 'add')
            self.assertEqual(added['itemType'], CATALOG_ENTITY_TYPE)
            self.assertIsNone(added.get('oldLog'))
            log_id = added['newLog']['logId']
            self.assertIsNotNone(log_id, msg='Added log has no id')
            self.assert_actor(added)

            self.mqtt.drain()
            self.catalog.update_first_catalog_log(updated_text)
            updated = self.wait_for_ui_event(
                support.TOPIC_LOG_UPDATE,
                support.all_of(support.id_at('itemId', item_id),
                               support.id_at('newLog.logId', log_id)),
                'log update %s' % log_id)
            self.assertEqual(updated['operation'], 'update')
            self.assertEqual(updated['textDiff'], '-%s\n+%s\n' % (log_text, updated_text))
            self.assertTrue(updated['newLog']['textOmitted'])
            self.assert_actor(updated)

            self.mqtt.drain()
            self.catalog.delete_first_catalog_log(updated_text)
            removed = self.wait_for_ui_event(
                support.TOPIC_LOG_DELETE,
                support.all_of(support.id_at('itemId', item_id),
                               support.id_at('oldLog.logId', log_id)),
                'log delete %s' % log_id)
            self.assertEqual(removed['operation'], 'delete')
            self.assertEqual(removed['oldLog']['text'], updated_text)
            self.assertIsNone(removed.get('newLog'))
            self.assert_actor(removed)

            self.catalog.delete_current_item()
            deleted = True
        finally:
            if not deleted:
                print('Cleanup: delete catalog item "%s" (id %s) manually' % (name, item_id))


if __name__ == '__main__':
    unittest.main()
