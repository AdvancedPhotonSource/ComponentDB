#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.browse_by import BrowseBy
from tests.base import CdbPortalTestCase


class BrowseByTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.browse_by = BrowseBy(self.driver)

    def test_browse_by_function_pages(self):
        self.browse_by.navigate_to_browse_by_function()
        self.browse_by.test_browse_by_function(self)

    def test_browse_by_owner(self):
        self.browse_by.navigate_to_browse_by_owner()
        self.browse_by.test_browse_by_owner(self)

    def test_browse_by_location(self):
        self.browse_by.navigate_to_browse_by_location()
        self.browse_by.test_browse_by_location(self)
