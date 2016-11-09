#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbObject import CdbObject

class AuthorizationPrincipal(CdbObject):

    def __init__(self, name, token=None, userInfo={}):
        CdbObject.__init__(self,{'name' : name, 'token' : token, 'userInfo' : userInfo})

    def getName(self):
        return self.get('name')

    def getAuthenticationToken(self):
        return self.get('token')

    def getToken(self):
        return self.get('token')

    def setRole(self, role):
        self['role'] = role

    def getRole(self):
        return self.get('role')

    def setUserInfo(self, userInfo):
        self['userInfo'] = userInfo

    def getUserInfo(self):
        return self.get('userInfo')
