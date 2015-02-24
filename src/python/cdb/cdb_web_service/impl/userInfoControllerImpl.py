#!/usr/bin/env python

#
# Implementation for user info controller.
#

#######################################################################

import threading

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.userInfoDbApi import UserInfoDbApi

#######################################################################

class UserInfoControllerImpl(CdbObjectManager):
    """ User info controller implementation class. """

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.userInfoDbApi = UserInfoDbApi()

    def getUserInfoList(self):
        return self.userInfoDbApi.getUserInfoList()

    def getUserInfoById(self, id):
        return self.userInfoDbApi.getUserInfoById(id)

    def getUserInfoByUsername(self, username):
        return self.userInfoDbApi.getUserInfoByUsername(username)

