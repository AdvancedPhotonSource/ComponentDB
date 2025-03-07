from datetime import datetime
import unittest
from cdbApi.exceptions import OpenApiException
from cdbApi.models import (
    ItemStatusBasicObject,
    SimpleLocationInformation,
    LogEntryEditInformation,
    ConciseItemOptions,
)

from test.cdb_test_base import CdbTestBase


class ItemTest(CdbTestBase):

    def test_fetching_item(self):
        result = self.itemApi.get_item_by_qr_id(self.INVENTORY_ITEM_QRID)
        self.assertNotEqual(result, None)
        self.assertEqual(result.qr_id, self.INVENTORY_ITEM_QRID)

        result = self.itemApi.get_item_by_id(self.INVENTORY_ITEM_ID)
        self.assertNotEqual(result, None)
        self.assertEqual(result.id, self.INVENTORY_ITEM_ID)

        result = self.itemApi.get_item_hierarchy_by_id(self.INVENTORY_ITEM_ID)
        result_item = result.item
        result_item_children = result.child_items

        self.assertEqual(
            result_item.id,
            self.INVENTORY_ITEM_ID,
            msg="Did not fetch the correct item with hierarchy",
        )
        self.assertEqual(
            result_item_children.__len__(),
            self.INVENTORY_ITEM_CHILDREN,
        )

    def test_update_contained_item(self):
        result = self.itemApi.get_item_hierarchy_by_id(self.INVENTORY_ITEM_ID)
        result_item_children = result.child_items

        for child_item in result_item_children:
            if child_item.element_name == self.INVENTORY_ITEM_ELEMENT_NAME:
                element_id = child_item.element_id

        self.assertEqual(
            self.verify_contained_item(result, self.INVENTORY_FIRST_CONTAINED_ITEM_ID),
            True,
            msg="Update already happened.",
        )

        self.loginAsAdmin()
        result = self.itemApi.update_contained_item(
            element_id, self.INVENTORY_FIRST_CONTAINED_NEW_ITEM_ID
        )
        self.assertNotEqual(result, None)
        result = self.itemApi.get_item_hierarchy_by_id(self.INVENTORY_ITEM_ID)
        self.assertEqual(
            self.verify_contained_item(
                result, self.INVENTORY_FIRST_CONTAINED_NEW_ITEM_ID
            ),
            True,
            msg="Contained item updated..",
        )

        failed = False
        try:
            self.itemApi.update_contained_item(
                element_id, self.INVENTORY_FIRST_CONTAINED_INVALID_ITEM_ID
            )
        except OpenApiException as ex:
            failed = True
        self.assertEqual(failed, True, msg="Updated contained item with invalid item.")

        result = self.itemApi.clear_contained_item(element_id)
        self.assertEqual(
            self.verify_contained_item(result, None),
            True,
            msg="Contained item failed to clear.",
        )

        result = self.itemApi.update_contained_item(
            element_id, self.INVENTORY_FIRST_CONTAINED_ITEM_ID
        )
        self.assertEqual(
            self.verify_contained_item(result, self.INVENTORY_FIRST_CONTAINED_ITEM_ID),
            True,
            msg="Failed to restore to original contained item.",
        )

    def test_get_items_derived_from_item(self):
        # Fetch inventory items for a catalog item.
        items = self.itemApi.get_items_derived_from_item_by_item_id(
            self.CATALOG_ITEM_ID
        )
        inventory_item_id = None

        for item in items:
            if item.id == self.INVENTORY_ITEM_ID:
                inventory_item_id = item.id
                break

        self.assertEqual(
            inventory_item_id,
            self.INVENTORY_ITEM_ID,
            msg="Could not find expected inventory item.",
        )

    def test_item_permissions(self):
        item_permission = self.itemApi.get_item_permissions(self.CATALOG_ITEM_ID)
        original_owner_user = item_permission.owner_user
        self.assertNotEqual(
            item_permission, None, msg="Could not fetch item permissions"
        )

        self.loginAsAdmin()

        new_owner_user = self.userApi.get_user_by_username(self.TEST_USER_USERNAME)
        item_permission.owner_user = new_owner_user
        self.itemApi.update_item_permission(item_permissions=item_permission)
        item_permission = self.itemApi.get_item_permissions(self.CATALOG_ITEM_ID)
        self.assertEqual(item_permission.owner_user, new_owner_user)

        # Update permissions back as a standard user
        self.loginAsUser()
        permission_result = self.itemApi.verify_user_permission_for_item(
            self.CATALOG_ITEM_ID
        )
        self.assertEqual(
            permission_result, True, msg="User does not have expected permission"
        )
        item_permission.owner_user = original_owner_user
        self.itemApi.update_item_permission(item_permissions=item_permission)
        item_permission = self.itemApi.get_item_permissions(self.CATALOG_ITEM_ID)
        self.assertEqual(item_permission.owner_user, original_owner_user)

        permission_result = self.itemApi.verify_user_permission_for_item(
            self.CATALOG_ITEM_ID
        )
        self.assertEqual(
            permission_result, False, msg="User does not have expected permission"
        )

    def test_item_status(self):
        status = self.itemApi.get_item_status(self.INVENTORY_ITEM_ID)
        self.assertNotEqual(
            status, None, msg="Could not fetch status for inventory item"
        )

        original_value = status.value

        self.loginAsAdmin()

        new_status = ItemStatusBasicObject(status="invalid status 123")
        failed = False
        try:
            self.itemApi.update_item_status(
                self.INVENTORY_ITEM_ID, item_status_basic_object=new_status
            )
        except OpenApiException as ex:
            failed = True
        self.assertEqual(failed, True, msg="Invalid status was allowed to be entered.")

        statusPropertyType = self.propertyTypeApi.get_inventory_status_property_type()
        new_status_value = None
        for allowed_value in statusPropertyType.sorted_allowed_property_value_list:
            allowed_value_value = allowed_value.value
            if allowed_value_value != original_value:
                new_status_value = allowed_value_value
                break

        self.assertNotEqual(
            new_status_value, None, msg="Different new value for status not found."
        )

        new_status = ItemStatusBasicObject(status=new_status_value)
        self.itemApi.update_item_status(
            self.INVENTORY_ITEM_ID, item_status_basic_object=new_status
        )
        status = self.itemApi.get_item_status(self.INVENTORY_ITEM_ID)
        self.assertEqual(status.value, new_status_value, msg="New status value not set")

        new_status = ItemStatusBasicObject(status=original_value)
        self.itemApi.update_item_status(
            self.INVENTORY_ITEM_ID, item_status_basic_object=new_status
        )
        status = self.itemApi.get_item_status(self.INVENTORY_ITEM_ID)
        self.assertEqual(status.value, original_value, msg="Status value not reverted")

    def test_update_location(self):
        locations = self.itemApi.get_locations_top_level()
        new_location = locations[0]

        location_info = self.itemApi.get_item_location(self.INVENTORY_ITEM_ID)
        original_location_item = location_info.location_item

        self.loginAsAdmin()

        # Update location
        location_info = SimpleLocationInformation(
            locatable_item_id=self.INVENTORY_ITEM_ID, location_item_id=new_location.id
        )
        self.itemApi.update_item_location(simple_location_information=location_info)
        location_info = self.itemApi.get_item_location(self.INVENTORY_ITEM_ID)
        self.assertEqual(
            location_info.location_item.id,
            new_location.id,
            msg="Location not updated successfully",
        )

        # Revert location
        location_info = SimpleLocationInformation(
            locatable_item_id=self.INVENTORY_ITEM_ID,
            location_item_id=original_location_item.id,
        )
        self.itemApi.update_item_location(simple_location_information=location_info)
        location_info = self.itemApi.get_item_location(self.INVENTORY_ITEM_ID)
        self.assertEqual(
            location_info.location_item.id,
            original_location_item.id,
            msg="Location not updated successfully",
        )

    def test_update_item_details(self):
        inventory_item = self.itemApi.get_item_by_id(self.INVENTORY_ITEM_ID)
        original_name = inventory_item.name

        self.loginAsAdmin()

        # Update item
        new_name = "Here is a new name test 123451111"
        inventory_item.name = new_name
        self.itemApi.update_item_details(item=inventory_item)

        refreshed_item = self.itemApi.get_item_by_id(self.INVENTORY_ITEM_ID)
        self.assertEqual(
            refreshed_item.name, new_name, msg="failed updating item detail."
        )

        # Revert item
        inventory_item.name = original_name
        self.itemApi.update_item_details(item=inventory_item)

        refreshed_item = self.itemApi.get_item_by_id(self.INVENTORY_ITEM_ID)
        self.assertEqual(
            refreshed_item.name, original_name, msg="failed updating item detail."
        )

    def test_add_log_entry(self):
        now = str(datetime.now())
        log_message = "TEST LOG entered on %s" % now

        log_entry = LogEntryEditInformation(
            item_id=self.INVENTORY_ITEM_ID, log_entry=log_message
        )

        self.loginAsAdmin()
        self.itemApi.add_log_entry_to_item(log_entry_edit_information=log_entry)

        logs = self.itemApi.get_logs_for_item(self.INVENTORY_ITEM_ID)

        entered_log = None
        for log in logs:
            if log.text == log_message:
                entered_log = log.text
                break

        self.assertEqual(entered_log, log_message, msg="Log entry was not found")

    def test_fetch_item_by_domain(self):
        domains = self.itemApi.get_domain_list1()
        for domain in domains:
            items = self.itemApi.get_items_by_domain(domain.name)
            self.assertNotEqual(
                items, None, msg="Failed fetching items of domain %s" % domain.name
            )

        self.itemApi.get_catalog_items()
        self.itemApi.get_inventory_items()

        failed = False
        try:
            self.itemApi.get_favorite_catalog_items()
        except OpenApiException as ex:
            failed = True

        self.assertEqual(
            failed, True, msg="No error on fetching favorite items not logged in"
        )
        self.loginAsAdmin()
        self.itemApi.get_favorite_catalog_items()

    def test_fetch_concise_item_list(self):
        # Test with no options
        inventory = self.itemApi.get_concise_inventory_items()
        first_inventory = inventory[0]
        derived_from_item_id = first_inventory.derived_from_item_id
        self.assertEqual(
            None,
            derived_from_item_id,
            msg="derived from item should not be set be default.",
        )

        options = ConciseItemOptions(include_derived_from_item_info=True)
        inventory = self.itemApi.get_concise_inventory_items(
            concise_item_options=options
        )
        first_inventory = inventory[0]
        derived_from_item_id = first_inventory.derived_from_item_id
        self.assertNotEqual(
            None,
            derived_from_item_id,
            msg="derived from item should be set be after passing in options.",
        )

        catalog_items = self.itemApi.get_concise_catalog_items()
        self.assertNotEqual(
            None, catalog_items, msg="failed to fetch concise catalog item list."
        )

        domains = self.itemApi.get_domain_list1()
        for domain in domains:
            items = self.itemApi.get_concise_items_by_domain(
                domain.name, concise_item_options=options
            )
            self.assertNotEqual(
                items, None, msg="Failed fetching items of domain %s" % domain.name
            )


if __name__ == "__main__":
    unittest.main()
