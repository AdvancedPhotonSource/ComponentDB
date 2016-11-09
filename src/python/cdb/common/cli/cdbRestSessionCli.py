#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.cli.cdbRestCli import CdbRestCli

class CdbRestSessionCli(CdbRestCli):
    """ Base cdb session cli class. """

    def __init__(self, validArgCount=0):
        CdbRestCli.__init__(self, validArgCount)
        self.username = None
        self.password = None

        loginGroup = 'Login Options'
        self.addOptionGroup(loginGroup, None)
        self.addOptionToGroup(loginGroup, '', '--login-username', dest='loginUsername', help='Login username.')
        self.addOptionToGroup(loginGroup, '', '--login-password', dest='loginPassword', help='Login password.')

    def parseArgs(self, usage=None):
        CdbRestCli.parseArgs(self, usage)
        self.loginUsername = self.options.loginUsername
        self.loginPassword = self.options.loginPassword
        return (self.options, self.args)

    def getLoginUsername(self):
        return self.loginUsername

    def getLoginPassword(self):
        return self.loginPassword

    def hasCredentials(self):
        return (self.loginUsername != None and self.loginPassword != None)

#######################################################################
# Testing

if __name__ == '__main__':
        pass
