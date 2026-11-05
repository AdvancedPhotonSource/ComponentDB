#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.source import Source
from tests.base import CdbPortalTestCase


class SourceTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.source = Source(self.driver)

    def test_source_pages(self):
        self.source.navigate_to_source_list()
        self.source.test_source_pages()

    def test_source_export(self):
        self.source.navigate_to_source_list()
        self.source.export_source(self)

    def test_source_import_pages(self):
        self.source.navigate_to_source_list()
        self.source.test_import_create(self)
