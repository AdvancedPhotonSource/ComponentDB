#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from cdbSeleniumModules.cdbSeleniumModuleBase import CdbSeleniumModuleBase


class ItemBase(CdbSeleniumModuleBase):

	def delete_current_item(self):
		self._click_on_id('componentViewForm:componentViewDeleteButton')
		confirmButton = self._find_by_id('componentViewForm:componentDestroyDialogYesConfirmButton')
		confirmButton.click()

		WebDriverWait(self.driver, CdbSeleniumModuleBase.WAIT_FOR_ELEMENT_TIMEOUT).until(EC.url_contains('/list'))