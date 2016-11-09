#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbObject import CdbObject

class UserInfo(CdbObject):

    DEFAULT_KEY_LIST = [ 'id', 'username', 'firstName', 'lastName', 'middleName', 'email', 'description' ]

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

    def getDefaultUserGroup(self):
        userGroupList = self.get('userGroupList', [])
        if len(userGroupList):
            return userGroupList[0]
        return None
