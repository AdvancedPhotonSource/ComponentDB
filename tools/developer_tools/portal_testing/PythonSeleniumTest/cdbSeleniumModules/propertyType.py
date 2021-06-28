#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
import time

from selenium.webdriver import ActionChains
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase

SAMPLE_PROPERTY_TYPE_NAME = '0000000000000Sample'

class PropertyType(CdbSeleniumModuleBase):

	def _navigateToAdminPropertyType(self):
		self._navigate_to_dropdown('administrativeButton', 'adminPropertyTypesButton', 'propertyType/list')

	def add_sample_test_property_type(self):
		self._navigateToAdminPropertyType()

		addBtn = self._wait_for_xpath('//*[@id="viewPropertyTypeListForm:propertyTypeAddButton"]/span[2]')
		addBtn.click()

		nameInput = self._wait_for_id('addPropertyTypeForm:name')
		nameInput.send_keys(SAMPLE_PROPERTY_TYPE_NAME)

		descriptionInput = self._find_by_id('addPropertyTypeForm:description')
		descriptionInput.send_keys("Sample Property Type created for tests")

		self._click_on_id('addPropertyTypeForm:allowedDomain')
		catalogCheckboxXpath = '//*[@id="addPropertyTypeForm:allowedDomain_panel"]/div[2]/ul/li[2]/div/div[2]'
		catalogCheckbox = self._wait_for_visible_xpath(catalogCheckboxXpath)
		inventoryCheckbox = self._wait_for_visible_xpath('//*[@id="addPropertyTypeForm:allowedDomain_panel"]/div[2]/ul/li[3]/div/div[2]')
		catalogCheckbox.click()
		inventoryCheckbox.click()
		self._click_on_id('addPropertyTypeForm:allowedDomain')

		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.invisibility_of_element_located((By.XPATH, '//*[@id="addPropertyTypeForm:allowedDomain_panel"]/div[2]/ul/li[2]')))

		self._click_on_id('addPropertyTypeForm:category')
		firstCategory = self._wait_for_visible_id('addPropertyTypeForm:category_1')
		firstCategory.click()

		self._click_on_id("addPropertyTypeForm:propertyTypeCreateSaveButton")
		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.url_contains('/view'))
		self._wait_for_id('viewPropertyTypeForm:name')

	def delete_sample_test_property_type(self):
		self._navigateToAdminPropertyType()

		nameFilter = self._wait_for_id("viewPropertyTypeListForm:propertyTypeListDataTable:propertyTypeNameFilter:filter")
		nameFilter.send_keys(SAMPLE_PROPERTY_TYPE_NAME)

		samplePropertyLink = self._wait_for(By.LINK_TEXT, SAMPLE_PROPERTY_TYPE_NAME)
		samplePropertyLink.click()
		self._wait_for_id_and_click('viewPropertyTypeForm:propertyTypeViewDeleteButton')

		confirmDelete = self._wait_for_visible_xpath('//*[@id="viewPropertyTypeForm:confirmDestroyPropertyType"]')
		confirmDelete.click()

		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.url_contains('/list'))