#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

from cdbSeleniumModules.user_info import UserInfo
from tests.base import CdbPortalTestCase


class UserInfoTest(CdbPortalTestCase):

    def setUp(self):
        super().setUp()
        self.user_info = UserInfo(self.driver)

    def test_user_info_pages(self):
        self.user_info.navigate_to_user_info_list()
        self.user_info.test_user_info_pages()

    def test_user_info_export(self):
        self.user_info.navigate_to_user_info_list()
        self.user_info.export_user_info(self)
