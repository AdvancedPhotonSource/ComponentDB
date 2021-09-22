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

		self._click_on_id('componentViewForm:componentViewLogEntriesPanel_toggler')
		self._wait_for_id_and_click('componentViewForm:componentLogAddButton')
		logEntry = self._wait_for_id('componentViewForm:logEntryValue')

		logEntry.send_keys(logEntryText)
		self._click_on_id('componentViewForm:componentSaveLogButton')

		newLogEntrySelector = "#componentViewForm\\3a componentLogListDataTable\\3a 0\\3a logEntryColumnCellEditor > div.ui-cell-editor-output"
		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.text_to_be_present_in_element((By.CSS_SELECTOR, newLogEntrySelector), logEntryText))


	def add_property_to_catalog_item(self):
		self._click_on_id('componentViewForm:componentViewPropertiesPanel_toggler')

		self._wait_for_id_and_click('componentViewForm:componentPropertyAddButton')

		categorySelectionXpath = "//div[@id='componentViewForm:filterViewItemCategorySelection']/div[2]/ul/li[2]"
		categorySel = self._wait_for_visible_xpath(categorySelectionXpath)
		categorySel.click()

		self._wait_for_xpath("//tbody[@id='componentViewForm:componentPropertySelectDataTable_data']/tr[1]/td[4]").click()

		import time
		time.sleep(5)
		pass

	def test_catalog_pages(self):
		self._click_on_xpath('//*[@id="componentListForm:componentListDataTable_data"]/tr[1]/td[2]/a')
		self._wait_for_url_contains('itemDomainCatalog/view')

		self._click_on_id('componentViewForm:componentViewEditButton')
		self._wait_for_url_contains('itemDomainCatalog/edit')

		self._click_on_id('componentEditForm:componentEditViewButton')
		self._wait_for_url_contains('itemDomainCatalog/view')



