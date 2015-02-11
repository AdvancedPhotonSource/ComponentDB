#!/usr/bin/env python

from cdbObject import CdbObject

class AuthorizationPrincipal(CdbObject):

    def __init__(self, name, token=None):
        CdbObject.__init__(self,{'name' : name, 'token' : token})

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
