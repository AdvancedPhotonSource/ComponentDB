#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdb.common.cli.cdbRestCli import CdbRestCli
from cdb.common.utility.configurationManager import ConfigurationManager

class CdbRestSessionCli(CdbRestCli):
    """ Base cdb session cli class. """

    def __init__(self, validArgCount=0):
        CdbRestCli.__init__(self, validArgCount)

        loginGroup = 'Login Options'
        self.addOptionGroup(loginGroup, None)
        self.addOptionToGroup(loginGroup, '', '--username', dest='username', help='Login username.')
        self.addOptionToGroup(loginGroup, '', '--password', dest='password', help='Login password.')
        self.addOptionToGroup(loginGroup, '', '--login-file', dest='loginFile', help='Login file (can be set via CDB_LOGIN_FILE environment variable).')

    def parseArgs(self, usage=None):
        CdbRestCli.parseArgs(self, usage)
        self.username = self.options.username
        self.password = self.options.password
        self.loginFile = self.options.loginFile
        self.parseLoginFile()
        return (self.options, self.args)

    def getUsername(self):
        return self.username

    def getPassword(self):
        return self.password

    def getLoginFile(self):
        if not self.loginFile:
            configManager = ConfigurationManager.getInstance()
            self.loginFile = configManager.getLoginFile()
        return self.loginFile

    def parseLoginFile(self):
        if self.getLoginFile() and not self.username and not self.password:
            try:
                # Assume form <username>|<password>
                tokenList = open(self.loginFile).readline().split('|')
                if len(tokenList) == 2:
                    self.username = tokenList[0].strip()
                    self.password = tokenList[1].strip()
            except:
                # Ignore invalid login file
                pass

    def hasCredentials(self):
        return (self.username != None and self.password != None)

#######################################################################
# Testing

if __name__ == '__main__':
    pass
