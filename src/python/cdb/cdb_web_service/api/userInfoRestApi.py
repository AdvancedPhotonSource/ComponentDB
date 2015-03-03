#!/usr/bin/env python

import os
import urllib

from cdb.common.utility.encoder import Encoder
from cdb.common.exceptions.cdbException import CdbException
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.common.api.cdbRestApi import CdbRestApi
from cdb.common.objects.userInfo import UserInfo
from cdb.common.objects.userGroup import UserGroup

class UserInfoRestApi(CdbRestApi):
    
    def __init__(self, username=None, password=None, host=None, port=None, protocol=None):
        CdbRestApi.__init__(self, username, password, host, port, protocol)

    def getUserGroupList(self):
        try:
            url = '%s/userGroups' % (self.getContextRoot())
            responseData = self.sendRequest(url=url, method='GET')
            return self.toCdbObjectList(responseData, UserGroup)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getUserInfoList(self):
        try:
            url = '%s/users' % (self.getContextRoot())
            responseData = self.sendRequest(url=url, method='GET')
            return self.toCdbObjectList(responseData, UserInfo)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getUserInfoById(self, id):
        try:
            if id is None:
                raise InvalidRequest('User id must be provided.')
            url = '%s/users/%s' % (self.getContextRoot(), id)
            responseData = self.sendRequest(url=url, method='GET')
            return UserInfo(responseData)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

    def getUserInfoByUsername(self, username):
        try:
            if username is None:
                raise InvalidRequest('Username must be provided.')
            url = '%s/usersByUsername/%s' % (self.getContextRoot(), username)
            responseData = self.sendRequest(url=url, method='GET')
            return UserInfo(responseData)
        except CdbException, ex:
            raise
        except Exception, ex:
            self.getLogger().exception('%s' % ex)
            raise CdbException(exception=ex)

#######################################################################
# Testing.

if __name__ == '__main__':
    api = UserInfoRestApi('sveseli', 'sveseli', 'zagreb.svdev.net', 10232, 'http')
    userGroupList = api.getUserGroupList()
    for userGroup in userGroupList:
        print userGroup.getDisplayString()




