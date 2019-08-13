#!/usr/bin/env python

from cdb.common.utility.loggingManager import LoggingManager

class AuthorizationPrincipalAuthenticator:

    def __init__(self, name=None):
        self.name = name
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)

    def getName(self):
        return self.name

    def authenticatePrincipal(self, principal, password):
        return None

#######################################################################
# Testing.
if __name__ == '__main__':
    pass

