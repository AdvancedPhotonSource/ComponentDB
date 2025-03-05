import unittest
from cdbApi.exceptions import OpenApiException
from cdbApi.models import CableDesignConnectionListObject
from test.cdb_test_base import CdbTestBase


class ItemCableTest(CdbTestBase):

    CABLE_WITH_METADATA_VALUES = {
        "external_cable_name": "CAT6A Ethernet Cable",
        "import_cable_id": "CABLE-2023-001",
        "alternate_cable_id": "ALT-CABLE-2023-001",
        "laying": "Underground",
        "voltage": "240V",
        "routed_length": "150m",
        "route": "Building A to Building B",
        "total_req_length": "160m",
        "notes": "Ensure cable is shielded for outdoor use.",
        "endpoint1_description": "Server Room A",
        "endpoint1_route": "Rack 12, Panel 3",
        "endpoint1_end_length": "5m",
        "endpoint1_termination": "RJ45 Connector",
        "endpoint1_pinlist": "568B Standard",
        "endpoint1_notes": "Connect to switch port 24.",
        "endpoint1_drawing": "Drawing_Endpoint1_v2.pdf",
        "endpoint2_description": "Data Center B",
        "endpoint2_route": "Rack 5, Panel 1",
        "endpoint2_end_length": "3m",
        "endpoint2_termination": "RJ45 Connector",
        "endpoint2_pinlist": "568B Standard",
        "endpoint2_notes": "Connect to router port 8.",
        "endpoint2_drawing": "Drawing_Endpoint2_v2.pdf",
    }

    CABLE_DESIGN_WITH_ENDPOINTS = {
        121: {"cable_connector": "End-1", "md_connector": "Eth0"},
        122: {"cable_connector": "End-2", "md_connector": "Eth1"},
    }

    def test_cable_api(self):
        self.cableCatalogApi.get_cable_catalog_item_list()

    def test_get_cable_design_connections(self):
        results: list[CableDesignConnectionListObject] = (
            self.cableDesignApi.get_cable_design_connection_list(
                self.CABLE_DESIGN_ITEM_ID
            )
        )

        for connection in results:
            self.assertIn(
                connection.md_item.id,
                self.CABLE_DESIGN_ENDPOINT_IDS,
                msg="Unexpected endpoint",
            )
            self.assertEqual(connection.cable_design.id, self.CABLE_DESIGN_ITEM_ID)

    def test_fetch_cable_designs_with_connections(self):
        cable_designs = self.cableDesignApi.get_cable_design_item_list(
            include_connections=True
        )

        sample_design = None
        sample_design_with_connector_names = None

        for cable_design in cable_designs:
            if cable_design.id == self.CABLE_DESIGN_ITEM_ID:
                sample_design = cable_design
            if cable_design.id == self.CABLE_DESIGN_WITH_ENDPOINTS_METADATA_ITEM_ID:
                sample_design_with_connector_names = cable_design

        self.assertIsNotNone(sample_design, "Sample test cable design was not found")

        # Ensure that connections can be found.
        self.assertIsNotNone(
            sample_design.connection_list, "Connection list was not found"
        )

        self.assertEqual(
            len(sample_design.connection_list),
            2,
            msg="Did not find two endpoints for the sample cable design item.",
        )

        for connection in sample_design.connection_list:
            self.assertIn(
                connection.md_item_id,
                self.CABLE_DESIGN_ENDPOINT_IDS,
                msg="Cable design endpoints aren't matching as expected.",
            )

        self.verify_connection_list(
            sample_design_with_connector_names.connection_list, is_summary=True
        )

    def test_fetch_cable_catalog_items(self):
        cable_catalog_items = self.cableCatalogApi.get_cable_catalog_item_list()

        cable_catalog_with_connectors = None
        for cable_catalog in cable_catalog_items:
            if cable_catalog.id == self.CABLE_TYPE_WITH_CONNECTORS_ITEM_ID:
                cable_catalog_with_connectors = cable_catalog
                break

        # Ensure that cable catalog with connectors is found
        self.assertIsNotNone(
            cable_catalog_with_connectors,
            msg="List of cable catalogs did not result in expected catalog with connectors.",
        )

        item_connectors = cable_catalog_with_connectors.connector_list

        self.assertEqual(
            len(item_connectors), 2, msg="The cable should have two connectors."
        )

        for item_connector in item_connectors:
            connector = item_connector.connector
            connector_type = connector.connector_type

            self.assertIsNotNone(
                connector.name, msg="Connector name should be defined/fetched."
            )
            self.assertIsNotNone(
                connector_type.name,
                msg="Connector type name should be defined/fetched.",
            )

    def test_fetch_cable_design_metadata(self):
        metadata = self.cableDesignApi.get_cable_design_metadata(
            cable_design_id=self.CABLE_DESIGN_WITH_ENDPOINTS_METADATA_ITEM_ID
        )

        # Ensure that metadata is returned.
        self.assertIsNotNone(
            metadata, msg="No metadata returned for cable design with metadata."
        )

        for key, value in self.CABLE_WITH_METADATA_VALUES.items():
            self.assertEqual(
                getattr(metadata, key),
                value,
                msg=f"{key} in the fetched metadata does not match expected value.",
            )

    def test_fetch_cable_design_endpoints(self):
        connection_list = self.cableDesignApi.get_cable_design_connection_list(
            cable_design_id=self.CABLE_DESIGN_WITH_ENDPOINTS_METADATA_ITEM_ID
        )

        # Ensure that connection_list is returned.
        self.assertIsNotNone(
            connection_list,
            msg="No connection_list returned for cable design with metadata.",
        )

        self.assertEqual(
            len(connection_list), 2, msg="The cable should have two connections"
        )

        self.verify_connection_list(connection_list)

    def verify_connection_list(self, connection_list, is_summary=False):
        for connection in connection_list:
            if is_summary:
                md_item_id = connection.md_item_id
            else:
                md_item_id = connection.md_item.id

            expected_vals = self.CABLE_DESIGN_WITH_ENDPOINTS[md_item_id]

            self.assertEqual(
                connection.md_connector_name,
                expected_vals["md_connector"],
                msg=f"Machine connector is invalid for {md_item_id}",
            )

            self.assertEqual(
                connection.item_connector_name,
                expected_vals["cable_connector"],
                msg=f"Item connector is invalid for {md_item_id}",
            )

    def test_update_cable_design_metadata(self):
        self.skipTest("Not implemented")

    def test_add_cable_design(self):
        self.skipTest("Not implemented")

    def test_update_cable_design(self):
        self.skipTest("Not implemented")


if __name__ == "__main__":
    unittest.main()
