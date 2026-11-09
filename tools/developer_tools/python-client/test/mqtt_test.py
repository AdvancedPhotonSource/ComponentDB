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
from cdbApi.models import (
    NewLocationInformation,
    PropertyValue,
    SimpleLocationInformation,
)

from test.cdb_test_base import CdbTestBase
from test.mqtt_test_support import (
    TOPIC_ENTITY_ADD,
    TOPIC_ENTITY_DELETE,
    TOPIC_ENTITY_UPDATE,
    TOPIC_INVENTORY_ASSEMBLY_ELEMENT,
    TOPIC_ITEM_LOCATION,
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

    def assert_item_snapshot(self, snapshot, item_id, msg=None):
        self.assertIsNotNone(snapshot, msg=msg)
        self.assertEqual(str(snapshot.get("id")), str(item_id), msg=msg)

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

    # -- location events ---------------------------------------------------

    def set_item_location(self, item_id, location_id, details):
        self.itemApi.update_item_location(
            simple_location_information=SimpleLocationInformation(
                locatable_item_id=item_id, location_item_id=location_id,
                location_details=details))

    def test_item_location_update(self):
        item_id = self.INVENTORY_ITEM_ID
        original = self.itemApi.get_item_location(item_id)
        original_location_id = original.location_item.id if original.location_item else None
        original_details = original.location_details or None

        new_location_id = self.LOC_ROOM_104_ID
        if new_location_id == original_location_id:
            new_location_id = self.LOC_BUILDING_1_ID
        new_details = "mqtt-%s" % self.gen_unique_name()

        self.loginAsAdmin()
        changed = False
        try:
            self.mqtt.drain()
            self.set_item_location(item_id, new_location_id, new_details)
            changed = True

            event = self.mqtt.wait_for(
                TOPIC_ITEM_LOCATION,
                all_of(id_at("item.id", item_id), id_at("newLocation.id", new_location_id)),
                description="item %s -> location %s" % (item_id, new_location_id))
            self.assert_item_snapshot(event["item"], item_id)
            self.assertEqual(event["item"]["domain"], "Inventory")
            if original_location_id is None:
                self.assertIsNone(event.get("oldLocation"))
            else:
                self.assert_item_snapshot(event["oldLocation"], original_location_id)
                self.assertEqual(event["oldLocation"]["domain"], "Location")
            self.assert_item_snapshot(event["newLocation"], new_location_id)
            self.assertEqual(event.get("oldLocationDetails"), original_details)
            self.assertEqual(event.get("newLocationDetails"), new_details)
            self.assert_actor(event)
        finally:
            if changed:
                self.set_item_location(item_id, original_location_id, original_details)

        restored = self.itemApi.get_item_location(item_id)
        restored_id = restored.location_item.id if restored.location_item else None
        self.assertEqual(restored_id, original_location_id, msg="Location not restored")

    # -- inventory assembly events -----------------------------------------

    def find_assembly_element(self, assembly_id, element_name):
        hierarchy = self.itemApi.get_item_hierarchy_by_id(assembly_id)
        for child in hierarchy.child_items:
            if child.element_name == element_name:
                contained_id = child.item.id if child.item else None
                return child.element_id, contained_id
        self.fail("Element %s not found on item %s" % (element_name, assembly_id))

    def wait_for_assembly_event(self, assembly_id, element_id, new_item_id):
        return self.mqtt.wait_for(
            TOPIC_INVENTORY_ASSEMBLY_ELEMENT,
            all_of(id_at("inventoryAssembly.id", assembly_id),
                   id_at("assemblyElement.id", element_id),
                   id_at("newAssignedInventoryItem.id", new_item_id)),
            description="assembly %s element %s -> %s" % (assembly_id, element_id, new_item_id))

    def assert_assembly_event(self, event, assembly_id, element_id, old_item_id, new_item_id):
        self.assert_item_snapshot(event["inventoryAssembly"], assembly_id)
        self.assertEqual(str(event["assemblyElement"]["id"]), str(element_id))
        self.assertEqual(event["assemblyElement"]["name"], self.INVENTORY_ITEM_ELEMENT_NAME)
        if old_item_id is None:
            self.assertIsNone(event.get("oldAssignedInventoryItem"))
        else:
            self.assert_item_snapshot(event["oldAssignedInventoryItem"], old_item_id)
        if new_item_id is None:
            self.assertIsNone(event.get("newAssignedInventoryItem"))
        else:
            self.assert_item_snapshot(event["newAssignedInventoryItem"], new_item_id)
        self.assert_actor(event)

    def test_inventory_assembly_element_assign_clear_restore(self):
        assembly_id = self.INVENTORY_ITEM_ID
        element_id, original_id = self.find_assembly_element(
            assembly_id, self.INVENTORY_ITEM_ELEMENT_NAME)
        self.assertEqual(original_id, self.INVENTORY_FIRST_CONTAINED_ITEM_ID,
                         msg="Test database not at baseline for assembly element")
        replacement_id = self.INVENTORY_FIRST_CONTAINED_NEW_ITEM_ID

        self.loginAsAdmin()
        try:
            self.mqtt.drain()
            self.itemApi.update_contained_item(element_id, replacement_id)
            event = self.wait_for_assembly_event(assembly_id, element_id, replacement_id)
            self.assert_assembly_event(event, assembly_id, element_id, original_id, replacement_id)

            self.mqtt.drain()
            self.itemApi.clear_contained_item(element_id)
            event = self.wait_for_assembly_event(assembly_id, element_id, None)
            self.assert_assembly_event(event, assembly_id, element_id, replacement_id, None)

            self.mqtt.drain()
            self.itemApi.update_contained_item(element_id, original_id)
            event = self.wait_for_assembly_event(assembly_id, element_id, original_id)
            self.assert_assembly_event(event, assembly_id, element_id, None, original_id)
        finally:
            _, current_id = self.find_assembly_element(
                assembly_id, self.INVENTORY_ITEM_ELEMENT_NAME)
            if current_id != original_id:
                self.itemApi.update_contained_item(element_id, original_id)

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
