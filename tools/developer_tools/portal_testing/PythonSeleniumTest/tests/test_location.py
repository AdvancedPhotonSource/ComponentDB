#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.location import Location
from tests.base import CdbPortalTestCase


class LocationTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.location = Location(self.driver)

    def test_location_pages(self):
        self.location.navigate_to_location_list()
        self.location.test_location_pages()
