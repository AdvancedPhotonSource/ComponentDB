import unittest
import uuid

from CdbApiFactory import CdbApiFactory


class CdbTestBase(unittest.TestCase):
    TEST_USER_USERNAME = "testUser"
    TEST_USER_PASSWORD = "cdb"
    ADMIN_USERNAME = "cdb"
    ADMIN_PASSWORD = "cdb"
    PROJECT_ID = 1
    CATALOG_ITEM_ID = 2
    CATALOG_ITEM_ID_2 = 18
    INVENTORY_ITEM_ID = 56
    INVENTORY_ITEM_ELEMENT_NAME = "E1"
    INVENTORY_ITEM_QRID = 1010001
    INVENTORY_ITEM_CHILDREN = 3
    INVENTORY_FIRST_CONTAINED_ITEM_ID = 45
    INVENTORY_FIRST_CONTAINED_NEW_ITEM_ID = 41
    INVENTORY_FIRST_CONTAINED_INVALID_ITEM_ID = 97
    LOCATION_WITH_INVENTORY_ID = "89"
    LOC_BUILDING_1_ID = 89
    LOC_ROOM_104_ID = 91
    MACHINE_DESIGN_ID = 93
    MACHINE_DESIGN_PARENT_ID = 94
    MACHINE_DESIGN_CHILD_ID = 95
    MACHINE_TO_REPRESENT_ID = 112
    CABLE_DESIGN_ITEM_ID = 110
    CABLE_DESIGN_ITEM_NAME = "Test Cable Design"
    CABLE_DESIGN_WITH_ENDPOINTS_METADATA_ITEM_ID = 125
    CABLE_DESIGN_ENDPOINT_IDS = [
        MACHINE_DESIGN_PARENT_ID,
        MACHINE_DESIGN_CHILD_ID,
    ]
    MAARC_DOMAIN_NAME = "MAARC"
    MAARC_ITEM_ID = 119
    ELEMENT_NAME_TO_REPRESENT = "E1"
    INVALID_CONTROL_INTERFACE = "Some other interface"
    MD_CONTROL_NAME_TOP_LEVEL = "Top Control Node"
    MD_CONTROL_NAME_TOP_LEVEL_2 = "Another Control Node"
    MD_CONTROL_NAME = "ioctest"
    MD_CONTROL_INTERFACE_TO_PARENT = "Direct Connection"
    MD_CONTROL_EXPECTED_PARENT_ID = 109
    MD_CREATED_FROM_TEMPLATE_ID = 118
    TEST_PROPERTY_TYPE_NAME = "Test Property"
    CONTROL_INTERFACE_PROPERTY_TYPE_ID = "14"
    LOCATION_QRID_TESTUSER_PERMISSIONS = 101111101
    TEST_NEW_CATALOG_ITEM_NAME = "new catalog from test"
    TEST_NEW_INVENTORY_ITEM_TAG = "TEST_TAG"
    SAMPLE_IMAGE_PATH = "./data/AnlLogo.png"
    SAMPLE_DOC_PATH = "./data/CdbSchema-v3.0-3.pdf"
    APP_DOMAIN_NAME = "App"
    NEW_APP_NAME = "Test app"
    NEW_APP_DESCRIPTION = "Created from API"
    CABLE_TYPE_WITH_CONNECTORS_ITEM_ID = 71

    @classmethod
    def setUpClass(cls):
        cls.factory = CdbApiFactory("http://127.0.0.1:8080/cdb")
        cls.loggedIn = False

    def setUp(self):
        # TESTS AS ADMIN / TESTS AS USER
        self.itemApi = self.factory.itemApi
        self.locationApi = self.factory.locationItemApi
        self.userApi = self.factory.usersApi
        self.cableCatalogApi = self.factory.cableCatalogItemApi
        self.cableDesignApi = self.factory.cableDesignItemApi
        self.machineDesignApi = self.factory.machineDesignItemApi
        self.componentCatalogApi = self.factory.componentCatalogItemApi
        self.componentInventoryApi = self.factory.componentInventoryItemApi
        self.maarcApi = self.factory.maarcItemApi
        self.propertyTypeApi = self.factory.propertyTypeApi
        self.propertyValueApi = self.factory.propertyValueApi
        self.domainApi = self.factory.domainApi
        self.appApi = self.factory.appItemApi
        self.logApi = self.factory.logApi
        self.searchApi = self.factory.searchApi

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

    def gen_unique_name(self):
        return uuid.uuid4().hex[:10]

    def assert_exception_message(self, exception, message):
        parsedException = self.factory.parseApiException(exception)
        self.assertEqual(parsedException.message, message)

    def is_item_parent(self, parent, child):
        """
        Looks for parent in first level of location hierarchy, verifies if child is part of that parent.

        :param parent:
        :param child:
        :return: Boolean that specifies if child is found.
        """

        location_hierarchy = self.itemApi.get_location_hierarchy()
        for location in location_hierarchy:
            location_item = location.item
            if parent.id == location_item.id:
                for child_location in location.child_items:
                    child_location_item = child_location.item
                    if child.id == child_location_item.id:
                        return True

        return False

    def verify_contained_item(self, item_hierarchy_object, expected_contained_item):
        result_item_children = item_hierarchy_object.child_items
        if result_item_children.__len__() != 0:
            for result_child in result_item_children:
                item = result_child.item
                if item is not None:
                    contained_item_id = item.id
                    if expected_contained_item == contained_item_id:
                        return True
        return expected_contained_item is None
