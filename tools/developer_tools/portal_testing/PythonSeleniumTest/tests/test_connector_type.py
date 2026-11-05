#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.connector_type import ConnectorType
from tests.base import CdbPortalTestCase


class ConnectorTypeTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.connector_type = ConnectorType(self.driver)

    def test_connector_type_pages(self):
        self.connector_type.navigate_to_connector_type_list()
        self.connector_type.test_connector_type_pages()

    def test_connector_type_export(self):
        self.connector_type.navigate_to_connector_type_list()
        self.connector_type.export_connector_type(self)
