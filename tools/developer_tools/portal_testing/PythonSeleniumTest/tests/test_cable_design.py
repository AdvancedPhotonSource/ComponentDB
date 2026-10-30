#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.cable_design import CableDesign
from tests.base import CdbPortalTestCase


class CableDesignTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.cable_design = CableDesign(self.driver)

    def test_cable_design_pages(self):
        self.cable_design.navigate_to_cable_design_list()
        self.cable_design.test_cable_design_pages()

    def test_cable_design_detail_page(self):
        self.cable_design.navigate_to_cable_design_list()
        self.cable_design.test_cable_design_detail_page(self)

    def test_cable_design_export(self):
        self.cable_design.navigate_to_cable_design_list()
        self.cable_design.export_cable_design(self)

    def test_cable_design_import(self):
        self.cable_design.navigate_to_cable_design_list()
        self.cable_design.test_import_create(self)
