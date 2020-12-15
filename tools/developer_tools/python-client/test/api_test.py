import unittest

from CdbApiFactory import CdbApiFactory
from cdbApi import OpenApiException


class MyTestCase(unittest.TestCase):
    TEST_USER_USERNAME = "testUser"

    def setUp(self):
        self.factory = CdbApiFactory('http://127.0.0.1:8080/cdb')
        # TESTS AS ADMIN / TESTS AS USER
        self.itemApi = self.factory.itemApi
        self.locationApi = self.factory.locationItemApi

    def tearDown(self):
        self.factory.logOutUser()

    def loginAsAdmin(self):
        self.factory.authenticateUser('cdb', 'cdb')

    def loginAsUser(self):
        self.factory.authenticateUser(self.TEST_USER_USERNAME, 'cdb')

    def is_item_parent(self, parent, child):
        '''
        Looks for parent in first level of location hierarchy, verifies if child is part of that parent.

        :param parent:
        :param child:
        :return: Boolean that specifies if child is found.
        '''

        location_hierarchy = self.itemApi.get_location_hierarchy()
        for location in location_hierarchy:
            location_item = location.item
            if parent.id == location_item.id:
                for child_location in location.child_items:
                    child_location_item = child_location.item
                    if child.id == child_location_item.id:
                        return True

        return False

    def test_update_location_parent_as_admin(self):
        # Fetch two arbitrary locations
        location_hierarchy = self.itemApi.get_location_hierarchy()

        newParent = location_hierarchy[0].item
        newParent2 = location_hierarchy[1].item
        updateLocation = location_hierarchy[2].item

        self.loginAsAdmin()

        self.locationApi.update_location_parent(updateLocation.id, newParent.id)
        self.assertEqual(True, self.is_item_parent(newParent, updateLocation),
                         msg='Moving a top level location into existing location')

        self.locationApi.update_location_parent(updateLocation.id, newParent2.id)
        self.assertEqual(True, self.is_item_parent(newParent2, updateLocation),
                         msg='Moving child location to another parent.')

    def test_update_location_parent_as_user(self):
        self.loginAsUser()

        location_with_permissions = self.itemApi.get_item_by_qr_id(999111500)
        permission_result = self.itemApi.verify_user_permission_for_item(location_with_permissions.id)
        self.assertEqual(permission_result, True, msg="No Permission to location owned by %s" % self.TEST_USER_USERNAME)

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

        self.assertEqual(False, self.is_item_parent(new_parent, location_with_permissions),
                         msg='The update already happened.')
        self.locationApi.update_location_parent(location_with_permissions.id, new_parent.id)
        self.assertEqual(True, self.is_item_parent(new_parent, location_with_permissions),
                         msg='The update was successful')

        # Try update parent of item that user doesnt have permission to
        failed = False
        try:
            self.locationApi.update_location_parent(location_without_permissions.id, new_parent.id)
        except OpenApiException as ex:
            failed = True

        self.assertEqual(failed, True, msg="Exception not thrown for item with no permissions")
        self.assertEqual(False, self.is_item_parent(location_without_permissions, new_parent))


if __name__ == '__main__':
    unittest.main()
