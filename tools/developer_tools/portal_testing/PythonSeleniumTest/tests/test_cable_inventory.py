#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.cable_inventory import CableInventory
from tests.base import CdbPortalTestCase


class CableInventoryTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.cable_inventory = CableInventory(self.driver)

    def test_cable_inventory_pages(self):
        self.cable_inventory.navigate_to_cable_inventory_list()
        self.cable_inventory.test_cable_inventory_pages()

    def test_cable_inventory_detail_page(self):
        self.cable_inventory.navigate_to_cable_inventory_list()
        self.cable_inventory.test_cable_inventory_detail_page(self)

    def test_cable_inventory_import(self):
        self.cable_inventory.navigate_to_cable_inventory_list()
        self.cable_inventory.test_import_create(self)
