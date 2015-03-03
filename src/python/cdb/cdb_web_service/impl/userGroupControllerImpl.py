#!/usr/bin/env python

#
# Implementation for user group controller.
#

#######################################################################

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.userInfoDbApi import UserInfoDbApi

#######################################################################

class UserGroupControllerImpl(CdbObjectManager):
    """ User group controller implementation class. """

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.userInfoDbApi = UserInfoDbApi()

    def getUserGroupList(self):
        return self.userInfoDbApi.getUserGroupList()


