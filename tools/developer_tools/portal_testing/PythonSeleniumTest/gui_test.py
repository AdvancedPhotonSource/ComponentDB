#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import unittest

from selenium import webdriver

from cdbSeleniumModules.browse_by import BrowseBy
from cdbSeleniumModules.cable_catalog import CableCatalog
from cdbSeleniumModules.cable_design import CableDesign
from cdbSeleniumModules.cable_inventory import CableInventory
from cdbSeleniumModules.catalog import Catalog
from cdbSeleniumModules.connector_type import ConnectorType
from cdbSeleniumModules.inventory import Inventory
from cdbSeleniumModules.location import Location
from cdbSeleniumModules.machineDesign import MachineDesign
from cdbSeleniumModules.portal import Portal
from cdbSeleniumModules.propertyType import PropertyType
from cdbSeleniumModules.source import Source
from cdbSeleniumModules.user_info import UserInfo


class CdbPortalFunctionalTestSuite(unittest.TestCase):

    PORTAL_URL = 'http://localhost:8080/cdb'
    MACHINE_DESIGN_CSV_PATH = 'data/md-test.csv'
    MACHINE_DESIGN_PROJECT = 'APS-U'

    def setUp(self):
        self.driver = webdriver.Chrome()
        self.driver.set_window_position(0, 0)
        self.driver.maximize_window()
        self.driver.get(self.PORTAL_URL)

        self.portal = Portal(self.driver)
        self.property_type = PropertyType(self.driver)
        self.catalog = Catalog(self.driver)
        self.inventory = Inventory(self.driver)
        self.cable_inventory = CableInventory(self.driver)
        self.cable_catalog = CableCatalog(self.driver)
        self.cable_design = CableDesign(self.driver)
        self.machine_design = MachineDesign(self.driver)
        self.location = Location(self.driver)
        self.browse_by = BrowseBy(self.driver)
        self.source = Source(self.driver)
        self.user_info = UserInfo(self.driver)
        self.connector_type = ConnectorType(self.driver)

        self.portal.login()

    def tearDown(self):
        self.portal.logout()
        self.driver.close()

    def test_create_property_type(self):
        self.property_type.add_sample_test_property_type()
        self.property_type.delete_sample_test_property_type()

    def test_create_catalog_item(self):
        self.catalog.start_create_sample_catalog_item()
        self.catalog.finish_create_sample_catalog_item()

        self.catalog.display_more_columns()
        self.catalog.search_for_sample_catalog_item()
        self.catalog.add_log_to_catalog_item()

        self.catalog.delete_current_item()

    def test_machine_design(self):
        self.machine_design.navigate_to_machine_design()
        self.machine_design.input_hierarchy_from_sample_file(csvFile=self.MACHINE_DESIGN_CSV_PATH,
                                                             project=self.MACHINE_DESIGN_PROJECT)

    def test_browse_by_function_pages(self):
        self.browse_by.navigate_to_browse_by_function()
        self.browse_by.test_browse_by_function(self)

    def test_browse_by_owner(self):
        self.browse_by.navigate_to_browse_by_owner()
        self.browse_by.test_browse_by_owner(self)

    def test_browse_by_location(self):
        self.browse_by.navigate_to_browse_by_location()
        self.browse_by.test_browse_by_location(self)

    def test_catalog_pages(self):
        self.catalog.navigate_to_catalog_list()
        self.catalog.test_catalog_pages()

    def test_cable_catalog_pages(self):
        self.cable_catalog.navigate_to_cable_catalog_list()
        self.cable_catalog.test_cable_catalog_pages()

    def test_inventory_pages(self):
        self.inventory.navigate_to_inventory_list()
        self.inventory.test_inventory_pages()

    def test_cable_inventory_pages(self):
        self.cable_inventory.navigate_to_cable_inventory_list()
        self.cable_inventory.test_cable_inventory_pages()

    def test_cable_design_pages(self):
        self.cable_design.navigate_to_cable_design_list()
        self.cable_design.test_cable_design_pages()

    def test_source_pages(self):
        self.source.navigate_to_source_list()
        self.source.test_source_pages()

    def test_user_info_pages(self):
        self.user_info.navigate_to_user_info_list()
        self.user_info.test_user_info_pages()

    def test_connector_type_pages(self):
        self.connector_type.navigate_to_connector_type_list()
        self.connector_type.test_connector_type_pages()

    def test_location_pages(self):
        self.location.navigate_to_location_list()
        self.location.test_location_pages()


if __name__ == '__main__':
    unittest.main()