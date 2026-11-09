"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.

MQTT integration tests: perform API mutations against the deployed portal and
verify the events it publishes through Payara's cdb/MQTT/resource connector.

Skipped when CDB_TEST_MQTT=false, when paho-mqtt is missing, or when the broker
cannot be reached. See README.md ("MQTT integration tests").
"""

import unittest

from cdbApi.exceptions import OpenApiException
from cdbApi.models import NewLocationInformation, PropertyValue

from test.cdb_test_base import CdbTestBase
from test.mqtt_test_support import (
    TOPIC_ENTITY_ADD,
    TOPIC_ENTITY_DELETE,
    TOPIC_ENTITY_UPDATE,
    TOPIC_PROPERTY_ADD,
    TOPIC_PROPERTY_DELETE,
    TOPIC_PROPERTY_UPDATE,
    all_of,
    get_path,
    id_at,
    start_collector_or_skip,
)

LOCATION_ENTITY_TYPE = "ItemDomainLocation"


class MqttIntegrationTest(CdbTestBase):

    @classmethod
    def setUpClass(cls):
        super().setUpClass()
        cls.mqtt = start_collector_or_skip()

    @classmethod
    def tearDownClass(cls):
        cls.mqtt.stop()
        super().tearDownClass()

    def setUp(self):
        super().setUp()
        self.mqtt.drain()

    # -- helpers -----------------------------------------------------------

    def assert_actor(self, payload, username=None):
        self.assertEqual(payload.get("triggeredByUsername"),
                         username or self.ADMIN_USERNAME, msg="Unexpected event actor")
        self.assertIn("eventTimestamp", payload)
        self.assertNotIn("topic", payload, msg="Topic must not be serialized into payload")

    def wait_for_entity_event(self, topic, entity_id, entity_type):
        return self.mqtt.wait_for(
            topic,
            all_of(id_at("entityId", entity_id),
                   lambda p: p.get("entityType") == entity_type),
            description="%s id=%s" % (entity_type, entity_id))

    def delete_item_quietly(self, item_id):
        try:
            self.itemApi.delete_item_by_id(item_id)
        except OpenApiException:
            pass

    # -- harness -----------------------------------------------------------

    def test_broker_round_trip(self):
        payload = self.mqtt.probe()
        self.assertIn("probe", payload)

    # -- generic entity events ---------------------------------------------

    def test_entity_add_update_delete(self):
        self.loginAsAdmin()
        name = "MQTT test location %s" % self.gen_unique_name()
        location_id = None
        deleted = False
        try:
            self.mqtt.drain()
            location = self.itemApi.create_location(
                new_location_information=NewLocationInformation(
                    location_name=name, location_type="Building"))
            location_id = location.id

            added = self.wait_for_entity_event(
                TOPIC_ENTITY_ADD, location_id, LOCATION_ENTITY_TYPE)
            self.assertEqual(added["operation"], "add")
            self.assertIn(name, added["entityDescription"])
            self.assertIn("[Item Id: %s]" % location_id, added["entityDescription"])
            self.assert_actor(added)

            self.mqtt.drain()
            new_name = name + " renamed"
            item = self.itemApi.get_item_by_id(location_id)
            item.name = new_name
            self.itemApi.update_item_details(item=item)

            updated = self.wait_for_entity_event(
                TOPIC_ENTITY_UPDATE, location_id, LOCATION_ENTITY_TYPE)
            self.assertEqual(updated["operation"], "update")
            self.assertIn(new_name, updated["entityDescription"])
            self.assert_actor(updated)

            self.mqtt.drain()
            self.itemApi.delete_item_by_id(location_id)
            deleted = True

            removed = self.wait_for_entity_event(
                TOPIC_ENTITY_DELETE, location_id, LOCATION_ENTITY_TYPE)
            self.assertEqual(removed["operation"], "delete")
            self.assertIn(new_name, removed["entityDescription"])
            self.assert_actor(removed)
        finally:
            if location_id is not None and not deleted:
                self.delete_item_quietly(location_id)

    # -- item property events ----------------------------------------------

    def test_item_property_add_update_delete(self):
        self.loginAsAdmin()
        item_id = self.INVENTORY_ITEM_ID
        property_type = self.propertyTypeApi.get_property_type_by_name(
            self.TEST_PROPERTY_TYPE_NAME)
        initial_value = "mqtt-%s" % self.gen_unique_name()
        updated_value = initial_value + "-updated"
        property_id = None
        deleted = False
        on_item = id_at("itemId", item_id)
        try:
            self.mqtt.drain()
            created = self.itemApi.add_item_property_value(
                item_id, property_value=PropertyValue(
                    property_type=property_type, value=initial_value))
            property_id = created.id

            # New property ids are not assigned until persist, so match the add by value.
            added = self.mqtt.wait_for(
                TOPIC_PROPERTY_ADD,
                all_of(on_item, lambda p: get_path(p, "newProperty.value") == initial_value),
                description="item %s value %s" % (item_id, initial_value))
            self.assertEqual(added["operation"], "add")
            self.assertEqual(added["itemType"], "ItemDomainInventory")
            self.assertIsNone(added.get("oldProperty"))
            new_property = added["newProperty"]
            self.assertEqual(new_property["propertyTypeName"], self.TEST_PROPERTY_TYPE_NAME)
            self.assertEqual(str(new_property["propertyTypeId"]), str(property_type.id))
            if new_property.get("propertyValueId") is not None:
                self.assertEqual(str(new_property["propertyValueId"]), str(property_id))
            self.assert_actor(added)

            self.mqtt.drain()
            created.value = updated_value
            self.itemApi.update_item_property_value(item_id, property_value=created)

            updated = self.mqtt.wait_for(
                TOPIC_PROPERTY_UPDATE,
                all_of(on_item, id_at("newProperty.propertyValueId", property_id)),
                description="item %s property %s" % (item_id, property_id))
            self.assertEqual(updated["operation"], "update")
            self.assertEqual(updated["oldProperty"]["value"], initial_value)
            self.assertEqual(updated["newProperty"]["value"], updated_value)
            self.assertEqual(str(updated["oldProperty"]["propertyValueId"]), str(property_id))
            self.assert_actor(updated)

            self.mqtt.drain()
            self.propertyValueApi.delete_property_by_id(property_id)
            deleted = True

            removed = self.mqtt.wait_for(
                TOPIC_PROPERTY_DELETE,
                all_of(on_item, id_at("oldProperty.propertyValueId", property_id)),
                description="item %s property %s" % (item_id, property_id))
            self.assertEqual(removed["operation"], "delete")
            self.assertEqual(removed["oldProperty"]["value"], updated_value)
            self.assertIsNone(removed.get("newProperty"))
            self.assert_actor(removed)
        finally:
            if property_id is not None and not deleted:
                try:
                    self.propertyValueApi.delete_property_by_id(property_id)
                except OpenApiException:
                    pass


if __name__ == "__main__":
    unittest.main()
