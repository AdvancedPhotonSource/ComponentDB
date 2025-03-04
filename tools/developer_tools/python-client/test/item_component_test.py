import unittest
from cdbApi.exceptions import OpenApiException
from cdbApi.models.new_catalog_element_information import (
    NewCatalogElementInformation,
    NewCatalogInformation,
    NewInventoryInformation,
)
from test.cdb_test_base import CdbTestBase


class ItemComponentTest(CdbTestBase):

    def test_create_catalog_item(self):
        self.loginAsAdmin()
        info = NewCatalogInformation(name=self.TEST_NEW_CATALOG_ITEM_NAME)
        failed = False

        try:
            new_catalog_item = self.componentCatalogApi.create_catalog(info)
        except OpenApiException as ex:
            failed = True

        self.assertEqual(failed, True, msg="Not all required arguments were passed in.")

        full_project_list = self.itemApi.get_item_project_list()
        info.item_projects_list = [full_project_list[0]]
        try:
            new_catalog_item = self.componentCatalogApi.create_catalog(info)
        except OpenApiException as ex:
            self.fail(msg=ex.body)

        self.assertNotEqual(
            new_catalog_item.id, None, msg="New catalog item wasn't created"
        )

    def test_create_catalog_element(self):
        self.loginAsAdmin()
        info = NewCatalogElementInformation(catalog_item_id=self.CATALOG_ITEM_ID)
        try:
            element = self.componentCatalogApi.add_catalog_element(
                self.CATALOG_ITEM_ID_2, info
            )
        except OpenApiException as ex:
            self.fail(msg="Failed to create catalog element. ")

        self.assertNotEqual(None, element.id, msg="No Element id was returned to user.")

        info.part_name = "TEST_E_NAME"
        try:
            element = self.componentCatalogApi.add_catalog_element(
                self.CATALOG_ITEM_ID_2, info
            )
        except OpenApiException as ex:
            self.fail(msg="Failed to create catalog element. ")

        self.assertEqual(
            info.part_name, element.name, msg="Custom part name was not set."
        )

    def test_create_inventory_item(self):
        self.loginAsAdmin()
        info = NewInventoryInformation(catalog_id=self.CATALOG_ITEM_ID)

        try:
            result = self.componentInventoryApi.create_inventory(info)
        except OpenApiException as ex:
            self.fail(msg=ex.body)

        self.assertNotEqual(result.id, None, msg="New inventory not returned.")

        info.tag = self.TEST_NEW_INVENTORY_ITEM_TAG

        try:
            result = self.componentInventoryApi.create_inventory(info)
        except OpenApiException as ex:
            self.fail(msg=ex.body)

        self.assertEqual(
            result.tag,
            self.TEST_NEW_INVENTORY_ITEM_TAG,
            msg="tag name kept from API entered.",
        )


if __name__ == "__main__":
    unittest.main()
