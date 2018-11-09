#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import unittest
from selenium import webdriver

from cdbSeleniumModules.catalog import Catalog
from cdbSeleniumModules.machineDesign import MachineDesign
from cdbSeleniumModules.portal import Portal
from cdbSeleniumModules.propertyType import PropertyType


class CdbPortalFunctionalTestSuite(unittest.TestCase):

	def setUp(self):
		self.driver = webdriver.Chrome()
		self.driver.set_window_position(0,0)
		self.driver.maximize_window()
		self.driver.get("http://localhost:8080/cdb")

		self.portal = Portal(self.driver)
		self.propertyType = PropertyType(self.driver)
		self.catalog = Catalog(self.driver)
		self.machineDesign = MachineDesign(self.driver)

	def tearDown(self):
		self.driver.close()

	def test_portal(self):
		self.portal.login()
		"""
		self.propertyType.addSampleTestPropertyType()

		self.catalog.startCreateSampleCatalogItem()
		self.catalog.finishCreateSampleCatalogItem()

		self.catalog.displayMoreColumns()
		self.catalog.searchForSampleCatalogItem()
		self.catalog.addLogToCatalogItem()

		#TODO Finish
		#self.catalog.addPropertyToCatalogItem()

		self.catalog.deleteCurrentItem()
		self.propertyType.deleteSampleTestPropertyType()
		"""
		self.machineDesign.navigateToMachineDesign()

		#print self.machineDesign._findXPathForMachineDesignItem(['S1'])
		#print self.machineDesign._findXPathForMachineDesignItem(['S1', 'Some new placeholder'])
		#print self.machineDesign._findXPathForMachineDesignItem(['S1', 'Some new placeholder', '123'])
		#print self.machineDesign._findXPathForMachineDesignItem(['S1', 'Some new placeholder', '6435'])

		#print self.machineDesign._findXPathForMachineDesignItem(['APS: Typical SR Mezzanine Double-Sector', 'S03'])
		self.machineDesign.inputHierarchyFromSampleFile()
		self.machineDesign.inputHierarchyFromSampleFile(csvFile='data/Rack-2018-11-06 - apsu.csv', project='APS-U')

		#self.machineDesign.addMachineDesign()
		#self.machineDesign.addChildToMachineDesign()
		#self.machineDesign.addChildToMachineDesign()
		#self.machineDesign.addChildToMachineDesign()
		#self.machineDesign.addChildToMachineDesign()
		#self.machineDesign.addChildToMachineDesign()

		self.portal.logout()

