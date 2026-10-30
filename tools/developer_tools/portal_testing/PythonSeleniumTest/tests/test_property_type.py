#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.propertyType import PropertyType
from tests.base import CdbPortalTestCase


class PropertyTypeTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.property_type = PropertyType(self.driver)

    def test_create_property_type(self):
        self.property_type.add_sample_test_property_type()
        self.property_type.delete_sample_test_property_type()
