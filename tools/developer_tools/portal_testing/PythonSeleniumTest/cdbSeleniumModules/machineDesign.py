#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import time
import csv

from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver import ActionChains
from selenium.webdriver.common.keys import Keys

from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase
from cdbSeleniumModules.cdbSeleniumModuleDecorators import addStaleProtection

class MachineDesign(CdbSeleniumModuleBase):

	TBODY_ID = 'itemMachineDesignListForm:itemMachineDesignListDataTable_data'
	MACHINE_DESIGN_ROW_XPATH_FORMULA = '//*[@id="itemMachineDesignListForm:itemMachineDesignListDataTable_node%s"]/td[%s]/span[%s]'
	MACHINE_DESIGN_ROW_XPATH_NAME_IDX = 1

	CSV_NAME_COLUMN_HEADER = 'Machine Design Item Name'
	CSV_DESCRIPTION_COLUMN_HEADER = 'Machine Design Item Description'
	CSV_ASSIGNED_CATALOG = 'Assigned Catalog'
	CSV_ASSIGNED_CATALOG_BLANK = 'placeholder'

	def navigateToMachineDesign(self):
		self._clickOnId('machineDesignMenubarButton')

	def addMachineDesign(self, name, description, project='APS-OPS'):
		self._waitForIdAndClick('itemMachineDesignListForm:itemMachineDesignAddButton')

		nameInput = self._waitForId('addComponentForm:nameInputText')
		nameInput.send_keys(name)

		self._typeInId('addComponentForm:descriptionITA', description)

		self._waitForIdAndClick('addComponentForm:itemProjectSelectCB')

		projectFilter = self._waitForXpath('//*[@id="addComponentForm:itemProjectSelectCB_panel"]/div[1]/div[2]/input')
		projectFilter.send_keys(project)

		selectAllProjectsXpath = '//*[@id="addComponentForm:itemProjectSelectCB_panel"]/div[1]/div[1]'
		selectAllProjects = self._findByXpath(selectAllProjectsXpath)
		action = ActionChains(self.driver)
		action.move_to_element(selectAllProjects)
		action.move_by_offset(0, 30)
		action.click().perform()

		self._clickOnId('addComponentForm:itemProjectSelectCB')

		self._clickOnId('addComponentForm:itemMachineDesignCreateSaveButton')

		self._waitForUrlContains('Design/view')
		self._clickOnId('itemMachineDesignListForm:backToListFromDetails')
		self._waitForUrlContains('/list')

	def _findXpathOfSelectedItem(self, expectedName = None):
		tbody = self._findById(MachineDesign.TBODY_ID)
		resultingId = None

		for row in tbody.find_elements_by_xpath('./tr'):
			if row.get_attribute('aria-selected') == 'true':
				if expectedName is not None:
					dataCells = row.find_elements_by_xpath('./td')
					nameCell = dataCells[MachineDesign.MACHINE_DESIGN_ROW_XPATH_NAME_IDX - 1]
					spans = nameCell.find_elements_by_xpath('./span')
					name = spans[-1].text
					if name == expectedName:
						resultingId = row.get_attribute('id')
						break;
				else:
					resultingId = row.get_attribute('id')
					break;

		if resultingId is not None:
			resultingXpath = '//*[@id="%s"]' % resultingId
			return resultingXpath
		else:
			raise Exception


	def _findXPathForMachineDesignItem(self, hierarchy, reset=False):
		"""
		Expands the hierarchy and returns the desired item at the end

		:param hierarchy: list ['topParentName', 'SubParentName', 'ActualItem']
		:return: xpath to the desired item
		"""


		rowXPathFormula = MachineDesign.MACHINE_DESIGN_ROW_XPATH_FORMULA

		nameTdIdx = MachineDesign.MACHINE_DESIGN_ROW_XPATH_NAME_IDX
		nameSpanIdxOffset = 3
		togglerSpanIdxOffset = 1

		if not reset:
			# collapse the tree nodes
			redoCollapseCheck = True
			while redoCollapseCheck:
				redoCollapseCheck = False
				previous = None

				tbody = self._findById(MachineDesign.TBODY_ID)
				rows = tbody.find_elements_by_xpath('./tr')
				for row in reversed(rows):
					dataCells = row.find_elements_by_xpath('./td')
					nameCell = dataCells[MachineDesign.MACHINE_DESIGN_ROW_XPATH_NAME_IDX - 1]
					spans = nameCell.find_elements_by_xpath('./span')
					rowExpander = spans[-3]

					rowExpanderClass = rowExpander.get_attribute("class")
					rowExpanderStyle = rowExpander.get_attribute("style")

					if rowExpanderClass.__contains__('ui-icon-triangle-1-s') and previous is not None and rowExpanderStyle == '':
						redoCollapseCheck = True
						rowExpander.click()
						redoCollapseCheck = True
						break

					previous = row
		else:
			self._clickOnIdWithStaleProtection('itemMachineDesignListForm:itemMachineDesignResetFiltersButton')
			#TODO figure out a way to see when page is loaded
			time.sleep(2)

		rowStart = 0
		rowEnd = None

		hierarchyPart = ''

		for i in range(0, len(hierarchy)):
			mdName = hierarchy[i]
			hierarchyPartFormula = hierarchyPart + '_%s'

			spanIdx = nameSpanIdxOffset + i

			if rowEnd is None:
				rowEnd = self._getRowCountOfTbody()

			for j in range(rowStart, rowEnd):
				hierarchyPart = hierarchyPartFormula % (j - rowStart)
				xpath = rowXPathFormula % (hierarchyPart, nameTdIdx, spanIdx)

				rowName = self._getAttributeInXpathWithStaleProtection('innerHTML', xpath)
				if mdName == rowName:
					if i == len(hierarchy) -1:
						return xpath

					togglerSpanIdx = togglerSpanIdxOffset + i
					togglerXpath = rowXPathFormula % (hierarchyPart, nameTdIdx, togglerSpanIdx)
					toggler = self._waitForVisibleXpath(togglerXpath)
					toggler.click()

					firstXpathOfNext = rowXPathFormula % (hierarchyPart + '_0', nameTdIdx, spanIdx)
					self._waitForVisibleXpath(firstXpathOfNext)

					size = self._getRowCountOfTbody()

					removeEnd = rowEnd - (j + 1)
					rowStart = j + 1
					rowEnd = size - removeEnd

					break

		raise Exception("Cannot find item for hierarchy: %s" % hierarchy)

	@addStaleProtection
	def _getRowCountOfTbody(self):
		tBody = self._waitForId(MachineDesign.TBODY_ID)
		return len(tBody.find_elements_by_xpath('./tr'))

	def _contextClickXPath(self, xpath, menuItemXpath):
		action = ActionChains(self.driver)

		mdItem = self._findByXpath(xpath)

		action.move_to_element(mdItem)

		action.context_click()
		action.perform()

		time.sleep(0.5)

		addButtonXpath = menuItemXpath

		addItemButton = self._waitUntilEnabledByXpath(addButtonXpath)
		self._clickUsingJavascript(addItemButton)


	def addChildToMachineDesign(self, parentListItemXpath, name, description):
		self._contextClickXPath(parentListItemXpath, '//*[@id="itemMachineDesignListForm:machineDesignDualViewMachineDesignContextMenu"]/ul/li[1]/a')

		self.__clickOnPlaceholderNewMDOption()

		placeholderName = self._waitForVisibleId('itemMachineDesignListForm:newMachineDesignNameplaceholder')
		placeholderName.send_keys(name)
		placeholderName.send_keys(Keys.TAB)

		descriptionField = self._findById('itemMachineDesignListForm:descriptionITAplaceholder')
		descriptionField.send_keys(description)

		addBtnXpath = '//*[@id="itemMachineDesignListForm:machineDesignDualListViewAddButtonPlaceholder"]'
		addButton = self._waitUntilEnabledByXpath(addBtnXpath)
		addButton.click()

		self._waitForInvisibleByXpath(addBtnXpath)

	def assignCatalogToMachineDesign(self, parentListItemXpath, catalogName):
		self._contextClickXPath(parentListItemXpath, '//*[@id="itemMachineDesignListForm:machineDesignDualViewMachineDesignMemberContextMenu"]/ul/li[2]/a')

		catalogNamefilter = self._waitForVisibleXpath('//*[@id="itemMachineDesignListForm:itemMachineDesignItemSelectDataTable:itemMachineDesignListObjectNameColumn:filter"]')

		currentFilterText = catalogNamefilter.get_attribute('value')

		if currentFilterText != catalogName:
			catalogNamefilter.clear()
			catalogNamefilter.send_keys('@!!!#$#%^#@$%^@#$%%@#$&^#$%R%^#@$%@#$%#$%@#$%@@#$@#$!@%$#@$#@$!@#$%')
			catalogNamefilter.clear()
			time.sleep(0.5)
			catalogNamefilter.send_keys(catalogName)
			time.sleep(0.5)

		tBody = self._waitForId('itemMachineDesignListForm:itemMachineDesignItemSelectDataTable_data')
		attempts = 0
		found = False
		while attempts < 6:
			resultCount = len(tBody.find_elements_by_xpath('./tr'))
			if resultCount == 1:
				found = True;
				break;

			time.sleep(0.5)
			attempts += 1

		if found:
			self._clickOnXpath('//*[@id="itemMachineDesignListForm:itemMachineDesignItemSelectDataTable_data"]/tr')
		else:
			raise Exception('Couldn\'t find catalog item %s' % catalogName)

		time.sleep(1)

		saveButtonId = 'itemMachineDesignListForm:assignCatalogToMachineDesignSave'
		self._waitForIdAndClick(saveButtonId)
		self._waitForInvisibleById(saveButtonId)

	@addStaleProtection
	def __clickOnPlaceholderNewMDOption(self):
		placeholderOpt = self._waitForClickableId('itemMachineDesignListForm:machineDesignAddOptions:0')
		action = ActionChains(self.driver)
		action.move_to_element(placeholderOpt)
		action.pause(0.5)
		action.click()
		action.release()
		action.perform()

	def inputHierarchyFromSampleFile(self, csvFile='data/Rack-2018-11-06.csv', project='APS-OPS'):
		with open(csvFile) as csvFile:
			csvReader = csv.reader(csvFile, delimiter=',')

			hierarchyDepth = 0
			lineCount = 0

			descriptionIdx = None
			assignedCatalogIdx = None

			currentHierarchy = []

			lastParentXpath = None
			lastParent = None

			for row in csvReader:
				# Establish hierarchy depth and description index
				if lineCount == 0:
					ctr = 0
					while ctr < len(row):
						header = row[ctr]
						if header == MachineDesign.CSV_NAME_COLUMN_HEADER:
							hierarchyDepth += 1
							ctr += 1
							while row[ctr] == '':
								hierarchyDepth += 1
								ctr += 1
							ctr -= 1
						elif header == MachineDesign.CSV_DESCRIPTION_COLUMN_HEADER:
							descriptionIdx = ctr
						elif header == MachineDesign.CSV_ASSIGNED_CATALOG:
							assignedCatalogIdx = ctr
						ctr += 1

				elif lineCount > 1:
					for hierarchyIdx in range(0, hierarchyDepth):
						currentItem = row[hierarchyIdx]

						if currentItem is not '':
							if len(currentHierarchy) < hierarchyIdx:
								raise Exception("Invalid Hierarchy in file: %s " % csvFile)
							elif len(currentHierarchy) > hierarchyIdx:
								currentHierarchy = currentHierarchy[0:hierarchyIdx]

							currentHierarchy.append(currentItem)
							description = row[descriptionIdx]

							currentHierarchySize = len(currentHierarchy)
							if (currentHierarchySize == 1):
								self.addMachineDesign(currentHierarchy[0], description, project)
							else:
								currentParent = currentHierarchy[0:currentHierarchySize-1]
								if lastParent != currentParent:
									lastParent = currentParent
									lastParentXpath = self._findXPathForMachineDesignItem(lastParent)
								else:
									expactedInnerHTML = lastParent[-1]
									try:
										innerHTML = self._getAttributeInXpathWithStaleProtection('innerHTML', lastParentXpath)
										if expactedInnerHTML != innerHTML:
											lastParentXpath = self._findXPathForMachineDesignItem(lastParent)
									except NoSuchElementException:
										lastParentXpath = self._findXPathForMachineDesignItem(lastParent)


								self.addChildToMachineDesign(lastParentXpath, currentHierarchy[-1], description)
								if assignedCatalogIdx:
									assignedCatalogName = row[assignedCatalogIdx]
									if assignedCatalogName != MachineDesign.CSV_ASSIGNED_CATALOG_BLANK:
										try:
											itemXpath = self._findXpathOfSelectedItem(currentHierarchy[-1])
										except:
											itemXpath = self._findXPathForMachineDesignItem(currentHierarchy)

										self.assignCatalogToMachineDesign(itemXpath, assignedCatalogName)
							break
				lineCount += 1


