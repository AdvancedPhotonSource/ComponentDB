#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
import os
import shutil
import time
import unittest

from selenium import webdriver

from cdbSeleniumModules.portal import Portal


class CdbPortalTestCase(unittest.TestCase):

    PORTAL_URL = 'http://localhost:8080/cdb'
    DATA_PATH = "%s/data-dw" % os.getcwd()
    DOWNLOAD_TIMEOUT = 10

    def setUp(self):
        worker = os.getenv("PYTEST_XDIST_WORKER")
        if worker:
            self.DATA_PATH = os.path.join(self.DATA_PATH, worker)
        self.prepare_download_area()
        opts = webdriver.ChromeOptions()
        prefs = {"download.default_directory": self.DATA_PATH}
        opts.add_experimental_option("prefs", prefs)

        headless = os.getenv("CDB_TEST_HEADLESS", 'True').lower() in ('true', '1', 't', 'y', 'yes')
        if headless:
            opts.add_argument("headless")
            opts.add_argument("window-size=1920,1080")

        self.driver = webdriver.Chrome(options=opts)
        self.driver.set_window_position(0, 0)
        if not headless:
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
            for entry in os.scandir(self.DATA_PATH):
                if entry.is_dir():
                    shutil.rmtree(entry.path)
                else:
                    os.remove(entry.path)
        else:
            # Create directory
            os.makedirs(self.DATA_PATH)

    def verify_file_downloaded(self, file_name):
        file_path = os.path.join(self.DATA_PATH, file_name)
        deadline = time.monotonic() + self.DOWNLOAD_TIMEOUT

        while time.monotonic() < deadline:
            partial_downloads = [
                entry for entry in os.scandir(self.DATA_PATH)
                if entry.name.endswith('.crdownload')
            ]
            if os.path.exists(file_path) and not partial_downloads:
                return
            time.sleep(0.1)

        self.fail('Downloaded file %s was not found or did not finish downloading' % file_name)
