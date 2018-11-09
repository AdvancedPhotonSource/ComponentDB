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

from cdbSeleniumModules.cdbSeleniumModuleDecorators import addStaleProtection


class CdbSeleniumModuleBase:

	WAIT_FOR_ELEMENT_TIMEOUT = 10

	def __init__(self, driver):
		self.driver = driver

		assert isinstance(self.driver, webdriver.Chrome)

	def _clickUsingJavascript(self, element):
		self.driver.execute_script('arguments[0].click()', element)

	def _clickOnXpath(self, id):
		element = self._findByXpath(id)
		element.click()

	def _clickOnId(self, id):
		element = self._findById(id)
		element.click()

	def _typeInId(self, id, text):
		input = self._findById(id)
		input.send_keys(text)

	def _findById(self, id):
		return self.driver.find_element_by_id(id)

	def _findByXpath(self, xpath):
		return self.driver.find_element_by_xpath(xpath)

	def _waitForIdAndClick(self, id):
		element = self._waitForId(id)
		element.click()

	def _waitForVisibleId(self, id):
		return self._waitForVisible(By.ID, id)

	def _waitForVisibleXpath(self, xpath):
		return self._waitForVisible(By.XPATH, xpath)

	def _waitForVisible(self, by, identifier):
		return WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(
			EC.visibility_of_element_located((by, identifier)))

	def _waitForInvisibleById(self, identifier):
		return self._waitForInvisible(By.ID, identifier)

	def _waitForInvisibleByXpath(self, xpath):
		return self._waitForInvisible(By.XPATH, xpath)

	def _waitForInvisible(self, by, identifier):
		return WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(
			EC.invisibility_of_element((by, identifier)))

	def _waitForClickableId(self, id):
		return self._waitForClickable(By.ID, id)

	def _waitForClickableXpath(self, xpath):
		return self._waitForClickable(By.XPATH, xpath)

	def _waitForClickable(self, by, identifier):
		return WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(
			EC.element_to_be_clickable((by, identifier)))

	def _waitForId(self, id):
		return self._waitFor(By.ID, id)

	def _waitForCSSSelector(self, selector):
		return self._waitFor(By.CSS_SELECTOR, selector)

	def _waitForXpath(self, xpath):
		return self._waitFor(By.XPATH, xpath)

	def _waitFor(self, by, identifier):
		return WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(
			EC.presence_of_element_located((by, identifier))
		)

	def _waitForUrlContains(self, urlSubstring):
		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.url_contains(urlSubstring))

	def _waitUntilEnabledByXpath(self, xpath):
		timeout = time.time() + CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT

		while time.time() < timeout:
			time.sleep(0.01)
			try:
				element = self._waitForXpath(xpath)
				cssClass = element.get_attribute('class')
				if not cssClass.__contains__('ui-state-disabled'):
					return element
			except StaleElementReferenceException:
				pass

	@addStaleProtection
	def _clickOnIdWithStaleProtection(self, identifier):
		self._clickOnId(identifier)

	@addStaleProtection
	def _clickOnXPathWithStaleProtection(self, xpath):
		self._clickOnXpath(xpath)

	@addStaleProtection
	def _getAttributeInXpathWithStaleProtection(self, attribute, xpath):
		element = self._findByXpath(xpath)
		return element.get_attribute(attribute)
