#!/usr/bin/env python

from cdbWebServiceCli import CdbWebServiceCli
from cdb.cdb_web_service.api.userInfoRestApi import UserInfoRestApi

class GetUserGroupListCli(CdbWebServiceCli):
    def __init__(self):
        CdbWebServiceCli.__init__(self)

    def runCommand(self):
        self.parseArgs(usage="""
    cdb-get-user-group-list 

Description:
    Retrieves list of registered user groups.
        """)
        api = UserInfoRestApi(self.getUsername(), self.getPassword(), self.getServiceHost(), self.getServicePort(), self.getServiceProtocol())
        userGroupList = api.getUserGroupList()
        for userGroup in userGroupList:
            print userGroup.getDisplayString(self.getDisplayKeys(), self.getDisplayFormat())


#######################################################################
# Run command.
if __name__ == '__main__':
    cli = GetUserGroupListCli()
    cli.run()
