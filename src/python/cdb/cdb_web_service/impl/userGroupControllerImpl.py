#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


#
# Implementation for user group controller.
#

#######################################################################

from cdb.common.objects.cdbObject import CdbObject
from cdb.common.objects.cdbObjectManager import CdbObjectManager
from cdb.common.db.api.userDbApi import UserDbApi

#######################################################################

class UserGroupControllerImpl(CdbObjectManager):
    """ User group controller implementation class. """

    def __init__(self):
        CdbObjectManager.__init__(self)
        self.userDbApi = UserDbApi()

    def getUserGroups(self):
        return self.userDbApi.getUserGroups()

    def getUserGroupByName(self, groupName):
        return self.userDbApi.getUserGroupByName(groupName)


