import unittest
from datetime import datetime

from CdbApiFactory import CdbApiFactory
from cdbApi import OpenApiException, ItemStatusBasicObject, NewLocationInformation, SimpleLocationInformation, \
    LogEntryEditInformation, PropertyValue, PropertyMetadata


class MyTestCase(unittest.TestCase):
    TEST_USER_USERNAME = "testUser"
    TEST_USER_PASSWORD = 'cdb'
    ADMIN_USERNAME = 'cdb'
    ADMIN_PASSWORD = 'cdb'
    CATALOG_ITEM_ID = 2
    INVENTORY_ITEM_ID = 56
    INVENTORY_ITEM_QRID = 1010001
    INVENTORY_ITEM_CHILDREN = 3
    INVENTORY_FIRST_CONTAINED_ITEM_ID = 45
    INVENTORY_FIRST_CONTAINED_NEW_ITEM_ID = 41
    INVENTORY_FIRST_CONTAINED_INVALID_ITEM_ID = 97
    MACHINE_DESIGN_ID = 93
    TEST_PROPERTY_TYPE_NAME = "Test Property"
    LOCATION_QRID_TESTUSER_PERMISSIONS = 101111101
    SAMPLE_IMAGE_PATH = './data/AnlLogo.png'
    SAMPLE_DOC_PATH = './data/CdbSchema-v3.0-3.pdf'

    def setUp(self):
        self.factory = CdbApiFactory('http://127.0.0.1:8080/cdb')
        # TESTS AS ADMIN / TESTS AS USER
        self.itemApi = self.factory.itemApi
        self.locationApi = self.factory.locationItemApi
        self.userApi = self.factory.usersApi
        self.cableCatalogApi = self.factory.cableCatalogItemApi
        self.machineDesignApi = self.factory.machineDesignItemApi
        self.propertyTypeApi = self.factory.propertyTypeApi
        self.propertyValueApi = self.factory.propertyValueApi
        self.domainApi = self.factory.domainApi
        self.logApi = self.factory.logApi
        self.loggedIn = False

    def tearDown(self):
        if self.loggedIn:
            self.factory.logOutUser()

    def loginAsAdmin(self):
        if self.loggedIn:
            self.factory.logOutUser()
        self.loggedIn = True
        self.factory.authenticateUser(self.ADMIN_USERNAME, self.ADMIN_PASSWORD)

    def loginAsUser(self):
        if self.loggedIn:
            self.factory.logOutUser()
        self.loggedIn = True
        self.factory.authenticateUser(self.TEST_USER_USERNAME, self.TEST_USER_PASSWORD)

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

        location_with_permissions = self.itemApi.get_item_by_qr_id(self.LOCATION_QRID_TESTUSER_PERMISSIONS)
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

        self.assertEqual(result_item.id, self.INVENTORY_ITEM_ID, msg="Did not fetch the correct item with hierarchy")
        self.assertEqual(result_item_children.__len__(), self.INVENTORY_ITEM_CHILDREN, )

    def verify_contained_item(self, item_hierarchy_object, expected_contained_item):
        result_item_children = item_hierarchy_object.child_items
        item = result_item_children[0].item
        if item is not None:
            contained_item_id = item.id
            return expected_contained_item == item.id
        return expected_contained_item is None

    def test_update_contained_item(self):
        result = self.itemApi.get_item_hierarchy_by_id(self.INVENTORY_ITEM_ID)
        result_item_children = result.child_items

        element_id = result_item_children[0].element_id

        self.assertEqual(self.verify_contained_item(result, self.INVENTORY_FIRST_CONTAINED_ITEM_ID),
                         True, msg='Update already happened.')

        self.loginAsAdmin()
        result = self.itemApi.update_contained_item(element_id, self.INVENTORY_FIRST_CONTAINED_NEW_ITEM_ID)
        self.assertNotEqual(result, None)
        result = self.itemApi.get_item_hierarchy_by_id(self.INVENTORY_ITEM_ID)
        self.assertEqual(self.verify_contained_item(result, self.INVENTORY_FIRST_CONTAINED_NEW_ITEM_ID),
                         True, msg='Contained item updated..')

        failed = False
        try:
            self.itemApi.update_contained_item(element_id, self.INVENTORY_FIRST_CONTAINED_INVALID_ITEM_ID)
        except OpenApiException as ex:
            failed = True
        self.assertEqual(failed, True, msg='Updated contained item with invalid item.')

        result = self.itemApi.clear_contained_item(element_id)
        self.assertEqual(self.verify_contained_item(result, None),
                         True, msg='Contained item failed to clear.')

        result = self.itemApi.update_contained_item(element_id, self.INVENTORY_FIRST_CONTAINED_ITEM_ID)
        self.assertEqual(self.verify_contained_item(result, self.INVENTORY_FIRST_CONTAINED_ITEM_ID),
                         True, msg='Failed to restore to original contained item.')

    def test_get_items_derived_from_item(self):
        # Fetch inventory items for a catalog item.
        items = self.itemApi.get_items_derived_from_item_by_item_id(self.CATALOG_ITEM_ID)
        inventory_item_id = None

        for item in items:
            if item.id == self.INVENTORY_ITEM_ID:
                inventory_item_id = item.id
                break

        self.assertEqual(inventory_item_id, self.INVENTORY_ITEM_ID, msg='Could not find expected inventory item.')

    def test_item_permissions(self):
        item_permission = self.itemApi.get_item_permissions(self.CATALOG_ITEM_ID)
        original_owner_user = item_permission.owner_user
        self.assertNotEqual(item_permission, None, msg="Could not fetch item permissions")

        self.loginAsAdmin()

        new_owner_user = self.userApi.get_user_by_username(self.TEST_USER_USERNAME)
        item_permission.owner_user = new_owner_user
        self.itemApi.update_item_permission(item_permissions=item_permission)
        item_permission = self.itemApi.get_item_permissions(self.CATALOG_ITEM_ID)
        self.assertEqual(item_permission.owner_user, new_owner_user)

        # Update permissions back as a standard user
        self.loginAsUser()
        permission_result = self.itemApi.verify_user_permission_for_item(self.CATALOG_ITEM_ID)
        self.assertEqual(permission_result, True, msg='User does not have expected permission')
        item_permission.owner_user = original_owner_user
        self.itemApi.update_item_permission(item_permissions=item_permission)
        item_permission = self.itemApi.get_item_permissions(self.CATALOG_ITEM_ID)
        self.assertEqual(item_permission.owner_user, original_owner_user)

        permission_result = self.itemApi.verify_user_permission_for_item(self.CATALOG_ITEM_ID)
        self.assertEqual(permission_result, False, msg='User does not have expected permission')

    def test_item_status(self):
        status = self.itemApi.get_item_status(self.INVENTORY_ITEM_ID)
        self.assertNotEqual(status, None, msg='Could not fetch status for inventory item')

        original_value = status.value

        self.loginAsAdmin()

        new_status = ItemStatusBasicObject(status='invalid status 123')
        failed = False
        try:
            self.itemApi.update_item_status(self.INVENTORY_ITEM_ID, item_status_basic_object=new_status)
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

        self.assertNotEqual(new_status_value, None, msg='Different new value for status not found.')

        new_status = ItemStatusBasicObject(status=new_status_value)
        self.itemApi.update_item_status(self.INVENTORY_ITEM_ID, item_status_basic_object=new_status)
        status = self.itemApi.get_item_status(self.INVENTORY_ITEM_ID)
        self.assertEqual(status.value, new_status_value, msg='New status value not set')

        new_status = ItemStatusBasicObject(status=original_value)
        self.itemApi.update_item_status(self.INVENTORY_ITEM_ID, item_status_basic_object=new_status)
        status = self.itemApi.get_item_status(self.INVENTORY_ITEM_ID)
        self.assertEqual(status.value, original_value, msg='Status value not reverted')

    def test_update_location(self):
        locations = self.itemApi.get_locations_top_level()
        new_location = locations[0]

        location_info = self.itemApi.get_item_location(self.INVENTORY_ITEM_ID)
        original_location_item = location_info.location_item

        self.loginAsAdmin()

        # Update location
        location_info = SimpleLocationInformation(locatable_item_id=self.INVENTORY_ITEM_ID,
                                                  location_item_id=new_location.id)
        self.itemApi.update_item_location(simple_location_information=location_info)
        location_info = self.itemApi.get_item_location(self.INVENTORY_ITEM_ID)
        self.assertEqual(location_info.location_item.id, new_location.id, msg="Location not updated successfully")

        # Revert location
        location_info = SimpleLocationInformation(locatable_item_id=self.INVENTORY_ITEM_ID,
                                                  location_item_id=original_location_item.id)
        self.itemApi.update_item_location(simple_location_information=location_info)
        location_info = self.itemApi.get_item_location(self.INVENTORY_ITEM_ID)
        self.assertEqual(location_info.location_item.id, original_location_item.id, msg="Location not updated successfully")

    def test_create_and_delete_location(self):
        locInfo = NewLocationInformation(location_name='TEST_LOCATION', location_type='Building')
        self.loginAsAdmin()
        result = self.itemApi.create_location(new_location_information=locInfo)
        self.assertNotEqual(result, None, msg="Error creating a location.")
        location_id = result.id

        # Test fetching the new location.
        fetched = self.itemApi.get_item_by_id(location_id)
        self.assertEqual(result.id, fetched.id, msg='Fetched location doesn\'t match the created one.')

        # Delete the newly created Location
        self.itemApi.delete_item_by_id(location_id)
        failed = False
        try:
            self.itemApi.get_item_by_id(location_id)
        except OpenApiException as ex:
            failed = True

        self.assertEqual(failed, True, msg='Failed to remove item.')

    def test_update_item_details(self):
        inventory_item = self.itemApi.get_item_by_id(self.INVENTORY_ITEM_ID)
        original_name = inventory_item.name

        self.loginAsAdmin()

        # Update item
        new_name = 'Here is a new name test 123451111'
        inventory_item.name = new_name
        self.itemApi.update_item_details(item=inventory_item)

        refreshed_item = self.itemApi.get_item_by_id(self.INVENTORY_ITEM_ID)
        self.assertEqual(refreshed_item.name, new_name, msg='failed updating item detail.')

        # Revert item
        inventory_item.name = original_name
        self.itemApi.update_item_details(item=inventory_item)

        refreshed_item = self.itemApi.get_item_by_id(self.INVENTORY_ITEM_ID)
        self.assertEqual(refreshed_item.name, original_name, msg='failed updating item detail.')

    def test_update_item_property(self):
        # Try adding status
        status_type = self.propertyTypeApi.get_inventory_status_property_type()

        property_value = PropertyValue(property_type=status_type, value="Unknown")
        failed = False

        self.loginAsAdmin()

        try:
            self.itemApi.add_item_property_value(self.INVENTORY_ITEM_ID, property_value=property_value)
        except OpenApiException as ex:
            failed = True
        self.assertEqual(failed, True, msg="Adding status should be prevented")

        # Try adding a new property value
        test_property_type = self.propertyTypeApi.get_property_type_by_name(self.TEST_PROPERTY_TYPE_NAME)
        property_value = PropertyValue(property_type=test_property_type, value="Test Val")
        property_value = self.itemApi.add_item_property_value(self.INVENTORY_ITEM_ID, property_value=property_value)

        self.assertNotEqual(property_value, None, msg='Property value not returned.')

        property_id = property_value.id

        properties = self.itemApi.get_properties_for_item(self.INVENTORY_ITEM_ID)
        found = False
        for property in properties:
            if property_id == property.id:
                found = True
                break

        self.assertEqual(found, True, msg='Could not find new property in item properties.')

        # Try updating property value
        new_value = 'updatedValue'
        property_value.value = new_value
        property_value = self.itemApi.update_item_property_value(self.INVENTORY_ITEM_ID, property_value=property_value)
        self.assertEqual(property_value.value, new_value, msg='Failed to update value of property.')

        # Try updating property metadata
        metadata = PropertyMetadata(metadata_key='test', metadata_value='test')
        result = self.itemApi.update_item_property_metadata(self.INVENTORY_ITEM_ID, property_id,
                                                   property_metadata=metadata)
        self.assertNotEqual(result, None, msg='No value for updating metadata')

        metadata = self.propertyValueApi.get_property_value_metadata(property_id)
        self.assertNotEqual(metadata, None)

    def test_add_log_entry(self):
        now = str(datetime.now())
        log_message = "TEST LOG entered on %s" % now

        log_entry = LogEntryEditInformation(item_id=self.INVENTORY_ITEM_ID, log_entry=log_message)

        self.loginAsAdmin()
        self.itemApi.add_log_entry_to_item(log_entry_edit_information=log_entry)

        logs = self.itemApi.get_logs_for_item(self.INVENTORY_ITEM_ID)

        entered_log = None
        for log in logs:
            if log.text == log_message:
                entered_log = log.text
                break

        self.assertEqual(entered_log, log_message, msg="Log entry was not found")

    def test_upload_document(self):
        self.loginAsAdmin()
        upload = self.factory.createFileUploadObject(self.SAMPLE_DOC_PATH)
        self.itemApi.upload_image_for_item(self.INVENTORY_ITEM_ID, file_upload_object=upload)

    def test_upload_image(self):
        self.loginAsAdmin()
        upload = self.factory.createFileUploadObject(self.SAMPLE_IMAGE_PATH)
        self.itemApi.upload_image_for_item(self.INVENTORY_ITEM_ID, file_upload_object=upload)

    def test_fetch_item_by_domain(self):
        domains = self.itemApi.get_domain_list1()
        for domain in domains:
            items = self.itemApi.get_items_by_domain(domain.name)
            self.assertNotEqual(items, None, msg="Failed fetching items of domain %s" % domain.name)

        self.itemApi.get_catalog_items()
        self.itemApi.get_inventory_items()

        failed=False
        try:
            self.itemApi.get_favorite_catalog_items()
        except OpenApiException as ex:
            failed = True

        self.assertEqual(failed, True, msg='No error on fetching favorite items not logged in')
        self.loginAsAdmin()
        self.itemApi.get_favorite_catalog_items()

    def test_search(self):
        result = self.itemApi.get_detailed_catalog_search_results('Test')
        self.assertNotEqual(result, None, msg='Failed searching catalog')

        results = self.itemApi.get_search_results("Test")
        self.assertNotEqual(results, None, msg='Failed searching catalog')

    def test_cable_api(self):
        self.cableCatalogApi.get_cable_catalog_item_list()

    def test_domain_api(self):
        domains = self.domainApi.get_domain_list()
        self.assertNotEqual(domains, None)

        domain_id = domains[0].id
        domain_by_id = self.domainApi.get_domain_by_id(domain_id)
        self.assertNotEqual(domain_by_id, None)

    def test_log_api(self):
        self.loginAsUser()
        fail = False
        try:
            result = self.logApi.get_successful_login_log()
        except OpenApiException as ex:
            fail = True
        self.assertEqual(fail, True)

        self.loginAsAdmin()
        result = self.logApi.get_successful_login_log()
        self.assertNotEqual(result, None)

    def test_machine_api(self):
        items = self.machineDesignApi.get_machine_design_item_list()
        self.assertNotEqual(items, None)

        md_id = items[0].id
        md_item_by_id = self.machineDesignApi.get_machine_design_item_by_id(md_id)
        self.assertNotEqual(md_item_by_id, None)

    def test_md_update_assigned_item(self):
        self.loginAsAdmin()

        result = self.machineDesignApi.update_assigned_item(self.MACHINE_DESIGN_ID, self.INVENTORY_ITEM_ID)
        self.assertNotEqual(result, None)

        # revert back
        result = self.machineDesignApi.update_assigned_item(self.MACHINE_DESIGN_ID, self.CATALOG_ITEM_ID)
        self.assertNotEqual(result, None)

    def test_user_route(self):
        users = self.userApi.get_all1()
        self.assertNotEqual(users, None)

        username = users[0].username
        user_by_username = self.userApi.get_user_by_username(username)

        self.assertEqual(user_by_username.username, username)

if __name__ == '__main__':
    unittest.main()
