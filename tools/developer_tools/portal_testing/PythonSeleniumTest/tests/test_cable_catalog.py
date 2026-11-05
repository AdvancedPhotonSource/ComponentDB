#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.cable_catalog import CableCatalog
from tests.base import CdbPortalTestCase


class CableCatalogTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.cable_catalog = CableCatalog(self.driver)

    def test_cable_catalog_pages(self):
        self.cable_catalog.navigate_to_cable_catalog_list()
        self.cable_catalog.test_cable_catalog_pages()

    def test_cable_catalog_detail_page(self):
        self.cable_catalog.navigate_to_cable_catalog_list()
        self.cable_catalog.test_cable_catalog_detail_page(self)

    def test_cable_catalog_export(self):
        self.cable_catalog.navigate_to_cable_catalog_list()
        self.cable_catalog.export_cable_catalog(self)

    def test_cable_catalog_import(self):
        self.cable_catalog.navigate_to_cable_catalog_list()
        self.cable_catalog.test_import_create(self)
