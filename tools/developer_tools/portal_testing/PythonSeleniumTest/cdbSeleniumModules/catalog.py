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

	def startCreateSampleCatalogItem(self):
		self._findById('catalogButton').click()
		self._waitForIdAndClick('componentListForm:componentAddButton')

		nameInput = self._waitForId('addComponentForm:nameInputTextCreateWizard')
		nameInput.send_keys(SAMPLE_CATALOG_ITEM_NAME)

		self._typeInId('addComponentForm:itemIdentifier1ITCreateWizard', SAMPLE_CATALOG_ITEM_MODEL)
		self._typeInId('addComponentForm:itemIdentifier2ITCreateWizard', SAMPLE_CATALOG_ITEM_ALTERNATE_NAME)
		self._typeInId('addComponentForm:descriptionITACreateWizard', 'Description')

	def finishCreateSampleCatalogItem(self):
		self._clickOnId('addComponentForm:componentcreateWizardNextStep')

		self._waitForIdAndClick('addComponentForm:itemProjectSelectCBCreateWizard')


		firstProject = self._waitForXpath('//*[@id="addComponentForm:itemProjectSelectCBCreateWizard_panel"]/div[2]/ul/li/div/div[2]')
		firstProject.click()

		nextStepId = 'addComponentForm:componentcreateWizardNextStep'
		self._clickOnId(nextStepId)

		groupWriteableToggle = self._waitForXpath('//*[@id="addComponentForm:groupWriteableSBBCreateWizard"]/span')
		groupWriteableToggle.click()

		self._clickOnId(nextStepId)

		self._waitForIdAndClick('addComponentForm:componentCreateSaveButton')

		self._waitForIdAndClick('componentViewForm:componentViewInfoButton')

	def displayMoreColumns(self):
		self._clickOnId('catalogButton')
		time.sleep(0.5)
		self._waitForIdAndClick('componentListForm:componentCustomizeDisplayButton')

		self._waitForVisibleId('componentListForm:componentListCustomizeDisplayDialog')
		display_ii2_input = self._findById('componentListForm:domainEntityCustomizeDisplayDialogAccordionPanel:displayItemIdentifier2_input')
		display_project_input = self._findById('componentListForm:domainEntityCustomizeDisplayDialogAccordionPanel:displayItemProject_input')

		# Sould figure out a way to find the new location of the inputs
		self._clickUsingJavascript(display_ii2_input)
		self._clickUsingJavascript(display_project_input)

		doneButton = self._findById('componentListForm:componentListCustomizeDisplayDialogDone2Button')
		self._clickUsingJavascript(doneButton)

		# Verify columns are shown
		self._waitForId('componentListForm:componentListDataTable:componentListObjectItemIdentifier2Column')
		# TODO add id for project column
		# TODO add id for sources column

	def searchForSampleCatalogItem(self):
		self._findById('catalogButton').click()

		nameFilter = self._waitForId('componentListForm:componentListDataTable:componentListObjectNameColumn:filter')
		nameFilter.send_keys(SAMPLE_CATALOG_ITEM_NAME)
		self._typeInId('componentListForm:componentListDataTable:componentListObjectItemIdentifier1Column:filter', SAMPLE_CATALOG_ITEM_MODEL)
		self._typeInId('componentListForm:componentListDataTable:componentListObjectItemIdentifier2Column:filter', SAMPLE_CATALOG_ITEM_ALTERNATE_NAME)

		sampleItemLink = self._waitFor(By.LINK_TEXT, SAMPLE_CATALOG_ITEM_NAME)
		sampleItemLink.click()
		self._waitForIdAndClick('componentViewForm:componentViewInfoButton')


	def addLogToCatalogItem(self):
		logEntryText = 'My Awesome New Log Entry!!!'

		self._clickOnId('componentViewForm:componentViewLogEntriesPanel_toggler')
		self._waitForIdAndClick('componentViewForm:componentLogAddButton')
		logEntry = self._waitForId('componentViewForm:logEntryValue')

		logEntry.send_keys(logEntryText)
		self._clickOnId('componentViewForm:componentSaveLogButton')

		newLogEntrySelector = "#componentViewForm\\3a componentLogListDataTable\\3a 0\\3a logEntryColumnCellEditor > div.ui-cell-editor-output"
		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.text_to_be_present_in_element((By.CSS_SELECTOR, newLogEntrySelector), logEntryText))


	def addPropertyToCatalogItem(self):
		self._clickOnId('componentViewForm:componentViewPropertiesPanel_toggler')

		self._waitForIdAndClick('componentViewForm:componentPropertyAddButton')

		categorySelectionXpath = "//div[@id='componentViewForm:filterViewItemCategorySelection']/div[2]/ul/li[2]"
		categorySel = self._waitForVisibleXpath(categorySelectionXpath)
		categorySel.click()

		self._waitForXpath("//tbody[@id='componentViewForm:componentPropertySelectDataTable_data']/tr[1]/td[4]").click()

		import time
		time.sleep(5)
		pass
