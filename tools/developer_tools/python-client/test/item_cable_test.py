import unittest
from cdbApi.exceptions import OpenApiException
from cdbApi.models import CableDesignConnectionListObject
from test.cdb_test_base import CdbTestBase


class ItemCableTest(CdbTestBase):

    def test_cable_api(self):
        self.cableCatalogApi.get_cable_catalog_item_list()

    def test_get_cable_design_connections(self):
        results: list[CableDesignConnectionListObject] = (
            self.cableDesignApi.get_cable_design_connection_list(
                self.CABLE_DESIGN_ITEM_ID
            )
        )

        endpoint_ids = [self.MACHINE_DESIGN_PARENT_ID, self.MACHINE_DESIGN_CHILD_ID]

        for connection in results:
            self.assertIn(
                connection.md_item.id, endpoint_ids, msg="Unexpected endpoint"
            )
            self.assertEqual(connection.cable_design.id, self.CABLE_DESIGN_ITEM_ID)


if __name__ == "__main__":
    unittest.main()
