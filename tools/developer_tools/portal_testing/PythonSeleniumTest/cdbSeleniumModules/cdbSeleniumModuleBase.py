#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import time

from selenium import webdriver
from selenium.common.exceptions import StaleElementReferenceException
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

from cdbSeleniumModules.cdbSeleniumModuleDecorators import add_stale_protection


class CdbSeleniumModuleBase:

	WAIT_FOR_ELEMENT_TIMEOUT = 10

	def __init__(self, driver):
		self.driver = driver

		assert isinstance(self.driver, webdriver.Chrome)

	def _navigate_to_dropdown(self, dropdownId, buttonId, urlPartContains):
		self._wait_for_id_and_click(dropdownId)
		time.sleep(0.1)
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
	def _click_on_id_with_stale_protection(self, identifier):
		self._click_on_id(identifier)

	@add_stale_protection
	def _click_on_x_path_with_stale_protection(self, xpath):
		self._click_on_xpath(xpath)

	@add_stale_protection
	def _get_attribute_in_xpath_with_stale_protection(self, attribute, xpath):
		element = self._find_by_xpath(xpath)
		return element.get_attribute(attribute)
