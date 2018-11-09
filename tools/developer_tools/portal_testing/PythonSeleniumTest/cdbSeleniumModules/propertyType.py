#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from selenium.webdriver import ActionChains
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase

SAMPLE_PROPERTY_TYPE_NAME = '0000000000000Sample'

class PropertyType(CdbSeleniumModuleBase):

	def _navigateToAdminPropertyType(self):
		# Open the property types page
		adminButton = self._findById('administrativeButton')
		ActionChains(self.driver).move_to_element(adminButton).perform()

		adminPropertyTypesButton = self._waitForVisibleId('adminPropertyTypesButton')
		adminPropertyTypesButton.click()

		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.url_contains('propertyType/list'))

	def addSampleTestPropertyType(self):
		self._navigateToAdminPropertyType()

		addBtn = self._waitForXpath('//*[@id="viewPropertyTypeListForm:propertyTypeAddButton"]/span[2]')
		addBtn.click()

		nameInput = self._waitForId('addPropertyTypeForm:name')
		nameInput.send_keys(SAMPLE_PROPERTY_TYPE_NAME)

		descriptionInput = self._findById('addPropertyTypeForm:description')
		descriptionInput.send_keys("Sample Property Type created for tests")

		self._clickOnId('addPropertyTypeForm:allowedDomain')
		catalogCheckboxXpath = '//*[@id="addPropertyTypeForm:allowedDomain_panel"]/div[2]/ul/li[2]'
		catalogCheckbox = self._waitForVisibleXpath(catalogCheckboxXpath)
		inventoryCheckbox = self._waitForVisibleXpath('//*[@id="addPropertyTypeForm:allowedDomain_panel"]/div[2]/ul/li[3]')
		catalogCheckbox.click()
		inventoryCheckbox.click()
		self._clickOnId('addPropertyTypeForm:allowedDomain')

		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.invisibility_of_element_located((By.XPATH, '//*[@id="addPropertyTypeForm:allowedDomain_panel"]/div[2]/ul/li[2]')))


		self._clickOnId('addPropertyTypeForm:category')
		firstCategory = self._waitForVisibleId('addPropertyTypeForm:category_1')
		firstCategory.click()

		self._clickOnId("addPropertyTypeForm:propertyTypeCreateSaveButton")
		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.url_contains('/view'))

	def deleteSampleTestPropertyType(self):
		self._navigateToAdminPropertyType()

		nameFilter = self._waitForId("viewPropertyTypeListForm:propertyTypeListDataTable:propertyTypeNameFilter:filter")
		nameFilter.send_keys(SAMPLE_PROPERTY_TYPE_NAME)

		samplePropertyLink = self._waitFor(By.LINK_TEXT, SAMPLE_PROPERTY_TYPE_NAME)
		samplePropertyLink.click()
		self._waitForIdAndClick('viewPropertyTypeForm:propertyTypeViewDeleteButton')

		confirmDelete = self._waitForVisibleXpath('//*[@id="viewPropertyTypeForm:confirmDestroyPropertyType"]')
		confirmDelete.click()

		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.url_contains('/list'))