#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import time
import csv

from selenium.common.exceptions import NoSuchElementException, StaleElementReferenceException, TimeoutException
from selenium.webdriver import ActionChains
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.wait import WebDriverWait

from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase
from cdbSeleniumModules.cdbSeleniumModuleDecorators import add_stale_protection


class MachineDesign(CdbSeleniumModuleBase):

	TBODY_ID = 'itemMachineDesignListForm:itemMachineDesignListDataTable_data'
	NAME_MACHINE_DESIGN_ROW_XPATH_FORMULA = '//*[@id="itemMachineDesignListForm:itemMachineDesignListDataTable:%s:draggableMachineDesign"]/span[2]'
	ROW_TOGGLER_MACHINE_DESIGN_ROW_XPATH_FORMULA = '//*[@id="itemMachineDesignListForm:itemMachineDesignListDataTable_node_%s"]/td[1]/span'
	
	MACHINE_DESIGN_CONTEXT_MENU_ID_FORMULA = "machineDesign%sDualViewMachineDesignContextMenu"
	MACHINE_DESIGN_CONTEXT_MENU_ID = MACHINE_DESIGN_CONTEXT_MENU_ID_FORMULA % ''
	MACHINE_DESIGN_CATALOG_CONTEXT_MENU_ID = MACHINE_DESIGN_CONTEXT_MENU_ID_FORMULA % 'Catalog'
	MACHINE_DESIGN_CONTEXT_MENU_XPATH_FORMULA = '//*[@id="itemMachineDesignListForm:%s"]/ul/li[%d]/a'
	MACHINE_DESIGN_CONTEXT_MENU_DETAILS_INX = 3

	
	MACHINE_DESIGN_ROW_XPATH_NAME_IDX = 1

	CSV_NAME_COLUMN_HEADER = 'Machine Design Item Name'
	CSV_DESCRIPTION_COLUMN_HEADER = 'Machine Design Item Description'
	CSV_ALTERNATE_NAME_COLUMN_HEADER = 'Alternate Name'
	CSV_ASSIGNED_CATALOG = 'Assigned Catalog'
	CSV_ASSIGNED_CATALOG_BLANK = 'placeholder'
	VIEW_BASE_NAME = 'itemDomainMachineDesign'
	ENTITY_TYPE_NAME = "itemMachineDesign"
	LIST_FORM_NAME = "%sListForm" % ENTITY_TYPE_NAME	
	EXPORT_FORM_NAME = "exportMachineDesignForm"
	IMPORT_FORM_NAME = "importMachineDesignForm"
	VIEW_FORM_NAME = "itemMachineDesignViewForm"
	EDIT_FORM_NAME = "itemMachineDesignEditForm"
	
	IMPORT_FILE_NAME = "Machine Design Import.xlsx"
	EXPORT_FILE_NAME = "Machine Element Update Export.xlsx"

	NAME_FILTER_ID = 'itemMachineDesignListForm:itemMachineDesignListDataTable:nameMdFilter'
	# Top-level standard machine without an assignment uses this context menu.
	MACHINE_DESIGN_MEMBER_CONTEXT_MENU_ID = 'machineDesignDualViewMachineDesignMemberContextMenu'
	MENU_ITEM_BY_TEXT_XPATH_FORMULA = '//*[@id="itemMachineDesignListForm:%s"]//a[.//span[text()="%s"]]'
	MENU_ITEM_BY_INDEX_XPATH_FORMULA = '//*[@id="itemMachineDesignListForm:%s"]/ul/li[%d]/a'
	ASSIGN_CATALOG_MENU_ITEM_IDX = 2
	LINK_ASSEMBLY_PART_TEXT = 'Link to assembly part'
	SET_REP_ELEMENT_DIALOG_ID = 'itemMachineDesignListForm:setRepElementDialog'
	SET_REP_ELEMENT_ITEMS_XPATH = '//*[@id="%s"]//*[contains(@class, "ui-selectlistbox-item")]' % SET_REP_ELEMENT_DIALOG_ID
	SET_REP_ELEMENT_SAVE_XPATH = '//*[@id="%s"]//button[.//span[text()="Save"]]' % SET_REP_ELEMENT_DIALOG_ID
	DROP_CONFIRM_YES_XPATH = '//div[contains(@class, "ui-confirm-dialog")][.//div[@id="machineDesignDropConfirmDialogMessage"]]//button[.//span[text()="Yes"]]'
	MOVE_TO_TRASH_YES_BUTTON_ID = 'itemMachineDesignListForm:moveToTrashDialogYesButton'
	DELETED_ITEMS_VIEW = 'itemDomainMachineDesign/deletedItemsList'
	# Deleted-items page ids are matched by suffix so they do not depend on its form name.
	ROW_BY_NAME_XPATH_FORMULA = '//tbody[contains(@id, "itemMachineDesignListDataTable_data")]/tr[.//div[@class="mdName" and normalize-space(text())="%s"]]'
	RESTORE_MENU_ITEM_XPATH = '//*[contains(@id, "deletedItemsListContextMenu")]//a[.//span[text()="Put back..."]]'
	RESTORE_YES_BUTTON_XPATH = '//*[contains(@id, "restoreDeletedItemDialogPanel")]//button[.//span[text()="Yes"]]'
	UI_ACTION_TIMEOUT = 30
	# "Assign Item to Machine Element" panel (item/private/itemSelectDataTable.xhtml, viewName assignCatalogSelection).
	ASSIGN_PANEL_TABLE_KEY = 'ItemSelectDataTableassignCatalogSelection'
	ASSIGN_PANEL_NAME_FILTER_XPATH = '//input[contains(@id, "%s:") and contains(@id, "NameColumn:filter")]' % ASSIGN_PANEL_TABLE_KEY
	ASSIGN_PANEL_ROWS_XPATH = '//tbody[contains(@id, "%s_data")]/tr' % ASSIGN_PANEL_TABLE_KEY
	ASSIGN_PANEL_SAVE_ID = 'itemMachineDesignListForm:assignCatalogToMachineDesignSave'

	def __init__(self, driver):
		super().__init__(driver)

	def navigate_to_machine_design(self):
		self._navigate_to_dropdown('designMenubarDropdownButton', 'machineDesignMenubarButton', 'itemDomainMachineDesign/list')

	def add_machine_design(self, name, description, project='APS-OPS', alternateName=None):
		self._wait_for_id_and_click('itemMachineDesignListForm:itemMachineDesignAddButton')

		nameInput = self._wait_for_id('addComponentForm:nameInputText')
		nameInput.send_keys(name)

		if alternateName is not None:
			alternateNameInput = self._wait_for_id("addComponentForm:itemIdentifier1IT")
			alternateNameInput.send_keys(alternateName)

		self._type_in_id('addComponentForm:descriptionITA', description)

		self._wait_for_id_and_click('addComponentForm:itemProjectSelectCB')

		projectFilter = self._wait_for_xpath('//*[@id="addComponentForm:itemProjectSelectCB_panel"]/div[1]/div[2]/input')
		projectFilter.send_keys(project)

		selectAllProjectsXpath = '//*[@id="addComponentForm:itemProjectSelectCB_panel"]/div[1]/div[1]/div[2]'
		self._click_on_xpath(selectAllProjectsXpath)

		self._click_on_id('addComponentForm:itemProjectSelectCB')

		self._click_on_id('addComponentForm:itemMachineDesignCreateSaveButton')

		self._wait_for_url_contains('itemDomainMachineDesign/view')

		# Click 'Return'
		self._click_on_id('%s:itemMachineDesignViewDoneButton' % self.VIEW_FORM_NAME)

		self._wait_for_url_contains('itemDomainMachineDesign/list')

	def _find_xpath_of_selected_item(self, expectedName = None):
		tbody = self._find_by_id(MachineDesign.TBODY_ID)
		resultingId = None

		for row in tbody.find_elements(by=By.XPATH, value='./tr'):
			if row.get_attribute('aria-selected') == 'true':
				if expectedName is not None:
					dataCells = row.find_elements(by=By.XPATH, value='./td')
					nameCell = dataCells[MachineDesign.MACHINE_DESIGN_ROW_XPATH_NAME_IDX - 1]
					spans = nameCell.find_elements(by=By.XPATH, value='./span')
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

	def _find_x_path_for_machine_design_item(self, hierarchy, reset=False):
		"""
		Expands the hierarchy and returns the desired item at the end

		:param hierarchy: list ['topParentName', 'SubParentName', 'ActualItem']
		:return: xpath to the desired item
		"""

		if not reset:
			# collapse the tree nodes
			redoCollapseCheck = True
			while redoCollapseCheck:
				redoCollapseCheck = False
				previous = None
				try:
					tbody = self._find_by_id(MachineDesign.TBODY_ID)
					rows = tbody.find_elements(by=By.XPATH, value='./tr')
					for row in reversed(rows):
						dataCells = row.find_elements(by=By.XPATH, value='./td')
						nameCell = dataCells[MachineDesign.MACHINE_DESIGN_ROW_XPATH_NAME_IDX - 1]
						spans = nameCell.find_elements(by=By.XPATH, value='./span')
						rowExpander = spans[0]

						rowExpanderClass = rowExpander.get_attribute("class")
						rowExpanderStyle = rowExpander.get_attribute("style")


						if rowExpanderClass.__contains__('ui-icon-triangle-1-s') and previous is not None and rowExpanderStyle == '':
							redoCollapseCheck = True
							self._wait_for_invisible_by_id('loadingDialog_modal')
							rowExpander.click()
							redoCollapseCheck = True
							break

						previous = row
				except StaleElementReferenceException:
					print('Stale Protection - Retrying')
					redoCollapseCheck = True
					break
		else:
			self._click_on_id_with_stale_protection('itemMachineDesignListForm:itemMachineDesignResetFiltersButton')
			#TODO figure out a way to see when page is loaded
			time.sleep(2)

		rowStart = 0
		rowEnd = None

		hierarchyPart = ''

		for i in range(0, len(hierarchy)):
			mdName = hierarchy[i]
			if hierarchyPart == '':
				hierarchyPartFormula = '%s'
			else:
				hierarchyPartFormula = hierarchyPart + '_%s'

			if rowEnd is None:
				rowEnd = self._get_row_count_of_tbody()

			for j in range(rowStart, rowEnd):
				hierarchyPart = hierarchyPartFormula % (j - rowStart)
				xpath = self.NAME_MACHINE_DESIGN_ROW_XPATH_FORMULA % (hierarchyPart)

				rowName = self._get_attribute_in_xpath_with_stale_protection('innerHTML', xpath)
				if mdName == rowName:
					if i == len(hierarchy) -1:
						return xpath

					togglerXpath = self.ROW_TOGGLER_MACHINE_DESIGN_ROW_XPATH_FORMULA % (hierarchyPart)
					toggler = self._wait_for_visible_xpath(togglerXpath)
					#//*[@id="itemMachineDesignListForm:itemMachineDesignListDataTable_node_23"]/td[1]/span
					toggler.click()

					firstXpathOfNext = self.NAME_MACHINE_DESIGN_ROW_XPATH_FORMULA % (hierarchyPart + '_0')
					self._wait_for_visible_xpath(firstXpathOfNext)

					size = self._get_row_count_of_tbody()

					removeEnd = rowEnd - (j + 1)
					rowStart = j + 1
					rowEnd = size - removeEnd

					break

		raise Exception("Cannot find item for hierarchy: %s" % hierarchy)

	@add_stale_protection
	def _get_row_count_of_tbody(self):
		tBody = self._wait_for_id(MachineDesign.TBODY_ID)
		return len(tBody.find_elements(by=By.XPATH, value='./tr'))

	def _context_click_x_path(self, xpath, menuItemXpath):
		action = ActionChains(self.driver)

		mdItem = self._find_by_xpath(xpath)

		action.move_to_element(mdItem)

		action.context_click()
		action.perform()

		time.sleep(0.5)

		addButtonXpath = menuItemXpath

		addItemButton = self._wait_until_enabled_by_xpath(addButtonXpath)
		self._click_using_javascript(addItemButton)

	def add_child_to_machine_design(self, parentListItemXpath, name, description, alternateName=None):
		if '_' in parentListItemXpath:
			menuId='machineDesignDualViewMachineDesignMemberContextMenu'
		else:
			menuId = 'machineDesignDualViewMachineDesignContextMenu'

		menu_xpath = '//*[@id="itemMachineDesignListForm:%s"]/ul/li[1]'
		menu_xpath = menu_xpath % menuId
		add_menu_xpath = menu_xpath + '/a'
		menu_placeholder_xpath = menu_xpath + '/ul/li[1]/a'

		self._context_click_x_path(parentListItemXpath, add_menu_xpath)

		addPlaceholderElement = self._find_by_xpath(menu_placeholder_xpath)

		action = ActionChains(self.driver)
		action.move_to_element(addPlaceholderElement)
		# action.move_by_offset(0, 15)
		action.click().perform()

		placeholderName = self._wait_for_visible_id('itemMachineDesignListForm:newMachineDesignNameplaceholder')
		placeholderName.send_keys(name)
		placeholderName.send_keys(Keys.TAB)

		addBtnXpath = '//*[@id="itemMachineDesignListForm:machineDesignDualListViewAddButtonPlaceholder"]'
		addButton = self._wait_until_enabled_by_xpath(addBtnXpath)

		if alternateName is not None:
			alternateNameInput = self._wait_for_visible_id('itemMachineDesignListForm:itemIdentifier1ITplaceholder')
			alternateNameInput.send_keys(alternateName)

		descriptionField = self._find_by_id('itemMachineDesignListForm:descriptionITAplaceholder')
		descriptionField.send_keys(description)

		addButton.click()

		self._wait_for_invisible_by_xpath(addBtnXpath)

	def assign_catalog_to_machine_design(self, parentListItemXpath, catalogName):
		"""Assign a catalog item to the child machine at ``parentListItemXpath``."""
		menu_item_xpath = self.MENU_ITEM_BY_INDEX_XPATH_FORMULA % (
			self.MACHINE_DESIGN_MEMBER_CONTEXT_MENU_ID, self.ASSIGN_CATALOG_MENU_ITEM_IDX)
		self._context_click_x_path(parentListItemXpath, menu_item_xpath)
		self.select_item_in_assign_panel(catalogName)

	def input_hierarchy_from_sample_file(self, csvFile='data/Rack-2018-11-06.csv', project='APS-OPS'):
		with open(csvFile) as csvFile:
			csvReader = csv.reader(csvFile, delimiter=',')

			hierarchyDepth = 0
			lineCount = 0

			descriptionIdx = None
			alternateNameIdx = None
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
						elif header == MachineDesign.CSV_ALTERNATE_NAME_COLUMN_HEADER:
							alternateNameIdx = ctr
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

							alternateName = None
							if alternateNameIdx is not None:
								alternateName = row[alternateNameIdx]


							currentHierarchySize = len(currentHierarchy)
							if (currentHierarchySize == 1):
								self.add_machine_design(currentHierarchy[0], description, project, alternateName=alternateName)
							else:
								currentParent = currentHierarchy[0:currentHierarchySize-1]
								if lastParent != currentParent:
									lastParent = currentParent
									lastParentXpath = self._find_x_path_for_machine_design_item(lastParent)
								else:
									expactedInnerHTML = lastParent[-1]
									try:
										innerHTML = self._get_attribute_in_xpath_with_stale_protection('innerHTML', lastParentXpath)
										if expactedInnerHTML != innerHTML:
											lastParentXpath = self._find_x_path_for_machine_design_item(lastParent)
									except NoSuchElementException:
										lastParentXpath = self._find_x_path_for_machine_design_item(lastParent)


								self.add_child_to_machine_design(lastParentXpath, currentHierarchy[-1], description, alternateName=alternateName)
								if assignedCatalogIdx:
									assignedCatalogName = row[assignedCatalogIdx]
									if assignedCatalogName != MachineDesign.CSV_ASSIGNED_CATALOG_BLANK and assignedCatalogName != '':
										try:
											itemXpath = self._find_xpath_of_selected_item(currentHierarchy[-1])
										except:
											time.sleep(4)
											itemXpath = self._find_x_path_for_machine_design_item(currentHierarchy)

										self.assign_catalog_to_machine_design(itemXpath, assignedCatalogName)
							break
				lineCount += 1

	def test_machine_pages(self):
		node_xpath = '//*[@id="%s:itemMachineDesignListDataTable_node_0"]/td[1]' % self.LIST_FORM_NAME
		view_details_xpath = '//*[@id="%s:machineDesignCatalogDualViewMachineDesignContextMenu"]/ul/li[3]/a' % self.LIST_FORM_NAME

		self._context_click_x_path(node_xpath, view_details_xpath)

		self._wait_for_url_contains('%s/listView' % self.VIEW_BASE_NAME)
		
		self._click_on_id("%s:itemMachineDesignViewButton" % self.LIST_FORM_NAME)
		
		self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)
		
		self._click_on_id('%s:%sViewEditButton' % (self.VIEW_FORM_NAME, self.ENTITY_TYPE_NAME))
		self._wait_for_url_contains('%s/edit' % self.VIEW_BASE_NAME)
		
		self._click_on_id('%s:%sEditViewButton' % (self.EDIT_FORM_NAME, self.ENTITY_TYPE_NAME))
		self._wait_for_url_contains('%s/view' % self.VIEW_BASE_NAME)

	def test_detail_page(self, test):
		#first_item_xpath = '//*[@id="itemMachineDesignListForm:itemMachineDesignListDataTable_node_0"]/td[1]'
		first_item_xpath = self.ROW_TOGGLER_MACHINE_DESIGN_ROW_XPATH_FORMULA % (0)
		context_item_xpath = self.MACHINE_DESIGN_CONTEXT_MENU_XPATH_FORMULA % (self.MACHINE_DESIGN_CATALOG_CONTEXT_MENU_ID, self.MACHINE_DESIGN_CONTEXT_MENU_DETAILS_INX)
		self._context_click_x_path(first_item_xpath, context_item_xpath)		
		self._add_log_to_item(self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, 'Machine Design Log!', False)		
		self._add_property_to_item(test, self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, "Machine Prop", False)		
		self._add_image_to_item(self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME, needs_toggler=False)		

	def filter_machine_by_name(self, name, expected_name=None):
		"""Replace any existing name filter, unlike filter_machine which appends."""
		self._wait_for_clickable_id_with_stale_protection(self.NAME_FILTER_ID)
		filter_box = self._find_by_id(self.NAME_FILTER_ID)
		filter_box.clear()
		filter_box.send_keys(name + "\n")
		self._wait_for_invisible_loading_dialog(timeout=self.UI_ACTION_TIMEOUT)
		self._wait_for_xpath(self.ROW_BY_NAME_XPATH_FORMULA % (expected_name or name))

	def add_machine_design_with_child(self, name, child_name, description, project='APS-OPS'):
		"""Create a top-level machine with one placeholder child from the list page."""
		self.add_machine_design(name, description, project)
		self.filter_machine_by_name(name)
		parent_xpath = self._find_x_path_for_machine_design_item([name])
		self.add_child_to_machine_design(parent_xpath, child_name, description)

	def _context_menu_id_for(self, hierarchy):
		# Machines without an assignment: top-level vs member menu.
		return self.MACHINE_DESIGN_CONTEXT_MENU_ID if len(hierarchy) == 1 \
			else self.MACHINE_DESIGN_MEMBER_CONTEXT_MENU_ID

	def assign_catalog_to_machine(self, hierarchy, catalog_name, filter_text):
		"""Assign a catalog item to an unassigned machine via the context menu.

		``filter_text`` must match every name in ``hierarchy`` so none is filtered out.
		"""
		self.filter_machine_by_name(filter_text, expected_name=hierarchy[0])
		item_xpath = self._find_x_path_for_machine_design_item(hierarchy)
		menu_item_xpath = self.MENU_ITEM_BY_INDEX_XPATH_FORMULA % (
			self._context_menu_id_for(hierarchy), self.ASSIGN_CATALOG_MENU_ITEM_IDX)
		self._context_click_x_path(item_xpath, menu_item_xpath)
		self.select_item_in_assign_panel(catalog_name)

	def link_child_to_assembly_part(self, parent_name, child_name, filter_text):
		"""Link an unassigned child to the first assembly part of its parent's catalog.

		Returns the selected element name.
		"""
		self.filter_machine_by_name(filter_text, expected_name=parent_name)
		child_xpath = self._find_x_path_for_machine_design_item([parent_name, child_name])
		menu_item_xpath = self.MENU_ITEM_BY_TEXT_XPATH_FORMULA % (
			self.MACHINE_DESIGN_MEMBER_CONTEXT_MENU_ID, self.LINK_ASSEMBLY_PART_TEXT)
		self._context_click_x_path(child_xpath, menu_item_xpath)

		wait = WebDriverWait(self.driver, self.UI_ACTION_TIMEOUT)
		wait.until(EC.visibility_of_element_located((By.ID, self.SET_REP_ELEMENT_DIALOG_ID)))
		element_item = wait.until(EC.element_to_be_clickable((By.XPATH, self.SET_REP_ELEMENT_ITEMS_XPATH)))
		element_name = element_item.text.strip()
		element_item.click()
		self._click_on_xpath(self.SET_REP_ELEMENT_SAVE_XPATH)
		self._wait_for_invisible_loading_dialog(timeout=self.UI_ACTION_TIMEOUT)
		# Save only refreshes the dialog contents; close it explicitly.
		self.driver.execute_script("PF('setRepElementDialogWidget').hide()")
		return element_name

	def drag_machine_onto(self, source_name, target_name, filter_text):
		"""Drag a top-level machine onto another machine and confirm the move."""
		self.filter_machine_by_name(filter_text, expected_name=target_name)
		drag_xpath = '%s//*[contains(@id, "draggableMachineDesign")]'
		source = self._wait_for_visible_xpath(drag_xpath % (self.ROW_BY_NAME_XPATH_FORMULA % source_name))
		target = self._wait_for_visible_xpath(drag_xpath % (self.ROW_BY_NAME_XPATH_FORMULA % target_name))

		# Step through moves so jQuery UI registers a drag rather than a click.
		ActionChains(self.driver).click_and_hold(source).move_by_offset(5, 5) \
			.move_to_element(target).move_by_offset(1, 1).release().perform()

		WebDriverWait(self.driver, self.UI_ACTION_TIMEOUT).until(
			EC.element_to_be_clickable((By.XPATH, self.DROP_CONFIRM_YES_XPATH))).click()
		self._wait_for_invisible_loading_dialog(timeout=self.UI_ACTION_TIMEOUT)

	def select_item_in_assign_panel(self, item_name):
		"""Filter the assign panel to a uniquely named item, select it, and save."""
		wait = WebDriverWait(self.driver, self.UI_ACTION_TIMEOUT)
		name_filter = wait.until(EC.visibility_of_element_located(
			(By.XPATH, self.ASSIGN_PANEL_NAME_FILTER_XPATH)))
		name_filter.clear()
		name_filter.send_keys(item_name)

		def single_matching_row(driver):
			try:
				rows = driver.find_elements(By.XPATH, self.ASSIGN_PANEL_ROWS_XPATH)
				if len(rows) == 1 and item_name in rows[0].text:
					return rows[0]
			except StaleElementReferenceException:
				pass
			return False

		wait.until(single_matching_row).click()
		# rowSelect + remote command re-render the panel under the loading dialog.
		try:
			WebDriverWait(self.driver, 3).until(
				EC.visibility_of_element_located((By.ID, self.LOADING_DIALOG_ID)))
		except TimeoutException:
			pass
		self._wait_for_invisible_loading_dialog(timeout=self.UI_ACTION_TIMEOUT)

		def row_selected(driver):
			row = single_matching_row(driver)
			return bool(row) and (row.get_attribute('aria-selected') == 'true'
								  or 'ui-state-highlight' in (row.get_attribute('class') or ''))

		wait.until(row_selected)

		wait.until(EC.element_to_be_clickable((By.ID, self.ASSIGN_PANEL_SAVE_ID))).click()
		self._wait_for_invisible_by_id(self.ASSIGN_PANEL_SAVE_ID, timeout=self.UI_ACTION_TIMEOUT)
		self._wait_for_invisible_loading_dialog(timeout=self.UI_ACTION_TIMEOUT)

	def move_top_level_machine_to_trash(self, name, menu_id=MACHINE_DESIGN_CONTEXT_MENU_ID):
		"""Move a top-level machine (and its hierarchy) to trash via the context menu.

		Pass MACHINE_DESIGN_CATALOG_CONTEXT_MENU_ID when the machine has a catalog assigned.
		"""
		self.filter_machine_by_name(name)
		item_xpath = self._find_x_path_for_machine_design_item([name])
		self._context_click_x_path(
			item_xpath, self.MENU_ITEM_BY_TEXT_XPATH_FORMULA % (menu_id, 'Move to Trash'))

		yes_button = WebDriverWait(self.driver, self.UI_ACTION_TIMEOUT).until(
			EC.element_to_be_clickable((By.ID, self.MOVE_TO_TRASH_YES_BUTTON_ID)))
		yes_button.click()
		self._wait_for_invisible_by_id(self.MOVE_TO_TRASH_YES_BUTTON_ID, timeout=self.UI_ACTION_TIMEOUT)
		self._wait_for_invisible_loading_dialog(timeout=self.UI_ACTION_TIMEOUT)

	def navigate_to_deleted_machine_design(self):
		base_url = self.driver.current_url.split('/views/')[0]
		self.driver.get('%s/views/%s.xhtml' % (base_url, self.DELETED_ITEMS_VIEW))
		self._wait_for_url_contains(self.DELETED_ITEMS_VIEW)

	def restore_deleted_machine(self, name):
		"""Put back a top-level machine from the deleted-items list."""
		row_xpath = self.ROW_BY_NAME_XPATH_FORMULA % name
		WebDriverWait(self.driver, self.UI_ACTION_TIMEOUT).until(
			EC.presence_of_element_located((By.XPATH, row_xpath)))
		self._context_click_x_path(row_xpath + '/td[1]', self.RESTORE_MENU_ITEM_XPATH)

		yes_button = WebDriverWait(self.driver, self.UI_ACTION_TIMEOUT).until(
			EC.element_to_be_clickable((By.XPATH, self.RESTORE_YES_BUTTON_XPATH)))
		yes_button.click()
		self._wait_for_invisible_by_xpath(self.RESTORE_YES_BUTTON_XPATH)
		self._wait_for_invisible_loading_dialog(timeout=self.UI_ACTION_TIMEOUT)

	def filter_machine(self, filter_string='Test Machine'):
		filterbox_id = 'itemMachineDesignListForm:itemMachineDesignListDataTable:nameMdFilter'
		self._wait_for_clickable_id_with_stale_protection(filterbox_id)
		self._type_in_id(filterbox_id, filter_string + "\n")
		self._wait_for_invisible_loading_dialog()

	def export_machine(self, test):
		self._navigate_to_export_from_list(self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME)
		self._export(self.EXPORT_FORM_NAME, self.EXPORT_FILE_NAME, test, has_levels=True)

	def import_machine(self, test):
		self._wait_for_id_and_click('%s:%sImportButton' % (self.LIST_FORM_NAME, self.ENTITY_TYPE_NAME))
		table_results = self._import_navigate_to_verification_data_table(self.IMPORT_FORM_NAME, self.IMPORT_FILE_NAME, additional_pre_file_step=self.__additional_pre_file_import_step)
		test.assertEqual(len(table_results), 6, msg='6 items were imported in the spreadsheet')
		self._import_complete(self.IMPORT_FORM_NAME, self.VIEW_BASE_NAME)

	def __additional_pre_file_import_step(self):
		self._import_next_step(self.IMPORT_FORM_NAME)


