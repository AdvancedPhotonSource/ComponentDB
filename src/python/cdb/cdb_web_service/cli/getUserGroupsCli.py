#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.userRestApi import UserRestApi

class GetUserGroupsCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-user-groups 

Description:
    Retrieves list of registered user groups.
        """)
        api = UserRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        userGroups = api.getUserGroups()
        for userGroup in userGroups:
            print userGroup.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())


#######################################################################
# Run command.
def runCommand():
    cli = GetUserGroupsCli()
    cli.run()

if __name__ == '__main__':
    runCommand()
