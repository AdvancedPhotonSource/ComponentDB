#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.machineDesign import MachineDesign
from tests.base import CdbPortalTestCase


class MachineDesignTest(CdbPortalTestCase):

    MACHINE_DESIGN_CSV_PATH = 'data/md-test.csv'
    MACHINE_DESIGN_PROJECT = 'APS-U'

    def setUp(self):
        super().setUp()
        self.machine_design = MachineDesign(self.driver)

    def test_machine_design(self):
        self.machine_design.navigate_to_machine_design()
        self.machine_design.input_hierarchy_from_sample_file(csvFile=self.MACHINE_DESIGN_CSV_PATH,
                                                             project=self.MACHINE_DESIGN_PROJECT)

    def test_machine_design_export(self):
        self.machine_design.navigate_to_machine_design()
        self.machine_design.filter_machine()
        self.machine_design.export_machine(self)

    def test_machine_design_import(self):
        self.machine_design.navigate_to_machine_design()
        self.machine_design.import_machine(self)

    def test_machine_design_details_page(self):
        self.machine_design.navigate_to_machine_design()
        self.machine_design.test_detail_page(self)

    def test_machine_pages(self):
        self.machine_design.navigate_to_machine_design()
        self.machine_design.test_machine_pages()
