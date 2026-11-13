"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.

MQTT integration tests: perform API mutations against the deployed portal and
verify the events it publishes through Payara's cdb/MQTT/resource connector.

Skipped when CDB_TEST_MQTT=false, when paho-mqtt is missing, or when the broker
cannot be reached. See README.md ("MQTT integration tests").
"""

import os
import unittest

from cdbApi.exceptions import OpenApiException
from cdbApi.models import (
    LogEntryEditInformation,
    NewLocationInformation,
    NewMachinePlaceholderOptions,
    PropertyValue,
    RepresentingAssemblyElementForMachineInformation,
    SimpleLocationInformation,
    UpdateMachineAssignedItemInformation,
)

from test.cdb_test_base import CdbTestBase
from test.mqtt_test_support import (
    TOPIC_ENTITY_ADD,
    TOPIC_ENTITY_DELETE,
    TOPIC_ENTITY_UPDATE,
    TOPIC_INVENTORY_ASSEMBLY_ELEMENT,
    TOPIC_ITEM_LOCATION,
    TOPIC_LOG_ADD,
    TOPIC_LOG_ATTACHMENT_ADD,
    TOPIC_LOG_DELETE,
    TOPIC_LOG_UPDATE,
    TOPIC_MD_ADD,
    TOPIC_MD_ASSIGNED_ITEM,
    TOPIC_MD_MOVE,
    TOPIC_MD_REPRESENTED_ELEMENT,
    TOPIC_PROPERTY_ADD,
    TOPIC_PROPERTY_DELETE,
    TOPIC_PROPERTY_UPDATE,
    all_of,
    get_path,
    id_at,
    start_collector_or_skip,
)

LOCATION_ENTITY_TYPE = "ItemDomainLocation"
MACHINE_ENTITY_TYPE = "ItemDomainMachineDesign"
MACHINE_DOMAIN = "Machine Design"
# Grace period for asserting that no specialized event follows the generic update.
NO_EVENT_GRACE_SECONDS = 2.0


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

    # -- machine design events ---------------------------------------------

    def set_assigned_item(self, machine_id, item_id, installed=None):
        # The route rejects isInstalled unless the assignment is an inventory item.
        return self.machineDesignApi.update_assigned_item(
            UpdateMachineAssignedItemInformation(
                md_item_id=machine_id, assigned_item_id=item_id, is_installed=installed))

    def get_assignment(self, machine_id):
        """Return (assigned item id, is inventory, installed flag) for a machine."""
        machine = self.machineDesignApi.get_machine_design_item_by_id(machine_id)
        item = machine.assigned_item
        is_inventory = bool(item and item.domain and item.domain.name == "Inventory")
        return (item.id if item else None), is_inventory, bool(machine.is_housed)

    def restore_assignment(self, machine_id, item_id, is_inventory, installed):
        self.set_assigned_item(machine_id, item_id, installed if is_inventory else None)

    def test_machine_assigned_item_update(self):
        machine_id = self.MACHINE_DESIGN_ID
        original_id, original_is_inventory, original_installed = self.get_assignment(machine_id)
        if original_id == self.INVENTORY_ITEM_ID:
            new_id, new_installed, expected_new_installed = self.CATALOG_ITEM_ID, None, True
        else:
            # Planned (not installed) inventory makes the installed transition observable.
            new_id, new_installed, expected_new_installed = self.INVENTORY_ITEM_ID, False, False

        self.loginAsAdmin()
        changed = False
        try:
            self.mqtt.drain()
            self.set_assigned_item(machine_id, new_id, new_installed)
            changed = True
            event = self.mqtt.wait_for(
                TOPIC_MD_ASSIGNED_ITEM,
                all_of(id_at("machine.id", machine_id), id_at("newAssignedItem.id", new_id)),
                description="machine %s -> item %s" % (machine_id, new_id))
            self.assert_item_snapshot(event["machine"], machine_id)
            self.assertEqual(event["machine"]["domain"], MACHINE_DOMAIN)
            if original_id is None:
                self.assertIsNone(event.get("oldAssignedItem"))
            else:
                self.assert_item_snapshot(event["oldAssignedItem"], original_id)
            self.assert_item_snapshot(event["newAssignedItem"], new_id)
            self.assertEqual(event["oldInstalled"], original_installed)
            self.assertEqual(event["newInstalled"], expected_new_installed)
            self.assert_actor(event)

            self.mqtt.drain()
            self.restore_assignment(machine_id, original_id, original_is_inventory,
                                    original_installed)
            changed = False
            event = self.mqtt.wait_for(
                TOPIC_MD_ASSIGNED_ITEM,
                all_of(id_at("machine.id", machine_id), id_at("newAssignedItem.id", original_id)),
                description="machine %s -> item %s (revert)" % (machine_id, original_id))
            self.assert_item_snapshot(event["oldAssignedItem"], new_id)
            self.assertEqual(event["oldInstalled"], expected_new_installed)
            self.assertEqual(event["newInstalled"], original_installed)
            self.assert_actor(event)
        finally:
            if changed:
                self.restore_assignment(machine_id, original_id, original_is_inventory,
                                        original_installed)

    def test_machine_assigned_item_noop_publishes_no_specialized_event(self):
        machine_id = self.MACHINE_DESIGN_ID
        current_id, is_inventory, installed = self.get_assignment(machine_id)

        self.loginAsAdmin()
        self.mqtt.drain()
        # Re-saving the same assignment still updates the entity, which acts as a barrier.
        self.restore_assignment(machine_id, current_id, is_inventory, installed)
        self.wait_for_entity_event(TOPIC_ENTITY_UPDATE, machine_id, MACHINE_ENTITY_TYPE)
        self.mqtt.assert_no_message(
            TOPIC_MD_ASSIGNED_ITEM, id_at("machine.id", machine_id),
            within=NO_EVENT_GRACE_SECONDS,
            description="no-op assignment on machine %s" % machine_id)

    def create_placeholder(self, parent_id, name):
        return self.machineDesignApi.create_placeholder(
            parent_id, NewMachinePlaceholderOptions(name=name))

    def remove_machine_quietly(self, machine_id):
        try:
            self.machineDesignApi.detach_machine_from_hierarchy(machine_id)
        except OpenApiException:
            pass
        self.delete_item_quietly(machine_id)

    def wait_for_machine_add(self, machine_id):
        return self.mqtt.wait_for(
            TOPIC_MD_ADD, id_at("machine.id", machine_id),
            description="new machine %s" % machine_id)

    def test_machine_add_nested_placeholders(self):
        root_id = self.MACHINE_DESIGN_ID
        suffix = self.gen_unique_name()
        self.loginAsAdmin()
        created = []
        try:
            self.mqtt.drain()
            parent = self.create_placeholder(root_id, "MQTT add parent %s" % suffix)
            created.append(parent.id)
            event = self.wait_for_machine_add(parent.id)
            self.assert_item_snapshot(event["machine"], parent.id)
            self.assertEqual(event["machine"]["domain"], MACHINE_DOMAIN)
            self.assert_item_snapshot(event["parent"], root_id)
            self.assertNotIn("oldParent", event)
            self.assertNotIn("newParent", event)
            # Template fields are omitted for non-template machines.
            self.assertNotIn("sourceTemplate", event)
            self.assertNotIn("templateAppliedToMachine", event)
            self.assertNotIn("templateCreatedMachineCount", event)
            self.assert_actor(event)
            # Placeholders are cascaded through the parent, so the standard event is its update.
            self.wait_for_entity_event(TOPIC_ENTITY_UPDATE, root_id, MACHINE_ENTITY_TYPE)

            child = self.create_placeholder(parent.id, "MQTT add child %s" % suffix)
            created.append(child.id)
            event = self.wait_for_machine_add(child.id)
            self.assert_item_snapshot(event["parent"], parent.id)
            self.assert_actor(event)
            # The existing parent must not be re-announced when a child is added.
            self.mqtt.assert_no_message(
                TOPIC_MD_ADD, id_at("machine.id", parent.id),
                within=NO_EVENT_GRACE_SECONDS,
                description="existing parent %s re-announced" % parent.id)
        finally:
            for machine_id in reversed(created):
                self.remove_machine_quietly(machine_id)

    def test_machine_add_root_has_null_parent(self):
        suffix = self.gen_unique_name()
        self.loginAsAdmin()
        machine_id = None
        try:
            self.mqtt.drain()
            # Top-level control node: a direct create with no parent.
            machine = self.machineDesignApi.create_control_element(NewMachinePlaceholderOptions(
                name="MQTT add root %s" % suffix, project_id=self.PROJECT_ID))
            machine_id = machine.id
            event = self.wait_for_machine_add(machine_id)
            self.assert_item_snapshot(event["machine"], machine_id)
            self.assertEqual(event["machine"]["domain"], MACHINE_DOMAIN)
            self.assertIn("parent", event)
            self.assertIsNone(event["parent"])
            self.assert_actor(event)
            # Standard entity add is still published alongside the specialized event.
            self.wait_for_entity_event(TOPIC_ENTITY_ADD, machine_id, MACHINE_ENTITY_TYPE)
        finally:
            if machine_id is not None:
                self.delete_item_quietly(machine_id)

    def wait_for_move(self, machine_id, new_parent_id):
        return self.mqtt.wait_for(
            TOPIC_MD_MOVE,
            all_of(id_at("machine.id", machine_id), id_at("newParent.id", new_parent_id)),
            description="machine %s -> parent %s" % (machine_id, new_parent_id))

    def test_machine_move(self):
        root_id = self.MACHINE_DESIGN_ID
        suffix = self.gen_unique_name()
        self.loginAsAdmin()
        created = []
        try:
            new_parent = self.create_placeholder(root_id, "MQTT move parent %s" % suffix)
            created.append(new_parent.id)
            child = self.create_placeholder(root_id, "MQTT move child %s" % suffix)
            created.append(child.id)

            self.mqtt.drain()
            self.machineDesignApi.move_machine(child.id, new_parent.id)
            event = self.wait_for_move(child.id, new_parent.id)
            self.mqtt.assert_no_message(
                TOPIC_MD_ADD, id_at("machine.id", child.id),
                within=NO_EVENT_GRACE_SECONDS,
                description="move announced as new machine %s" % child.id)
            self.assert_item_snapshot(event["machine"], child.id)
            self.assertEqual(event["machine"]["domain"], MACHINE_DOMAIN)
            self.assert_item_snapshot(event["oldParent"], root_id)
            self.assert_item_snapshot(event["newParent"], new_parent.id)
            self.assert_actor(event)

            self.mqtt.drain()
            self.machineDesignApi.move_machine(child.id, root_id)
            event = self.wait_for_move(child.id, root_id)
            self.assert_item_snapshot(event["oldParent"], new_parent.id)
            self.assert_item_snapshot(event["newParent"], root_id)
            self.assert_actor(event)
        finally:
            for machine_id in reversed(created):
                self.remove_machine_quietly(machine_id)

    def find_represented_element(self):
        """Return the E1 element id of the represented machine's parent assembly."""
        hierarchy = self.machineDesignApi.get_housing_hierarchy_by_id(
            self.MACHINE_TO_REPRESENT_ID)
        parent_id = None
        child = hierarchy.child_items[0] if hierarchy.child_items else None
        while child is not None:
            if child.item.id == self.MACHINE_TO_REPRESENT_ID:
                parent_id = hierarchy.item.id
                break
            hierarchy = child
            child = hierarchy.child_items[0] if hierarchy.child_items else None
        self.assertIsNotNone(parent_id, msg="Parent of represented machine not found")

        parent = self.machineDesignApi.get_machine_design_item_by_id(parent_id)
        assembly = self.itemApi.get_item_hierarchy_by_id(parent.assigned_item.id)
        for element in assembly.child_items:
            if element.element_name == self.ELEMENT_NAME_TO_REPRESENT:
                return element.element_id
        self.fail("Element %s not found" % self.ELEMENT_NAME_TO_REPRESENT)

    def set_represented_element(self, machine_id, element_id):
        return self.machineDesignApi.update_representing_assembly_element_for_machine(
            RepresentingAssemblyElementForMachineInformation(
                machine_id=machine_id, assembly_element_id=element_id))

    def get_represented_element_id(self, machine_id):
        machine = self.machineDesignApi.get_machine_design_item_by_id(machine_id)
        element = machine.assigned_represented_element
        return element.id if element else None

    def test_machine_represented_element_set_clear(self):
        machine_id = self.MACHINE_TO_REPRESENT_ID
        element_id = self.find_represented_element()
        restore_id = self.get_represented_element_id(machine_id)

        self.loginAsAdmin()
        try:
            if restore_id == element_id:
                self.set_represented_element(machine_id, None)
            old_id = None if restore_id == element_id else restore_id

            self.mqtt.drain()
            self.set_represented_element(machine_id, element_id)
            event = self.mqtt.wait_for(
                TOPIC_MD_REPRESENTED_ELEMENT,
                all_of(id_at("machine.id", machine_id),
                       id_at("newRepresentedElement.id", element_id)),
                description="machine %s represents element %s" % (machine_id, element_id))
            self.assert_item_snapshot(event["machine"], machine_id)
            if old_id is None:
                self.assertIsNone(event.get("oldRepresentedElement"))
            else:
                self.assertEqual(str(event["oldRepresentedElement"]["id"]), str(old_id))
            new_element = event["newRepresentedElement"]
            self.assertEqual(new_element["name"], self.ELEMENT_NAME_TO_REPRESENT)
            self.assertIsNotNone(new_element.get("parentItem"))
            self.assert_actor(event)

            self.mqtt.drain()
            self.set_represented_element(machine_id, None)
            event = self.mqtt.wait_for(
                TOPIC_MD_REPRESENTED_ELEMENT,
                all_of(id_at("machine.id", machine_id),
                       id_at("oldRepresentedElement.id", element_id)),
                description="machine %s clears element %s" % (machine_id, element_id))
            self.assertIsNone(event.get("newRepresentedElement"))
            self.assert_actor(event)
        finally:
            if self.get_represented_element_id(machine_id) != restore_id:
                self.set_represented_element(machine_id, restore_id)

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


    # -- item log events ---------------------------------------------------

    def add_item_log(self, item_id, text):
        return self.itemApi.add_log_entry_to_item(
            log_entry_edit_information=LogEntryEditInformation(item_id=item_id, log_entry=text))

    def delete_item_log_quietly(self, item_id, log_id):
        try:
            self.itemApi.delete_log_entry_from_item(item_id, log_id)
        except OpenApiException:
            pass

    def wait_for_log_event(self, topic, item_id, log_path, log_id):
        return self.mqtt.wait_for(
            topic, all_of(id_at("itemId", item_id), id_at(log_path, log_id)),
            description="log %s on item %s" % (log_id, item_id))

    def test_item_log_add_update_delete(self):
        item_id = self.CATALOG_ITEM_ID
        text = "MQTT log %s" % self.gen_unique_name()
        updated_text = text + "\nsecond line"
        self.loginAsAdmin()
        log_id = None
        try:
            self.mqtt.drain()
            log_id = self.add_item_log(item_id, text).id
            event = self.wait_for_log_event(TOPIC_LOG_ADD, item_id, "newLog.logId", log_id)
            self.assertEqual(event["operation"], "add")
            self.assertIsNone(event.get("oldLog"))
            self.assertEqual(event["newLog"]["text"], text)
            self.assertFalse(event["newLog"]["textOmitted"])
            self.assertEqual(event["newLog"]["enteredByUsername"], self.ADMIN_USERNAME)
            self.assertIsNone(event.get("textDiff"))
            self.assertNotIn("attachmentList", event["newLog"])
            self.assert_actor(event)
            # A new entry is an add only, never also an update.
            self.mqtt.assert_no_message(
                TOPIC_LOG_UPDATE, id_at("newLog.logId", log_id),
                within=NO_EVENT_GRACE_SECONDS, description="update for new log %s" % log_id)

            self.mqtt.drain()
            self.itemApi.update_log_entry_for_item(
                log_id, log_entry_edit_information=LogEntryEditInformation(
                    item_id=item_id, log_entry=updated_text))
            event = self.wait_for_log_event(TOPIC_LOG_UPDATE, item_id, "newLog.logId", log_id)
            self.assertEqual(event["operation"], "update")
            self.assertEqual(str(event["oldLog"]["logId"]), str(log_id))
            self.assertEqual(event["textDiff"], "+second line\n")
            self.assertFalse(event["diffTruncated"])
            self.assertTrue(event["oldLog"]["textOmitted"])
            self.assertIsNone(event["newLog"].get("text"))
            self.assert_actor(event)

            self.mqtt.drain()
            self.itemApi.delete_log_entry_from_item(item_id, log_id)
            deleted_id, log_id = log_id, None
            event = self.wait_for_log_event(TOPIC_LOG_DELETE, item_id, "oldLog.logId", deleted_id)
            self.assertEqual(event["operation"], "delete")
            self.assertEqual(event["oldLog"]["text"], updated_text)
            self.assertIsNone(event.get("newLog"))
            self.assert_actor(event)
        finally:
            if log_id is not None:
                self.delete_item_log_quietly(item_id, log_id)

    def test_item_log_long_text_is_bounded(self):
        item_id = self.CATALOG_ITEM_ID
        limit = 16 * 1024
        text = ("MQTT long log %s " % self.gen_unique_name()).ljust(limit + 100, "x")
        self.loginAsAdmin()
        log_id = None
        try:
            self.mqtt.drain()
            log_id = self.add_item_log(item_id, text).id
            event = self.wait_for_log_event(TOPIC_LOG_ADD, item_id, "newLog.logId", log_id)
            self.assertEqual(event["newLog"]["text"], text[:limit])
            self.assertTrue(event["diffTruncated"])
        finally:
            if log_id is not None:
                self.delete_item_log_quietly(item_id, log_id)


    def test_item_log_attachment_add(self):
        item_id = self.CATALOG_ITEM_ID
        file_name = os.path.basename(self.SAMPLE_DOC_PATH)
        self.loginAsAdmin()
        log_id = None
        try:
            log_id = self.add_item_log(item_id, "MQTT attachment log %s" % self.gen_unique_name()).id
            self.wait_for_log_event(TOPIC_LOG_ADD, item_id, "newLog.logId", log_id)

            self.mqtt.drain()
            upload = self.factory.createFileUploadObject(self.SAMPLE_DOC_PATH)
            result = self.itemApi.upload_log_attachment_for_item(
                item_id, log_id, file_upload_object=upload)
            stored_name = result.attachment_list[-1].name
            event = self.mqtt.wait_for(
                TOPIC_LOG_ATTACHMENT_ADD,
                all_of(id_at("itemId", item_id), id_at("logId", log_id)),
                description="attachment on log %s" % log_id)
            self.assertEqual(event["operation"], "add")
            self.assertEqual(event["attachment"]["name"], stored_name)
            self.assertEqual(event["attachment"]["originalFilename"], file_name)
            # Scalar metadata only: no file bytes, DB id or path.
            self.assertEqual(set(event["attachment"]), {"name", "originalFilename"})
            self.assertNotIn("fileUploadObject", event)
            self.assert_actor(event)
            # Attachment-only edits are not log-entry updates, and the attachment is announced once.
            self.mqtt.assert_no_message(
                TOPIC_LOG_UPDATE, id_at("newLog.logId", log_id),
                within=NO_EVENT_GRACE_SECONDS, description="log update for attachment upload")
            self.mqtt.assert_no_message(
                TOPIC_LOG_ATTACHMENT_ADD, id_at("logId", log_id),
                within=NO_EVENT_GRACE_SECONDS, description="duplicate attachment event")
        finally:
            if log_id is not None:
                self.delete_item_log_quietly(item_id, log_id)


if __name__ == "__main__":
    unittest.main()
