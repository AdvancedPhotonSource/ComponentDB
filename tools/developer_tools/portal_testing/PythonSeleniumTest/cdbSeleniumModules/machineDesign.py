#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import time
import csv

from selenium.webdriver import ActionChains
from selenium.webdriver.common.keys import Keys

from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase
from cdbSeleniumModules.cdbSeleniumModuleDecorators import addStaleProtection

class MachineDesign(CdbSeleniumModuleBase):

	TBODY_ID = 'itemMachineDesignListForm:itemMachineDesignListDataTable_data'

	CSV_NAME_COLUMN_HEADER = 'Machine Design Item Name'
	CSV_DESCRIPTION_COLUMN_HEADER = 'Machine Design Item Description'

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

	def _findXPathForMachineDesignItem(self, hierarchy):
		"""
		Expands the hierarchy and returns the desired item at the end

		:param hierarchy: list ['topParentName', 'SubParentName', 'ActualItem']
		:return: xpath to the desired item
		"""


		rowXPathFormula = '//*[@id="itemMachineDesignListForm:itemMachineDesignListDataTable_node%s"]/td[%s]/span[%s]'

		nameTdIdx = 1
		nameSpanIdxOffset = 3
		togglerSpanIdxOffset = 1

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

	def addChildToMachineDesign(self, parentListItemXpath, name, description):
		action = ActionChains(self.driver)

		mdItem = self._findByXpath(parentListItemXpath)

		action.move_to_element(mdItem)

		action.context_click()
		action.perform()

		time.sleep(0.5)
		addButtonXpath = '//*[@id="itemMachineDesignListForm:machineDesignDualViewMachineDesignContextMenu"]/ul/li[1]/a'

		addItemButton = self._waitUntilEnabledByXpath(addButtonXpath)
		self._clickUsingJavascript(addItemButton)

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

								self.addChildToMachineDesign(lastParentXpath, currentHierarchy[-1], description)
							break
				lineCount += 1


