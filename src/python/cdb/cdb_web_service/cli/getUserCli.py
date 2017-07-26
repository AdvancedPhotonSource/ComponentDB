#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.userRestApi import UserRestApi
from cdb.common.exceptions.invalidRequest import InvalidRequest

class GetUserCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)
        self.addOption('', '--id', dest='id', help='User id. Either id or username must be provided. If both are provided, id takes precedence.')
        self.addOption('', '--username', dest='username', help='User username. Either id or username must be provided. If both are provided, id takes precedence.')

    def checkArgs(self):
        if self.options.id is None and self.options.username is None:
            raise InvalidRequest('Either user id or username must be provided.')

    def getId(self):
        return self.options.id

    def getUsername(self):
        return self.options.username

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-user --id=ID|--username=USERNAME

Description:
    Retrieves user information.
        """)
        self.checkArgs()
        api = UserRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        if self.getId() is not None:
            userInfo = api.getUserById(self.getId())
        else:
            userInfo = api.getUserByUsername(self.getUsername())
        print userInfo.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())

#######################################################################
# Run command.
def runCommand():
    cli = GetUserCli()
    cli.run()

if __name__ == '__main__':
    runCommand()
