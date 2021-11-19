#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
import os
import time

from selenium import webdriver
from selenium.common.exceptions import StaleElementReferenceException, NoSuchElementException
from selenium.webdriver import ActionChains
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

from cdbSeleniumModules.cdbSeleniumModuleDecorators import add_stale_protection

GROWL_MESSAGE_CONTAINER = '//*[@id="messages_container"]/div/div'
GROWL_MESSAGE_CONTAINER_2 = '//*[@id="messages_container"]/div[2]/div'
GROWL_MESSAGE_CLOSE_BUTTON = GROWL_MESSAGE_CONTAINER + '/div[1]'

class CdbSeleniumModuleBase:

	WAIT_FOR_ELEMENT_TIMEOUT = 10

	def __init__(self, driver):
		self.driver = driver

		assert isinstance(self.driver, webdriver.Chrome)

	def _clear_notifications(self):
		growl_container = 0
		while growl_container is not None:
			time.sleep(0.5)
			try:
				growl_container = self._find_by_xpath(GROWL_MESSAGE_CONTAINER)
			except NoSuchElementException:
				growl_container = None
			try:
				next_growl = self._find_by_xpath(GROWL_MESSAGE_CONTAINER_2)
			except NoSuchElementException:
				next_growl = None

			if growl_container is not None:
				growl_close = self._find_by_xpath(GROWL_MESSAGE_CLOSE_BUTTON)
				action = ActionChains(self.driver)
				action.move_to_element(growl_container)
				action.perform()
				time.sleep(.1)
				growl_close.click()
				time.sleep(0.5)

				growl_container = next_growl

	def _navigate_to_dropdown(self, dropdownId, buttonId, urlPartContains):
		dropdownElement = self._find_by_id(dropdownId)
		action = ActionChains(self.driver)
		action.move_to_element(dropdownElement)
		action.perform()

		time.sleep(0.5)
		self._wait_for_id_and_click(buttonId)

		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(
			EC.url_contains(urlPartContains))

	def _click_using_javascript(self, element):
		self.driver.execute_script('arguments[0].click()', element)

	def _click_on_xpath(self, id):
		element = self._find_by_xpath(id)
		element.click()

	def _click_on_id(self, id):
		element = self._find_by_id(id)
		element.click()

	def _type_in_id(self, id, text):
		input = self._find_by_id(id)
		input.send_keys(text)

	def _find_by_id(self, id):
		return self.driver.find_element_by_id(id)

	def _find_by_xpath(self, xpath):
		return self.driver.find_element_by_xpath(xpath)

	def _wait_for_id_and_click(self, id):
		element = self._wait_for_id(id)
		element.click()

	def _wait_for_visible_id(self, id):
		return self._wait_for_visible(By.ID, id)

	def _wait_for_visible_xpath(self, xpath):
		return self._wait_for_visible(By.XPATH, xpath)

	def _wait_for_visible(self, by, identifier):
		return WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(
			EC.visibility_of_element_located((by, identifier)))

	def _wait_for_invisible_by_id(self, identifier):
		return self._wait_for_invisible(By.ID, identifier)

	def _wait_for_invisible_by_xpath(self, xpath):
		return self._wait_for_invisible(By.XPATH, xpath)

	def _wait_for_invisible(self, by, identifier):
		return WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(
			EC.invisibility_of_element((by, identifier)))

	def _wait_for_clickable_id(self, id):
		return self._wait_for_clickable(By.ID, id)

	def _wait_for_clickable_xpath(self, xpath):
		return self._wait_for_clickable(By.XPATH, xpath)

	def _wait_for_clickable(self, by, identifier):
		return WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(
			EC.element_to_be_clickable((by, identifier)))

	def _wait_for_id(self, id):
		return self._wait_for(By.ID, id)

	def _wait_for_css_selector(self, selector):
		return self._wait_for(By.CSS_SELECTOR, selector)

	def _wait_for_xpath(self, xpath):
		return self._wait_for(By.XPATH, xpath)

	def _wait_for(self, by, identifier):
		return WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(
			EC.presence_of_element_located((by, identifier))
		)

	def _wait_for_url_contains(self, urlSubstring):
		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.url_contains(urlSubstring))

	def _wait_until_enabled_by_xpath(self, xpath):
		timeout = time.time() + CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT

		while time.time() < timeout:
			time.sleep(0.01)
			try:
				element = self._wait_for_xpath(xpath)
				cssClass = element.get_attribute('class')
				if not cssClass.__contains__('ui-state-disabled'):
					return element
			except StaleElementReferenceException:
				pass

	@add_stale_protection
	def _wait_for_clickable_id_with_stale_protection(self,id):
		self._wait_for_visible_id(id)

	@add_stale_protection
	def _click_on_id_with_stale_protection(self, identifier):
		self._click_on_id(identifier)

	@add_stale_protection
	def _click_on_x_path_with_stale_protection(self, xpath):
		self._click_on_xpath(xpath)

	@add_stale_protection
	def _get_attribute_in_xpath_with_stale_protection(self, attribute, xpath):
		element = self._find_by_xpath(xpath)
		return element.get_attribute(attribute)

	def _read_data_table(self, form_name, table_id):
		headerRowXpath = '//*[@id="%s:%sContent_head"]/tr' % (form_name, table_id)
		header = self._wait_for_xpath(headerRowXpath)

		header_cells = header.find_elements_by_tag_name("th")
		header_titles = []

		for h_cell in header_cells:
			header_text = h_cell.text
			header_titles.append(header_text)

		data_id = "%s:%sContent_data" % (form_name, table_id)
		data = self._find_by_id(data_id)
		rows = data.find_elements_by_tag_name('tr')

		table_data = []

		for row in rows:
			cells = row.find_elements_by_tag_name('td')

			row_data = {}
			for i, val in enumerate(cells):
				key = header_titles[i]
				row_data[key] = val.text

			table_data.append(row_data)

		return table_data

	def _import_navigate_to_verification_data_table(self, form_name, import_file_name):
		# self.module._wait_for_clickable_id('importSourceForm:importWizardSelectFormatMenu')
		self._wait_for_clickable_id_with_stale_protection('%s:importWizardSelectFormatMenu' % form_name)
		self._click_on_id('%s:importWizardSelectFormatMenu' % form_name)
		self._wait_for_id_and_click('%s:importWizardSelectFormatMenu_1' % form_name)

		next_step_id = '%s:importWizardNextButton' % form_name
		self._wait_for_clickable_id_with_stale_protection(next_step_id)
		self._click_on_id_with_stale_protection(next_step_id)

		self._wait_for_id_and_click('%s:importWizardRadioCreate' % form_name)

		self._wait_for_clickable_id_with_stale_protection(next_step_id)
		self._click_on_id_with_stale_protection(next_step_id)

		upload_input_id = '%s:importWizardSelectFileUpload_input' % form_name
		upload_input = self._wait_for_id(upload_input_id)

		rel_path = 'data/' + import_file_name
		abs_path = os.path.abspath(rel_path)
		upload_input.send_keys(abs_path)

		self._wait_for_clickable_id_with_stale_protection(next_step_id)
		self._click_on_id_with_stale_protection(next_step_id)

		table_data = self._read_data_table(form_name, 'importWizardTable')

		return table_data

	def _import_complete(self, form_name, view_base_name):
		next_step_id = '%s:importWizardNextButton' % form_name
		self._wait_for_clickable_id_with_stale_protection(next_step_id)
		self._click_on_id_with_stale_protection(next_step_id)

		finish_id = '%s:importWizardFinishButton' % form_name
		self._wait_for_clickable_id_with_stale_protection(finish_id)
		self._click_on_id_with_stale_protection(finish_id)

		self._wait_for_url_contains('%s/list' % view_base_name)

