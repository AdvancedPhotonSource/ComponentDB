#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import os
import urllib

from cdb.common.utility.encoder import Encoder
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.api.cdbRestApi import CdbRestApi
from cdb.common.objects.userInfo import UserInfo
from cdb.common.objects.userGroup import UserGroup


class UserRestApi(CdbRestApi):
    
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    @CdbRestApi.execute
    def getUserGroups(self):
        url = '%s/userGroups' % (self.getContextRoot())
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, UserGroup)

    @CdbRestApi.execute
    def getUsers(self):
        url = '%s/users' % (self.getContextRoot())
        responseData = self.sendRequest(url=url, method='GET')
        return self.toCdbObjectList(responseData, UserInfo)

    @CdbRestApi.execute
    def getUserById(self, id):
        if id is None:
            raise InvalidRequest('User id must be provided.')
        url = '%s/users/%s' % (self.getContextRoot(), id)
        responseData = self.sendRequest(url=url, method='GET')
        return UserInfo(responseData)

    @CdbRestApi.execute
    def getUserByUsername(self, username):
        if username is None:
            raise InvalidRequest('Username must be provided.')
        url = '%s/usersByUsername/%s' % (self.getContextRoot(), username)
        responseData = self.sendRequest(url=url, method='GET')
        return UserInfo(responseData)

    def getUserGroupByName(self, groupName):
        if groupName is None:
            raise InvalidRequest('Group name must be provided.')

        url = '%s/userGroupsByName/%s' % (self.getContextRoot(), groupName)

        responseData = self.sendRequest(url=url, method='GET')
        return UserGroup(responseData)

#######################################################################
# Testing.

if __name__ == '__main__':
    api = UserRestApi('sveseli', 'sveseli', 'zagreb.svdev.net', 10232, 'http')
    userGroups = api.getUserGroups()
    for userGroup in userGroups:
        print userGroup.getDisplayString()




