#!/usr/bin/env python

from cdb.common.cli.cdbRestCli import CdbRestCli

class CdbRestSessionCli(CdbRestCli):
    """ Base cdb session cli class. """

    def __init__(self, validArgCount=0):
        CdbRestCli.__init__(self, validArgCount)
        self.username = None
        self.password = None

        loginGroup = 'Login Options'
        self.addOptionGroup(loginGroup, None)
        self.addOptionToGroup(loginGroup, '', '--username', dest='username', help='Login username.')
        self.addOptionToGroup(loginGroup, '', '--password', dest='password', help='Login password.')

    def parseArgs(self, usage=None):
        CdbRestCli.parseArgs(self, usage)
        self.username = self.options.username
        self.password = self.options.password
        return (self.options, self.args)

    def getUsername(self):
        return self.username

    def getPassword(self):
        return self.password

    def hasCredentials(self):
        return (self.username != None and self.password != None)

#######################################################################
# Testing

if __name__ == '__main__':
        pass
