#!/usr/bin/env python

from cdb.common.utility.loggingManager import LoggingManager

class AuthorizationPrincipalRetriever:

    def __init__(self, name=None):
        self.adminGroupName = None
        self.name = name
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

    def getName(self):
        return self.name

    def setAdminGroupName(self, adminGroupName):
        self.adminGroupName = adminGroupName

    def getAuthorizationPrincipal(self, username):
        return None

#######################################################################
# Testing.
if __name__ == '__main__':
    pass

