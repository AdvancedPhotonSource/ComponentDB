#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.userRestApi import UserRestApi

class GetUsersCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-users 

Description:
    Retrieves list of registered users.
        """)
        api = UserRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        users = api.getUsers()
        for user in users:
            print user.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())


#######################################################################
# Run command.
def runCommand():
    cli = GetUsersCli()
    cli.run()

if __name__ == '__main__':
    runCommand()
