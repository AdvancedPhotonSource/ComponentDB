#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
import os
import unittest

from selenium import webdriver

from cdbSeleniumModules.portal import Portal


class CdbPortalTestCase(unittest.TestCase):

    PORTAL_URL = 'http://localhost:8080/cdb'
    DATA_PATH = "%s/data-dw" % os.getcwd()

    def setUp(self):
        self.prepare_download_area()
        opts = webdriver.ChromeOptions()
        prefs = {"download.default_directory": self.DATA_PATH}
        opts.add_experimental_option("prefs", prefs)

        headless = os.getenv("CDB_TEST_HEADLESS", 'False').lower() in ('true', '1', 't', 'y', 'yes')
        if headless:
            opts.add_argument("headless")

        self.driver = webdriver.Chrome(options=opts)
        self.driver.set_window_position(0, 0)
        self.driver.maximize_window()
        self.driver.get(self.PORTAL_URL)

        self.portal = Portal(self.driver)
        self.portal.login()

    def tearDown(self):
        self.portal.logout()
        self.driver.close()

    def prepare_download_area(self):
        if os.path.exists(self.DATA_PATH):
            # Clean up any residual downloads
            for file in os.scandir(self.DATA_PATH):
                os.remove(file.path)
        else:
            # Create directory
            os.mkdir(self.DATA_PATH)

    def verify_file_downloaded(self, file_name):
        file_path = "%s/%s" % (self.DATA_PATH, file_name)
        exists = os.path.exists(file_path)
        self.assertEqual(True, exists, msg='Downloaded file %s was not found' % file_name)
