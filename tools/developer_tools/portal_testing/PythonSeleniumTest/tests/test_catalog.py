#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.catalog import Catalog
from tests.base import CdbPortalTestCase


class CatalogTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.catalog = Catalog(self.driver)

    def test_create_catalog_item(self):
        self.catalog.start_create_sample_catalog_item()
        self.catalog.finish_create_sample_catalog_item()

        self.catalog.display_more_columns()
        self.catalog.search_for_sample_catalog_item()
        self.catalog.add_log_to_catalog_item()
        self.catalog.add_property_to_catalog_item(self)
        self.catalog.add_image_to_catalog_item()

        self.catalog.delete_current_item()

    def test_catalog_pages(self):
        self.catalog.navigate_to_catalog_list()
        self.catalog.test_catalog_pages()

    def test_catalog_export(self):
        self.catalog.navigate_to_catalog_list()
        self.catalog.export_catalog(self)

    def test_catalog_import(self):
        self.catalog.navigate_to_catalog_list()
        self.catalog.test_import_create(self)
