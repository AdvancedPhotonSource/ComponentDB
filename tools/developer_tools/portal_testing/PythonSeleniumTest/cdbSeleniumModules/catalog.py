#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import time

from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase
from cdbSeleniumModules.itemBase import ItemBase

SAMPLE_CATALOG_ITEM_NAME='Sample Item'
SAMPLE_CATALOG_ITEM_MODEL='Sample Model'
SAMPLE_CATALOG_ITEM_ALTERNATE_NAME= 'Alternate'


class Catalog(ItemBase):

	VIEW_BASE_NAME = "itemDomainCatalog"
	ENTITY_TYPE_NAME = "component"
	LIST_FORM_NAME = "%sListForm" % ENTITY_TYPE_NAME
	VIEW_FORM_NAME = "%sViewForm" % ENTITY_TYPE_NAME
	IMPORT_FORM_NAME = 'importCableCatalogForm'
	EXPORT_FORM_NAME = "exportCatalogForm"

	EXPORT_FILE_NAME = "Component Catalog Export.xlsx"
	IMPORT_FILE_NAME = "Catalog Import.xlsx"

	def navigate_to_catalog_list(self):
		self._navigate_to_dropdown('catalogDropdownButton', 'componentCatalogButton', 'itemDomainCatalog/list')

	def start_create_sample_catalog_item(self):
		self.navigate_to_catalog_list()
		self._wait_for_id_and_click('componentListForm:componentAddButton')

		nameInput = self._wait_for_id('addComponentForm:nameInputTextCreateWizard')
		nameInput.send_keys(SAMPLE_CATALOG_ITEM_NAME)

		self._type_in_id('addComponentForm:itemIdentifier1ITCreateWizard', SAMPLE_CATALOG_ITEM_MODEL)
		self._type_in_id('addComponentForm:itemIdentifier2ITCreateWizard', SAMPLE_CATALOG_ITEM_ALTERNATE_NAME)
		self._type_in_id('addComponentForm:descriptionITACreateWizard', 'Description')

	def finish_create_sample_catalog_item(self):
		self._click_on_id('addComponentForm:componentcreateWizardNextStep')

		self._wait_for_id_and_click('addComponentForm:itemProjectSelectCBCreateWizard')


		firstProject = self._wait_for_xpath('//*[@id="addComponentForm:itemProjectSelectCBCreateWizard_panel"]/div[2]/ul/li/div/div[2]')
		firstProject.click()

		nextStepId = 'addComponentForm:componentcreateWizardNextStep'
		self._click_on_id(nextStepId)

		groupWriteableToggle = self._wait_for_xpath('//*[@id="addComponentForm:groupWriteableSBBCreateWizard"]/span')
		groupWriteableToggle.click()

		self._click_on_id(nextStepId)

		self._wait_for_id_and_click('addComponentForm:componentCreateSaveButton')

		self._wait_for_id_and_click('componentViewForm:componentViewInfoButton')

	def display_more_columns(self):
		self.navigate_to_catalog_list()
		time.sleep(0.5)
		self._wait_for_id_and_click('componentListForm:componentCustomizeDisplayButton')

		self._wait_for_visible_id('componentListForm:componentListCustomizeDisplayDialog')
		display_ii2_input = self._find_by_id('componentListForm:domainEntityCustomizeDisplayDialogAccordionPanel:displayItemIdentifier2_input')
		display_project_input = self._find_by_id('componentListForm:domainEntityCustomizeDisplayDialogAccordionPanel:displayItemProject_input')

		# Sould figure out a way to find the new location of the inputs
		self._click_using_javascript(display_ii2_input)
		self._click_using_javascript(display_project_input)

		doneButton = self._find_by_id('componentListForm:componentListCustomizeDisplayDialogDone2Button')
		self._click_using_javascript(doneButton)

		# Verify columns are shown
		self._wait_for_id('componentListForm:componentListDataTable:componentListObjectItemIdentifier2Column')
		# TODO add id for project column
		# TODO add id for sources column

	def search_for_sample_catalog_item(self):
		time.sleep(1)
		self.navigate_to_catalog_list()

		nameFilter = self._wait_for_id('componentListForm:componentListDataTable:componentListObjectNameColumn:filter')
		nameFilter.send_keys(SAMPLE_CATALOG_ITEM_NAME)
		self._type_in_id('componentListForm:componentListDataTable:componentListObjectItemIdentifier1Column:filter', SAMPLE_CATALOG_ITEM_MODEL)
		self._type_in_id('componentListForm:componentListDataTable:componentListObjectItemIdentifier2Column:filter', SAMPLE_CATALOG_ITEM_ALTERNATE_NAME)

		time.sleep(1)

		sampleItemLink = self._wait_for(By.LINK_TEXT, SAMPLE_CATALOG_ITEM_NAME)
		sampleItemLink.click()
		self._wait_for_id_and_click('componentViewForm:componentViewInfoButton')

	def add_log_to_catalog_item(self):
		logEntryText = 'My Awesome New Log Entry!!!'

		self._add_log_to_item(self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME, logEntryText)

	def add_property_to_catalog_item(self, test):
		self._clear_notifications()		
		prop_value_text = "Catalog Test Property"

		self._add_property_to_item(test, self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME, prop_value_text)

	def add_image_to_catalog_item(self):
		self._add_image_to_item(self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME)

	def test_catalog_pages(self):
		self._click_on_xpath('//*[@id="componentListForm:componentListDataTable_data"]/tr[1]/td[2]/a')
		self._wait_for_url_contains('itemDomainCatalog/view')

		self._click_on_id('componentViewForm:componentViewEditButton')
		self._wait_for_url_contains('itemDomainCatalog/edit')

		self._click_on_id('componentEditForm:componentEditViewButton')
		self._wait_for_url_contains('itemDomainCatalog/view')

	def export_catalog(self, test):
		self._navigate_to_export_from_list(self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME)
		self._export(self.EXPORT_FORM_NAME, self.EXPORT_FILE_NAME, test)
	
	def test_import_create(self, test):
		self._wait_for_id_and_click('%s:componentImportButton' % self.LIST_FORM_NAME)
		table_results = self._import_navigate_to_verification_data_table(self.IMPORT_FORM_NAME, self.IMPORT_FILE_NAME)
		test.assertEqual(len(table_results), 10, msg='10 items were imported in the spreadsheet')
		self._import_complete(self.IMPORT_FORM_NAME, self.VIEW_BASE_NAME)




