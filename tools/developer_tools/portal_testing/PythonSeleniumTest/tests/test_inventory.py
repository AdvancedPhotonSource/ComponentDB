#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.inventory import Inventory
from tests.base import CdbPortalTestCase


class InventoryTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.inventory = Inventory(self.driver)

    def test_inventory_pages(self):
        self.inventory.navigate_to_inventory_list()
        self.inventory.test_inventory_pages()

    def test_inventory_detail_page(self):
        self.inventory.navigate_to_inventory_list()
        self.inventory.test_inventory_detail_page(self)

    def test_inventory_machine_assignment(self):
        self.inventory.test_machine_assignment_acceptance(self)

    def test_inventory_import(self):
        self.inventory.navigate_to_inventory_list()
        self.inventory.test_import_create(self)
