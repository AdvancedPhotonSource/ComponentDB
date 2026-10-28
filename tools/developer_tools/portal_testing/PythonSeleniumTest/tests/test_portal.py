#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from selenium.webdriver.common.by import By

from cdbSeleniumModules.catalog import Catalog
from cdbSeleniumModules.inventory import Inventory
from cdbSeleniumModules.portal import LOGIN_BUTTON_ID, LOGOUT_BUTTON_ID
from tests.base import CdbPortalTestCase


class PortalTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.catalog = Catalog(self.driver)
        self.inventory = Inventory(self.driver)

    def test_invalid_login_credentials(self):
        self.portal.logout()
        self.portal._click_on_id(LOGIN_BUTTON_ID)

        self.portal._type_in_id('loginForm:username', 'cdb')
        self.portal._type_in_id('loginForm:password', 'invalid-password')
        self.portal._click_on_id('loginForm:loginButton')

        self.assertIn(
            'Username/password combination could not be verified.',
            self.portal._wait_for_visible_xpath(
                "//*[contains(@class, 'ui-growl-message') "
                "and contains(., 'Invalid Credentials')]").text)
        self.assertTrue(self.portal._wait_for_id(LOGIN_BUTTON_ID).is_displayed())
        self.assertFalse(self.driver.find_elements(By.ID, LOGOUT_BUTTON_ID))

    def test_login_and_logout(self):
        self.catalog.navigate_to_catalog_list()
        self.portal.logout()

        self.portal.login()
        self.inventory.navigate_to_inventory_list()
        self.portal.logout()
