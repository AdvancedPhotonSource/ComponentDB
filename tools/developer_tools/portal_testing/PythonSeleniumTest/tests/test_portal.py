#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.catalog import Catalog
from cdbSeleniumModules.inventory import Inventory
from tests.base import CdbPortalTestCase


class PortalTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.catalog = Catalog(self.driver)
        self.inventory = Inventory(self.driver)

    def test_login_and_logout(self):
        self.catalog.navigate_to_catalog_list()
        self.portal.logout()

        self.portal.login()
        self.inventory.navigate_to_inventory_list()
        self.portal.logout()
