import unittest
from cdbApi.exceptions import OpenApiException
from cdbApi.models import NewLocationInformation, ItemDomainInventoryBase
from test.cdb_test_base import CdbTestBase


class ItemLocationTest(CdbTestBase):

    def test_update_location_parent_as_admin(self):
        # Fetch two arbitrary locations
        location_hierarchy = self.itemApi.get_location_hierarchy()

        newParent = location_hierarchy[0].item
        newParent2 = location_hierarchy[1].item
        updateLocation = location_hierarchy[2].item

        self.loginAsAdmin()

        self.locationApi.update_location_parent(updateLocation.id, newParent.id)
        self.assertEqual(
            True,
            self.is_item_parent(newParent, updateLocation),
            msg="Moving a top level location into existing location",
        )

        self.locationApi.update_location_parent(updateLocation.id, newParent2.id)
        self.assertEqual(
            True,
            self.is_item_parent(newParent2, updateLocation),
            msg="Moving child location to another parent.",
        )

    def test_update_location_parent_as_user(self):
        self.loginAsUser()

        location_with_permissions = self.itemApi.get_item_by_qr_id(
            self.LOCATION_QRID_TESTUSER_PERMISSIONS
        )
        permission_result = self.itemApi.verify_user_permission_for_item(
            location_with_permissions.id
        )
        self.assertEqual(
            permission_result,
            True,
            msg="No Permission to location owned by %s" % self.TEST_USER_USERNAME,
        )

        location_hierarchy = self.itemApi.get_location_hierarchy()
        new_parent = None
        location_without_permissions = None
        for location in location_hierarchy:
            location_item = location.item
            permission = self.itemApi.verify_user_permission_for_item(location_item.id)

            if not permission and new_parent is None:
                new_parent = location_item
                continue

            if not permission and location_without_permissions is None:
                location_without_permissions = location_item
                break

        self.assertEqual(
            False,
            self.is_item_parent(new_parent, location_with_permissions),
            msg="The update already happened.",
        )
        self.locationApi.update_location_parent(
            location_with_permissions.id, new_parent.id
        )
        self.assertEqual(
            True,
            self.is_item_parent(new_parent, location_with_permissions),
            msg="The update was successful",
        )

        # Try update parent of item that user doesnt have permission to
        failed = False
        try:
            self.locationApi.update_location_parent(
                location_without_permissions.id, new_parent.id
            )
        except OpenApiException as ex:
            failed = True

        self.assertEqual(
            failed, True, msg="Exception not thrown for item with no permissions"
        )
        self.assertEqual(
            False, self.is_item_parent(location_without_permissions, new_parent)
        )

    def test_create_and_delete_location(self):
        locInfo = NewLocationInformation(
            location_name="TEST_LOCATION", location_type="Building"
        )
        self.loginAsAdmin()
        result = self.itemApi.create_location(new_location_information=locInfo)
        self.assertNotEqual(result, None, msg="Error creating a location.")
        location_id = result.id

        # Test fetching the new location.
        fetched = self.itemApi.get_item_by_id(location_id)
        self.assertEqual(
            result.id, fetched.id, msg="Fetched location doesn't match the created one."
        )

        # Delete the newly created Location
        self.itemApi.delete_item_by_id(location_id)
        failed = False
        try:
            self.itemApi.get_item_by_id(location_id)
        except OpenApiException as ex:
            failed = True

        self.assertEqual(failed, True, msg="Failed to remove item.")

    def test_get_inventory_located_here(self):
        results: list[ItemDomainInventoryBase] = (
            self.locationApi.get_inventory_located_here(self.LOCATION_WITH_INVENTORY_ID)
        )

        inventory_here = [
            self.INVENTORY_ITEM_ID,
            self.INVENTORY_FIRST_CONTAINED_ITEM_ID,
            self.INVENTORY_FIRST_CONTAINED_NEW_ITEM_ID,
        ]
        for item in results:
            self.assertIn(
                item.id, inventory_here, msg="Unexpected item located in location"
            )


if __name__ == "__main__":
    unittest.main()
