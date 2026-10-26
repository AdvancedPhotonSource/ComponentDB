import unittest
from cdbApi.exceptions import OpenApiException
from cdbApi.models import SearchEntitiesOptions, SearchEntitiesResults
from test.cdb_test_base import CdbTestBase


class SearchTest(CdbTestBase):

    def test_item_search_api(self):
        result = self.itemApi.get_detailed_catalog_search_results("Test")
        self.assertNotEqual(result, None, msg="Failed searching catalog")

        results = self.itemApi.get_search_results("Test")
        self.assertNotEqual(results, None, msg="Failed searching catalog")

    def test_search(self):
        search_opts = SearchEntitiesOptions(
            search_text="cdb",
            include_catalog=True,
            include_inventory=True,
            include_machine_design=True,
            include_cable_catalog=True,
            include_cable_inventory=True,
            include_cable_design=True,
        )

        result: SearchEntitiesResults = self.searchApi.search_entities(search_opts)

        self.assertNotEqual(None, result.item_domain_catalog_results)
        self.assertNotEqual(None, result.item_domain_inventory_results)
        self.assertNotEqual(None, result.item_domain_machine_design_results)
        self.assertNotEqual(None, result.item_domain_cable_catalog_results)
        self.assertNotEqual(None, result.item_domain_cable_inventory_results)
        self.assertNotEqual(None, result.item_domain_cable_design_results)
        self.assertEqual(None, result.item_type_results)


if __name__ == "__main__":
    unittest.main()
